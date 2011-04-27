// usbhidj.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"

#include <wtypes.h>
#include <initguid.h>

extern "C" {

// This file is in the Windows DDK available from Microsoft.
#include "hidsdi.h"

#include <setupapi.h>
#include <dbt.h>
}


GUID						HidGuid;
HANDLE						hDevInfo;
SP_DEVICE_INTERFACE_DATA	devInfoData;
int							MemberIndex = 0;
LONG						Result;	
HIDP_CAPS					Capabilities;
//HANDLE						DeviceHandle=NULL;
HANDLE						ReadHandle;
OVERLAPPED					HIDOverlapped;
HANDLE						hEventObject;
PSP_DEVICE_INTERFACE_DETAIL_DATA	detailData = NULL;
DWORD								NumberOfBytesRead;
char								InputReport[256];


void GetDeviceCapabilities(HANDLE DeviceHandle);
void PrepareForOverlappedTransfer();
void ReadInputReport();
void ReadInputReportContiniously();
void DisplayIfChanged();


// 0583 A00B


HANDLE findDevice(USHORT vendorID, USHORT deviceID)
{
	ULONG								Length = 0;
	
	bool								LastDevice;
	ULONG								Required;
	HIDD_ATTRIBUTES						Attributes;

	HidD_GetHidGuid(&HidGuid);	

	hDevInfo = SetupDiGetClassDevs(&HidGuid, NULL, NULL, DIGCF_PRESENT|DIGCF_INTERFACEDEVICE);
			
	printf("devInfo = %08X\n", hDevInfo);

	devInfoData.cbSize = sizeof(devInfoData);
	MemberIndex = 0;
	LastDevice = FALSE;

	do 
	{
		Result=SetupDiEnumDeviceInterfaces(hDevInfo, 0, &HidGuid, MemberIndex, &devInfoData);

		if (Result != 0) 
		{
			printf("Index = %i\n", MemberIndex);

			Result = SetupDiGetDeviceInterfaceDetail(hDevInfo, &devInfoData, NULL, 0, &Length, NULL);

			detailData = (PSP_DEVICE_INTERFACE_DETAIL_DATA)malloc(Length);
			
			//Set cbSize in the detailData structure.

			detailData -> cbSize = sizeof(SP_DEVICE_INTERFACE_DETAIL_DATA);

			//Call the function again, this time passing it the returned buffer size.

			Result = SetupDiGetDeviceInterfaceDetail(hDevInfo, &devInfoData, detailData, Length, &Required, NULL);
			
			HANDLE DeviceHandle=CreateFile(detailData->DevicePath, 0, FILE_SHARE_READ|FILE_SHARE_WRITE, (LPSECURITY_ATTRIBUTES)NULL,OPEN_EXISTING, 0, NULL);

			printf("DeviceHandle = %08X\n", DeviceHandle);

			Attributes.Size = sizeof(Attributes);

			Result = HidD_GetAttributes(DeviceHandle, &Attributes);

			printf("VID=%04X PID=%04X  %ws\n", Attributes.VendorID, Attributes.ProductID, detailData->DevicePath);

			if (vendorID == Attributes.VendorID && deviceID == Attributes.ProductID) 
			{
				return DeviceHandle;
			}
		}
		else 
		{
			LastDevice = TRUE;
		}
	
		MemberIndex = MemberIndex + 1;
	}
	while ((LastDevice == FALSE));


	return NULL;

}

int _tmain(int argc, _TCHAR* argv[])
{

	HANDLE DeviceHandle = findDevice(0x0583, 0xA00B);

	if (DeviceHandle == NULL) 
	{
		printf("Device not found\n");
	}

	GetDeviceCapabilities(DeviceHandle);

	PrepareForOverlappedTransfer();	

	ReadInputReportContiniously();

	return 0;
}


