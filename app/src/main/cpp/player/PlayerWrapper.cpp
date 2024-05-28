#include "PlayerWrapper.h"

void PlayerWrapper::Init(JNIEnv *jniEnv, jobject obj, char *url) {

    m_MediaPlayer = new FFMediaPlayer();
    if(m_MediaPlayer)
        m_MediaPlayer->Init(jniEnv, obj, url);
}

void PlayerWrapper::UnInit() {
    if(m_MediaPlayer) {
        m_MediaPlayer->UnInit();
        delete m_MediaPlayer;
        m_MediaPlayer = nullptr;
    }
}

void PlayerWrapper::Play() {
    if(m_MediaPlayer) {
        m_MediaPlayer->Play();
    }
}

void PlayerWrapper::Pause() {
    if(m_MediaPlayer) {
        m_MediaPlayer->Pause();
    }
}

void PlayerWrapper::Stop() {
    if(m_MediaPlayer) {
        m_MediaPlayer->Stop();
    }
}

void PlayerWrapper::SeekToPosition(float position) {
    if(m_MediaPlayer) {
        m_MediaPlayer->SeekToPosition(position);
    }

}

float PlayerWrapper::GetDuration() {
    if (m_MediaPlayer)
        return m_MediaPlayer->GetDuration();
}
