#include "thread_tcp1.h"

const int thread_tcp1::port = 325;
const char thread_tcp1::dnsaddr[] = "tdrone.ddns.net";
const int thread_tcp1::mainloop_wait = 50;
const int thread_tcp1::connloop_wait = 100;
const int thread_tcp1::conn_msg_timeout = 5000;
const int thread_tcp1::in_buffer_size = 64000;
const int thread_tcp1::out_buffer_size = 256;
char thread_tcp1::in_buffer[in_buffer_size];
char thread_tcp1::out_buffer[out_buffer_size];
bool thread_tcp1::has_valid_ip = 0;
bool thread_tcp1::connected = 0;
tcp_client thread_tcp1::t_cl;
timer thread_tcp1::conn_msg_timer;
bool connected = 0;

DWORD WINAPI thread_tcp1::main(LPVOID lpParameter) {
	if (t_cl.initwinsock() != 0) {
		_errdbg.errorMSGwithGLEnum((LPTSTR)"initwinsock",t_cl.socketlasterror());
		ExitProcess(-1);
	}
	WaitForSingleObject(_dt_network.mutex, INFINITE);
	_dt_network.winsock_init = 1;
	ReleaseMutex(_dt_network.mutex);

	if (t_cl.makesocket() != 0) {
		_errdbg.errorMSGwithGLEnum((LPTSTR)"initwinsock", t_cl.socketlasterror());
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
	ret_v = t_cl.sendsck(out_buffer, to_st, sent);
	if (ret_v != 10035 && ret_v != 0) {
		connected = 0;
		WaitForSingleObject(_dt_network.mutex, INFINITE);
		_dt_network.connected1 = 0;
		ReleaseMutex(_dt_network.mutex);
		t_cl.disconnect();
		return;
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
	for (int i = 0; i < rec; i++) {
		std::cout << in_buffer[i];
	}
}


int thread_tcp1::encode_message() {
	strcpy_s(out_buffer, "hello\n");
	return 6;
}
