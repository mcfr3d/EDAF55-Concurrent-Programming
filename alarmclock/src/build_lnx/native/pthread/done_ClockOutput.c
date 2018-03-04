#include <gc.h>
#include <javatypes.h>

#include "clockgui.h"

GC_PROC_BEGIN(done_ClockOutput_setupWindow)
  GC_FUNC_ENTER
{
	start_clockgui();
}
  GC_FUNC_LEAVE
GC_PROC_END(done_ClockOutput_setupWindow)

GC_VAR_FUNC_BEGIN(JBoolean, done_InputSampler_haveInput)
  GC_FUNC_ENTER
  {
	  JBoolean ret = FALSE;
      pthread_mutex_lock(&clockmutex);
	  if (have_input) {
		  have_input = 0;
		  ret = TRUE;
	  }
      pthread_mutex_unlock(&clockmutex);
      GC_RETURN_VAR(ret);
  }
  GC_FUNC_LEAVE
GC_VAR_FUNC_END(JBoolean, done_InputSampler_haveInput)

GC_PROC_BEGIN(done_ClockOutput_doAlarm)
  GC_FUNC_ENTER
{
	do_alarm();
}
  GC_FUNC_LEAVE
GC_PROC_END(done_ClockOutput_doAlarm)

GC_PROC_BEGIN(done_ClockOutput_showTime_int, JInt time)
  GC_FUNC_ENTER;
{
	show_time((int)time);
}
  GC_FUNC_LEAVE
GC_PROC_END(done_ClockOutput_showTime_int)

