#include <stdio.h>
#include <gc.h>
#include <javatypes.h>
#include <ncurses.h>
#include <pthread.h>
#include <unistd.h>

// Directional buttons
#define BTN_NONE	0
#define BTN_LEFT	1
#define BTN_RIGHT	2
#define BTN_UP		3
#define BTN_DOWN	4

// Clock input mode
#define MODE_NONE	0
#define MODE_TSHOW	1
#define MODE_ASET	2
#define MODE_TSET	3
#define MODE_ATOGGLE    4

// These need to correspond to the constants given in ClockInput.java
#define IN_TSHOW	0
#define IN_ASET		1
#define IN_TSET		2

static int row1;
static int row2;
static int status1;
static int status2;
static int beeprow;
static int col;

static volatile int btnstate = BTN_NONE;
static volatile int clockmode = MODE_NONE;
static volatile int curinput = 0;
static volatile int clocktime = 0;
static volatile int alarmtime = 0;
static volatile int alarmpulse = 0;
static volatile int cursor = 0;

volatile int inmode = IN_TSHOW;
volatile int inputval = 0;
volatile int alarmflag = 0;
pthread_mutex_t clockmutex = PTHREAD_MUTEX_INITIALIZER;

void *ClockDeviceThread(void *data);

GC_PROC_BEGIN(done_ClockOutput_setupAlarm)
  GC_FUNC_ENTER
{
  pthread_t clockthread;
  int x;
  int y;

  // Here we set up ncurses
  initscr();// start display
  start_color();// enable color
  noecho();// disable keyboard echo
  curs_set(0);// hide cursor
  keypad(stdscr, 1);// enable keycodes
  init_pair(1, COLOR_RED, COLOR_BLACK);
  init_pair(2, COLOR_GREEN, COLOR_BLACK);
  getmaxyx(stdscr, y, x);
  row1 = y/2;
  row2 = row1+1;
  status1 = row2+2;
  status2 = status1+1;
  beeprow = status2+1;
  col = (x-8)/2;

  // Create mutex

  // Start clock servicing thread
  pthread_create(&clockthread, NULL, ClockDeviceThread, NULL);
}
  GC_FUNC_LEAVE
GC_PROC_END(done_ClockOutput_setupAlarm)

GC_PROC_BEGIN(done_ClockOutput_doAlarm)
  GC_FUNC_ENTER
{
  pthread_mutex_lock(&clockmutex);
  alarmpulse = 1;
  pthread_mutex_unlock(&clockmutex);
  PrintClockLCD();
  usleep(300000);
  pthread_mutex_lock(&clockmutex);
  alarmpulse = 0;
  pthread_mutex_unlock(&clockmutex);
  PrintClockLCD();

}
  GC_FUNC_LEAVE
GC_PROC_END(done_ClockOutput_doAlarm)

void PrintClockLCD()
{
  char lcdout[9];
  int hhmmss;

  // lock clockmutex
  pthread_mutex_lock(&clockmutex);

  hhmmss = clocktime;
  lcdout[8] = '\0';
  lcdout[7] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[6] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[5] = ':';
  lcdout[4] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[3] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[2] = ':';
  lcdout[1] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[0] = (char)(hhmmss % 10 + '0');

  mvprintw(row1, col, "%s", lcdout);

  hhmmss = alarmtime;
  lcdout[8] = '\0';
  lcdout[7] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[6] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[4] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[3] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[1] = (char)(hhmmss % 10 + '0');
  hhmmss /= 10;
  lcdout[0] = (char)(hhmmss % 10 + '0');

  if (alarmpulse) {
    lcdout[5] = ' ';
    lcdout[2] = ' ';
  } else {
    if (alarmflag) {
      lcdout[5] = ':';
      lcdout[2] = ':';
    } else {
      lcdout[5] = '_';
      lcdout[2] = '_';
    }
  }

  attron(COLOR_PAIR(1));
  mvprintw(row2, col, "%s", lcdout);
  attroff(COLOR_PAIR(1));

  // print status row
  attron(COLOR_PAIR(2));
  char *mode;
  switch (clockmode) {
    case MODE_TSET:
      mode = "tset ";
      break;
    case MODE_ASET:
      mode = "aset ";
      break;
    default:
      mode = "tshow";
  }
  mvprintw(status1, col, "mode: %s", mode);
  char *onoroff;
  if (alarmflag) {
    onoroff = "on ";
  } else {
    onoroff = "off";
  }
  mvprintw(status2, col, "alarm: %s", onoroff);
  if (alarmpulse) {
    mvprintw(beeprow, col, "beep!");
  } else {
    mvprintw(beeprow, col, "     ");
  }
  attroff(COLOR_PAIR(2));

  // draw cursor
  if (clockmode == MODE_TSET || clockmode == MODE_ASET) {
    curs_set(1);
    int cur = 5-cursor;
    if (cursor < 4) {
      cur ++;
      if (cursor < 2) {
        cur ++;
      }
    } 

    if (clockmode == MODE_TSET) {
      move(row1, col+cur);
    } else {
      move(row2, col+cur);
    }
  } else {
    curs_set(0);
  }

  /* refresh window */
  refresh();

  /* unlock clockmutex */
  pthread_mutex_unlock(&clockmutex);
}

