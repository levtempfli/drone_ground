#ifndef THREAD_VIDEO_DISPLAY
#define THREAD_VIDEO_DISPLAY
#include <Windows.h>
#include "../globals.h"
class thread_video_display {
public:
	static DWORD WINAPI main(LPVOID lpParameter);
};

#endif // !THREAD_VIDEO_DISPLAY

