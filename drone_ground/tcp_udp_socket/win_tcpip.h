#ifndef TCPIP_H
#define TCPIP_H
#define _WINSOCK_DEPRECATED_NO_WARNINGS
#include <WinSock2.h>
#include <WS2tcpip.h>
#include <Windows.h>
#include <iphlpapi.h>
#include <vector>

#pragma comment(lib, "Ws2_32.lib")
#pragma comment(lib, "iphlpapi.lib")



//TCP socket class
class tcp_socket {
public:
	SOCKET sck; //Socket

	//Initializes winsock
	//Returns:0 - If no error occurs, otherwise returns the error code
	int initwinsock();

	//Creates TCP/IP socket
	//Returns:0 - If no error occurs, otherwise returns the error code
	int makesocket();

	//Sets socket mode(blocking/nonblocking)
	//0-blocking, 1-nonblocking
	//Returns:0 - If no error occurs, otherwise returns the error code
	int setsocketmode(u_long mode);

	//Gives error status of the last failed WinSock operation
	//Returns:error code
	int socketlasterror();

	//Disconnects
	//Returns:0 - If no error occurs, otherwise returns the error code
	int disconnect();

	//Closes the socket
	//Returns:0 - If no error occurs, otherwise returns the error code
	int closesck();

	//Terminates use of the Winsock 2 DLL
	//Returns:0 - If no error occurs, otherwise returns the error code
	int closewinsock();
};

//TCP client without connect
class tcp_handle :public tcp_socket {
public:
	//Sends data over socket
	//Returns:0 - If no error occurs, otherwise returns the error code, 10035 - nothing sent(only in non-blocking), 10053,10054- connection lost
	int sendsck(char *buff, int bufflen, int &sent);

	//Receives data over socket
	//Returns:0 - If no error occurs, otherwise returns the error code, -1 - closed connection, 10035 - nothing received(only in non-blocking)
	int receivesck(char *buff, int bufflen, int &received);
};

//TCP client
class tcp_client :public tcp_handle {
public:
	//Connects to specified ip and port
	//Returns:0 - If no error occurs, otherwise returns the error code
	int connectsck(const char *destip, const int destport);
};

//TCP server
class tcp_server :public tcp_socket {
public:
	//Associates a local address with a socket
	//Returns:0 - If no error occurs, otherwise returns the error code
	int bindsck(const char *ip, int port);

	//Places the socket in a state in which it is listening for an incoming connection
	//Returns:0 - If no error occurs, otherwise returns the error code
	int listensck();

	//Permits an incoming connection attempt on a socket.
	//Returns:0 - If no error occurs, otherwise returns the error code, 10035 - no incoming connection(only in non-blocking)
	int acceptsck(SOCKET &newsocket, char *in_ip, int &in_port);
};

//TCPIP utility
class tcp_utility {
public:
	//Initializes winsock
	//Returns:0 - If no error occurs, otherwise returns the error code
	static int initwinsock();
	//Lists the available network ip addresses
	//Returns:0 - If no error occurs, otherwise returns the error code
	static int getiptable(std::vector<std::string> &ips);

	//Get ip adress from hostname
	//Returns:0 - If no error occurs, otherwise returns the error code
	static int getipaddr(const char *addr, std::vector<std::string> &ips);

	//Terminates use of the Winsock 2 DLL
	//Returns:0 - If no error occurs, otherwise returns the error code
	static int closewinsock();
};


#endif // !TCPIP_H