void GetDeviceCapabilities(HANDLE DeviceHandle)
{
	PHIDP_PREPARSED_DATA	PreparsedData;

	HidD_GetPreparsedData(DeviceHandle, &PreparsedData);
	
	HidP_GetCaps(PreparsedData, &Capabilities);
	
	printf("%s%X\n", "Usage Page:                       ",	Capabilities.UsagePage);	
	printf("%s%d\n", "Input Report Byte Length:         ",	Capabilities.InputReportByteLength);	
	printf("%s%d\n", "Output Report Byte Length:        ",	Capabilities.OutputReportByteLength);
	printf("%s%d\n", "Feature Report Byte Length:       ",	Capabilities.FeatureReportByteLength);
	printf("%s%d\n", "Number of Link Collection Nodes:  ",	Capabilities.NumberLinkCollectionNodes);
	printf("%s%d\n", "Number of Input Button Caps:      ",	Capabilities.NumberInputButtonCaps);
	printf("%s%d\n", "Number of InputValue Caps:        ",	Capabilities.NumberInputValueCaps);
	printf("%s%d\n", "Number of InputData Indices:      ",	Capabilities.NumberInputDataIndices);
	printf("%s%d\n", "Number of Output Button Caps:     ",	Capabilities.NumberOutputButtonCaps);
	printf("%s%d\n", "Number of Output Value Caps:      ",	Capabilities.NumberOutputValueCaps);
	printf("%s%d\n", "Number of Output Data Indices:    ",	Capabilities.NumberOutputDataIndices);
	printf("%s%d\n", "Number of Feature Button Caps:    ",	Capabilities.NumberFeatureButtonCaps);
	printf("%s%d\n", "Number of Feature Value Caps:     ",	Capabilities.NumberFeatureValueCaps);
	printf("%s%d\n", "Number of Feature Data Indices:   ",	Capabilities.NumberFeatureDataIndices);

	//No need for PreparsedData any more, so free the memory it's using.

	HidD_FreePreparsedData(PreparsedData);
	//DisplayLastError("HidD_FreePreparsedData: ") ;
}

void PrepareForOverlappedTransfer()
{
	//Get a handle to the device for the overlapped ReadFiles.

	ReadHandle=CreateFile(detailData->DevicePath, GENERIC_READ, FILE_SHARE_READ|FILE_SHARE_WRITE,(LPSECURITY_ATTRIBUTES)NULL, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, NULL);

	if (hEventObject == 0)
	{
		hEventObject = CreateEvent(NULL, TRUE, TRUE, L"");

		HIDOverlapped.hEvent = hEventObject;
		HIDOverlapped.Offset = 0;
		HIDOverlapped.OffsetHigh = 0;
		
	}
}

void ReadInputReport()
{

	DWORD	Result;

	//BYTE outRep[4];

	//memset(outRep, 0xFF, 4);


	
	//The first byte is the report number.
	InputReport[0]=0;

	if (ReadHandle != INVALID_HANDLE_VALUE)
	{
		Result = ReadFile(ReadHandle, InputReport, Capabilities.InputReportByteLength, &NumberOfBytesRead,(LPOVERLAPPED) &HIDOverlapped); 

		//Result = WriteFile(ReadHandle, outRep, 4, &NumberOfBytesRead,(LPOVERLAPPED) &HIDOverlapped); 

	}
	else 
	{
		printf("Invalid read handle\n");
	}
 
	Result = WaitForSingleObject(hEventObject, 	6000);

	switch (Result)
	{
		case WAIT_OBJECT_0:
		{
			
			DisplayIfChanged();
			
			break;
		}
	case WAIT_TIMEOUT:
		{
			printf("%s\n", "ReadFile timeout");
		
			Result = CancelIo(ReadHandle);
		
			//A timeout may mean that the device has been removed. 
			//Close the device handles and set MyDeviceDetected = False 
			//so the next access attempt will search for the device.
			
			printf("Can't read from device");
			break;
		}
	default:
		{
			printf("%s\n", "Undefined error");

			//Close the device handles and set MyDeviceDetected = False 
			//so the next access attempt will search for the device.

			printf("Can't read from device");

			break;
		}

	ResetEvent(hEventObject);

	//Display the report data.
		
	}
}

void ReadInputReportContiniously() 
{
	for (;;)
	{
		ReadInputReport();
		Sleep(10);
	}
}

void Display() ;

unsigned char inputReport[9];

void fillReport(unsigned char* arr, int len)
{
	for (int i=0; i<len; i++)
	{
		arr[i] = inputReport[i];
	}
}

void DisplayIfChanged() 
{
	bool changed = false;

	for (int ByteNumber=0; ByteNumber < Capabilities.InputReportByteLength; ByteNumber++)
	{
		unsigned char ReceivedByte = InputReport[ByteNumber];
		//printf("%02X ", ReceivedByte);
		changed |= (inputReport[ByteNumber] != ReceivedByte);
		inputReport[ByteNumber] = ReceivedByte;
	}
	//printf("\n");

	if (changed) 
	{
		Display();
	}

}

void Display() 
{
	for (int ByteNumber=0; ByteNumber < Capabilities.InputReportByteLength; ByteNumber++)
	{
		unsigned char ReceivedByte = InputReport[ByteNumber];
		printf("%02X ", ReceivedByte);
	}
	printf("\n");

}