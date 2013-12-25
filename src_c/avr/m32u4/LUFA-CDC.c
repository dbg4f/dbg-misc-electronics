/*
             LUFA Library
     Copyright (C) Dean Camera, 2013.

  dean [at] fourwalledcubicle [dot] com
           www.lufa-lib.org
*/

/*
  Copyright 2013  Dean Camera (dean [at] fourwalledcubicle [dot] com)

  Permission to use, copy, modify, distribute, and sell this
  software and its documentation for any purpose is hereby granted
  without fee, provided that the above copyright notice appear in
  all copies and that both that the copyright notice and this
  permission notice and warranty disclaimer appear in supporting
  documentation, and that the name of the author not be used in
  advertising or publicity pertaining to distribution of the
  software without specific, written prior permission.

  The author disclaims all warranties with regard to this
  software, including all implied warranties of merchantability
  and fitness.  In no event shall the author be liable for any
  special, indirect or consequential damages or any damages
  whatsoever resulting from loss of use, data or profits, whether
  in an action of contract, negligence or other tortious action,
  arising out of or in connection with the use or performance of
  this software.
*/

/** \file
 *
 *  Main source file for the USBtoSerial project. This file contains the main tasks of
 *  the project and is responsible for the initial application hardware configuration.
 */

#include "USBtoSerial.h"


static RingBuffer_t Response_Buffer;
static uint8_t      Response_Buffer_Data[128];



/** LUFA CDC Class driver interface configuration and state information. This structure is
 *  passed to all CDC Class driver functions, so that multiple instances of the same class
 *  within a device can be differentiated from one another.
 */
USB_ClassInfo_CDC_Device_t VirtualSerial_CDC_Interface =
	{
		.Config =
			{
				.ControlInterfaceNumber         = INTERFACE_ID_CDC_CCI,
				.DataINEndpoint                 =
					{
						.Address                = CDC_TX_EPADDR,
						.Size                   = CDC_TXRX_EPSIZE,
						.Banks                  = 1,
					},
				.DataOUTEndpoint                =
					{
						.Address                = CDC_RX_EPADDR,
						.Size                   = CDC_TXRX_EPSIZE,
						.Banks                  = 1,
					},
				.NotificationEndpoint           =
					{
						.Address                = CDC_NOTIFICATION_EPADDR,
						.Size                   = CDC_NOTIFICATION_EPSIZE,
						.Banks                  = 1,
					},
			},
	};


// -----------------------------------------------------------------------------------------------------------------


#define CMD_L1_ECHO 		    0x01
#define CMD_L1_READ_REG 	    0x14
#define CMD_L1_WRITE_REG 	    0x15
#define CMD_L1_READ_ADC0 	    0x16
#define CMD_L1_ENABLE_ADC 	    0x17
#define CMD_L1_SWITCH_WATCHDOG 	0x18
#define CMD_L1_RESET_WATCHDOG 	0x19
#define CMD_L1_WRITE_REG_MASK 	0x1A
#define CMD_L1_SET_PORT_BITS 	0x1B
#define CMD_L1_CLEAR_PORT_BITS 	0x1C

#define RESP_UNKNOWN_CMD	0xEE
#define RESP_OK	            0xAA

#define RESP_ERROR_CRC_MISMATCH  0x03
#define RESP_ERROR_NOT_IN_SYNC   0x55

#define RESP_RESET_MARKER_A0   0x55
#define RESP_RESET_MARKER_A1   0x33
#define RESP_RESET_MARKER_B0   0xAA
#define RESP_RESET_MARKER_B1   0x55

#define START_PACKET_MARKER     0x55
#define START_ASYNC_MARKER_ADC  0x51
#define START_ASYNC_MARKER_CT   0x52


#define ADC_BUFFER_SIZE         16
#define ADC_BUFFER_DIV_SHIFT    4

#define ADC_CHANNELS_IN_USE     4

#define TX_MAX_BUFFER_SIZE      20

// -----------------------------------------------------------------------------------------------------------------



// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_adc_buffer
{
    uint8_t valuations[ADC_BUFFER_SIZE];
    uint8_t index;
} ADC_BUFFER, *PADC_BUFFER;

// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_adc_context
{
    ADC_BUFFER adc_buffers[ADC_CHANNELS_IN_USE];
    uint8_t avg_values[ADC_CHANNELS_IN_USE];
    uint8_t reported_values[ADC_CHANNELS_IN_USE];
    uint8_t adc_buf_index;
    uint8_t valuations_count;
    uint8_t updated;
} ADC_CONTEXT, *PADC_CONTEXT;

// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_counter_context
{
    uint8_t ct_value;
    uint8_t updated;
} CT_CONTEXT, *PCT_CONTEXT;

// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_response_context
{
    uint8_t response[TX_MAX_BUFFER_SIZE];
    uint8_t length;
    uint8_t updated;
} RESP_CONTEXT, *PRESP_CONTEXT;


// -----------------------------------------------------------------------------------------------------------------

typedef struct struct_tx_context
{
    uint8_t enabled;
} TX_CONTEXT, *PTX_CONTEXT;

static ADC_CONTEXT adc_context;
static TX_CONTEXT tx_context;
static CT_CONTEXT ct_context;
static RESP_CONTEXT resp_context;



static uint8_t crcUpdate(uint8_t crc, uint8_t data);
static void TxContextSendNext();
static uint8_t send_with_crc(uint8_t crc, uint8_t data);
static uint8_t IsAdcSnapshotChanged(PADC_CONTEXT p_adc_context);

// -----------------------------------------------------------------------------------------------------------------
static uint8_t adc_buf_avg(PADC_BUFFER p_adc_buffer)
{
    uint16_t avg = 0;
    uint8_t i = 0;
    for (i = 0; i<ADC_BUFFER_SIZE; i++)
    {
        avg += p_adc_buffer->valuations[i];
    }
    return (uint8_t)(avg >> ADC_BUFFER_DIV_SHIFT);
}

// -----------------------------------------------------------------------------------------------------------------
static void adc_buf_init(PADC_BUFFER p_adc_buffer)
{
    uint8_t i = 0;
    for (i = 0; i<ADC_BUFFER_SIZE; i++)
    {
        p_adc_buffer->valuations[i] = 0;
    }
    p_adc_buffer->index = 0;
}

// -----------------------------------------------------------------------------------------------------------------
static void adc_buf_add(PADC_BUFFER p_adc_buffer, uint8_t value)
{
    uint8_t i = p_adc_buffer->index + 1;
    if (i == ADC_BUFFER_SIZE)
    {
        i = 0;
    }
    p_adc_buffer->valuations[i] = value;
    p_adc_buffer->index = i;
}


// -----------------------------------------------------------------------------------------------------------------
static void tx_resp_snapshot(PRESP_CONTEXT p_resp_context)
{

    uint16_t expectedSize = p_resp_context->length + 2;
    uint8_t crc = 0xFF;
    if (RingBuffer_GetFreeCount(&Response_Buffer) >= expectedSize)
    {

        p_resp_context->updated = 0;

        crc = send_with_crc(crc, START_PACKET_MARKER);

        uint8_t i = 0;
        for (i=0; i<p_resp_context->length; i++)
        {
            crc = send_with_crc(crc, p_resp_context->response[i]);
        }

        crc = send_with_crc(crc, crc);

    }

}


// -----------------------------------------------------------------------------------------------------------------
static void tx_ct_snapshot(PCT_CONTEXT p_ct_context)
{

    uint16_t expectedSize = 3;
    uint8_t crc = 0xFF;
    if (RingBuffer_GetFreeCount(&Response_Buffer) >= expectedSize)
    {

        p_ct_context->updated = 0;

        crc = send_with_crc(crc, START_ASYNC_MARKER_CT);
        crc = send_with_crc(crc, p_ct_context->ct_value);
        crc = send_with_crc(crc, crc);


    }


}

