#include <jni.h>
#include <pthread.h>

extern volatile int inmode;
extern volatile int inputval;
extern volatile int alarmflag;
extern pthread_mutex_t clockmutex;

JNIEXPORT jboolean JNICALL Java_done_ClockInput_getAlarmFlag
  (JNIEnv * env, jclass this)
{
    int ret;
    pthread_mutex_lock(&clockmutex);
    ret = alarmflag;
    pthread_mutex_unlock(&clockmutex);
	return ret;
}

JNIEXPORT jint JNICALL Java_done_ClockInput_getChoice
  (JNIEnv * env, jclass this)
{
    int ret;
    pthread_mutex_lock(&clockmutex);
    ret = inmode;
    pthread_mutex_unlock(&clockmutex);
	return ret;
}

JNIEXPORT jint JNICALL Java_done_ClockInput_getValue
  (JNIEnv * env, jclass this)
{
    int ret;
    pthread_mutex_lock(&clockmutex);
    ret = inputval;
    pthread_mutex_unlock(&clockmutex);
	return ret;
}

