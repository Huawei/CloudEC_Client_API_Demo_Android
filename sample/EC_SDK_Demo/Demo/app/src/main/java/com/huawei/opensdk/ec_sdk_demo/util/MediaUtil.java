package com.huawei.opensdk.ec_sdk_demo.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.LogUtil;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * This class is about media player.
 * 媒体播放类
 */
public class MediaUtil {

    private static final String TAG = MediaUtil.class.getSimpleName();

    /**
     * 对象实例
     */
    private static MediaUtil instance;

    /**
     * 音频管理器
     */
    private final AudioManager manager;

    /**
     * 媒体播放器
     */
    private MediaPlayer mediaPlayer;

    /**
     * 媒体播放线程池实例
     */
    private ExecutorService executorService;
    private Context context;

    /**
     * 构造方法
     */
    private MediaUtil() {
        context = LocContext.getContext();
        manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 获取唯一实例
     * @return
     */
    public static MediaUtil getInstance() {
        synchronized (MediaUtil.class) {
            if (null == instance) {
                instance = new MediaUtil();
            }
            return instance;
        }
    }

    /**
     * 获取MediaPlayer唯一实例
     * @return
     */
    private synchronized MediaPlayer getMediaPlayer() {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
        }

        return mediaPlayer;
    }

    /**
     * 播放指定路径铃声
     * @param id 资源ID
     */
    public void playFromRawFile(int id) {
        LogUtil.i(TAG, "playFromRawFile");

        AssetFileDescriptor afd = null;
        try {
            Resources resources = context.getResources();
            afd = resources.openRawResourceFd(id);
        } catch (NotFoundException e) {
            LogUtil.e(TAG, e.getMessage());
        }
        // 暂停应用内其他声音，防止声音重叠
        pausePlayer();

        try {
            getExecutorService().execute(new PlayAction(afd));
        } catch (RejectedExecutionException error) {
            LogUtil.e(TAG, error.getMessage());
        }
    }

    /**
     * 停止播放声音
     */
    public void stopPlayFromRawFile() {
        LogUtil.i(TAG, "stopPlayFromRawFile");

        releaseMediaPlayer();
    }

    /**
     * 暂停播放
     */
    private void pausePlayer() {
        if (isPlaying()) {
            stopPlayFromRawFile();
        }
    }

    /**
     * 判断是否正在播放
     * @return
     */
    private synchronized boolean isPlaying() {
        if (null == mediaPlayer) {
            return false;
        }

        boolean isPlaying = false;

        try {
            isPlaying = mediaPlayer.isPlaying();
        } catch (IllegalStateException stateException) {
            LogUtil.e(TAG, stateException.getMessage());
        }

        return isPlaying;
    }

    /**
     * 释放资源
     */
    private synchronized void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        getExecutorService().shutdownNow();
        executorService = null;
    }

    /**
     * 获取执行音乐播放的唯一线程池实例
     * @return
     */
    private ExecutorService getExecutorService() {
        synchronized (MediaUtil.class) {
            if (null == executorService) {
                executorService = Executors.newSingleThreadExecutor();
            }
        }

        return executorService;
    }

    /**
     * 播放铃声task
     */
    private class PlayAction implements Runnable {
        private AssetFileDescriptor afd;

        public PlayAction(AssetFileDescriptor afd) {
            this.afd = afd;
        }

        @Override
        public void run() {
            if (afd == null) {
                LogUtil.i(TAG, "afd is null");
                return;
            }

            try {
                MediaPlayer mediaPlayer = getMediaPlayer();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } catch (IllegalArgumentException e) {
                LogUtil.e(TAG, e.getMessage());
            } catch (IllegalStateException e) {
                LogUtil.e(TAG, e.getMessage());
            } catch (IOException e) {
                LogUtil.e(TAG, e.getMessage());
            }
        }
    }
}