// -----------------------------------------------------------------------------------------------------------------
static uint8_t IsAdcSnapshotChanged(PADC_CONTEXT p_adc_context)
{
	uint8_t i = 0;
	for (i=0; i<ADC_CHANNELS_IN_USE; i++)
    {            
		if (p_adc_context->reported_values[i] != p_adc_context->avg_values[i])
		{
			return 1;
		}
		
		if (i == 0)
		{
			break; // TODO: only ADC0 in use
		}
    }
    
    return 0;
	
}


// -----------------------------------------------------------------------------------------------------------------
static void tx_adc_snapshot(PADC_CONTEXT p_adc_context)
{

    uint16_t expectedSize = ADC_CHANNELS_IN_USE + 2;
    uint8_t crc = 0xFF;
    if (RingBuffer_GetFreeCount(&Response_Buffer) >= expectedSize)
    {

        p_adc_context->updated = 0;

        crc = send_with_crc(crc, START_ASYNC_MARKER_ADC);

        uint8_t i = 0;
        for (i=0; i<ADC_CHANNELS_IN_USE; i++)
        {
            crc = send_with_crc(crc, p_adc_context->avg_values[i]);
            p_adc_context->reported_values[i] = p_adc_context->avg_values[i];
        }

        crc = send_with_crc(crc, crc);

    }



}


// -----------------------------------------------------------------------------------------------------------------
static void adc_ctx_init(PADC_CONTEXT p_adc_context)
{
    uint8_t i = 0;
    for (i = 0; i<ADC_CHANNELS_IN_USE; i++)
    {
        adc_buf_init(&p_adc_context->adc_buffers[i]);
        p_adc_context->avg_values[i] = 0;
        p_adc_context->reported_values[i] = 0;
    }

    p_adc_context->adc_buf_index = 0;
    p_adc_context->valuations_count = 0;
    p_adc_context->updated = 0;
}

// -----------------------------------------------------------------------------------------------------------------
static void adc_run_next(uint8_t channel)
{

    // enable ADC, int, start, once, div/8
    ADCSRA = (1<<ADEN)|(1<<ADIE)|(1<<ADSC)|(0<<ADATE)|(3<<ADPS0);

    //REFS  -- 0b[01]000101 use AVCC ref
    //ADLAR -- 0b01[1]00101 left alignment
    //MUX   -- 0b0100[0101] channel 5.
    ADMUX = (0b01100000 | (channel & 0b00000111));

}


// -----------------------------------------------------------------------------------------------------------------
static void adc_ctx_new_value(PADC_CONTEXT p_adc_context, uint8_t value)
{
    p_adc_context->valuations_count++;

    uint8_t buf_index = p_adc_context->adc_buf_index;

    adc_buf_add(&p_adc_context->adc_buffers[buf_index], value);

    if (p_adc_context->valuations_count == ADC_BUFFER_SIZE)
    {

        p_adc_context->avg_values[buf_index] = adc_buf_avg(&p_adc_context->adc_buffers[buf_index]);

        buf_index++;

        if (buf_index == ADC_CHANNELS_IN_USE)
        {
            buf_index = 0;
            p_adc_context->updated = 1; // all buffers filled with new values, set flag for TX cycle
        }

        p_adc_context->adc_buf_index = buf_index;

        p_adc_context->valuations_count = 0;

    }

    adc_run_next(p_adc_context->adc_buf_index);

}

// -----------------------------------------------------------------------------------------------------------------
static void ct_ctx_increase(PCT_CONTEXT p_ct_context)
{
    p_ct_context->ct_value++;
    p_ct_context->updated = 1;
}

static void resp_ctx_add(PRESP_CONTEXT p_resp_context, uint8_t byte1, uint8_t byte2, uint8_t sequence)
{
    p_resp_context->response[0] = 3; // length of resp bytes
    p_resp_context->response[1] = sequence;
    p_resp_context->response[2] = byte1;
    p_resp_context->response[3] = byte2;
    p_resp_context->length = 4; // buf length
    p_resp_context->updated = 1;
}


// -----------------------------------------------------------------------------------------------------------------
static void tx_ctx_init(PTX_CONTEXT p_tx_context)
{
    p_tx_context->enabled = 1;
}

