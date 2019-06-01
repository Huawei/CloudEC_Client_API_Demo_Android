package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

//import com.huawei.data.entity.InstantMessage;
//import com.huawei.data.unifiedmessage.MediaResource;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.im.ChatTools;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;
import com.huawei.opensdk.ec_sdk_demo.ui.im.contact.HeadIconTools;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.imservice.data.UmTransProgressData;

import java.io.File;

/**
 * This class is about Media scan activity.
 */
public class MediaScanActivity extends BaseActivity implements View.OnClickListener, LocBroadcastReceiver
{
    private static final int SCAN_MEDIA = 0;
    private static final int UPDATE_DOWNLOAD_PROGRESS = 1;
    private String fileName;
//    private InstantMessage currentMediaMessage;
    private ImageView pictureImageView;
    private VideoView videoView;
    private TextView progressTextView;
    private boolean isPicture = true;
    private ImMgr imMgr = ImMgr.getInstance();
    private String[] mAction = new String[]{CustomBroadcastConstants.ACTION_DOWNLOAD_PROCESS,
            CustomBroadcastConstants.ACTION_DOWNLOAD_FINISH};

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_media_scan);
        initView();
        downloadMediaResource();
    }

    @Override
    public void initializeData()
    {
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocBroadcast.getInstance().registerBroadcast(this, mAction);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocBroadcast.getInstance().unRegisterBroadcast(this, mAction);
    }

    private void initView()
    {
//        currentMediaMessage = (InstantMessage) getIntent().getSerializableExtra(UIConstants.MEDIA_RESOURCE);
//        if (currentMediaMessage.getMediaRes().getMediaType() == MediaResource.MEDIA_PICTURE)
        {
            pictureImageView = (ImageView) findViewById(R.id.media_picture_scan);
            pictureImageView.setVisibility(View.VISIBLE);
            isPicture = true;
        }
//        else if (currentMediaMessage.getMediaRes().getMediaType() == MediaResource.MEDIA_VIDEO)
        {
            videoView = (VideoView) findViewById(R.id.media_video_scan);
            isPicture = false;
        }
        progressTextView = (TextView) findViewById(R.id.media_scan_download_progress);
    }

    private void downloadMediaResource()
    {
        if (isPicture)
        {
//            String filePath = getPictureFilePath(currentMediaMessage);
//            if (isPictureDownload(filePath))
            {
//                String filepath = getPictureFilePath(currentMediaMessage);
//                Bitmap bitmap = HeadIconTools.getBitmapByPath(filepath);
//                pictureImageView.setImageBitmap(bitmap);
            }
//            else
            {
//                imMgr.downloadFile(currentMediaMessage, false);
            }
        }
        else
        {
//            String filePath = getVideoFilePath(currentMediaMessage);
//            if (isVideoDownload(filePath))
            {
                videoView.setVisibility(View.VISIBLE);
                videoView.setMediaController(new MediaController(this));
//                Uri videoUri = Uri.parse(filePath);
//                videoView.setVideoURI(videoUri);
                videoView.start();
            }
//            else
            {
//                boolean result = imMgr.downloadFile(currentMediaMessage, true);
//                videoFilePath = getVideoFilePath(currentMediaMessage);
//                Log.i("MediaScanActivity", "result = " + result);
            }
        }
    }

    /**
     * Is picture download boolean.
     *
     * @param filepath the filepath
     * @return the boolean
     */
    public boolean isPictureDownload(String filepath)
    {

        if (TextUtils.isEmpty(filepath))
        {
            Log.e("MediaScanActivity", "downloadFailed");
            return false;
        }

        File file = new File(filepath);
        if (file.exists())
        {
            return true;
        }
        return false;
    }

//    private String getPictureFilePath(InstantMessage instantMsg)
//    {
//        MediaResource mediaRes = instantMsg.getMediaRes();
//        fileName = mediaRes.getName();
//        if (!TextUtils.isEmpty(fileName))
//        {
//            String filepath = ChatTools.APP_PATH + File.separator + "Img" + File.separator + fileName;
//            return filepath;
//        }
//        return null;
//    }

    private boolean isVideoDownload(String filePath)
    {
        if (TextUtils.isEmpty(filePath))
        {
            Log.e("ChatAdapter", "downloadFailed");
            return false;
        }

        File file = new File(filePath);
        if (file.exists())
        {
            return true;
        }
        return false;
    }

//    private String getVideoFilePath(InstantMessage instantMsg)
//    {
//        MediaResource mediaRes = instantMsg.getMediaRes();
//        fileName = mediaRes.getName();
//        if (!TextUtils.isEmpty(fileName))
//        {
//            String filePath = ChatTools.APP_PATH + File.separator + "Video" + File.separator + fileName;
//            return filePath;
//        }
//        return null;
//    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SCAN_MEDIA:
                    downloadMediaResource();
                    break;
                case UPDATE_DOWNLOAD_PROGRESS:
                    int progress = msg.arg1;
                    progressTextView.setText(String.valueOf(progress) + "%");

                    if (progress == 100)
                    {
//                        String videoFilePath = getVideoFilePath(currentMediaMessage);
//                        Bitmap bitmap = ChatTools.getVideoThumbnail(videoFilePath);
//                        ChatTools.setVideoThumbnailMap(currentMediaMessage.getMessageId(), bitmap);
                        progressTextView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v)
    {

    }

    @Override
    public void onReceive(String broadcastName, Object obj)
    {
        if (CustomBroadcastConstants.ACTION_DOWNLOAD_PROCESS.equals(broadcastName))
        {
            UmTransProgressData progressData = (UmTransProgressData) obj;
            int progress = progressData.getProgress();
            Message msg = new Message();
            msg.arg1 = progress;
            msg.what = UPDATE_DOWNLOAD_PROGRESS;
            handler.sendMessage(msg);
        }
        else if ( CustomBroadcastConstants.ACTION_DOWNLOAD_FINISH.equals(broadcastName))
        {
            handler.sendEmptyMessage(SCAN_MEDIA);
        }
    }
}
