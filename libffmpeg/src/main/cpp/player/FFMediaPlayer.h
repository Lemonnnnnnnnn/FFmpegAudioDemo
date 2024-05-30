#ifndef LEARNFFMPEG_FFMEDIAPLAYER_H
#define LEARNFFMPEG_FFMEDIAPLAYER_H

#include "MediaPlayer.h"

class FFMediaPlayer : public MediaPlayer {
public:
    FFMediaPlayer(){};
    virtual ~FFMediaPlayer(){};

    virtual void Init(JNIEnv *jniEnv, jobject obj, char *url);
    virtual void UnInit();

    virtual void Play();
    virtual void Pause();
    virtual void Stop();
    virtual void SeekToPosition(float position);
    virtual float GetDuration();

private:
    virtual JNIEnv *GetJNIEnv(bool *isAttach);
    virtual jobject GetJavaObj();
    virtual JavaVM *GetJavaVM();

    static void PostMessage(void *context, int msgType, float msgCode);

    AudioDecoder *m_AudioDecoder = nullptr;
    AudioRender *m_AudioRender = nullptr;

};


#endif //LEARNFFMPEG_FFMEDIAPLAYER_H
