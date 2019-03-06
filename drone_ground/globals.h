#ifndef GLOBALS_H
#define GLOBALS_H
#include <Windows.h>
#include "data/data_controls.h"
#include "data/data_telemetry.h"
#include "data/data_video.h"

extern HANDLE _data_telemetry_mtx;
extern HANDLE _data_video_mtx;
extern HANDLE _data_controls_mtx;

extern data_controls _dt_controls;
extern data_telemetry _dt_telemetry;
extern data_video _dt_video;


#endif // !GLOBALS_H
