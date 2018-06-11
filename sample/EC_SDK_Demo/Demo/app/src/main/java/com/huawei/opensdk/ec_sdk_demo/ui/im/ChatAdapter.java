package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huawei.data.entity.InstantMessage;
import com.huawei.data.unifiedmessage.MediaResource;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.logic.im.ChatTools;
import com.huawei.opensdk.ec_sdk_demo.logic.im.FileTypeLogic;
import com.huawei.opensdk.ec_sdk_demo.logic.im.MessageItemType;
import com.huawei.opensdk.ec_sdk_demo.ui.im.contact.HeadIconTools;
import com.huawei.opensdk.ec_sdk_demo.util.FileUtil;
import com.huawei.opensdk.ec_sdk_demo.widget.CircleProgressBar;
import com.huawei.opensdk.imservice.ImMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.utils.DateUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is about chat adapter.
 */
public class ChatAdapter extends BaseAdapter
{
    private final Context mContext;
    private final String mMyAccount;
    private List<MessageItemType> mMessages = new ArrayList<>();
    private LayoutInflater mInflater;

    private Map<String, MessageItemType> itemMap = new HashMap<>();
    private static final long ONE_SECOND = 1000L;
    private boolean audioPlaying = false;
    private int audioHandle = -1;

    public ChatAdapter(Context context)
    {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mMyAccount = LoginMgr.getInstance().getAccount();
    }

    @Override
    public int getCount()
    {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final MessageItemType item = mMessages.get(position);
        final MessageItemType.ItemType type = getItemViewType(item);
        convertView = getViewByType(type);
        if (convertView != null)
        {
            loadViewByType(convertView, type, item);
        }
        return convertView;
    }

    public void setMessages(List<MessageItemType> messages)
    {
        this.mMessages = messages;
    }

    public void clearMessages()
    {
        mMessages.clear();
    }

    private View getViewByType(MessageItemType.ItemType typeCode)
    {
        View view = null;
        BaseMessageHolder viewHolder = null;

        switch (typeCode)
        {
            case MsgSendText:
                view = mInflater.inflate(R.layout.chat_item_send_text, null);
                viewHolder = getTextHolder(view);
                break;
            case MsgReceiveText:
                view = mInflater.inflate(R.layout.chat_item_recv_text, null);
                viewHolder = getTextHolder(view);
                break;
            case MsgReceivePic:
                view = mInflater.inflate(R.layout.chat_item_recv_pic, null);
                viewHolder = getPictureHolder(view);
                break;
            case MsgSendPic:
                view = mInflater.inflate(R.layout.chat_item_send_pic, null);
                viewHolder = getPictureHolder(view);
                break;
            case MsgSendAudio:
                view = mInflater.inflate(R.layout.chat_item_send_audio, null);
                viewHolder = getAudioHolder(view);
                break;
            case MsgReceiveAudio:
                view = mInflater.inflate(R.layout.chat_item_recv_audio, null);
                viewHolder = getAudioHolder(view);
                break;
            case MsgSendVideo:
                view = mInflater.inflate(R.layout.chat_item_send_video, null);
                viewHolder = getVideoHolder(view);
                break;
            case MsgReceiveVideo:
                view = mInflater.inflate(R.layout.chat_item_recv_video, null);
                viewHolder = getVideoHolder(view);
                break;
            case MsgSendFile:
                view = mInflater.inflate(R.layout.chat_item_send_file, null);
                viewHolder = getFileHolder(view);
                break;
            case MsgReceiveFile:
                view = mInflater.inflate(R.layout.chat_item_recv_file, null);
                viewHolder = getFileHolder(view);
                break;
            default:
                break;
        }
        if (viewHolder == null)
        {
            viewHolder = new BaseMessageHolder();
        }
        if (view != null)
        {
            view.setTag(viewHolder);
        }
        return view;
    }

    private TextMessageHolder getTextHolder(View view)
    {
        TextMessageHolder holder = new TextMessageHolder();
        modifyBaseMsgHolder(holder, view);
        holder.contentText = (TextView) view.findViewById(R.id.chat_content);
        return holder;
    }

