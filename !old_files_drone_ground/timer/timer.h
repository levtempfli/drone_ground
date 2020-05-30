#ifndef TIMER
#define TIMER

#include "../error/error.h"
#include <Windows.h>

class timer {
public:
	timer();
	void StartCounter();
	double GetCounter();
private:
	double PCFreq = 0.0;
	__int64 CounterStart = 0;
};


#endif // !TIMER

