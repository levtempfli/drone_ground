#ifndef THREAD_MAP_H
#define THREAD_MAP_H
#include <Windows.h>
#include "../globals.h"
class thread_map {
public:
	static DWORD WINAPI main(LPVOID lpParameter);
};

#endif // !THREAD_MAP_H

