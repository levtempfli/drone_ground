#ifndef ERROR_H
#define ERROR_H

#include <Windows.h>
#include <io.h>
#include <fcntl.h>
#include <iostream>
#include <tchar.h>
#include <strsafe.h>
#include <string>

class error_debug {
public:
	void RedirectIOToConsole();
	void errorMsg(LPTSTR message);
	void errorMSGwithGLE(LPTSTR lpszFunction);
	void errorMSGwithGLEnum(LPTSTR lpszFunction, DWORD dw);
};

extern error_debug _errdbg;

#endif // !ERROR_H
