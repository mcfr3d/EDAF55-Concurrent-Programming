#ifndef native_pthread_clockgui_h
#define native_pthread_clockgui_h

#include <stdio.h>
#include <stdlib.h>
#include <gtk/gtk.h>
#include <gdk/gdkkeysyms.h>
#include <gdk-pixbuf/gdk-pixbuf.h>
#include <assert.h>

#include <pthread.h>
#include <unistd.h>

/* Directional buttons */
#define BTN_NONE	0
#define BTN_LEFT	1
#define BTN_RIGHT	2
#define BTN_UP		3
#define BTN_DOWN	4

/* Clock input mode */
#define MODE_NONE	0
#define MODE_TSHOW	1
#define MODE_ASET	2
#define MODE_TSET	3
#define MODE_ATOGGLE    4

/* These need to correspond to the constants given in ClockInput.java */
#define IN_TSHOW	0
#define IN_ASET		1
#define IN_TSET		2

extern volatile int have_input;
extern volatile int inmode;
extern volatile int inputval;
extern volatile int alarmflag;
extern pthread_mutex_t clockmutex;

void start_clockgui();

void do_alarm();

void show_time(int time);

#endif
