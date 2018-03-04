#include <jni.h>
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

static GtkWidget* drawingarea;
static GdkPixmap* clock_pixmap;
static GdkBitmap* clock_mask;
static GdkPixmap* blue_pixmap;
static GdkBitmap* blue_mask;
static GdkPixmap* blue_p_pixmap;
static GdkBitmap* blue_p_mask;
static GdkPixmap* yellow_pixmap;
static GdkBitmap* yellow_mask;
static GdkPixmap* yellow_p_pixmap;
static GdkBitmap* yellow_p_mask;
static GdkPixmap* st_p;
static GdkPixmap* sa_p;
static GdkPixmap* up_p;
static GdkPixmap* down_p;
static GdkPixmap* left_p;
static GdkPixmap* right_p;
static PangoContext* pcontext;
static PangoLayout* playout;

static volatile int btnstate = BTN_NONE;
static volatile int clockmode = MODE_TSHOW;
static volatile int curinput = 0;
static volatile int clocktime = 0;
static volatile int alarmtime = 0;
static volatile int alarmpulse = 0;
static volatile int cursor = 0;
volatile int have_input = 0;

volatile int inmode = IN_TSHOW;
volatile int inputval = 0;
volatile int alarmflag = 0;
pthread_mutex_t clockmutex = PTHREAD_MUTEX_INITIALIZER;

void *ClockDeviceThread(void *data);

JNIEXPORT void JNICALL Java_done_ClockOutput_setupWindow
  (JNIEnv * env, jclass this)
{
  pthread_t clockthread;

  /* Start the clock servicing thread */
  pthread_create(&clockthread, NULL, ClockDeviceThread, NULL);
}

JNIEXPORT void JNICALL Java_done_ClockOutput_doAlarm
  (JNIEnv * env, jclass this)
{
  	pthread_mutex_lock(&clockmutex);
  	alarmpulse = 1;
  	pthread_mutex_unlock(&clockmutex);
  	usleep(300000);
  	pthread_mutex_lock(&clockmutex);
  	alarmpulse = 0;
  	pthread_mutex_unlock(&clockmutex);
}

JNIEXPORT void JNICALL Java_done_ClockOutput_showTime
  (JNIEnv * env, jclass this, jint time)
{
  	pthread_mutex_lock(&clockmutex);
  	if (clockmode != MODE_TSET) {
    	clocktime = (int)time;
  	}
  	pthread_mutex_unlock(&clockmutex);
}

JNIEXPORT void JNICALL Java_done_ClockOutput_diagnostics
  (JNIEnv * env, jclass this)
{
	/* do nothing */
}

int get_digit(int number, int pos)
{
	int i;
	for (i = 0; i < pos; ++i) number /= 10;
	return (int) (number % 10);
}

int inc_digit(int number, int pos)
{
	int term = 1;
	int i;
	for (i = 0; i < pos; ++i) term *= 10;
	return number + term;
}

int dec_digit(int number, int pos)
{
	int term = 1;
	int i;
	for (i = 0; i < pos; ++i) term *= 10;
	return number - term;
}

int inc_time(int time, int pos)
{
	int newtime = time;
	if ((pos == 5 && get_digit(time, 5) < 2 && get_digit(time, 4) < 4) ||
			(pos == 5 && get_digit(time, 5) < 1) || 
			(pos == 4 && get_digit(time, 4) < 3 && get_digit(time, 5) <= 2) ||
			(pos == 4 && get_digit(time, 4) < 9 && get_digit(time, 5) <= 1) ||
			(pos == 3 && get_digit(time, 3) < 5) ||
			(pos == 1 && get_digit(time, 1) < 5) ||
			((pos == 0 || pos == 2) && get_digit(time, pos) < 9)) {
		newtime = inc_digit(time, pos);
	}
	return newtime;
}

static gint refresh(gpointer data) {
  gtk_widget_queue_draw(drawingarea);
  return TRUE;
}