    private PicMessageHolder getPictureHolder(View view)
    {
        PicMessageHolder holder = new PicMessageHolder();
        modifyBaseMsgHolder(holder, view);
        holder.contentImage = (ImageView) view.findViewById(R.id.image_content);
        holder.progress = (TextView) view.findViewById(R.id.trans_progress);
        return holder;
    }

    private BaseMessageHolder getVideoHolder(View view)
    {
        VideoMessageHolder holder = new VideoMessageHolder();
        modifyBaseMsgHolder(holder, view);
        holder.contentImage = (ImageView) view.findViewById(R.id.image_content);
        holder.videoTimeLength = (TextView) view.findViewById(R.id.item_video_time);
        holder.progress = (TextView) view.findViewById(R.id.video_trans_progress);
        holder.progressBar = (CircleProgressBar) view.findViewById(R.id.upload_progressbar);
        Log.e(UIConstants.DEMO_TAG, "holder.progress init id = " + holder.progress);
        return holder;
    }

    private UnknownMessageHolder getUnknownHolder(View view)
    {
        UnknownMessageHolder holder = new UnknownMessageHolder();
        modifyBaseMsgHolder(holder, view);
        return holder;
    }

    private FileMessageHolder getFileHolder(View view)
    {
        FileMessageHolder holder = new FileMessageHolder();
        modifyBaseMsgHolder(holder, view);
        holder.fileImage = (ImageView) view.findViewById(R.id.image_file);
        holder.fileName = (TextView) view.findViewById(R.id.file_name_tv);
        holder.fileSize = (TextView) view.findViewById(R.id.file_size_tv);
        holder.clickLayout = (RelativeLayout) view.findViewById(R.id.file_layout);
        return holder;
    }

    private BaseMessageHolder getAudioHolder(View view)
    {
        AudioMessageHolder holder = new AudioMessageHolder();
        modifyBaseMsgHolder(holder, view);
        holder.audioTimeLength = (TextView) view.findViewById(R.id.item_audio_time);
        holder.playImage = (ImageView) view.findViewById(R.id.chat_audio_image);
        holder.clickLayout = view.findViewById(R.id.rl_chat_audio_play);
        return holder;
    }

    private void modifyBaseMsgHolder(BaseMessageHolder baseHolder, View view)
    {
        baseHolder.chatNameText = (TextView) view.findViewById(R.id.chatter_name);
        baseHolder.chatTimeText = (TextView) view.findViewById(R.id.chat_sendtime);
        baseHolder.chatIconImageView = (ImageView) view.findViewById(R.id.chat_contact_head);
    }

    /**
     * Gets item view type.
     * @param item the item
     * @return the item view type
     */
    public MessageItemType.ItemType getItemViewType(MessageItemType item)
    {
        boolean isSender = item.instantMsg.getFromId().equals(mMyAccount);
        boolean isIm = (item.instantMsg.getMediaRes() == null);

        if (isIm)
        {
            return isSender ? MessageItemType.ItemType.MsgSendText : MessageItemType.ItemType.MsgReceiveText;
        }
        else
        {
            int mediaType = item.instantMsg.getMediaRes().getMediaType();
            switch (mediaType)
            {
                case MediaResource.MEDIA_PICTURE:
                    return isSender ? MessageItemType.ItemType.MsgSendPic : MessageItemType.ItemType.MsgReceivePic;
                case MediaResource.MEDIA_AUDIO:
                    return isSender ? MessageItemType.ItemType.MsgSendAudio : MessageItemType.ItemType.MsgReceiveAudio;
                case MediaResource.MEDIA_VIDEO:
                    return isSender ? MessageItemType.ItemType.MsgSendVideo : MessageItemType.ItemType.MsgReceiveVideo;
                case MediaResource.MEDIA_FILE:
                    return isSender ? MessageItemType.ItemType.MsgSendFile : MessageItemType.ItemType.MsgReceiveFile;
            }
        }
        return null;
    }

