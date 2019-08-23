#ifndef THREAD_TCP1_H
#define THREAD_TCP1_H
#include "../tcp_udp_socket/win_tcpip.h"
#include <Windows.h>
#include "../globals.h"
#include "../error/error.h"
#include "../timer/timer.h"


class thread_tcp1 {
public:
	static DWORD WINAPI main(LPVOID lpParameter);
private:
	static const int port;
	static const char dnsaddr[50];
	static const int mainloop_wait;
	static const int connloop_wait;
	static const int in_buffer_size;
	static const int out_buffer_size;
	static const int conn_msg_timeout;
	static  timer conn_msg_timer;
	static tcp_client t_cl;
	static bool has_valid_ip;
	static bool connected;
	static char in_buffer[];
	static char out_buffer[];
	static void main_loop();
	static void connected_loop();
	static void decode_message(int rec);
	static int encode_message();
};

#endif // !THREAD_TCP1_H

