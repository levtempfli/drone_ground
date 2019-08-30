#include "thread_tcp1.h"

const int thread_tcp1::port = 325;
const char thread_tcp1::dnsaddr[] = "tdrone.ddns.net";
const int thread_tcp1::mainloop_wait = 50;
const int thread_tcp1::connloop_wait = 10;
const int thread_tcp1::conn_msg_timeout = 5000;
const int thread_tcp1::msg_send_period = 100;
const int thread_tcp1::in_buffer_size = 64000;
const int thread_tcp1::out_buffer_size = 256;
const int thread_tcp1::slv_buffer_size = 4098;
const int thread_tcp1::msg_types_max = 3;
char thread_tcp1::in_buffer[thread_tcp1::in_buffer_size];
char thread_tcp1::out_buffer[thread_tcp1::out_buffer_size];
char thread_tcp1::slv_buffer[thread_tcp1::slv_buffer_size];
int thread_tcp1::msg_types_curr = 1;
int thread_tcp1::in_chd_1;
int thread_tcp1::in_chd_2;
int thread_tcp1::in_status = 0;
int thread_tcp1::slv_buff_i = 0;
bool thread_tcp1::has_valid_ip = 0;
bool thread_tcp1::connected = 0;
tcp_client thread_tcp1::t_cl;
timer thread_tcp1::msg_send_timer;
timer thread_tcp1::conn_msg_timer;
bool connected = 0;

DWORD WINAPI thread_tcp1::main(LPVOID lpParameter) {
	int r = t_cl.initwinsock();
	if ( r!= 0) {
		_errdbg.errorMSGwithGLEnum((LPTSTR)"initwinsock", r);
		ExitProcess(-1);
	}
	WaitForSingleObject(_dt_network.mutex, INFINITE);
	_dt_network.winsock_init = 1;
	ReleaseMutex(_dt_network.mutex);

	r = t_cl.makesocket();
	if (r != 0) {
		_errdbg.errorMSGwithGLEnum((LPTSTR)"initwinsock", r);
		ExitProcess(-1);
	}
	
	while (1) {
		main_loop();
		Sleep(mainloop_wait);
	}

	return 0;
}

void thread_tcp1::main_loop(){
	std::vector<std::string> ips;
	char ip[16];
	tcp_utility::getipaddr(dnsaddr, ips);
	if (ips.size() != 0) {
		WaitForSingleObject(_dt_network.mutex, INFINITE);
		strcpy_s(_dt_network.ip, ips[0].c_str());
		strcpy_s(ip, _dt_network.ip);
		ReleaseMutex(_dt_network.mutex);
		t_cl.setsocketmode(0);
		int r = t_cl.connectsck(ip, port);
		if (r == 0) {
			connected = 1;
			WaitForSingleObject(_dt_network.mutex, INFINITE);
			_dt_network.connected1 = 1;
			ReleaseMutex(_dt_network.mutex);
			t_cl.setsocketmode(1);
			conn_msg_timer.StartCounter();
			msg_send_timer.StartCounter();
		}
	}
	while (connected) {
		connected_loop();
		Sleep(connloop_wait);
	}
}

void thread_tcp1::connected_loop() {
	int ret_v,rec;
	ret_v = t_cl.receivesck(in_buffer, in_buffer_size, rec);
	if (ret_v != 10035 && ret_v != 0) {
		connected = 0;
		WaitForSingleObject(_dt_network.mutex, INFINITE);
		_dt_network.connected1 = 0;
		ReleaseMutex(_dt_network.mutex);
		t_cl.disconnect();
		return;
	}
	if (rec != 0) {
		decode_message(rec);
		conn_msg_timer.StartCounter();
	}

	int sent, to_st = encode_message();
	if (to_st != 0) {
		ret_v = t_cl.sendsck(out_buffer, to_st, sent);
		if (ret_v != 10035 && ret_v != 0) {
			connected = 0;
			WaitForSingleObject(_dt_network.mutex, INFINITE);
			_dt_network.connected1 = 0;
			ReleaseMutex(_dt_network.mutex);
			t_cl.disconnect();
			return;
		}
		else if (to_st == sent && ret_v != 10035) {
			msg_send_timer.StartCounter();
			msg_types_curr++;
			if (msg_types_curr > msg_types_max) msg_types_curr = 1;
		}
	}
	

	if (conn_msg_timer.GetCounter() > conn_msg_timeout) {
		t_cl.disconnect();
		connected = 0;
		WaitForSingleObject(_dt_network.mutex, INFINITE);
		_dt_network.connected1 = 0;
		ReleaseMutex(_dt_network.mutex);
	}

}

void thread_tcp1::decode_message(int rec) {
	char char_rec;
	for (int i = 0; i < rec; i++) {
		char_rec = in_buffer[i];
		switch (char_rec) {
		case '@':
			in_status = 1;
			slv_buff_i = 0;
			break;
		case '~':
			if (in_status != 1) break;
			in_status = 2;
			break;
		default:
			switch (in_status) {
			case 1:
				if (slv_buff_i == slv_buffer_size - 1) {
					in_status = 0;
					break;
				}
				slv_buff_i++;
				slv_buffer[slv_buff_i] = char_rec;
				break;
			case 2:
				if (char_rec < 58) in_chd_1 = char_rec - 48;
				else in_chd_1 = char_rec - 55;
				in_status = 3;
				break;
			case 3:
				if (char_rec < 58) in_chd_2 = char_rec - 48;
				else in_chd_2 = char_rec - 55;
				in_chd_1 = in_chd_1 * 16 + in_chd_2;
				if (in_chd_1 == calculate_checksum(slv_buffer, 1, slv_buff_i)) solve_message(slv_buff_i, 1);
				else solve_message(slv_buff_i, 0);
				in_status = 0;
				break;
			}
			break;
		}
	}
}


int thread_tcp1::encode_message() {
	if (msg_send_timer.GetCounter() > msg_send_period) {
		int len = create_message(msg_types_curr);
		out_buffer[0] = '@';
		int chs = calculate_checksum(out_buffer, 1, len);
		len++;
		out_buffer[len] = '~';
		len++;
		if (chs / 16 < 10) out_buffer[len] = 48 + (chs / 16);
		else out_buffer[len] = (chs / 16) - 10 + 65;
		len++;
		if (chs % 16 < 10) out_buffer[len] = 48 + (chs % 16);
		else out_buffer[len] = (chs % 16) - 10 + 65;
		return len + 1;
	}
	else return 0;

	return 6;
}

void thread_tcp1::solve_message(int len, bool correct) {
	printf("%d", correct);
	for (int i = 1; i <= len; i++) {
		std::cout << slv_buffer[i];///////DEBUG
	}
}

int thread_tcp1::create_message(int type) {
	switch (type){
	case 1:
		strcpy_s(out_buffer, "Rhello1Z");
		return 7;
		break;
	case 2:
		strcpy_s(out_buffer, "Rhello2Z");
		return 7;
		break;
	case 3:
		strcpy_s(out_buffer, "Rhello3Z");
		return 7;
		break;
	}
}

int thread_tcp1::calculate_checksum(char * buff, int begin, int end) {
	int chs = 0;
	for (int i = begin; i <= end; i++) {
		chs = chs ^ buff[i];
	}
	return chs;
}
