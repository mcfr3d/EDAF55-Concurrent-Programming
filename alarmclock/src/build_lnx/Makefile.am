## This file is the input for automake, and will be converted to Makefile.in,
## which in turn is converted to Makefile by autoconf

# This will allow us to configure what subdirs to automatically _build_
# from the configure script. All subdirs are still _configured_ though.
# @ variables are defined in the configure script
# SUBDIRS = @CONFIG_BUILDDIRS@

# Since we are defining SUBDIRS conditionally, we should include
# all directories that we would like to include in a distribution
# in the DIST_SUBDIRS variable
# DIST_SUBDIRS= @CONFIG_DISTDIRS@

# Main class of test case
TESTCLASS=Main.java

# -- Names of the main programs --
MAIN=Main

#if HOST_AVR
#noinst_LIBRARIES = libgc.a libjavanative.a libtobbe.a
#else
noinst_LIBRARIES = libgc.a libjavanative.a
#endif

noinst_HEADERS    = $(GC_DIR)/exception.h $(GC_DIR)/gc.h $(GC_DIR)/gc_incremental_ms.h $(GC_DIR)/gc-ms.h $(GC_DIR)/mem.h $(GC_DIR)/gc_impl_incremental_ms.h $(GC_DIR)/gc_impl_nonmoving.h $(GC_DIR)/gc_roots.h

Main_SOURCES = \
	$(TESTCLASS:java=c) \
	$(STORK_SOURCE)

## if HOST_AVR
##Main_LDADD = libgc.a libtobbe.a libjavanative.a
##else
##Main_LDADD = libgc.a libjavanative.a
##endif

if HOST_AVR
HOST_LIBS=libtobbe.a
endif

Main_LDADD = libgc.a libjavanative.a $(HOST_LIBS)

if HOST_AVR
libjavanative_a_SOURCES=\
	$(NATIVE_SOURCE) \
	$(UTIL_LINUX_NATIVE_SOURCE) \
        $(JAVA_LANG_MATH_NATIVE_SOURCE) \
        $(SE_LTH_CS_REALTIME_NATIVE_SOURCE) \
        $(SE_LTH_CS_ROBOT_NATIVE_SOURCE) \
	$(CPU_UTILIZATION_LOGG_NATIVE_SOURCE) \
	$(TOBBE_SOURCE) \
	$(TOBBE_ASM_SOURCE)
else
libjavanative_a_SOURCES=\
	$(NATIVE_SOURCE) \
	$(UTIL_LINUX_NATIVE_SOURCE) \
        $(JAVA_LANG_MATH_NATIVE_SOURCE) \
        $(SE_LTH_CS_REALTIME_NATIVE_SOURCE) \
        $(SE_LTH_CS_ROBOT_NATIVE_SOURCE) \
	$(CPU_UTILIZATION_LOGG_NATIVE_SOURCE)
endif

SOURCE_DEPENDS = done/ClockInput.java done/ClockOutput.java done/InputSampler.java todo/AlarmClock.java

######################################################################
# Define the program to be build
bin_PROGRAMS = $(MAIN)
# Hmm. We need this if we use AC_SUBST on bin_PROGRAMS.
# EXTRA_PROGRAMS = HelloWorld

BUILT_SOURCES = $(MAIN).c libgc.a

####### Directories #########################

# Root directory of the Java2C installation
#J2C_HOME=$(top_srcdir)/../../..
#J2C_HOME=__SUBST__J2C_HOME__SUBST__
J2C_HOME=@J2C_HOME@
J2C_JAVA2CFLAGS=@J2C_JAVA2CFLAGS@

# Directory where the GC is located
#GC_DIR=$(top_srcdir)/../../../gc
GC_DIR=$(J2C_HOME)/gc

# Directory where Java 2 C translator is located
#JAVA2C_DIR=$(top_srcdir)/../..
JAVA2C_DIR= $(J2C_HOME)/J2CTranslator

# Directory where native files (of the Java lib) are located
#NATIVE_DIR=$(top_srcdir)/../../javalib/native/@THREAD@
NATIVE_DIR=$(J2C_HOME)/javalib/native/@THREAD@

