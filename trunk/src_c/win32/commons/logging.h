#ifndef __LOGGING_H__
#define __LOGGING_H__

//void log(const char* fmt, ... )

#include <windows.h>

#include "appendfile.h"

#ifndef LOG_FILE_NAME
	#define LOG_FILE_NAME L"log-app.txt"
#endif


void logText(wchar_t* text);

void logByteBuffer(wchar_t* comment, void* byteBuf, int sizeInBytes);

void logText1(char* text);



#endif