GC_PROC_BEGIN(done_ClockOutput_showTime_int, JInt time)
  GC_FUNC_ENTER;
{
  pthread_mutex_lock(&clockmutex);
  if (clockmode != MODE_TSET) {
    clocktime = (int)time;
  }
  pthread_mutex_unlock(&clockmutex);
  PrintClockLCD();
}
  GC_FUNC_LEAVE
GC_PROC_END(done_ClockOutput_showTime_int)

int GetDigit(int number, int pos)
{
	int i;
	for (i = 0; i < pos; ++i) number /= 10;
	return (int) (number % 10);
}

int IncDigit(int number, int pos)
{
	int term = 1;
	int i;
	for (i = 0; i < pos; ++i) term *= 10;
	return number + term;
}

int DecDigit(int number, int pos)
{
	int term = 1;
	int i;
	for (i = 0; i < pos; ++i) term *= 10;
	return number - term;
}

int IncTime(int time, int pos)
{
	int newtime = time;
	if ((pos == 5 && GetDigit(time, 5) < 2 && GetDigit(time, 4) < 4) ||
			(pos == 5 && GetDigit(time, 5) < 1) || 
			(pos == 4 && GetDigit(time, 4) < 3 && GetDigit(time, 5) <= 2) ||
			(pos == 4 && GetDigit(time, 4) < 9 && GetDigit(time, 5) <= 1) ||
			(pos == 3 && GetDigit(time, 3) < 5) ||
			(pos == 1 && GetDigit(time, 1) < 5) ||
			((pos == 0 || pos == 2) && GetDigit(time, pos) < 9)) {
		newtime = IncDigit(time, pos);
	}
	return newtime;
}

void TimeSet(int prevmode)
{
  pthread_mutex_lock(&clockmutex);
  int dir = btnstate;

  if (prevmode != MODE_TSET) {
    curinput = clocktime;
  }

  if (dir == BTN_LEFT && cursor < 5) {
    cursor++;
  } else if (dir == BTN_RIGHT && cursor > 0) {
    cursor--;
  } else if (dir == BTN_UP) {
    curinput = IncTime(curinput, cursor);
  } else if (dir == BTN_DOWN) {
    if (GetDigit(curinput, cursor) > 0) {
      curinput = DecDigit(curinput, cursor);
    }
  }

  clocktime = curinput;
  inmode = IN_TSET;
  pthread_mutex_unlock(&clockmutex);
}

void AlarmSet(int prevmode)
{
  pthread_mutex_lock(&clockmutex);
  int dir = btnstate;

  if (prevmode != MODE_ASET) {
    curinput = alarmtime;
  }

  if (dir == BTN_LEFT && cursor < 5) {
    cursor++;
  } else if (dir == BTN_RIGHT && cursor > 0) {
    cursor--;
  } else if (dir == BTN_UP) {
    curinput = IncTime(curinput, cursor);
  } else if (dir == BTN_DOWN) {
    if (GetDigit(curinput, cursor) > 0) {
      curinput = DecDigit(curinput, cursor);
    }
  }

  alarmtime = curinput;
  inmode = IN_ASET;
  pthread_mutex_unlock(&clockmutex);
}

void *ClockDeviceThread(void *data)
{
  void *eFlag = 0;
  void **__eFlag__ = &eFlag;

  int ch;
  int prevmode;
  int anykey;

  PrintClockLCD();
  while (1) {
    prevmode = clockmode;

    // Get input
    ch = wgetch(stdscr);
    anykey = 1;
    btnstate = BTN_NONE;
    switch (ch) {
      case 48:
        // set alarm
        if (prevmode == MODE_ASET) {
          clockmode = MODE_NONE;
        } else {
          clockmode = MODE_ASET;
        }
        break;
      case 49:
        if (prevmode == MODE_TSET) {
          clockmode = MODE_NONE;
        } else {
          clockmode = MODE_TSET;
        }
        break;
      case 50:
        pthread_mutex_lock(&clockmutex);
        clockmode = MODE_ATOGGLE;
	if (alarmflag) {
	  alarmflag = 0;
	} else {
	  alarmflag = 1;
	}
        pthread_mutex_unlock(&clockmutex);
        break;
      case KEY_UP:
        btnstate = BTN_UP;
        break;
      case KEY_DOWN:
        btnstate = BTN_DOWN;
        break;
      case KEY_LEFT:
        btnstate = BTN_LEFT;
        break;
      case KEY_RIGHT:
        btnstate = BTN_RIGHT;
        break;
      default:
        anykey = 0;
    }

    if (anykey) {
      // Mode changed?
      if (clockmode != prevmode) {
        pthread_mutex_lock(&clockmutex);
        cursor = 0;
        if (prevmode == MODE_TSET ||
            prevmode == MODE_ASET) {
          inputval = curinput;
        }
        if (!(clockmode == MODE_TSET ||
            clockmode == MODE_ASET)) {
	  inmode = IN_TSHOW;
        }
        pthread_mutex_unlock(&clockmutex);
      }

      // Any button changed?
      if (clockmode == MODE_TSET) {
        TimeSet(prevmode);
      } else if (clockmode == MODE_ASET) {
        AlarmSet(prevmode);
      }

      PrintClockLCD();
      // Signal ClockInput
      GC_PROC_CALL(done_ClockInput_giveInput);
    }
  }
}