    private void loadViewByType(View view, MessageItemType.ItemType itemType, MessageItemType item)
    {
        BaseMessageHolder viewHolder = (BaseMessageHolder) view.getTag();
        loadCommonData(item, viewHolder);
        switch (itemType)
        {
            case MsgSendText:
            case MsgReceiveText:
                if (viewHolder instanceof TextMessageHolder)
                {
                    loadText(item, (TextMessageHolder) viewHolder);
                }
                break;
            case MsgSendPic:
            case MsgReceivePic:
                if (viewHolder instanceof PicMessageHolder)
                {
                    loadPicture(item, (PicMessageHolder) viewHolder);
                    saveDateToItemMap(item);
                }
                break;
            case MsgSendAudio:
            case MsgReceiveAudio:
                if (viewHolder instanceof AudioMessageHolder)
                {
                    loadAudio(item, (AudioMessageHolder) viewHolder);
                }
                break;
            case MsgSendVideo:
            case MsgReceiveVideo:
                if (viewHolder instanceof VideoMessageHolder)
                {
                    VideoMessageHolder holder = (VideoMessageHolder) viewHolder;
//                    showVideoProgress(item, holder);
                    saveDateToItemMap(item);
                    loadVideo(item, holder);
                }
                break;
            case MsgSendFile:
            case MsgReceiveFile:
                if (viewHolder instanceof FileMessageHolder)
                {
                    FileMessageHolder holder = (FileMessageHolder) viewHolder;
                    loadFile(item, holder);
                    saveDateToItemMap(item);
                }
                break;
            default:
                break;
        }
    }

    private void loadText(MessageItemType item, TextMessageHolder holder)
    {
        CharSequence charSequence = item.content;
        if (!TextUtils.isEmpty(charSequence))
        {
            holder.contentText.setText(charSequence);
        }
        else
        {
            Log.e("ChatAdapter", "The message content is empty.");
        }
    }

    private void loadPicture(MessageItemType item, PicMessageHolder picMessageHolder)
    {
        InstantMessage instantMsg = item.instantMsg;
        if (instantMsg == null || instantMsg.getMediaRes() == null)
        {
            return;
        }
        String filepath = getPicThumbNailPath(instantMsg);
        if (isFileDownload(filepath))
        {
            Bitmap bitmap = HeadIconTools.getBitmapByPath(filepath);
            picMessageHolder.contentImage.setImageBitmap(bitmap);
        }
        else
        {
            boolean result = ImMgr.getInstance().downloadFile(instantMsg, true);
            Log.i("ChatAdapter", "result = " + result);
        }
    }