static gboolean
key_press_event( GtkWidget *widget, GdkEventKey* event )
{
	void *eFlag = 0;
	void **__eFlag__ = &eFlag;

	switch (event->keyval) {
		case GDK_Shift_L:
		case GDK_Shift_R:
			pthread_mutex_lock(&clockmutex);
			if (clockmode == MODE_TSHOW)
				clockmode = MODE_TSET;
			else if (clockmode == MODE_ASET) {
				clockmode = MODE_ATOGGLE;
				alarmflag = !alarmflag;
				inputval = alarmtime;
				inmode = IN_TSHOW;
				have_input = 1;
			}
			pthread_mutex_unlock(&clockmutex);
			st_p = blue_p_pixmap;
			break;
		case GDK_Control_L:
		case GDK_Control_R:
			pthread_mutex_lock(&clockmutex);
			if (clockmode == MODE_TSHOW)
				clockmode = MODE_ASET;
			else if (clockmode == MODE_TSET) {
				clockmode = MODE_ATOGGLE;
				alarmflag = !alarmflag;
				inputval = clocktime;
				inmode = IN_TSHOW;
				have_input = 1;
			}
			pthread_mutex_unlock(&clockmutex);
			sa_p = blue_p_pixmap;
			break;
		case GDK_Left:
			pthread_mutex_lock(&clockmutex);
			if (clockmode == MODE_ASET
					|| clockmode == MODE_TSET) {
				if (++cursor > 5)
					cursor = 5;
			}
			pthread_mutex_unlock(&clockmutex);
			left_p = yellow_p_pixmap;
			break;
		case GDK_Right:
			if (clockmode == MODE_ASET
					|| clockmode == MODE_TSET) {
				if (--cursor < 0)
					cursor = 0;
			}
			right_p = yellow_p_pixmap;
			break;
			break;
		case GDK_Up:
			pthread_mutex_lock(&clockmutex);
			if (clockmode == MODE_ASET) {
				alarmtime = inc_time(alarmtime, cursor);
				inputval = alarmtime;
				inmode = IN_ASET;
				have_input = 1;
			} else if (clockmode == MODE_TSET) {
				clocktime = inc_time(clocktime, cursor);
				inputval = clocktime;
				inmode = IN_TSET;
				have_input = 1;
			}
			pthread_mutex_unlock(&clockmutex);
			up_p = yellow_p_pixmap;
			break;
		case GDK_Down:
			pthread_mutex_lock(&clockmutex);
			if (clockmode == MODE_ASET) {
				if (get_digit(alarmtime, cursor) > 0) {
					alarmtime = dec_digit(alarmtime, cursor);
					inputval = alarmtime;
					inmode = IN_ASET;
					have_input = 1;
				}
			} else if (clockmode == MODE_TSET) {
				if (get_digit(clocktime, cursor) > 0) {
					clocktime = dec_digit(clocktime, cursor);
					inputval = clocktime;
					inmode = IN_TSET;
					have_input = 1;
				}
			}
			pthread_mutex_unlock(&clockmutex);
			down_p = yellow_p_pixmap;
			break;
		default:
			return TRUE;
	}

	gtk_widget_queue_draw(drawingarea);

	return TRUE;
}

static gboolean
key_release_event(GtkWidget *widget, GdkEventKey* event)
{
	switch (event->keyval) {
		case GDK_Shift_L:
		case GDK_Shift_R:
			pthread_mutex_lock(&clockmutex);
			if (clockmode == MODE_TSET) {
				clockmode = MODE_TSHOW;
				cursor = 0;
				inputval = clocktime;
				inmode = IN_TSHOW;
				have_input = 1;
			} else if (clockmode == MODE_ATOGGLE)
				clockmode = MODE_ASET;
			pthread_mutex_unlock(&clockmutex);
			st_p = blue_pixmap;
			break;
		case GDK_Control_L:
		case GDK_Control_R:
			pthread_mutex_lock(&clockmutex);
			if (clockmode == MODE_ASET) {
				clockmode = MODE_TSHOW;
				cursor = 0;
				inputval = alarmtime;
				inmode = IN_TSHOW;
			} else if (clockmode == MODE_ATOGGLE)
				clockmode = MODE_TSET;
			pthread_mutex_unlock(&clockmutex);
			sa_p = blue_pixmap;
			break;
		case GDK_Left:
			left_p = yellow_pixmap;
			break;
		case GDK_Right:
			right_p = yellow_pixmap;
			break;
		case GDK_Up:
			up_p = yellow_pixmap;
			break;
		case GDK_Down:
			down_p = yellow_pixmap;
			break;
		default:
			return TRUE;
	}

	gtk_widget_queue_draw(drawingarea);

	return TRUE;
}

