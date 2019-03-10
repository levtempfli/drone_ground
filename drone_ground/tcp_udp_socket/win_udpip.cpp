#include "win_udpip.h"

//Initializes winsock
//Returns:0 - If no error occurs, otherwise returns the error code
int udp_socket::initwinsock() {
	WSADATA wsa;
	return WSAStartup(MAKEWORD(2, 2), &wsa);
}

//Creates TCP/IP socket
//Returns:0 - If no error occurs, otherwise returns the error code
int udp_socket::makesocket() {
	server_sck = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if (server_sck == INVALID_SOCKET) return WSAGetLastError();
	sender_sck = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
	if (sender_sck == INVALID_SOCKET) return WSAGetLastError();
	return 0;
}

//Sets the server socket mode(blocking/nonblocking)
//0-blocking, 1-nonblocking
//Returns:0 - If no error occurs, otherwise returns the error code
int udp_socket::setserversocketmode(u_long mode) {
	int r = ioctlsocket(server_sck, FIONBIO, &mode);
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Sets the sender socket mode(blocking/nonblocking)
//0-blocking, 1-nonblocking
//Returns:0 - If no error occurs, otherwise returns the error code
int udp_socket::setsendersocketmode(u_long mode) {
	int r = ioctlsocket(sender_sck, FIONBIO, &mode);
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Gives error status of the last failed WinSock operation
//Returns:error code
int udp_socket::socketlasterror() {
	return WSAGetLastError();
}


//Terminates use of the Winsock 2 DLL
//Returns:0 - If no error occurs, otherwise returns the error code
int udp_socket::closewinsock() {
	int r = WSACleanup();
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Disconnects the server socket
//Returns:0 - If no error occurs, otherwise returns the error code
int udp_socket::closeserversck() {
	//shutdown(sck, SD_BOTH);
	int r = closesocket(server_sck);
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Disconnects the sender socket
//Returns:0 - If no error occurs, otherwise returns the error code
int udp_socket::closesendersck() {
	//shutdown(sck, SD_BOTH);
	int r = closesocket(sender_sck);
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Associates a local address with a socket
	//Returns:0 - If no error occurs, otherwise returns the error code
int udp_socket::bindsck(const char *ip, int port) {
	sockaddr_in server; //Local endpoint specifier
	server.sin_family = AF_INET; //IPv4
	server.sin_port = htons(port);//convert port to TCP/IP network byte order
	InetPton(AF_INET, ip, &server.sin_addr.s_addr);//convert to numeric binary form
	int r = bind(server_sck, (SOCKADDR*)&server, sizeof(server));//bind
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Receives data over socket
//Returns:0 - If no error occurs, otherwise returns the error code; -1 - connection has been gracefully closed
int udp_socket::recvfromsck(char *buff, int bufflen, char *in_ip, int &in_port, int &received) {
	sockaddr_in client;//Remote endpoint specifier
	int clen = sizeof(client); //Size of sockaddr_in
	int r = recvfrom(server_sck, buff, bufflen, 0, (SOCKADDR*)&client, &clen);//Receive udp packet from ip, port
	if (r == SOCKET_ERROR) {
		received = 0;
		return WSAGetLastError();
	}
	else if (r == 0) {//closed?
		return -1;
	}
	else {
		inet_ntop(AF_INET, &(client.sin_addr), in_ip, INET_ADDRSTRLEN);//convert ipv4 to ascii
		in_port = ntohs(client.sin_port);//convert port to int
		received = r;
		return 0;
	}
}

//Sends data to a specific destination
//Returns:0 - If no error occurs, otherwise returns the error code
int udp_socket::sendtosck(char *buff, int bufflen, const char *destip, int destport, int &sent) {
	sockaddr_in dest; //Remote endpoint specifier
	dest.sin_family = AF_INET;//IPv4
	dest.sin_port = htons(destport);//convert port to TCP/IP network byte order
	InetPton(AF_INET, destip, &dest.sin_addr.s_addr);//convert to numeric binary form
	int r = sendto(sender_sck, buff, bufflen, 0, (SOCKADDR*)&dest, sizeof(dest)); //send UDP to ip and port
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else {
		sent = r;
		return 0;
	}
}