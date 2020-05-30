#ifndef UDPIP_H
#define UDPIP_H

#include <WinSock2.h>
#include <WS2tcpip.h>
#include <Windows.h>

#pragma comment(lib, "Ws2_32.lib")

//UDP socket
class udp_socket {
public:
	SOCKET server_sck; //Server Socket
	SOCKET sender_sck; //Sender socket

	//Initializes winsock
	//Returns:0 - If no error occurs, otherwise returns the error code
	int initwinsock();

	//Creates TCP/IP socket
	//Returns:0 - If no error occurs, otherwise returns the error code
	int makesocket();

	//Sets the server socket mode(blocking/nonblocking)
	//0-blocking, 1-nonblocking
	//Returns:0 - If no error occurs, otherwise returns the error code
	int setserversocketmode(u_long mode);

	//Sets the sender socket mode(blocking/nonblocking)
	//0-blocking, 1-nonblocking
	//Returns:0 - If no error occurs, otherwise returns the error code
	int setsendersocketmode(u_long mode);

	//Gives error status of the last failed WinSock operation
	//Returns:error code
	int socketlasterror();

	//Terminates use of the Winsock 2 DLL
	//Returns:0 - If no error occurs, otherwise returns the error code
	int closewinsock();

	//Disconnects the server socket
	//Returns:0 - If no error occurs, otherwise returns the error code
	int closeserversck();

	//Disconnects the sender socket
	//Returns:0 - If no error occurs, otherwise returns the error code
	int closesendersck();

	//Associates a local address with a socket
	//Returns:0 - If no error occurs, otherwise returns the error code
	int bindsck(const char *ip, int port);

	//Receives data over socket
	//Returns:0 - If no error occurs, otherwise returns the error code; -1 - connection has been gracefully closed
	int recvfromsck(char *buff, int bufflen, char *in_ip, int &in_port, int &received);

	//Sends data to a specific destination
	//Returns:0 - If no error occurs, otherwise returns the error code
	int sendtosck(char *buff, int bufflen, const char *destip, int destport, int &sent);
};

#endif // !UDPIP_H

