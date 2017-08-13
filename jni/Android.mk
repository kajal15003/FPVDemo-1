LOCAL_PATH := $(call my-dir)

CVROOT := C:/OpenCV-android-sdk/sdk/native/jni

include $(CLEAR_VARS)
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=STATIC
include $(CVROOT)/OpenCV.mk


LOCAL_MODULE += KCF
LOCAL_SRC_FILES +=  kcftracker.cpp fhog.cpp frame.cpp
#LOCAL_SRC_FILES +=  frame.cpp
LOCAL_LDLIBS += -llog

include $(BUILD_SHARED_LIBRARY)