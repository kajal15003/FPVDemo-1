#include <jni.h>
#include <iostream>
#include <string.h>
#include <algorithm>
#include "opencv2/opencv.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/core/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "kcftracker.hpp"

using namespace cv;
using namespace std;

extern "C" {

int counter = 0;

KCFTracker tracker(true, true, true, true);

Rect result;

JNIEXPORT jstring
JNICALL Java_com_dji_FPVDemo_MainActivity_KCFTracker
        (JNIEnv *env, jclass inter, jlong addrmat, jfloat x,jfloat y,jfloat w,jfloat h) {
    Mat &frame = *(Mat *) addrmat;

    if ( counter == 0 )
    {

        HOGDescriptor hog;
        hog.setSVMDetector(HOGDescriptor::getDefaultPeopleDetector());
        vector<Rect> found;
        hog.detectMultiScale(frame, found, 0, Size(8,8), Size(0,0), 1.05, 0);
        if(found.size()>=1)
        {
            Rect r = found[0];
            r.x += cvRound(r.width*0.1);
            r.width = cvRound(r.width*0.8);
            r.y += cvRound(r.height*0.07);
            r.height = cvRound(r.height*0.8);
            tracker.init( Rect((float)r.x,(float)r.y,(float)r.width,(float)r.height), frame ) ;
            counter=1;
            return (*env).NewStringUTF("YES");
        }
        else
        {
            counter=0;
            return (*env).NewStringUTF("NO");
        }
    }
    else
    {
        result = tracker.update(frame);
        rectangle( frame, Point(result.x, result.y), Point(result.x + result.width, result.y + result.height), Scalar(255, 0, 0),1, 8 );
        char buf1[64]; // assumed large enough to cope with result
        char buf2[64];
        char buf3[64];
        char buf4[64];
        char buff[2048];
        sprintf(buf1, "%f-", (float)result.x);
        sprintf(buf2, "%f-", (float)result.y);
        sprintf(buf3, "%f-", (float)result.width);
        sprintf(buf4, "%f", (float)result.height);

        strcat(buff,buf1);
        strcat(buff,buf2);
        strcat(buff,buf3);
        strcat(buff,buf4);

        return (*env).NewStringUTF(buff);

    }

}

}


