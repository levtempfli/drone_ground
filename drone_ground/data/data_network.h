#ifndef DATA_NETWORK_H
#define DATA_NETWORK_H

struct data_network {
	HANDLE mutex;
	bool winsock_init = 0;
	bool connected1 = 0;
	char ip[16];
};

#endif // !DATA_NETWORK_H
