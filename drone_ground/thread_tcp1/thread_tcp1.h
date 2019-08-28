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
	static const int slv_buffer_size;
	static const int conn_msg_timeout;
	static const int msg_send_period;
	static const int msg_types_max;
	static  timer conn_msg_timer;
	static timer msg_send_timer;
	static tcp_client t_cl;
	static int msg_types_curr;
	static int in_status;
	static int in_chd_1;
	static int in_chd_2;
	static int slv_buff_i;
	static bool has_valid_ip;
	static bool connected;
	static char in_buffer[];
	static char out_buffer[];
	static char slv_buffer[];
	static void main_loop();
	static void connected_loop();
	static void decode_message(int rec);
	static int encode_message();
	static void solve_message(int len);
	static int create_message(int type);
	static int calculate_checksum(char *buff, int begin, int end);
};

#endif // !THREAD_TCP1_H