// -----------------------------------------------------------------------------------------------------------------
static void ct_ctx_init(PCT_CONTEXT p_ct_context)
{
    p_ct_context->ct_value = 0;
    p_ct_context->updated = 0;
}

// -----------------------------------------------------------------------------------------------------------------
static void resp_ctx_init(PRESP_CONTEXT p_resp_context)
{
    p_resp_context->length = 0;
    uint8_t i = 0;
    for (i=0; i<TX_MAX_BUFFER_SIZE; i++)
    {
        p_resp_context->response[i] = 0;
    }
    p_resp_context->updated = 0;
}

// -----------------------------------------------------------------------------------------------------------------

static void tx_send_resp2_immediately(uint8_t byte1, uint8_t byte2, uint8_t sequence);

static void send_resp2(uint8_t byte1, uint8_t byte2, uint8_t sequence)
{
    if (tx_context.enabled)
    {
        resp_ctx_add(&resp_context, byte1, byte2, sequence);
    }
    else
    {
        tx_send_resp2_immediately(byte1, byte2, sequence);
    }
}


// -----------------------------------------------------------------------------------------------------------------
static void TxContextSendNext()
{

    if (tx_context.enabled) // send snapshots only in asynch mode
    {

        //For all updated buffers:
        // Check if snapshot will fit the USB TX ring buffer, copy and reset flag 'updated'

        if (ct_context.updated)
        {
            tx_ct_snapshot(&ct_context);
        }

        if (resp_context.updated)
        {
            tx_resp_snapshot(&resp_context);
        }

        if (adc_context.updated && IsAdcSnapshotChanged(&adc_context))
        {
            tx_adc_snapshot(&adc_context);
        }

    }

}

// -----------------------------------------------------------------------------------------------------------------

static void ct_init(void)
{
    ct_ctx_init(&ct_context);

    // any level change at int0 generates int
    EICRA |= (0<<ISC01)|(1<<ISC00);
    EIMSK |= (1<<INT0);

}

// -----------------------------------------------------------------------------------------------------------------

static void resp_init(void)
{
    resp_ctx_init(&resp_context);
}

// -----------------------------------------------------------------------------------------------------------------

static void adc_init(void)
{

    adc_ctx_init(&adc_context);

    adc_run_next(0);

}

// -----------------------------------------------------------------------------------------------------------------

static void tx_init(void)
{

    tx_ctx_init(&tx_context);

    ////////// UCSRB = (1<<RXEN)|(1<<TXEN)|(0<<RXCIE)|(1<<TXCIE)|(0<<UDRIE);

}





// -----------------------------------------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------------------------------------


#define COMMAND_BUF_LEN 10

typedef struct
{
    uint8_t CommandBuffer[COMMAND_BUF_LEN];
    uint8_t CommandIndex;
} DrvCommand_t;


DrvCommand_t DrvCommand;

void ResetCommandBuffer(DrvCommand_t* const pDrvCommand)
{
    int i;
    for (i=0; i<COMMAND_BUF_LEN; i++)
    {
        pDrvCommand->CommandBuffer[i] = 0;
    }
    pDrvCommand->CommandIndex = 0;
}

void AddCommandByte(DrvCommand_t* const pDrvCommand, int8_t CommandByte)
{
    if (pDrvCommand->CommandIndex < COMMAND_BUF_LEN - 1)
    {
        pDrvCommand->CommandBuffer[pDrvCommand->CommandIndex++] = CommandByte;
    }
}

// -----------------------------------------------------------------------------------------------------------------
static uint8_t crcUpdate(uint8_t crc, uint8_t data)
{
    uint8_t i;
    //
    crc ^= data;
    for(i = 0; i < 8; i++)
    {
        if(crc & 0x80)
            crc = (crc << 1) ^ 0xE5;
        else
            crc <<= 1;
    }
    return crc;
}

// -----------------------------------------------------------------------------------------------------------------
static void Sendchar(uint8_t Data)
{
    RingBuffer_Insert(&Response_Buffer, Data);
}


