#include "logging.h"

#ifndef _WIN32_WCE

#define malloc(A) LocalAlloc(LMEM_ZEROINIT, A)

#define free(A) LocalFree(A)

#endif 

void logText(wchar_t* text) {

	wchar_t   buff[4096];

	SYSTEMTIME t;

	//MessageBox(NULL, L"Append 2", L"", MB_OK);

	GetLocalTime(&t);

	wsprintfW(buff, L"%d.%02d %02d.%02d:%02d %03d - %s\r\n", t.wDay, t.wMonth,  t.wHour, t.wMinute, t.wSecond, t.wMilliseconds, text);

	//MessageBox(NULL, L"Append 3", L"", MB_OK);

//#ifdef LOG_TO_FILE	
	appendTextToFile(buff);
//#endif

#ifdef LOG_TO_CONSOLE
	//puts(buff);
#endif


}

void logByteBuffer(wchar_t* comment, void* byteBuf, int sizeInBytes) {
	wchar_t*   buff = (wchar_t*)malloc(wcslen(comment) + sizeInBytes*sizeof(wchar_t)*4 + 1000);	
	unsigned char* b = (unsigned char*)byteBuf;
	int i;
	
	wchar_t oneElement[10];

	wcscpy(buff, comment);

	wcscat(buff, L" ");


	wsprintfW(oneElement, L" (%d) ", sizeInBytes);
	wcscat(buff, oneElement);


	for (i=0; i<sizeInBytes; i++) {
		wsprintfW(oneElement, L" %02x", b[i]);
		wcscat(buff, oneElement);
	}

	//wcscat(buff, L"\r\n");

	logText(buff);

	free(buff);
}

void logText1(char* text)
{

	unsigned int i=0;

	int len = strlen(text)*sizeof(wchar_t) + 300;
	
	wchar_t* wbuff = (wchar_t*)malloc(len);

	memset(wbuff, 0, len);

	for (i=0; i<strlen(text); i++) {
		wbuff[i] = text[i];
	}

	//wsprintfW(wbuff, L"%s", text);

	logText(wbuff);

	free(wbuff);
}
