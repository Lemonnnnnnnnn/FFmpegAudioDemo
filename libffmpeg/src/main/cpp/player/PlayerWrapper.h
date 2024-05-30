#ifndef LEARNFFMPEG_PLAYERWRAPPER_H
#define LEARNFFMPEG_PLAYERWRAPPER_H

#include <jni.h>
#include "FFMediaPlayer.h"

static const int FFMEDIA_PLAYER = 0;
static const int HWCODEC_PLAYER = 1;

class PlayerWrapper {
public:
    PlayerWrapper(){};
    virtual ~PlayerWrapper(){};

    void Init(JNIEnv *jniEnv, jobject obj, char *url);
    void UnInit();

    void Play();
    void Pause();
    void Stop();
    void SeekToPosition(float position);
    float GetDuration();

private:
    MediaPlayer* m_MediaPlayer = nullptr;

};


#endif //LEARNFFMPEG_PLAYERWRAPPER_H
