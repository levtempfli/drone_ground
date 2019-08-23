#include "timer.h"

timer::timer() {
	LARGE_INTEGER li;
	if (!QueryPerformanceFrequency(&li)) {
		_errdbg.errorMSGwithGLE((LPTSTR)"QueryPerformanceFrequency");
	}
	PCFreq = double(li.QuadPart) / 1000.0;
}

void timer::StartCounter() {
	LARGE_INTEGER li;
	if (!QueryPerformanceCounter(&li)) {
		_errdbg.errorMSGwithGLE((LPTSTR)"QueryPerformanceCounter");
	}
	CounterStart = li.QuadPart;
}

double timer::GetCounter() {
	LARGE_INTEGER li;
	QueryPerformanceCounter(&li);
	return double(li.QuadPart - CounterStart) / PCFreq;
}