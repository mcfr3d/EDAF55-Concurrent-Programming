#include <gtk/gtk.h>
#include <gdk/gdkkeysyms.h>
#include <gdk-pixbuf/gdk-pixbuf.h>
#include <assert.h>

GtkWidget* drawingarea;
GdkPixmap* clock_pixmap;
GdkBitmap* clock_mask;
GdkPixmap* blue_pixmap;
GdkBitmap* blue_mask;
GdkPixmap* blue_p_pixmap;
GdkBitmap* blue_p_mask;
GdkPixmap* yellow_pixmap;
GdkBitmap* yellow_mask;
GdkPixmap* yellow_p_pixmap;
GdkBitmap* yellow_p_mask;
GdkPixmap* st_p;
GdkPixmap* sa_p;
GdkPixmap* up_p;
GdkPixmap* down_p;
GdkPixmap* left_p;
GdkPixmap* right_p;
PangoContext* pcontext;
PangoLayout* playout;

static gboolean
key_press_event( GtkWidget *widget, GdkEventKey* event )
{
	switch (event->keyval) {
		case GDK_Shift_L:
		case GDK_Shift_R:
			st_p = blue_p_pixmap;
			break;
		case GDK_Control_L:
		case GDK_Control_R:
			sa_p = blue_p_pixmap;
			break;
		case GDK_Left:
			left_p = yellow_p_pixmap;
			break;
		case GDK_Right:
			right_p = yellow_p_pixmap;
			break;
			break;
		case GDK_Up:
			up_p = yellow_p_pixmap;
			break;
		case GDK_Down:
			down_p = yellow_p_pixmap;
			break;
		default:
			return TRUE;
	}

  	gtk_widget_queue_draw_area(drawingarea, 0, 0,
			400,
			189);

  	return TRUE;
}

static gboolean
key_release_event( GtkWidget *widget, GdkEventKey* event )
{
	switch (event->keyval) {
		case GDK_Shift_L:
		case GDK_Shift_R:
			st_p = blue_pixmap;
			break;
		case GDK_Control_L:
		case GDK_Control_R:
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

  	gtk_widget_queue_draw_area(drawingarea, 0, 0,
			400,
			189);

  	return TRUE;
}

/* This callback quits the program */
static gboolean delete_event( GtkWidget *widget,
        GdkEvent  *event,
        gpointer   data )
{
    gtk_main_quit ();
    return FALSE;
}

static GdkPixmap *pixmap = NULL;

	static void
draw_brush (GtkWidget *widget, gdouble x, gdouble y)
{
  	GdkRectangle update_rect;

  	update_rect.x = x - 5;
  	update_rect.y = y - 5;
  	update_rect.width = 10;
  	update_rect.height = 10;
  	gdk_draw_rectangle (pixmap,
  		    widget->style->black_gc,
  		    TRUE,
		    update_rect.x, update_rect.y,
		    update_rect.width, update_rect.height);
  	gtk_widget_queue_draw_area (widget, 		      
            update_rect.x, update_rect.y,
		    update_rect.width, update_rect.height);
}

	static gboolean
configure_event( GtkWidget *widget, GdkEventConfigure *event )
{
  	if (pixmap)
    	g_object_unref(pixmap);

  	pixmap = gdk_pixmap_new(widget->window,
			widget->allocation.width,
			widget->allocation.height,
			-1);
  	//draw_brush(widget, 10, 10);
  	return TRUE;
}

static void draw_time(GtkWidget* widget)
{
	pango_layout_set_text(playout, "00:00:00", -1);

    gdk_draw_layout(pixmap,
            widget->style->black_gc,
            50, 30,
            playout);
}

static void draw_alarm(GtkWidget* widget)
{
	pango_layout_set_text(playout, "00_00_00", -1);

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

	draw_time(widget);
	draw_alarm(widget);

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

int main(int argc, char** argv)
{
    GtkWidget* window;
    GtkWidget* box;
	PangoFontDescription* pfont;

    gtk_init(&argc, &argv);
	
	/* Load image */
	load_images();

    window = gtk_window_new(GTK_WINDOW_TOPLEVEL);
	gtk_window_set_resizable(GTK_WINDOW(window), FALSE);
    gtk_window_set_title(GTK_WINDOW (window), "AlarmClock3");
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
    //gtk_container_add(GTK_CONTAINER (window), drawingarea);
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

    gtk_main();

    return 0;
}