// -----------------------------------------------------------------------------------------------------------------
static uint8_t send_with_crc(uint8_t crc, uint8_t data)
{
    Sendchar(data);
    return crcUpdate(crc, data);
}


// -----------------------------------------------------------------------------------------------------------------

static void tx_send_resp2_immediately(uint8_t byte1, uint8_t byte2, uint8_t sequence)
{
    uint8_t crc = 0xFF;
    crc = send_with_crc(crc, START_PACKET_MARKER);
    crc = send_with_crc(crc, 0x03);
    crc = send_with_crc(crc, sequence);
    crc = send_with_crc(crc, byte1);
    crc = send_with_crc(crc, byte2);
    crc = send_with_crc(crc, crc);
}


#define CASE_RD(REG_NAME, REG_SEL) case REG_SEL: res=REG_NAME ; break;

// -----------------------------------------------------------------------------------------------------------------
void read_reg(uint8_t reg, uint8_t sequence)
{
    uint8_t res = 0x00;

    char found = 1;

    switch (reg)
    {

        CASE_RD(PORTB   ,0x01)
        CASE_RD(PORTC   ,0x03)
        CASE_RD(PORTD   ,0x05)
        CASE_RD(PORTE   ,0x07)
        CASE_RD(PORTF   ,0x09)

    default :
        found = 0;
    }

    if (found)
    {
        send_resp2(0xDD, res, sequence);
    }
    else
    {
        send_resp2(0xAC, res, sequence);
    }


}

#define CASE_WR(REG_NAME, REG_SEL) case REG_SEL: REG_NAME = value; break;

// -----------------------------------------------------------------------------------------------------------------
void write_reg(uint8_t reg, uint8_t value, uint8_t mask, uint8_t sequence)
{

    char found = 1;

    //uint8_t tmp;

    switch (reg)
    {

        CASE_WR(PORTB   ,0x01)
        CASE_WR(PORTC   ,0x03)
        CASE_WR(PORTD   ,0x05)
        CASE_WR(PORTE   ,0x07)
        CASE_WR(PORTF   ,0x09)

        CASE_WR(DDRB   ,0x02)
        CASE_WR(DDRC   ,0x04)
        CASE_WR(DDRD   ,0x06)
        CASE_WR(DDRE   ,0x08)
        CASE_WR(DDRF   ,0x0A)


        CASE_WR(TCCR1A  ,0x0B)
        CASE_WR(TCCR3A  ,0x0C)
        CASE_WR(TCCR1B  ,0x0D)
        CASE_WR(TCCR3B  ,0x0E)
        CASE_WR(OCR1AH  ,0x0F)
        CASE_WR(OCR1AL  ,0x10)
        CASE_WR(OCR1BH  ,0x11)
        CASE_WR(OCR1BL  ,0x12)


    default :
        found = 0;
    }

    if (found)
    {
        send_resp2(0xDA, value, sequence);
    }
    else
    {
        send_resp2(0xDC, value, sequence);
    }

}



// -----------------------------------------------------------------------------------------------------------------


// -----------------------------------------------------------------------------------------------------------------
#define CASE_CLEAR_PORT(REG_NAME, REG_SEL) case REG_SEL: REG_NAME &= mask; break;
void clear_port_bits(uint8_t reg, uint8_t mask, uint8_t sequence)
{

    char found = 1;

    switch (reg) {

        CASE_CLEAR_PORT(PORTB   ,0x01)
        CASE_CLEAR_PORT(PORTC   ,0x03)
        CASE_CLEAR_PORT(PORTD   ,0x05)
        CASE_CLEAR_PORT(PORTE   ,0x07)
        CASE_CLEAR_PORT(PORTF   ,0x09)


    default :
        found = 0;
    }

    if (found)
    {
        send_resp2(0xDA, mask, sequence);
    }
    else
    {
        send_resp2(0xDC, mask, sequence);
    }


}
// -----------------------------------------------------------------------------------------------------------------
#define CASE_SET_PORT(REG_NAME, REG_SEL) case REG_SEL: REG_NAME |= mask; break;
void set_port_bits(uint8_t reg, uint8_t mask, uint8_t sequence)
{

    char found = 1;

    switch (reg) {

        CASE_SET_PORT(PORTB   ,0x01)
        CASE_SET_PORT(PORTC   ,0x03)
        CASE_SET_PORT(PORTD   ,0x05)
        CASE_SET_PORT(PORTE   ,0x07)
        CASE_SET_PORT(PORTF   ,0x09)

    default :
        found = 0;
    }

    if (found)
    {
        send_resp2(0xDA, mask, sequence);
    }
    else
    {
        send_resp2(0xDC, mask, sequence);
    }


}