# Thread native code
NATIVE_SOURCE=$(NATIVE_DIR)/ljrt_threading.c \
	$(NATIVE_DIR)/java_lang_Thread.c \
	$(NATIVE_DIR)/java_lang_System.c \
	$(NATIVE_DIR)/java_lang_Object.c \
	$(NATIVE_DIR)/java_lang_String.c \
	$(NATIVE_DIR)/java_net_Socket.c \
	$(NATIVE_DIR)/java_net_DatagramSocket.c \
	$(NATIVE_DIR)/java_net_ServerSocket.c \
	$(NATIVE_DIR)/java_io_OutputStream.c \
	$(NATIVE_DIR)/java_io_InputStream.c \
	$(NATIVE_DIR)/../common/java_lang_Math.c \
	$(NATIVE_DIR)/../common/java_lang_Float.c \
	$(NATIVE_DIR)/../common/java_lang_Double.c \
	$(NATIVE_DIR)/../common/InstanceOf.c \
	$(NATIVE_DIR)/../common/java_lang_VMClass.c \
	$(NATIVE_DIR)/../common/java_lang_Class.c \
	$(NATIVE_DIR)/se_lth_cs_camera_Axis211A.c \
	$(NATIVE_DIR)/se_lth_cs_camera_MotionDetector.c \
	$(NATIVE_DIR)/../common/javatypes.c  native/@THREAD@/done_ClockInput.c native/@THREAD@/done_ClockOutput.c native/@THREAD@/ClockDevice.c
# TODO: make these optional:
#	$(NATIVE_DIR)/se_lth_cs_camera_Axis211A.c \
#	$(NATIVE_DIR)/se_lth_cs_camera_MotionDetector.c

# Select which packages to include. (see configure.in.skel)
# If the code is actually included is selected in the C source files,
# based on whether the corresponding class index is defined in
# class_indices.h or not.
#

if ENABLE_CPU_UTILIZATION_LOGGING
   CPU_UTILIZATION_LOGG_NATIVE_SOURCE=$(NATIVE_DIR)/cpu_utilization_logger.c
endif

if ENABLE_JAVA_LANG_MATH
JAVA_LANG_MATH_NATIVE_SOURCE= \
	$(NATIVE_DIR)/../common/java_lang_Math.c
endif

if ENABLE_UTIL_LINUX
UTIL_LINUX_NATIVE_SOURCE= \
	$(NATIVE_DIR)/../common/KernelSymbol.c \
	$(NATIVE_DIR)/../common/ImmobileObjectFactory.c
endif

if ENABLE_SE_LTH_CS_REALTIME
SE_LTH_CS_REALTIME_NATIVE_SOURCE=\
	$(NATIVE_DIR)/se_lth_cs_realtime_RTSystem.c   \
	$(NATIVE_DIR)/se_lth_cs_realtime_semaphore_BinarySem.c   \
	$(NATIVE_DIR)/se_lth_cs_realtime_semaphore_CountingSem.c \
	$(NATIVE_DIR)/se_lth_cs_realtime_semaphore_MutexSem.c \
	$(NATIVE_DIR)/se_lth_cs_realtime_RTThread.c
endif

if ENABLE_SE_LTH_CS_ROBOT
SE_LTH_CS_ROBOT_NATIVE_SOURCE=\
	$(NATIVE_DIR)/se_lth_cs_robot_irb6_IO.c
endif

# Additional source files when building for STORK
# if HOST_STORK
#  STORK_SOURCE=@STORKDIR@/auxil/Main.c @STORKDIR@/auxil/fmtio.c
#endif

if HOST_AVR
TOBBE_SOURCE=\
	$(NATIVE_DIR)/AVR_Kernel.c \
	$(NATIVE_DIR)/AVR_Memory.c \
	$(NATIVE_DIR)/AVR_Queue.c \
	$(NATIVE_DIR)/AVR_Semaphore.c \
	$(NATIVE_DIR)/AVR_Thread.c \
	$(NATIVE_DIR)/AVR_LCD.c

TOBBE_ASM_SOURCE=$(NATIVE_DIR)/AVR_ContextSwitch.s \
		 $(NATIVE_DIR)/xram.s
endif

# The java2c compiler
JAVA_PROGRAM=$(JAVA2C_DIR)/JavaGenCCode.class

# GC algorithm sources
GC_SOURCE=$(GC_DIR)/gci_common.c $(GC_DIR)/gc_roots.c $(GC_DIR)/gci_thread.c $(GC_DIR)/gci_sched.c

if GC_BATCH_COPY
libgc_a_SOURCES  = $(GC_SOURCE) $(GC_DIR)/gc_batch_copy.c
endif

if GC_INCREMENTAL_MS
libgc_a_SOURCES  = $(GC_SOURCE) $(GC_DIR)/gc_incremental_ms.c
endif

if GC_NONMOVING
libgc_a_SOURCES   = $(GC_SOURCE) $(GC_DIR)/gc-ms.c $(GC_DIR)/mem.c
endif

if GC_INCREMENTAL_COPY
libgc_a_SOURCES  = $(GC_SOURCE) $(GC_DIR)/gc_incremental_copy.c
endif

