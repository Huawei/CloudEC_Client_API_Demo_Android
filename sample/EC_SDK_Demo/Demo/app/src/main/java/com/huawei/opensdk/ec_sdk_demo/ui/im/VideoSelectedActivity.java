package com.huawei.opensdk.ec_sdk_demo.ui.im;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is about video selected activity.
 */
public class VideoSelectedActivity extends BaseActivity
{
    private List<VideoInfo> videoInfos = new ArrayList<>();
    private static final int LOAD_SYSTEM_VIDEO = 0;
    private Cursor cursor;
    private GridView videoGridView;
    private VideoAdapter videoAdapter;
    private String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
            MediaStore.Video.Thumbnails.VIDEO_ID};
    private String[] mediaColumns = {MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE};

    @Override
    public void initializeComposition()
    {
        setContentView(R.layout.activity_video_select_send);
        initView();
    }

    @Override
    public void initializeData()
    {
        queryVideoList();
    }

    private void initView()
    {

        videoGridView = (GridView) findViewById(R.id.system_video);
        videoAdapter = new VideoAdapter(videoInfos);
        videoGridView.setAdapter(videoAdapter);

        videoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String sizeStr = videoInfos.get(position).getSize();
                int size = Integer.parseInt(sizeStr) / (1024 * 1024);
                if (size > 20)
                {
                    Toast.makeText(VideoSelectedActivity.this, "please select video inside 20M", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(UIConstants.VIDEO_SYSTEM_PATH, videoInfos.get(position).getPath());
                intent.putExtra(UIConstants.BUNDLE, bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private void queryVideoList()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
                if (cursor == null)
                {
                    Toast.makeText(VideoSelectedActivity.this, "did't find video files", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cursor.moveToFirst())
                {
                    do
                    {
                        VideoInfo info = new VideoInfo();
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                        Cursor thumbCursor = managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);
                        if (thumbCursor.moveToFirst())
                        {
                            info.setPath(thumbCursor.getString(thumbCursor
                                    .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                        }
                        info.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                        info.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                        info.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                        info.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                        info.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));

                        videoInfos.add(info);
                    } while (cursor.moveToNext());
                }
                handler.sendEmptyMessage(LOAD_SYSTEM_VIDEO);
                LogUtil.i(UIConstants.DEMO_TAG, "videoInfos = " + videoInfos.size());
            }
        }).start();
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case LOAD_SYSTEM_VIDEO:
                    videoAdapter.notifyDataChanged(videoInfos);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * The type Video adapter.
     */
    class VideoAdapter extends BaseAdapter
    {
        private List<VideoInfo> infos = new ArrayList<>();

        /**
         * Instantiates a new Video adapter.
         *
         * @param videoInfos the video infos
         */
        public VideoAdapter(List<VideoInfo> videoInfos)
        {
            infos = videoInfos;
        }

        /**
         * Notify data changed.
         *
         * @param videoInfos the video infos
         */
        public void notifyDataChanged(List<VideoInfo> videoInfos)
        {
            infos = videoInfos;
            notifyDataSetChanged();
        }

        @Override
        public int getCount()
        {
            return infos.size();
        }

        @Override
        public Object getItem(int position)
        {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            VideoInfo videoInfo = infos.get(position);
            convertView = LayoutInflater.from(VideoSelectedActivity.this).inflate(R.layout.gridview_system_video, null);
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.system_video_thumbnail);
            TextView videoName = (TextView) convertView.findViewById(R.id.system_video_name);

            int size = Integer.parseInt(videoInfo.getSize()) / (1024 * 1024);

            Bitmap bitmap = getVideoThumbnail(videoInfo.getPath());
            thumbnail.setImageBitmap(bitmap);
            videoName.setText(videoInfo.getDisplayName() + "(" + size + "M" + ")");
            return convertView;
        }
    }

    private Bitmap getVideoThumbnail(String videoPath)
    {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }

    /**
     * The type Video info.
     */
    public static class VideoInfo
    {
        private String path;
        private String title;
        private String displayName;
        private String mimeType;
        private String size;

        /**
         * Gets mime type.
         *
         * @return the mime type
         */
        public String getMimeType()
        {
            return mimeType;
        }

        /**
         * Sets mime type.
         *
         * @param mimeType the mime type
         */
        public void setMimeType(String mimeType)
        {
            this.mimeType = mimeType;
        }

        /**
         * Gets path.
         *
         * @return the path
         */
        public String getPath()
        {
            return path;
        }

        /**
         * Sets path.
         *
         * @param path the path
         */
        public void setPath(String path)
        {
            this.path = path;
        }

        /**
         * Gets title.
         *
         * @return the title
         */
        public String getTitle()
        {
            return title;
        }

        /**
         * Sets title.
         *
         * @param title the title
         */
        public void setTitle(String title)
        {
            this.title = title;
        }

        /**
         * Gets display name.
         *
         * @return the display name
         */
        public String getDisplayName()
        {
            return displayName;
        }

        /**
         * Sets display name.
         *
         * @param displayName the display name
         */
        public void setDisplayName(String displayName)
        {
            this.displayName = displayName;
        }

        /**
         * Gets size.
         *
         * @return the size
         */
        public String getSize()
        {
            return size;
        }

        /**
         * Sets size.
         *
         * @param size the size
         */
        public void setSize(String size)
        {
            this.size = size;
        }
    }
}