void ExecExtCommand(uint8_t cmd, uint8_t param, uint8_t param2, uint8_t param3, uint8_t sequence)
{

    switch (cmd)
    {
    case CMD_L1_ECHO:
        send_resp2(param, param, sequence);
        break;

   case CMD_L1_SET_PORT_BITS:
        set_port_bits(param, param2, sequence);
        break;

   case CMD_L1_CLEAR_PORT_BITS:
        clear_port_bits(param, param2, sequence);
        break;

    case CMD_L1_READ_REG:
        read_reg(param, sequence);
        break;

    case CMD_L1_WRITE_REG:
        write_reg(param, param2, 0xFF, sequence);
        break;

    case CMD_L1_ENABLE_ADC:
        adc_init();
        tx_init();
        ct_init();
        resp_init();
        break;

    default:
        send_resp2(RESP_UNKNOWN_CMD, param, sequence);
    }

}
// -----------------------------------------------------------------------------------------------------------------


void AddByteAndTryCommand(DrvCommand_t* const pDrvCommand, int8_t CommandByte)
{
    AddCommandByte(pDrvCommand, CommandByte);

    uint8_t Finalized = false;

    if (pDrvCommand->CommandIndex == 1)
    {
        int8_t Marker = pDrvCommand->CommandBuffer[0];

        if (Marker == 'S')
        {
            Sendchar('R');
            Sendchar('D');
            Sendchar('R');
            Sendchar('V');
            Sendchar('0');
            Sendchar('3');

            Finalized = true;
        }
        if (Marker != START_PACKET_MARKER)
        {
            send_resp2(RESP_ERROR_NOT_IN_SYNC, Marker, 0);
            Finalized = true;
        }

    }


    if (pDrvCommand->CommandIndex > 1)
    {

        int8_t Length = pDrvCommand->CommandBuffer[1];
        int8_t FullLength = Length + 3;

        if (pDrvCommand->CommandIndex == FullLength)
        {
            uint8_t crc;
            uint8_t i;

            uint8_t Sequence = 0;
            uint8_t Command = 0;
            uint8_t Parameter = 0;
            uint8_t Parameter2 = 0;
            uint8_t Parameter3 = 0;
            uint8_t ext_crc;

            crc = 0xFF;

            for (i = 0; i <= FullLength - 2; i++)
            {
                crc = crcUpdate(crc, pDrvCommand->CommandBuffer[i]);
            }

            ext_crc = pDrvCommand->CommandBuffer[FullLength - 1];

            if (ext_crc != crc)
            {
                send_resp2(RESP_ERROR_CRC_MISMATCH, crc, Sequence);
            }
            else
            {
                Command     = pDrvCommand->CommandBuffer[2];
                Sequence    = pDrvCommand->CommandBuffer[3];

                if (Length >= 3)
                    Parameter   = pDrvCommand->CommandBuffer[4];

                if (Length >= 4)
                    Parameter2  = pDrvCommand->CommandBuffer[5];

                if (Length >= 5)
                    Parameter3  = pDrvCommand->CommandBuffer[6];


                ExecExtCommand(Command, Parameter, Parameter2, Parameter3, Sequence);


            }




            Finalized = true;
        }



    }


    if (Finalized)
    {
        ResetCommandBuffer(pDrvCommand);
    }



}