# Class path used by the Java2C compiler for locating imported classes
#CLASSPATH=$(top_srcdir)/../../../javalib/java:$(top_srcdir)

if HOST_CYGWIN
  CLASSPATH=`cygpath -wp $(CLASSPATH_AUX)$(JAVA2C_DIR)/../javalib/java/:$(JAVA2C_DIR)/../javalib/vm/:$(top_srcdir)`
  # Class path used by jvm to find classes
  J2C_CLASSPATH=`cygpath -wp $(JAVA2C_DIR)`
else
if HOST_AVR
  CLASSPATH='$(CLASSPATH_AUX)$(JAVA2C_DIR)/../javalib/avrjava/:$(JAVA2C_DIR)/../javalib/vm/:$(top_srcdir)'
  # Class path used by jvm to find classes
  J2C_CLASSPATH='$(JAVA2C_DIR)'
else
  CLASSPATH='$(CLASSPATH_AUX)$(JAVA2C_DIR)/../javalib/java/:$(JAVA2C_DIR)/../javalib/vm/:$(top_srcdir)'
  # Class path used by jvm to find classes
  J2C_CLASSPATH='$(JAVA2C_DIR)'
endif
endif


# -- Compile --
#

all-local:	$(MAIN)
#all-local:	modules

# if HOST_RTAI_KERNEL
# all: modules
# SUBDIR = $(shell pwd)
# CROSS_PREFIX    = powerpc-603e-linux-gnu-

# # EXTRA_FLAGS += -DDEBUG=1
# EXTRA_FLAGS = -DDEBUG=1

# # obj-m := $(MAIN).o
# obj-m = $(MAIN).o


# # modules:
# #         $(MAKE) ARCH=ppc CROSS_COMPILE=$(CROSS_PREFIX) \
# #         -C $(KERNEL_SRC) SUBDIRS=$(SUBDIR) modules
# modules:
#         $(MAKE) ARCH=$(HOST_ARCH) CROSS_COMPILE=$(CROSS_PREFIX) \
#         -C $(LINUX_PATH) SUBDIRS=$(SUBDIR) modules

# # install_modules:
# #         cp *.ko ../../../root/modules/
# endif

if HOST_STORK
run:	$(MAIN)
	storkgo -target daneel -process vme2400 $(MAIN)
else
if HOST_AVR
rom:	$(MAIN).rom $(MAIN).elf

run:	$(MAIN).rom
	avrdude -c stk200 -p m128 -P /dev/parport0 -Uflash:w:$(MAIN).rom
#	uisp -dprog=stk200 -dpart=atmega128 -dlpt=/dev/parport0 --upload --erase if=$(MAIN).rom
else
run:	$(MAIN)
	./$(MAIN)
endif
endif

c:
	java -classpath $(J2C_CLASSPATH) JavaGenCCode -classpath $(CLASSPATH) \
	-bootclasspath . \
	 -o $(TESTCLASS:java=c) $(J2C_JAVA2CFLAGS) $(top_srcdir)/$(TESTCLASS)

$(MAIN):	$(TESTCLASS:java=c) $(GC_INCLUDE)libgc.a libjavanative.a

$(TESTCLASS:java=c):	$(top_srcdir)/$(TESTCLASS) $(JAVA_PROGRAM)  $(SOURCE_DEPENDS) Makefile
	java -classpath $(J2C_CLASSPATH) JavaGenCCode -classpath $(CLASSPATH) \
	-bootclasspath . \
	 -o $(TESTCLASS:java=c) $(J2C_JAVA2CFLAGS) $(top_srcdir)/$(TESTCLASS)
# -- Clean up by removing generated files and backup files from text editors --

if HOST_AVR
flash: run

%o: %s
	$(AS) $(CCASFLAGS) $< -o $@

%elf: %o libgc.a libjavanative.a
	$(CC) $(GCCLIB) \
	-Wl,-Map=$(<:.o=.map),--cref,-v,--section-start,.data=0x801100,--defsym=__stack=0x8010FF,--defsym=__heap_start=0x800100 -mmcu=atmega128 -v -o $@ \
	$<  libgc.a libjavanative.a xram.o

%obj: %elf
	avr-objcopy -O ihex $< $@

%bin: %elf
	avr-objcopy -O binary $< $@

%rom: %elf
	avr-objcopy -O ihex $< $@

%eep: %elf
	avr-objcopy -j .eeprom --set-section-flags=.eeprom="alloc,load" --change-section-lma .eeprom=0 -O ihex $< $@

endif

clean-local:
	/bin/rm -f $(MAIN)
	/bin/rm -f $(MAIN).c
	/bin/rm -f *.h
