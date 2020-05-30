#include "thread_tcp1/thread_tcp1.h"
#include <Windows.h>
#include "thread_control/thread_control.h"
#include "thread_map/thread_map.h"
#include "thread_tcp2/thread_tcp2.h"
#include "thread_video_display/thread_video_display.h"
#include "thread_window_parameter/thread_window_parameter.h"
#include "data/data_controls.h"
#include "data/data_telemetry.h"
#include "data/data_video.h"
#include "data/data_network.h"
#include "error/error.h"

thread_control _th_ctr;
thread_map _th_map;
thread_tcp1 _th_tcp1;
thread_tcp2 _th_tcp2;
thread_video_display _th_vid_dis;
thread_window_parameter _th_win_par;

data_controls _dt_controls;
data_telemetry _dt_telemetry;
data_video _dt_video;
data_network _dt_network;

int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow) {
	_errdbg.RedirectIOToConsole();
	
	_dt_controls.mutex = CreateMutex(NULL, FALSE, NULL);
	_dt_telemetry.mutex = CreateMutex(NULL, FALSE, NULL);
	_dt_video.mutex = CreateMutex(NULL, FALSE, NULL);
	_dt_network.mutex = CreateMutex(NULL, FALSE, NULL);

	if (_dt_controls.mutex == NULL || _dt_telemetry.mutex == NULL || _dt_video.mutex == NULL || _dt_network.mutex == NULL) {
		_errdbg.errorMSGwithGLE((LPTSTR)"CreateMutex");
		ExitProcess(-1);
	}

	HANDLE thr_hdl_ctr = CreateThread(NULL, 0, _th_ctr.main, NULL, CREATE_SUSPENDED, NULL);
	HANDLE thr_hdl_map = CreateThread(NULL, 0, _th_map.main, NULL, CREATE_SUSPENDED, NULL);
	HANDLE thr_hdl_tcp1 = CreateThread(NULL, 0, _th_tcp1.main, NULL, CREATE_SUSPENDED, NULL);
	HANDLE thr_hdl_tcp2 = CreateThread(NULL, 0, _th_tcp2.main, NULL, CREATE_SUSPENDED, NULL);
	HANDLE thr_hdl_vid_dis = CreateThread(NULL, 0, _th_vid_dis.main, NULL, CREATE_SUSPENDED, NULL);
	HANDLE thr_hdl_win_par = CreateThread(NULL, 0, _th_win_par.main, NULL, CREATE_SUSPENDED, NULL);

	if (thr_hdl_ctr == NULL || thr_hdl_map == NULL || thr_hdl_tcp1 == NULL || thr_hdl_tcp2 == NULL || thr_hdl_vid_dis == NULL || thr_hdl_win_par == NULL) {
		_errdbg.errorMSGwithGLE((LPTSTR)"CreateThread");
		ExitProcess(-1);
	}
	
	int r1, r2, r3, r4, r5, r6;
	r1 = ResumeThread(thr_hdl_ctr);
	r2 = ResumeThread(thr_hdl_map);
	r3 = ResumeThread(thr_hdl_tcp1);
	r4 = ResumeThread(thr_hdl_tcp2);
	r5 = ResumeThread(thr_hdl_vid_dis);
	r6 = ResumeThread(thr_hdl_win_par);
	if (r1 == -1 || r2 == -1 || r3 == -1 || r4 == -1 || r5 == -1 || r6 == -1) {
		_errdbg.errorMSGwithGLE((LPTSTR)"ResumeThread");
		ExitProcess(-1);
	}

	WaitForSingleObject(thr_hdl_ctr, INFINITE);
	WaitForSingleObject(thr_hdl_map, INFINITE);
	WaitForSingleObject(thr_hdl_tcp1, INFINITE);
	WaitForSingleObject(thr_hdl_tcp2, INFINITE);
	WaitForSingleObject(thr_hdl_vid_dis, INFINITE);
	WaitForSingleObject(thr_hdl_win_par, INFINITE);

	CloseHandle(thr_hdl_ctr);
	CloseHandle(thr_hdl_map);
	CloseHandle(thr_hdl_tcp1);
	CloseHandle(thr_hdl_tcp2);
	CloseHandle(thr_hdl_vid_dis);
	CloseHandle(thr_hdl_win_par);

	CloseHandle(_dt_controls.mutex);
	CloseHandle(_dt_network.mutex);
	CloseHandle(_dt_telemetry.mutex);
	CloseHandle(_dt_video.mutex);

	Sleep(2000);
	ExitProcess(0);
}