// -----------------------------------------------------------------------------------------------------------------
ISR(ADC_vect)
{
    // NB! sequence and count of ADC readings is important
    uint8_t adc0_valueL = ADCL;
    uint8_t adc0_valueH = ADCH;

    adc_ctx_new_value(&adc_context, adc0_valueH);

}




// -----------------------------------------------------------------------------------------------------------------
ISR(INT0_vect)
{
    ct_ctx_increase(&ct_context);
}




/** Main program entry point. This routine contains the overall program flow, including initial
 *  setup of all components and the main program loop.
 */
int main(void)
{
	SetupHardware();

    RingBuffer_InitBuffer(&Response_Buffer, Response_Buffer_Data, sizeof(Response_Buffer_Data));

	LEDs_SetAllLEDs(LEDMASK_USB_NOTREADY);
	GlobalInterruptEnable();

	for (;;)
	{
		/* Only try to read in bytes from the CDC interface if the transmit buffer is not full */
		if (!(RingBuffer_IsFull(&Response_Buffer)))
		{
			int16_t ReceivedByte = CDC_Device_ReceiveByte(&VirtualSerial_CDC_Interface);

			if (!(ReceivedByte < 0))
			{
			    AddByteAndTryCommand(&DrvCommand, (int8_t)ReceivedByte);
			}

		}


        TxContextSendNext();

		uint16_t BufferCount = RingBuffer_GetCount(&Response_Buffer);
		if (BufferCount)
		{
			Endpoint_SelectEndpoint(VirtualSerial_CDC_Interface.Config.DataINEndpoint.Address);

			/* Check if a packet is already enqueued to the host - if so, we shouldn't try to send more data
			 * until it completes as there is a chance nothing is listening and a lengthy timeout could occur */
			if (Endpoint_IsINReady())
			{
				/* Never send more than one bank size less one byte to the host at a time, so that we don't block
				 * while a Zero Length Packet (ZLP) to terminate the transfer is sent if the host isn't listening */
				uint8_t BytesToSend = MIN(BufferCount, (CDC_TXRX_EPSIZE - 1));

				while (BytesToSend--)
				{
					/* Try to send the next byte of data to the host, abort if there is an error without dequeuing */
					if (CDC_Device_SendByte(&VirtualSerial_CDC_Interface,
											RingBuffer_Peek(&Response_Buffer)) != ENDPOINT_READYWAIT_NoError)
					{
						break;
					}

					/* Dequeue the already sent byte from the buffer now we have confirmed that no transmission error occurred */
					RingBuffer_Remove(&Response_Buffer);
				}
			}
		}

		CDC_Device_USBTask(&VirtualSerial_CDC_Interface);
		USB_USBTask();
	}
}

/** Configures the board hardware and chip peripherals for the demo's functionality. */
void SetupHardware(void)
{
#if (ARCH == ARCH_AVR8)
	/* Disable watchdog if enabled by bootloader/fuses */
	MCUSR &= ~(1 << WDRF);
	wdt_disable();

	/* Disable clock division */
	clock_prescale_set(clock_div_1);
#endif

	/* Hardware Initialization */
	LEDs_Init();
	USB_Init();
}

/** Event handler for the library USB Connection event. */
void EVENT_USB_Device_Connect(void)
{
	LEDs_SetAllLEDs(LEDMASK_USB_ENUMERATING);
}

/** Event handler for the library USB Disconnection event. */
void EVENT_USB_Device_Disconnect(void)
{
	LEDs_SetAllLEDs(LEDMASK_USB_NOTREADY);
}

/** Event handler for the library USB Configuration Changed event. */
void EVENT_USB_Device_ConfigurationChanged(void)
{
	bool ConfigSuccess = true;

	ConfigSuccess &= CDC_Device_ConfigureEndpoints(&VirtualSerial_CDC_Interface);

	LEDs_SetAllLEDs(ConfigSuccess ? LEDMASK_USB_READY : LEDMASK_USB_ERROR);
}

/** Event handler for the library USB Control Request reception event. */
void EVENT_USB_Device_ControlRequest(void)
{
	CDC_Device_ProcessControlRequest(&VirtualSerial_CDC_Interface);
}