/* This callback quits the program */
static gboolean delete_event( GtkWidget *widget,
        GdkEvent  *event,
        gpointer   data )
{
    gtk_main_quit();
    exit(0);
    return FALSE;
}

static GdkPixmap* pixmap = NULL;

	static gboolean
configure_event( GtkWidget *widget, GdkEventConfigure *event )
{
	if (pixmap)
		g_object_unref(pixmap);

	pixmap = gdk_pixmap_new(widget->window,
			widget->allocation.width,
			widget->allocation.height,
			-1);
	return TRUE;
}

#define CLOCKSTRLEN (10)
static void draw_time(GtkWidget* widget)
{
	char timestr[CLOCKSTRLEN];
	snprintf(timestr, CLOCKSTRLEN, "%02d:%02d:%02d",
			clocktime/10000,
			(clocktime/100)%100,
			clocktime%100);
	pango_layout_set_text(playout, timestr, -1);

    gdk_draw_layout(pixmap,
            widget->style->black_gc,
            50, 30,
            playout);
}

static void draw_alarm(GtkWidget* widget)
{
	char timestr[CLOCKSTRLEN];
	const char* fmtstr = "%02d_%02d_%02d";
	if (alarmflag)
		fmtstr = "%02d:%02d:%02d";
	snprintf(timestr, CLOCKSTRLEN, fmtstr,
			alarmtime/10000,
			(alarmtime/100)%100,
			alarmtime%100);
	pango_layout_set_text(playout, timestr, -1);

    gdk_draw_layout(pixmap,
            widget->style->black_gc,
            50, 55,
            playout);
}

/* Redraw the screen from the backing pixmap */
static gboolean expose_event(GtkWidget *widget, GdkEventExpose *event)
{
	gdk_draw_rectangle(pixmap,
			widget->style->white_gc,
			TRUE,
			0, 0,
			widget->allocation.width,
			widget->allocation.height);
	gdk_draw_drawable(pixmap,
			widget->style->white_gc,
			clock_pixmap,
			0, 0,
			0, 0,
			-1, -1);
	gdk_draw_drawable(pixmap,
			widget->style->white_gc,
			st_p,
			0, 0,
			90, 120,
			-1, -1);
	gdk_draw_drawable(pixmap,
			widget->style->white_gc,
			sa_p,
			0, 0,
			90, 150,
			-1, -1);
	gdk_draw_drawable(pixmap,
			widget->style->white_gc,
			left_p,
			0, 0,
			177, 150,
			-1, -1);
	gdk_draw_drawable(pixmap,
			widget->style->white_gc,
			up_p,
			0, 0,
			228, 120,
			-1, -1);
	gdk_draw_drawable(pixmap,
			widget->style->white_gc,
			down_p,
			0, 0,
			228, 150,
			-1, -1);
	gdk_draw_drawable(pixmap,
			widget->style->white_gc,
			right_p,
			0, 0,
			279, 150,
			-1, -1);

	pthread_mutex_lock(&clockmutex);
	if (!alarmpulse) {
		draw_time(widget);
		draw_alarm(widget);

		if (clockmode == MODE_TSET || clockmode == MODE_ASET) {
			char cstr[] = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '\0'};
			int cur = 5-cursor;
			if (cursor < 4) {
				cur++;
				if (cursor < 2)
					cur++;
			}
			cstr[cur] = '_';
			int y = 30;
			if (clockmode == MODE_ASET)
				y += 25;
			pango_layout_set_text(playout, cstr, -1);
    		gdk_draw_layout(pixmap,
            		widget->style->black_gc,
            		50, y,
            		playout);
		}
	}
	pthread_mutex_unlock(&clockmutex);

	gdk_draw_drawable(widget->window,
			widget->style->fg_gc[GTK_WIDGET_STATE (widget)],
			pixmap,
			event->area.x, event->area.y,
			event->area.x, event->area.y,
			event->area.width, event->area.height);

	return FALSE;
}