    private void loadAudio(MessageItemType item, final AudioMessageHolder audioMessageHolder)
    {
        InstantMessage instantMsg = item.instantMsg;
        String audioFilepath = "";
        if (instantMsg == null || instantMsg.getMediaRes() == null)
        {
            return;
        }
        String filepath = getAudioFilePath(instantMsg);
        if (isFileDownload(filepath))
        {
            audioFilepath = getAudioFilePath(instantMsg);
        }
        else
        {
            if (!instantMsg.getContent().contains("LOCAL"))
            {
                boolean result = ImMgr.getInstance().downloadFile(instantMsg, false);
                LogUtil.i(UIConstants.DEMO_TAG, "result = " + result);
            }
        }
        MediaResource mediaResource = item.instantMsg.getMediaRes();
        audioMessageHolder.audioTimeLength.setText(DateUtil.getTime(mediaResource.getDuration()));
        final String finalAudioFilepath = audioFilepath;
        audioMessageHolder.clickLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                audioLayoutClick(audioMessageHolder, finalAudioFilepath);
            }
        });
    }

    private void audioLayoutClick(AudioMessageHolder holder, String audioPath)
    {
        if (!TextUtils.isEmpty(audioPath))
        {
            if (audioPlaying)
            {
                //if audio is playing, stop it
                boolean result = audioPlayEnd();
            }
            else
            {
                audioHandle = ImMgr.getInstance().startPlay(audioPath, 1);
                audioPlaying = true;
            }
        }
        if (audioPlaying)
        {
            holder.playImage.setImageResource(R.drawable.right_audio_play_selector);
            final AnimationDrawable ad = (AnimationDrawable) holder.playImage.getDrawable();
            ad.start();
        }
    }

    private void fileLayoutClick(FileMessageHolder holder, String filePath)
    {
        if (!TextUtils.isEmpty(filePath))
        {
            FileUtil.openBySystem(mContext, filePath);
        }
    }

    /**
     * Audio play end.
     * @return the boolean
     */
    public boolean audioPlayEnd()
    {
        if (audioHandle != -1)
        {
            int result = ImMgr.getInstance().stopPlay(audioHandle);
            if (result == 0)
            {
                audioHandle = -1;
                audioPlaying = false;
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    private void loadVideo(MessageItemType item, VideoMessageHolder videoMessageHolder)
    {
        InstantMessage instantMsg = item.instantMsg;
        if (instantMsg == null || instantMsg.getMediaRes() == null)
        {
            return;
        }

        MediaResource mediaRes = instantMsg.getMediaRes();
        long duration = mediaRes.getDuration() * ONE_SECOND;
        videoMessageHolder.videoTimeLength.setText(DateUtil.formatMillisInterval(duration, DateUtil.FMT_MS));
        String filepath = getVideoFilePath(instantMsg);

        if (isFileDownload(filepath))
        {
            //video Thumbnail
            Bitmap bitmap;
            bitmap = ChatTools.getVideoThumbnailMap(instantMsg.getMessageId());
            if (bitmap == null)
            {
                bitmap = getVideoThumbnail(filepath);
                ChatTools.setVideoThumbnailMap(instantMsg.getMessageId(), bitmap);
            }
            videoMessageHolder.contentImage.setImageBitmap(bitmap);
        }
        int progress = item.progress;
        if (progress > 0 && progress < 100)
        {
            videoMessageHolder.progressBar.setVisibility(View.VISIBLE);
            videoMessageHolder.progressBar.setProgress(progress);
        }
        if (progress == 100)
        {
            Bitmap bitmap = getVideoThumbnail(filepath);
            ChatTools.setVideoThumbnailMap(instantMsg.getMessageId(), bitmap);
        }
    }

    private void loadFile(MessageItemType item, final FileMessageHolder fileMessageHolder)
    {
        InstantMessage instantMsg = item.instantMsg;
        if (instantMsg == null || instantMsg.getMediaRes() == null)
        {
            return;
        }
        MediaResource mediaResource = item.instantMsg.getMediaRes();
        final int size = mediaResource.getSize();
        fileMessageHolder.fileSize.setText(FileUtil.makeUpSizeShow(size));
        fileMessageHolder.fileName.setText(mediaResource.getName());
        fileMessageHolder.fileImage.setImageResource(FileTypeLogic.getLogoIdByType(mediaResource.getName()));

        final String filepath = getFilePath(instantMsg);
        if (!isFileDownload(filepath))
        {
            boolean result = ImMgr.getInstance().downloadFile(instantMsg, false);
            LogUtil.i(UIConstants.DEMO_TAG, "result = " + result);
        }
        fileMessageHolder.clickLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fileLayoutClick(fileMessageHolder, filepath);
            }
        });

    }

    private Bitmap getVideoThumbnail(String videoPath)
    {
        Bitmap bitmap = null;
        try
        {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(videoPath);
            bitmap = media.getFrameAtTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void loadCommonData(MessageItemType item, BaseMessageHolder holder)
    {
        String contactName = item.instantMsg.getFromId();
        holder.chatNameText.setText(contactName);

        Bitmap headIcon = HeadIconTools.getInstance().getHeadImage(contactName);
        if (headIcon != null)
        {
            holder.chatIconImageView.setImageBitmap(headIcon);
        }
        else
        {
            holder.chatIconImageView.setImageResource(R.drawable.default_head);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern(DateUtil.FMT_YMDHM);
        String time = dateFormat.format(item.instantMsg.getTimestamp());
        holder.chatTimeText.setText(time);
    }

    private String getPicThumbNailPath(InstantMessage instantMsg)
    {
        MediaResource mediaRes = instantMsg.getMediaRes();
        String picName = mediaRes.getName();
        String[] pic = picName.split("\\.");
        String fileThumbNailName = pic[0] + "_s." + pic[1];
        if (!TextUtils.isEmpty(fileThumbNailName))
        {
            String filepath = ChatTools.APP_PATH + File.separator + "Img" + File.separator + fileThumbNailName;
            return filepath;
        }
        return null;
    }

    private String getAudioFilePath(InstantMessage instantMsg)
    {
        MediaResource mediaRes = instantMsg.getMediaRes();
        String fileName = mediaRes.getName();

        if (!TextUtils.isEmpty(fileName))
        {
            String filepath = ChatTools.APP_PATH + File.separator + "Audio" + File.separator + fileName;
            return filepath;
        }
        return null;
    }

    private String getFilePath(InstantMessage instantMsg)
    {
        MediaResource mediaRes = instantMsg.getMediaRes();
        String fileName = mediaRes.getName();

        if (!TextUtils.isEmpty(fileName))
        {
            String filepath = ChatTools.APP_PATH + File.separator + "File" + File.separator +
                    LoginMgr.getInstance().getAccount() + File.separator + fileName;
            return filepath;
        }
        return null;
    }

    private String getVideoFilePath(InstantMessage instantMsg)
    {
        MediaResource mediaRes = instantMsg.getMediaRes();
        String fileName = mediaRes.getName();
        if (!TextUtils.isEmpty(fileName))
        {
            return ChatTools.APP_PATH + File.separator + "Video" + File.separator + fileName;
        }
        return null;
    }

    private void saveDateToItemMap(MessageItemType item)
    {
        InstantMessage instantMessage = item.instantMsg;
        MediaResource resource = instantMessage.getMediaRes();
        boolean exist = itemMap.get(getKey(instantMessage.getId(), resource.getMediaId())) != null;
        if (!exist)
        {
            itemMap.put(getKey(instantMessage.getId(), resource.getMediaId()), item);
        }
    }

    /**
     * Gets key.
     * @param msgId the msg id
     * @param mediaId the media id
     * @return the key
     */
    private String getKey(long msgId, int mediaId)
    {
        return String.valueOf(msgId) + String.valueOf(mediaId);
    }

    private boolean isFileDownload(String filepath)
    {
        if (TextUtils.isEmpty(filepath))
        {
            Log.e("ChatAdapter", "downloadFailed");
            return false;
        }

        File file = new File(filepath);
        if (file.exists())
        {
            return true;
        }
        return false;
    }

    /**
     * The type Base message holder.
     */
    public static class BaseMessageHolder
    {
        /**
         * The Chat name text.
         */
        public TextView chatNameText;

        /**
         * The Chat time text.
         */
        public TextView chatTimeText;

        /**
         * The Chat icon image view.
         */
        public ImageView chatIconImageView;
    }

    /**
     * The type Text message holder.
     */
    public static class TextMessageHolder extends BaseMessageHolder
    {
        /**
         * The Content text.
         */
        public TextView contentText;
    }

    /**
     * 图片
     */
    public static class PicMessageHolder extends BaseMessageHolder
    {
        /**
         * The Content image.
         */
        public ImageView contentImage;

        /**
         * The Progress.
         */
        public TextView progress;
    }

    /**
     * The type Audio message holder.
     */
    public static class AudioMessageHolder extends BaseMessageHolder
    {
        /**
         * The Click layout.
         */
        public View clickLayout;
        /**
         * The Play image.
         */
        public ImageView playImage;
        /**
         * The Audio time length.
         */
        public TextView audioTimeLength;
    }

    /**
     * The type Video message holder.
     */
    public static class VideoMessageHolder extends BaseMessageHolder
    {
        /**
         * The Content image.
         */
        public ImageView contentImage;

        /**
         * The Video time length.
         */
        public TextView videoTimeLength;

        /**
         * The Progress.
         */
        public TextView progress;

        /**
         * The Progress bar.
         */
        public CircleProgressBar progressBar;
    }

    /**
     * The type File message holder.
     */
    public static class FileMessageHolder extends BaseMessageHolder
    {
        /**
         * The Content image.
         */
        public ImageView fileImage;

        /**
         * The Video time length.
         */
        public TextView fileName;

        /**
         * The Progress.
         */
        public TextView fileSize;

        public RelativeLayout clickLayout;
    }

    /**
     * The type unknown message holder.
     */
    public static class UnknownMessageHolder extends BaseMessageHolder
    {
    }
}
