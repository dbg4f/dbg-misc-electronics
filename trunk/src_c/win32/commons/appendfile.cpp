
#include "appendfile.h"
#include "logging.h"
#include "syserror.h"


HANDLE hAppend = NULL;

void openLogFile()
{

	if (hAppend != NULL) {
		return;
	}

	hAppend = CreateFileW((LOG_FILE_NAME), // open Two.txt
            /*FILE_APPEND_DATA*/GENERIC_WRITE,         // open for writing
            FILE_SHARE_READ,          // allow multiple readers
            NULL,                     // no security
            OPEN_ALWAYS,              // open or create
            FILE_ATTRIBUTE_NORMAL,    // normal file
            NULL);                    // no attr. template


	if (hAppend == INVALID_HANDLE_VALUE)
	{
		MessageBox(NULL, L"Append, failed to open file", L"", MB_OK);
		ErrorExit(L"Create log file");
	}
	else 
	{
		//MessageBox(NULL, L"Opened Log file OK", L"", MB_OK);
	}

}


void logToFile(wchar_t* text)
{
	SYSTEMTIME t;

	//MessageBox(NULL, L"Append 4", L"", MB_OK);

	DWORD  dwBytesWritten, dwPos;
	wchar_t   buff[4096];

	//MessageBox(NULL, L"Append 5", L"", MB_OK);

	openLogFile();

	GetLocalTime(&t);

	if (hAppend == INVALID_HANDLE_VALUE)
	{		
		MessageBox(NULL, L"Append Handle Bad", L"", MB_OK);
		return;
	}

	wsprintfW(buff, L"%d.%02d %02d.%02d:%02d %03d - %s\r\n", t.wDay, t.wMonth,  t.wHour, t.wMinute, t.wSecond, t.wMilliseconds, text);

	dwPos = SetFilePointer(hAppend, 0, NULL, FILE_END);
	
	
	MessageBox(NULL, buff, L"", MB_OK);
	
	BOOL res = WriteFile(hAppend, buff, wcslen(buff)*sizeof(wchar_t), &dwBytesWritten, NULL);
	
	if (!res) 
	{
		ErrorExit(L"Logging");
	}


	//CloseHandle(hAppend);

}


void appendTextToFile(wchar_t* text)
{

	DWORD  dwBytesWritten, dwPos;	

	//MessageBox(NULL, L"appendTextToFile", L"", MB_OK);
	
	//MessageBox(NULL, text, L"", MB_OK);

	openLogFile();
	
	dwPos = SetFilePointer(hAppend, 0, NULL, FILE_END);
	BOOL res = WriteFile(hAppend, (void*)text, wcslen(text)*sizeof(wchar_t), &dwBytesWritten, NULL);

	if (!res) 
	{
		ErrorExit(L"Logging");
	}
	
	//CloseHandle(hAppend);

}
