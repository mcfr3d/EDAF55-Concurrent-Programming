#include <gc.h>
#include <javatypes.h>
#include <pthread.h>

extern volatile int inmode;
extern volatile int inputval;
extern volatile pthread_mutex_t clockmutex;
extern volatile int alarmflag;

GC_VAR_FUNC_BEGIN(JBoolean, done_ClockInput_getAlarmFlag)
  GC_FUNC_ENTER
  {
    int ret;
    pthread_mutex_lock(&clockmutex);
    ret = alarmflag;
    pthread_mutex_unlock(&clockmutex);
    GC_RETURN_VAR(ret);
  }
  GC_FUNC_LEAVE
GC_VAR_FUNC_END(JBoolean, done_ClockInput_getAlarmFlag)

GC_VAR_FUNC_BEGIN(JInt, done_ClockInput_getChoice)
  GC_FUNC_ENTER
  {
    int ret;
    pthread_mutex_lock(&clockmutex);
    ret = inmode;
    pthread_mutex_unlock(&clockmutex);
    GC_RETURN_VAR((JInt)ret);
  }
  GC_FUNC_LEAVE
GC_VAR_FUNC_END(JInt, done_ClockInput_getChoice)

GC_VAR_FUNC_BEGIN(JInt, done_ClockInput_getValue)
  GC_FUNC_ENTER
  {
    int ret;
    pthread_mutex_lock(&clockmutex);
    ret = inputval;
    pthread_mutex_unlock(&clockmutex);
    GC_RETURN_VAR((JInt)ret);
  }
  GC_FUNC_LEAVE
GC_VAR_FUNC_END(JInt, done_ClockInput_getValue)

