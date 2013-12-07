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

static void send_resp2(uint8_t byte1, uint8_t byte2, uint8_t sequence)
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
    //adc0_valueL = ADCL;
    //adc0_valueH = ADCH;

    //adc_ctx_new_value(&adc_context, adc0_valueH);

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

