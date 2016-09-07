package com.yohann.traffic107.utils;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * RecordManager
 *
 * @author: lenovo
 * @time: 2016/8/12 19:01
 */
public class RecordManager {
    private MediaRecorder recorder;

    public RecordManager() {
        recorder = new MediaRecorder();
    }

    /**
     * 开始录音
     *
     * @param targetFile
     */
    public void startRecord(File targetFile) {

        if (!targetFile.exists()) {
            throw new RuntimeException(targetFile.getName() + "不存在");
        }

        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(targetFile.getAbsolutePath());

            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (recorder != null) {
            recorder.stop();
        }
    }

    public void release() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }
}
