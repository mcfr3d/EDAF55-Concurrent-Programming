#include <jni.h>
#include <pthread.h>

extern volatile int have_input;
extern pthread_mutex_t clockmutex;

JNIEXPORT jboolean JNICALL Java_done_InputSampler_haveInput
  (JNIEnv *env, jclass this)
{
	jboolean ret = 0;
    pthread_mutex_lock(&clockmutex);
	if (have_input) {
		have_input = 0;
		ret = 1;
	}
    pthread_mutex_unlock(&clockmutex);
    return ret;
}

