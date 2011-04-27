// IrCommSocketsSimple.cpp : Defines the entry point for the application.
//

#include "stdafx.h"

#include <af_irda.h>

#pragma comment(lib, "winsock.lib")
//#pragma comment(lib, "ws2_32.lib")

#define IAS_SET_ATTRIB_MAX_LEN 32



BOOL main2()
{
	// buffer for IAS set
	BYTE IASSetBuff[sizeof(IAS_SET) - 3 + IAS_SET_ATTRIB_MAX_LEN];
	int IASSetLen = sizeof(IASSetBuff);
	PIAS_SET pIASSet = (PIAS_SET) &IASSetBuff;

	// for the setsockopt call to enable 9 wire IrCOMM
	int Enable9WireMode = 1;

	// server sockaddr with IrCOMM name
	SOCKADDR_IRDA address = { AF_IRDA, 0, 0, 0, 0, "IrDA:IrCOMM" };

	SOCKET ServerSock;
	SOCKET ClientSock;

	// Specifies the server socket address
	int index = 0, // Integer index
	iReturn; // Return value of recv function
	char szServerA[100]; // ASCII string
	TCHAR szServerW[100]; // Unicode string
	TCHAR szError[100]; // Error message string
	TCHAR szText[100]; // Error message string
	WORD WSAVerReq = MAKEWORD(1,1);
	WSADATA WSAData;

	memset(szServerA, 0, sizeof (szServerA));

	//wsprintf(szText, TEXT("received: %d - %S\n"), 11, "AB");

	//MessageBox(NULL, szText, TEXT("A"), MB_OK); 


	if (WSAStartup(WSAVerReq, &WSAData) != 0)
	{
		MessageBox (NULL, TEXT("Error at WSA Startup"), TEXT("Error"), MB_OK);
		return FALSE;
	}

	// Create a socket bound to the server.
	if ((ServerSock = socket (AF_IRDA, SOCK_STREAM, 0)) == INVALID_SOCKET)
	{
		wsprintf (szError, TEXT("Allocating socket failed. Error: %d"),
		WSAGetLastError ());
		MessageBox (NULL, szError, TEXT("Error"), MB_OK);
		return FALSE;
	}

	// add IrCOMM IAS attributes for 3 wire cooked and 9 wire raw, see IrCOMM spec
	memcpy(&pIASSet->irdaClassName[0], "IrDA:IrCOMM", 12);
	memcpy(&pIASSet->irdaAttribName[0], "Parameters", 11);

	pIASSet->irdaAttribType = IAS_ATTRIB_OCTETSEQ;
	pIASSet->irdaAttribute.irdaAttribOctetSeq.Len = 6;

	memcpy(&pIASSet->irdaAttribute.irdaAttribOctetSeq.OctetSeq[0],"\000\001\006\001\001\001", 6);

	if (setsockopt(ServerSock, SOL_IRLMP, IRLMP_IAS_SET, (const char *) pIASSet, IASSetLen) == SOCKET_ERROR)
	{
		wsprintf (szError, TEXT("Setsockopt failed. Error: %d"), WSAGetLastError ());
		MessageBox (NULL, szError, TEXT("Error"), MB_OK);
		return FALSE;
	}

	// enable 9wire mode before bind()
	if (setsockopt(ServerSock, SOL_IRLMP, IRLMP_9WIRE_MODE, (const char *) &Enable9WireMode,sizeof(int)) == SOCKET_ERROR)
	{
		wsprintf (szError, TEXT("Setsockopt failed. Error: %d"),
		WSAGetLastError ());
		MessageBox (NULL, szError, TEXT("Error"), MB_OK);
		return FALSE;
	}

	if (bind(ServerSock, (const struct sockaddr *) &address, sizeof(SOCKADDR_IRDA)) == SOCKET_ERROR)
	{
		wsprintf (szError, TEXT("Binding socket failed. Error: %d"),
		WSAGetLastError ());
		MessageBox (NULL, szError, TEXT("Error"), MB_OK);
		closesocket (ServerSock);
		return FALSE;
	}

	puts("bind ok");


	// Establish a socket to listen for incoming connections.
	if (listen (ServerSock, 5) == SOCKET_ERROR)
	{
		wsprintf (szError,TEXT("Listening to the client failed. Error: %d"), WSAGetLastError ());
		MessageBox (NULL, szError, TEXT("Error"), MB_OK);
		closesocket (ServerSock);
		return FALSE;
	}

	puts("listen ok");

	// Accept a connection on the socket.
	if ((ClientSock = accept (ServerSock, 0, 0)) == INVALID_SOCKET)
	{
		wsprintf (szError, TEXT("Accepting connection with client failed.")TEXT(" Error: %d"), WSAGetLastError ());
		MessageBox (NULL, szError, TEXT("Error"), MB_OK);
		closesocket (ServerSock);
		return FALSE;
	}

	puts("accept ok");

	// Stop listening for connections from clients.
	closesocket (ServerSock);

	// Send a string from the server socket to the client socket.
	if (send (ClientSock, "To Client!", strlen ("To Client!") + 1, 0) == SOCKET_ERROR)
	{
		wsprintf (szError,TEXT("Sending data to the client failed. Error: %d"),WSAGetLastError ());
		MessageBox (NULL, szError, TEXT("Error"), MB_OK);
	}


	// Receive data from the client.
	iReturn = recv (ClientSock, szServerA, sizeof (szServerA), 0);

	wsprintf(szText, TEXT("received: %d - %S\n"), iReturn, szServerA);

	MessageBox(NULL, szText, TEXT("A"), MB_OK); 


	return TRUE;

}


int WINAPI WinMain(	HINSTANCE hInstance,
					HINSTANCE hPrevInstance,
					LPTSTR    lpCmdLine,
					int       nCmdShow)
{
 	// TODO: Place code here.


	//MessageBox(NULL, TEXT("Started"), TEXT("A"), MB_OK); 

	main2();

	return 0;
}

