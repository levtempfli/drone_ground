#ifndef THREAD_CONTROL_H
#define THREAD_CONTROL_H
#include <Windows.h>
#include "../globals.h"
class thread_control {
public:
	static DWORD WINAPI main(LPVOID lpParameter);
}; 

#endif // !THREAD_CONTROL_H
