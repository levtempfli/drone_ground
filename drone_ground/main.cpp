#include <Windows.h>
#include "thread_control/thread_control.h"
#include "thread_map/thread_map.h"
#include "thread_tcp1/thread_tcp1.h"
#include "thread_tcp2/thread_tcp2.h"
#include "thread_video_display/thread_video_display.h"
#include "thread_window_parameter/thread_window_parameter.h"
#include "data/data_controls.h"
#include "data/data_telemetry.h"
#include "data/data_video.h"

HANDLE _data_telemetry_mtx;
HANDLE _data_video_mtx;
HANDLE _data_controls_mtx;

data_controls _dt_controls;
data_telemetry _dt_telemetry;
data_video _dt_video;


int WINAPI WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPSTR lpCmdLine, int nCmdShow) {
	return 0;
}