void load_images()
{
	GdkPixbuf* pixbuf;

	pixbuf = gdk_pixbuf_new_from_file("clock.png", NULL);
	assert(pixbuf != NULL);
	gdk_pixbuf_render_pixmap_and_mask(pixbuf, &clock_pixmap, &clock_mask, 0);
	assert(clock_pixmap != NULL);

	pixbuf = gdk_pixbuf_new_from_file("btnblue.png", NULL);
	assert(pixbuf != NULL);
	gdk_pixbuf_render_pixmap_and_mask(pixbuf, &blue_pixmap, &blue_mask, 0);
	assert(blue_pixmap != NULL);

	pixbuf = gdk_pixbuf_new_from_file("btnblue_p.png", NULL);
	assert(pixbuf != NULL);
	gdk_pixbuf_render_pixmap_and_mask(pixbuf, &blue_p_pixmap, &blue_p_mask, 0);
	assert(blue_p_pixmap != NULL);

	pixbuf = gdk_pixbuf_new_from_file("btnyellow.png", NULL);
	assert(pixbuf != NULL);
	gdk_pixbuf_render_pixmap_and_mask(pixbuf, &yellow_pixmap, &yellow_mask, 0);
	assert(yellow_pixmap != NULL);

	pixbuf = gdk_pixbuf_new_from_file("btnyellow_p.png", NULL);
	assert(pixbuf != NULL);
	gdk_pixbuf_render_pixmap_and_mask(pixbuf, &yellow_p_pixmap, &yellow_p_mask, 0);
	assert(yellow_p_pixmap != NULL);

	st_p = blue_pixmap;
	sa_p = blue_pixmap;
	up_p = yellow_pixmap;
	down_p = yellow_pixmap;
	left_p = yellow_pixmap;
	right_p = yellow_pixmap;
}

static void init_window()
{
    GtkWidget* window;
    GtkWidget* box;
	PangoFontDescription* pfont;

	int argc = 0;
	char** argv = NULL;
    gtk_init(&argc, &argv);
	
	/* Load image */
	load_images();

    window = gtk_window_new(GTK_WINDOW_TOPLEVEL);
	gtk_window_set_resizable(GTK_WINDOW(window), FALSE);
    gtk_window_set_title(GTK_WINDOW (window), "AlarmClock v3");
    g_signal_connect(G_OBJECT (window), "delete_event",
            G_CALLBACK (delete_event), NULL);

    gtk_widget_set_events(window, GDK_KEY_PRESS_MASK | GDK_BUTTON_PRESS_MASK);
    g_signal_connect(G_OBJECT(window), "key_press_event",
			G_CALLBACK(key_press_event), NULL);
    g_signal_connect(G_OBJECT(window), "key_release_event",
			G_CALLBACK(key_release_event), NULL);

    gtk_container_set_border_width(GTK_CONTAINER (window), 0);

    box = gtk_hbox_new (FALSE, 0);
    gtk_container_add  (GTK_CONTAINER (window), box);

	drawingarea = gtk_drawing_area_new();
    gtk_widget_set_events(drawingarea, GDK_EXPOSURE_MASK);
    g_signal_connect(G_OBJECT (drawingarea), "configure_event",
			G_CALLBACK (configure_event), NULL);
    g_signal_connect(G_OBJECT (drawingarea), "expose_event",
			G_CALLBACK (expose_event), NULL);
    gtk_drawing_area_size(GTK_DRAWING_AREA(drawingarea), 400, 189);
    gtk_box_pack_start(GTK_BOX(box), drawingarea, TRUE, TRUE, 0);

	pcontext = gtk_widget_get_pango_context(window);
	playout = pango_layout_new(pcontext);
	pfont = pango_font_description_new();
	pango_font_description_set_family(pfont, "Courier");
	pango_font_description_set_size(pfont, 28*PANGO_SCALE);
	pango_layout_set_font_description(playout, pfont);

    gtk_widget_show(drawingarea);
    gtk_widget_show(box);
    gtk_widget_show(window);

	gtk_timeout_add(100, &refresh, NULL);
}

void* ClockDeviceThread(void* data)
{
  init_window();

  gtk_main();
  return NULL;
}

