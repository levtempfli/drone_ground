#include "win_tcpip.h"

//Initializes winsock
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_socket::initwinsock() {
	WSADATA wsa;
	return WSAStartup(MAKEWORD(2, 2), &wsa);
}

//Creates TCP/IP socket
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_socket::makesocket() {
	sck = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (sck == INVALID_SOCKET) return WSAGetLastError();
	else return 0;
}

//Sets socket mode(blocking/nonblocking)
//0-blocking, 1-nonblocking
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_socket::setsocketmode(u_long mode) {
	int r = ioctlsocket(sck, FIONBIO, &mode);
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Gives error status of the last failed WinSock operation
//Returns:error code
int tcp_socket::socketlasterror() {
	return WSAGetLastError();
}

//Disconnects
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_socket::disconnect() {
	int r = closesck();
	if (r != 0) return r;
	r = makesocket();
	if (r != 0) return r;
	return 0;
	/*int r = shutdown(sck, SD_BOTH);
	if (r == SOCKET_ERROR) return WSAGetLastError();*/
}

//Closes the socket
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_socket::closesck() {
	int r = closesocket(sck);
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Terminates use of the Winsock 2 DLL
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_socket::closewinsock() {
	int r = WSACleanup();
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Connects to specified ip and port
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_client::connectsck(const char *destip, const int destport) {
	sockaddr_in dest; //Remote endpoint specifier
	dest.sin_family = AF_INET;//IPv4
	dest.sin_port = htons(destport);//convert port to TCP/IP network byte order
	InetPton(AF_INET, destip, &dest.sin_addr.s_addr);//convert to numeric binary form
	int r = connect(sck, (SOCKADDR *)&dest, sizeof(dest));
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Sends data over socket
//Returns:0 - If no error occurs, otherwise returns the error code, 10035 - nothing sent(only in non-blocking), 10053,10054- connection lost
int tcp_handle::sendsck(char *buff, int bufflen, int &sent) {
	int r = send(sck, buff, bufflen, 0);
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else {
		sent = r;
		return 0;
	}

}

//Receives data over socket
//Returns:0 - If no error occurs, otherwise returns the error code, -1 - closed connection, 10035 - nothing received(only in non-blocking)
int tcp_handle::receivesck(char *buff, int bufflen, int &received) {
	int r = recv(sck, buff, bufflen, 0);
	if (r == SOCKET_ERROR) {
		received = 0;
		return WSAGetLastError();
	}
	else if (r == 0) {
		return -1;
	}
	else {
		received = r;
		return 0;
	}
}

//Associates a local address with a socket
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_server::bindsck(const char *ip, int port) {
	sockaddr_in server; //Local endpoint specifier
	server.sin_family = AF_INET; //IPv4
	server.sin_port = htons(port);//convert port to TCP/IP network byte order
	InetPton(AF_INET, ip, &server.sin_addr.s_addr);//convert to numeric binary form
	int r = bind(sck, (SOCKADDR*)&server, sizeof(server));
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Places the socket in a state in which it is listening for an incoming connection
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_server::listensck() {
	int r = listen(sck, SOMAXCONN);
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}

//Permits an incoming connection attempt on a socket.
//Returns:0 - If no error occurs, otherwise returns the error code, 10035 - no incoming connection(only in non-blocking)
int tcp_server::acceptsck(SOCKET &newsocket, char *in_ip, int &in_port) {
	sockaddr_in client; // Remote endpoint specifier
	int clen = sizeof(client);//Size of sockaddr_in
	newsocket = accept(sck, (SOCKADDR*)&client, &clen); // Accept connection
	if (newsocket == INVALID_SOCKET) {
		newsocket = NULL;
		return WSAGetLastError();
	}
	else {
		inet_ntop(AF_INET, &(client.sin_addr), in_ip, INET_ADDRSTRLEN);//convert numeric binary form to ascii characters
		in_port = ntohs(client.sin_port);//convert port to int
		return 0;
	}
}

//Initializes winsock
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_utility::initwinsock() {
	WSADATA wsa;
	return WSAStartup(MAKEWORD(2, 2), &wsa);
}

//Lists the available network ip addresses
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_utility::getiptable(std::vector<std::string> &ips) {
	DWORD size, r;
	PMIB_IPADDRTABLE ipaddrtable; //table of IPv4 address entries
	r = GetIpAddrTable(NULL, &size, 0); //retrieve the size of interface�to�IPv4 address mapping table
	if (r != ERROR_INSUFFICIENT_BUFFER) {
		return r;
	}
	ipaddrtable = (PMIB_IPADDRTABLE)malloc(size);//allocate memory for table
	r = GetIpAddrTable(ipaddrtable, &size, 0);//retrieve the interface�to�IPv4 address mapping table
	if (r != NO_ERROR) {
		return r;
		free(ipaddrtable);//Free the memory
	}
	MIB_IPADDRROW addr; //specifie information for a particular IPv4 address in the MIB_IPADDRTABLE structure
	char cip[16];//ip adress
	for (int i = 0; i < ipaddrtable->dwNumEntries; i++) {//go through the IPv4 adresses
		addr = ipaddrtable->table[i];//a single adress
		inet_ntop(AF_INET, &addr.dwAddr, cip, INET_ADDRSTRLEN);//convert numeric binary form to ascii characters
		ips.push_back(std::string(cip));//put the new ip as string into the list 
	}
	free(ipaddrtable); //Free the memory
	return 0;
}

//Get ip adress from hostname
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_utility::getipaddr(const char *addr, std::vector<std::string> &ips) {

	struct in_addr addri = { 0, };
	struct hostent * res;
	int i = 0;

	res = gethostbyname(addr);
	if(res!=NULL)
	while (res->h_addr_list[i] != 0)
	{
		char hstr[INET_ADDRSTRLEN];
		addri.s_addr = *(u_long *)res->h_addr_list[i++];
		inet_ntop(AF_INET, &addri, hstr, INET_ADDRSTRLEN);
		ips.push_back(std::string(hstr));
	}

	/*struct addrinfo hints; //criterias for selectiong the socket adress structures
	struct addrinfo *infoptr = NULL; //Adress informations
	memset(&hints, 0, sizeof(hints));//Fill the "hints" with 0
	hints.ai_family = AF_INET; // AF_INET means only IPv4 addresses

	int r = getaddrinfo(addr, NULL, &hints, &infoptr);//translate to an adress
	if (r != 0) {
		return r;
	}

	struct addrinfo *p;
	char host[22];

	for (p = infoptr; p != NULL; p = p->ai_next) {
		inet_ntop(AF_INET, &p->ai_addr, host, INET_ADDRSTRLEN);//convert numeric binary form to ascii characters
		ips.push_back(std::string(host));//put the new ip as string into the list 

	}

	freeaddrinfo(infoptr);//free the memory*/
	return 0;
}

//Terminates use of the Winsock 2 DLL
//Returns:0 - If no error occurs, otherwise returns the error code
int tcp_utility::closewinsock() {
	int r = WSACleanup();
	if (r == SOCKET_ERROR) return WSAGetLastError();
	else return 0;
}