#ifndef LEARNFFMPEG_MEDIAPLAYER_H
#define LEARNFFMPEG_MEDIAPLAYER_H

#include <jni.h>
#include <decoder/AudioDecoder.h>

#define JAVA_PLAYER_EVENT_CALLBACK_API_NAME "playerEventCallback"

#define MEDIA_PARAM_VIDEO_WIDTH         0x0001
#define MEDIA_PARAM_VIDEO_HEIGHT        0x0002
#define MEDIA_PARAM_VIDEO_DURATION      0x0003

#define MEDIA_PARAM_ASSET_MANAGER       0x0020


class MediaPlayer {
public:
    MediaPlayer(){};
    virtual ~MediaPlayer(){};

    virtual void Init(JNIEnv *jniEnv, jobject obj, char *url) = 0;
    virtual void UnInit() = 0;

    virtual void Play() = 0;
    virtual void Pause() = 0;
    virtual void Stop() = 0;
    virtual void SeekToPosition(float position) = 0;
    virtual float GetDuration() = 0;

    virtual JNIEnv *GetJNIEnv(bool *isAttach) = 0;
    virtual jobject GetJavaObj() = 0;
    virtual JavaVM *GetJavaVM() = 0;

    JavaVM *m_JavaVM = nullptr;
    jobject m_JavaObj = nullptr;
};

#endif //LEARNFFMPEG_MEDIAPLAYER_H
