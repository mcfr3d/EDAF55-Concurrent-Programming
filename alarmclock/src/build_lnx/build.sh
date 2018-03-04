#!/bin/sh

gcc -fPIC -Wall -c done_InputSampler.c `pkg-config --cflags --libs gtk+-2.0`
gcc -fPIC -Wall -c done_ClockInput.c `pkg-config --cflags --libs gtk+-2.0`
gcc -fPIC -Wall -c done_ClockOutput.c `pkg-config --cflags --libs gtk+-2.0`
gcc -shared -Wl,-soname,libclockgui.so -o libclockgui.so done_ClockInput.o done_ClockOutput.o done_InputSampler.o -static -lc `pkg-config --cflags --libs gtk+-2.0`

