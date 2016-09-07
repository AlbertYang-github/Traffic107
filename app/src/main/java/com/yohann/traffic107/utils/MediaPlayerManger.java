package com.yohann.traffic107.utils;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * MediaPlayerManger
 *
 * @author: lenovo
 * @time: 2016/8/13 21:51
 */
public class MediaPlayerManger {
    private MediaPlayer mediaPlayer;

    public MediaPlayerManger() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
            }
        });
    }

    public void startPlay(String path) throws IOException {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
    }

    public void pausePlay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void release() {
        mediaPlayer.release();
    }
}
