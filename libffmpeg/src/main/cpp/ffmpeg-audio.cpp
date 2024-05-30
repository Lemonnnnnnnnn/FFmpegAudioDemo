#include <cstdio>
#include <cstring>
#include <PlayerWrapper.h>
#include <render/audio/OpenSLRender.h>
#include "util/LogUtil.h"
#include "jni.h"

extern "C" {
#include <libavcodec/version.h>
#include <libavcodec/avcodec.h>
#include <libavformat/version.h>
#include <libavutil/version.h>
#include <libavfilter/version.h>
#include <libswresample/version.h>
#include <libswscale/version.h>
};

#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_ethan_libffmpeg_FFMediaPlayer
 * Method:    native_GetFFmpegVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL
Java_com_ethan_libffmpeg_FFMediaPlayer_native_1GetFFmpegVersion(JNIEnv *env, jclass cls)
{
    char strBuffer[1024 * 4] = {0};
    strcat(strBuffer, "libavcodec : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVCODEC_VERSION));
    strcat(strBuffer, "\nlibavformat : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVFORMAT_VERSION));
    strcat(strBuffer, "\nlibavutil : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVUTIL_VERSION));
    strcat(strBuffer, "\nlibavfilter : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVFILTER_VERSION));
    strcat(strBuffer, "\nlibswresample : ");
    strcat(strBuffer, AV_STRINGIFY(LIBSWRESAMPLE_VERSION));
    strcat(strBuffer, "\nlibswscale : ");
    strcat(strBuffer, AV_STRINGIFY(LIBSWSCALE_VERSION));
    strcat(strBuffer, "\navcodec_configure : \n");
    strcat(strBuffer, avcodec_configuration());
    strcat(strBuffer, "\navcodec_license : ");
    strcat(strBuffer, avcodec_license());
    LOGCATE("GetFFmpegVersion\n%s", strBuffer);

    //ASanTestCase::MainTest();

    return env->NewStringUTF(strBuffer);
}

/*
 * Class:     com_ethan_libffmpeg_FFMediaPlayer
 * Method:    native_Init
 * Signature: (JLjava/lang/String;Ljava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL
Java_com_ethan_libffmpeg_FFMediaPlayer_native_1Init(JNIEnv *env, jobject obj, jstring jurl)
{
    const char* url = env->GetStringUTFChars(jurl, nullptr);
    PlayerWrapper *player = new PlayerWrapper();
    player->Init(env, obj, const_cast<char *>(url));
    env->ReleaseStringUTFChars(jurl, url);
    return reinterpret_cast<jlong>(player);
}

/*
 * Class:     com_ethan_libffmpeg_FFMediaPlayer
 * Method:    native_Play
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_ethan_libffmpeg_FFMediaPlayer_native_1Play(JNIEnv *env, jobject obj, jlong player_handle)
{
    if(player_handle != 0)
    {
        PlayerWrapper *pPlayerWrapper = reinterpret_cast<PlayerWrapper *>(player_handle);
        pPlayerWrapper->Play();
    }

}

JNIEXPORT void JNICALL
Java_com_ethan_libffmpeg_FFMediaPlayer_native_1SeekToPosition(JNIEnv *env, jobject thiz,
                                                                      jlong player_handle, jfloat position) {
    if(player_handle != 0)
    {
        PlayerWrapper *ffMediaPlayer = reinterpret_cast<PlayerWrapper *>(player_handle);
        ffMediaPlayer->SeekToPosition(position);
    }
}

/*
 * Class:     com_ethan_libffmpeg_FFMediaPlayer
 * Method:    native_Pause
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_ethan_libffmpeg_FFMediaPlayer_native_1Pause
(JNIEnv *env, jobject obj, jlong player_handle)
{
    if(player_handle != 0)
    {
        PlayerWrapper *ffMediaPlayer = reinterpret_cast<PlayerWrapper *>(player_handle);
        ffMediaPlayer->Pause();
    }
}

/*
 * Class:     com_ethan_libffmpeg_FFMediaPlayer
 * Method:    native_Stop
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_ethan_libffmpeg_FFMediaPlayer_native_1Stop
(JNIEnv *env, jobject obj, jlong player_handle)
{
    if(player_handle != 0)
    {
        PlayerWrapper *ffMediaPlayer = reinterpret_cast<PlayerWrapper *>(player_handle);
        ffMediaPlayer->Stop();
    }
}

/*
 * Class:     com_ethan_libffmpeg_FFMediaPlayer
 * Method:    native_UnInit
 * Signature: (J)V
 */
JNIEXPORT void JNICALL
Java_com_ethan_libffmpeg_FFMediaPlayer_native_1UnInit(JNIEnv *env, jobject obj, jlong player_handle)
{
    if(player_handle != 0)
    {
        PlayerWrapper *ffMediaPlayer = reinterpret_cast<PlayerWrapper *>(player_handle);
        ffMediaPlayer->UnInit();
        delete ffMediaPlayer;
    }
}


JNIEXPORT jfloat JNICALL
Java_com_ethan_libffmpeg_FFMediaPlayer_native_1getDuration(JNIEnv *env, jobject thiz,jlong player_handle) {
    if(player_handle != 0)
    {
        PlayerWrapper *ffMediaPlayer = reinterpret_cast<PlayerWrapper *>(player_handle);
        return ffMediaPlayer->GetDuration();
    }
}

#ifdef __cplusplus
}
#endif


