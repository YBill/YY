package com.ioyouyun.media.view;

/**
 * Created by 卫彪 on 2016/7/21.
 */
public interface VoIPView {

    void setTipsText(String time);

    /**
     * 扬声器
     * @param isSpeaker
     */
    void toggleSpeaker(boolean isSpeaker);

    /**
     * 麦克风
     * @param isMicMut
     */
    void toggleMicro(boolean isMicMut);

}
