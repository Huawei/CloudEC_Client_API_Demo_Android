package com.huawei.opensdk.callmgr;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.Handler;
import android.view.OrientationEventListener;
import android.view.SurfaceView;

import com.huawei.ecterminalsdk.base.TsdkDeviceInfo;
import com.huawei.ecterminalsdk.base.TsdkDeviceType;
import com.huawei.ecterminalsdk.base.TsdkSvcVideoWndInfo;
import com.huawei.ecterminalsdk.base.TsdkVideoCtrlInfo;
import com.huawei.ecterminalsdk.base.TsdkVideoOrient;
import com.huawei.ecterminalsdk.base.TsdkVideoRenderInfo;
import com.huawei.ecterminalsdk.base.TsdkVideoWndDisplayMode;
import com.huawei.ecterminalsdk.base.TsdkVideoWndInfo;
import com.huawei.ecterminalsdk.base.TsdkVideoWndMirrorType;
import com.huawei.ecterminalsdk.base.TsdkVideoWndType;
import com.huawei.ecterminalsdk.models.TsdkManager;
import com.huawei.ecterminalsdk.models.call.TsdkCall;
import com.huawei.ecterminalsdk.models.call.TsdkCallManager;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.videoengine.ViERenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is about video management
 * 视频管理类
 */
public class VideoMgr {
    private static final String TAG = VideoMgr.class.getSimpleName();
    private static VideoMgr instance;
    private Context context;
    private Handler handler;

    private TsdkCallManager callManager;

    List<TsdkDeviceInfo> cameraList;

    private int currentCameraIndex = CallConstant.FRONT_CAMERA;
    private long currentCallId;

    private boolean isInitializedVideoWindows;

    private List<Long> currentSvcLabel;

    /**
     * 本地隐藏窗口（只能创建一个）
     */
    private SurfaceView localHideView;
    /**
     * 本地窗口（只能创建一个）
     */
    private SurfaceView localVideoView;
    /**
     * 远端窗口（可以创建多个）
     */
    private SurfaceView remoteBigVideoView;

    private SurfaceView remoteSmallVideoView_01;

    private SurfaceView remoteSmallVideoView_02;

    private SurfaceView remoteSmallVideoView_03;
    /**
     * 辅流窗口（只能创建一个，创建方法和远端窗口一致）
     */
    private SurfaceView auxDataView;

    private OrientationDetector orientationDetector;

    private static final String BMP_FILE = "CameraBlack.BMP";

    public static final int LAYOUT_PORTRAIT = 1;

    public static final int LAYOUT_LANDSCAPE = 2;

    public VideoMgr() {
        context = LocContext.getContext();
        if (context == null) {
            throw new NullPointerException("BaseApp not initialized.");
        }

        handler = new Handler(context.getMainLooper());

        callManager = TsdkManager.getInstance().getCallManager();

        cameraList = callManager.getDevices(TsdkDeviceType.TSDK_E_DEVICE_CAMERA);
    }

    public static VideoMgr getInstance() {
        if (instance == null) {
            instance= new VideoMgr();
        }
        return instance;
    }

    /**
     * 创建视频Renderer
     * Create video renderer
     */
    private void createVideoRenderer(boolean isSvcConf)
    {
        LogUtil.i(TAG, "createVideoRenderer() enter");

        // 创建本地视频窗口（本地窗口只能创建一个，底层可以直接获取到这个窗口）
        // 必须存在，否则远端视频无法显示
        if (localHideView == null) {
            localHideView = callManager.createLocalRenderer(context);
            localHideView.setZOrderOnTop(false);
        }

        // 本端窗口显示
        if (localVideoView == null) {
            localVideoView = callManager.createRemoteRenderer(context);
            localVideoView.setZOrderMediaOverlay(true);
        }

        // 创建远端视频窗口（可以创建多个）
        if (remoteBigVideoView == null) {
            remoteBigVideoView = callManager.createRemoteRenderer(context);
            remoteBigVideoView.setZOrderMediaOverlay(false);
        }

        if (isSvcConf) {
            if (remoteSmallVideoView_01 == null) {
                remoteSmallVideoView_01 = callManager.createRemoteRenderer(context);
                remoteSmallVideoView_01.setZOrderMediaOverlay(true);
            }

            if (remoteSmallVideoView_02 == null) {
                remoteSmallVideoView_02 = callManager.createRemoteRenderer(context);
                remoteSmallVideoView_02.setZOrderMediaOverlay(true);
            }

            if (remoteSmallVideoView_03 == null) {
                remoteSmallVideoView_03 = callManager.createRemoteRenderer(context);
                remoteSmallVideoView_03.setZOrderMediaOverlay(true);
            }
        }
    }


    /**
     * switch camera
     * 切换摄像头
     * @param call              呼叫信息
     * @param cameraIndex       设备下标
     * @return                  设置结果
     */
    public int switchCamera(TsdkCall call, int cameraIndex)
    {
        return setVideoOrient(call.getCallInfo().getCallId(), cameraIndex);
    }

    /**
     * open camera
     * 打开摄像头
     * @param call              呼叫信息
     * @return                  结果
     */
    public int openCamera(TsdkCall call) {
        return controlLocalCameraMode1(call, true);
    }

    /**
     * close camera
     * 关闭摄像头
     * @param call              呼叫信息
     * @return                  结果
     */
    public int closeCamera(TsdkCall call) {
        return controlLocalCameraMode1(call, false);
    }

    /**
     * Local video capture control
     * 本地视频采集控制
     *
     * @param call              呼叫信息
     * @param isOpen            是否打开摄像头
     * @return
     */
    private int controlLocalCameraMode1(TsdkCall call, boolean isOpen) {
        int result = 0;

        if (null == call) {
            return -1;
        }

        if (isOpen) {
            //重新设置摄像头采集角度
            result = call.setCaptureRotation(CallConstant.FRONT_CAMERA, 0);
            if (result != 0) {
                LogUtil.e(TAG, "setCaptureRotation is failed, result -->" + result);
            }
            else {
                setCurrentCameraIndex(CallConstant.FRONT_CAMERA);
            }
        } else {
            //采用发送默认图版本方式，替代关闭摄相头动作
            String picturePath = Environment.getExternalStorageDirectory() + File.separator + BMP_FILE;
            result = call.setCameraPicture( picturePath);
            if (result != 0) {
                LogUtil.e(TAG, "setVideoCaptureFile is failed, result -->" + result);
            }
            setCurrentCameraIndex(CallConstant.CAMERA_NON);
        }
        return result;
    }

    private int controlLocalCameraMode2(TsdkCall call, boolean isOpen) {
        int result;

        /**
         * operation, value :open 0x01，close 0x02，start 0x04，stop 0x08, value can be linked by "|"
         * 操作，取值: open 0x01，close 0x02，start 0x04，stop 0x08，可以使用逻辑运算符"|"连接，open|start，close|stop
         */
        int operation;

        /**
         * module,value:0x01 display remote window,0x02 display local window,0x04 video,0x08 coder,0x10 decoder
         * 模式，取值: 0x01显示远端窗口 0x02显示本端窗口 0x04摄相头 0x08编码器  0x10解码器
         */
        int module;

        if (isOpen) {
            module = 0x02 | 0x04;
            operation = 0x04;

            TsdkVideoCtrlInfo tsdkVideoCtrlInfo = new TsdkVideoCtrlInfo(0, operation, module);
            result = call.videoControl(tsdkVideoCtrlInfo);
            if (result != 0) {
                LogUtil.e(TAG, "videoControl is failed, result --> " + result);
                return result;
            }
        } else {
            module = 0x02 | 0x04;
            operation = 0x08;

            TsdkVideoCtrlInfo tsdkVideoCtrlInfo = new TsdkVideoCtrlInfo(0, operation, module);
            result = call.videoControl(tsdkVideoCtrlInfo);
            if (result != 0) {
                LogUtil.e(TAG, "videoControl is failed, result --> " + result);
                return result;
            }

            result = setVideoOrient(call.getCallInfo().getCallId(), CallConstant.FRONT_CAMERA);
            if (result != 0) {
                LogUtil.e(TAG, "setVideoOrient is failed, result --> " + result);
                return result;
            }
        }

        return 0;
    }

    /**
     * 设置视频窗口方向
     * @param callId            0表示全局设置,不为0表示 会话中设置
     * @param cameraIndex       摄像头index
     * @return int result       视频角度
     */
    public int setVideoOrient(long callId, int cameraIndex)
    {
        int result = 0;
        int orient;
        int portrait;
        int landscape;
        int seascape;

        Configuration configuration = LocContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            orient = 1;
        } else {
            orient = 2;
        }

        if (cameraIndex == CallConstant.FRONT_CAMERA) {
            portrait = 3;
            landscape = 0;
            seascape = 2;
        } else if (cameraIndex == CallConstant.BACK_CAMERA) {
            portrait = 1;
            landscape = 0;
            seascape = 2;
        } else {
            return -1;
        }

        /**
         * 横竖屏信息stOrient 设置标志位
         * @param int callId    0表示全局设置,不为0表示 会话中设置
         * @param int index     摄像头index
         *
         * @param int orient    视频横竖屏情况 1：竖屏；2：横屏；3：反向横屏
         * @param int portrait  竖屏视频捕获（逆时针旋转）角度 0：0度；1：90度；2：180度；3：270度；
         * @param int landscape 横屏视频捕获（逆时针旋转）角度 0：0度；1：90度；2：180度；3：270度；
         * @param int seascape 反向横屏视频捕获（逆时针旋转）角度 0：0度；1：90度；2：180度；3：270度；
         * @return int result  视频角度
         */
        TsdkVideoOrient videoOrient = new TsdkVideoOrient(portrait, seascape, landscape,orient);
        TsdkCall tsdkCall = callManager.getCallByCallId(callId);
        if (tsdkCall != null) {
            result = tsdkCall.setVideoOrient(cameraIndex, videoOrient);
            if (result != 0) {
                LogUtil.e(TAG, "set video orient is failed. result --> " + result);
            } else {
                setCurrentCameraIndex(cameraIndex);
            }
        }

        if (orientationDetector != null)
        {
            orientationDetector.updateRotation(true);
        }

        return result;
    }

    /**
     * Initializing the video window
     * 初始化视频窗口
     *
     * @param callId            呼叫id
     */
    public void initVideoWindow(final long callId)
    {
        LogUtil.i(TAG, "initVideoWindow() enter" + callId);


        handler.post(new Runnable() {
            @Override
            public void run() {

                if (isInitializedVideoWindows == false) {
                    createVideoRenderer(false);
                }
                isInitializedVideoWindows = true;

                setCurrentCallId(callId);

                //设置视频窗口方向参数
                setVideoOrient(callId, CallConstant.FRONT_CAMERA);

                // 设置本地视频窗口
                TsdkVideoWndInfo localWndInfo = new TsdkVideoWndInfo();
                localWndInfo.setVideoWndType(TsdkVideoWndType.TSDK_E_VIDEO_WND_LOCAL);
                localWndInfo.setRender(ViERenderer.getIndexOfSurface(localVideoView));
                localWndInfo.setDisplayMode(TsdkVideoWndDisplayMode.TSDK_E_VIDEO_WND_DISPLAY_CUT);

                //设置远端视频窗口
                TsdkVideoWndInfo remoteWndInfo = new TsdkVideoWndInfo();
                remoteWndInfo.setVideoWndType(TsdkVideoWndType.TSDK_E_VIDEO_WND_REMOTE);
                remoteWndInfo.setRender(ViERenderer.getIndexOfSurface(remoteBigVideoView));
                remoteWndInfo.setDisplayMode(TsdkVideoWndDisplayMode.TSDK_E_VIDEO_WND_DISPLAY_CUT);

                List<TsdkVideoWndInfo> list = new ArrayList<>();
                list.add(localWndInfo);
                list.add(remoteWndInfo);

                TsdkCall tsdkCall = callManager.getCallByCallId(callId);
                if (tsdkCall != null) {
                    tsdkCall.setVideoWindow(list);
                }
            }
        });

    }

    public void initSvcVideoWindow(final long callId, List<Long> svcLabel)
    {
        LogUtil.i(TAG, "initSvcVideoWindow() enter, callID: " + callId);
        currentSvcLabel = svcLabel;

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (isInitializedVideoWindows == false) {
                    createVideoRenderer(true);
                }
                else {
                    LogUtil.i(TAG, "p2p to conference.");
                    createVideoRenderer(true);
                }
                isInitializedVideoWindows = true;

                setCurrentCallId(callId);

                //设置视频窗口方向参数
                setVideoOrient(callId, CallConstant.FRONT_CAMERA);

                TsdkCall tsdkCall = callManager.getCallByCallId(callId);
                if (tsdkCall == null) {
                    return;
                }

                // 设置本地视频窗口
                TsdkVideoWndInfo localWndInfo = new TsdkVideoWndInfo();
                localWndInfo.setVideoWndType(TsdkVideoWndType.TSDK_E_VIDEO_WND_LOCAL);
                localWndInfo.setRender(ViERenderer.getIndexOfSurface(localVideoView));
                localWndInfo.setDisplayMode(TsdkVideoWndDisplayMode.TSDK_E_VIDEO_WND_DISPLAY_CUT);

                List<TsdkVideoWndInfo> list = new ArrayList<>();
                list.add(localWndInfo);

                tsdkCall.setVideoWindow(list);

                //设置SVC远端视频窗口
                TsdkSvcVideoWndInfo bigSvcVideoWndInfo = new TsdkSvcVideoWndInfo();
                bigSvcVideoWndInfo.setRender(ViERenderer.getIndexOfSurface(remoteBigVideoView));
                bigSvcVideoWndInfo.setLabel(currentSvcLabel.get(0));
                bigSvcVideoWndInfo.setWidth(960); // (960*540) usBandWidth[1300] (320*180) usBandWidth[195] (640*360) usBandWidth[620]
                bigSvcVideoWndInfo.setHeight(540);

                TsdkSvcVideoWndInfo smallSvcVideoWndInfo_01 = new TsdkSvcVideoWndInfo();
                smallSvcVideoWndInfo_01.setRender(ViERenderer.getIndexOfSurface(remoteSmallVideoView_01));
                smallSvcVideoWndInfo_01.setLabel(currentSvcLabel.get(1));
                smallSvcVideoWndInfo_01.setWidth(160); //320
                smallSvcVideoWndInfo_01.setHeight(90);//180

                TsdkSvcVideoWndInfo smallSvcVideoWndInfo_02 = new TsdkSvcVideoWndInfo();
                smallSvcVideoWndInfo_02.setRender(ViERenderer.getIndexOfSurface(remoteSmallVideoView_02));
                smallSvcVideoWndInfo_02.setLabel(currentSvcLabel.get(2));
                smallSvcVideoWndInfo_02.setWidth(160); //320
                smallSvcVideoWndInfo_02.setHeight(90);//180

                TsdkSvcVideoWndInfo smallSvcVideoWndInfo_03 = new TsdkSvcVideoWndInfo();
                smallSvcVideoWndInfo_03.setRender(ViERenderer.getIndexOfSurface(remoteSmallVideoView_03));
                smallSvcVideoWndInfo_03.setLabel(currentSvcLabel.get(3));
                smallSvcVideoWndInfo_03.setWidth(160); //320
                smallSvcVideoWndInfo_03.setHeight(90);//180

                List<TsdkSvcVideoWndInfo> svcWndInfoList = new ArrayList<>();
                svcWndInfoList.add(bigSvcVideoWndInfo);
                svcWndInfoList.add(smallSvcVideoWndInfo_01);
                svcWndInfoList.add(smallSvcVideoWndInfo_02);
                svcWndInfoList.add(smallSvcVideoWndInfo_03);

                tsdkCall.setSvcVideoWindow(svcWndInfoList);
            }
        });

    }

    /**
     * Clear data
     */
    public void clearCallVideo()
    {
        LogUtil.i(TAG, "clearCallVideo() enter");

        handler.post(new Runnable() {
            @Override
            public void run() {

            ViERenderer.freeLocalRenderResource();
            if (localVideoView != null) {
                ViERenderer.setSurfaceNull(localVideoView);
                localVideoView = null;
            }

            if (remoteBigVideoView != null) {
                ViERenderer.setSurfaceNull(remoteBigVideoView);
                remoteBigVideoView = null;
            }

            if (remoteSmallVideoView_01 != null) {
                ViERenderer.setSurfaceNull(remoteSmallVideoView_01);
                remoteSmallVideoView_01 = null;
            }

            if (remoteSmallVideoView_02 != null) {
                ViERenderer.setSurfaceNull(remoteSmallVideoView_02);
                remoteSmallVideoView_02 = null;
            }

            if (remoteSmallVideoView_03 != null) {
                ViERenderer.setSurfaceNull(remoteSmallVideoView_03);
                remoteSmallVideoView_03 = null;
            }

            if (auxDataView != null) {
                ViERenderer.setSurfaceNull(auxDataView);
                auxDataView = null;
            }

            if (localHideView != null) {
                localHideView = null;
            }

            isInitializedVideoWindows = false;
            }
        });
    }


    /**
     * Gets local hide view.
     *
     * @return the local hide view
     */
    public SurfaceView getLocalHideView() {
        return localHideView;
    }

    /**
     * Gets local call view.
     *
     * @return the local call view
     */
    public SurfaceView getLocalVideoView() {
        return localVideoView;
    }

    /**
     * Gets remote call view.
     *
     * @return the remote call view
     */
    public SurfaceView getRemoteBigVideoView() {
        return remoteBigVideoView;
    }

    public SurfaceView getRemoteSmallVideoView_01() {
        return remoteSmallVideoView_01;
    }

    public SurfaceView getRemoteSmallVideoView_02() {
        return remoteSmallVideoView_02;
    }

    public SurfaceView getRemoteSmallVideoView_03() {
        return remoteSmallVideoView_03;
    }


    public int getCurrentCameraIndex() {
        return currentCameraIndex;
    }

    public void setCurrentCameraIndex(int currentCameraIndex) {
        this.currentCameraIndex = currentCameraIndex;
    }

    public long getCurrentCallId() {
        return currentCallId;
    }

    public void setCurrentCallId(long currentCallId) {
        this.currentCallId = currentCallId;
    }

    /**
     * is support video calls
     * @return the boolean
     */
    public boolean isSupportVideo()
    {
        if (cameraList != null) {
            if (cameraList.size() > 0)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Set the video automatic rotation
     * 设置视频自动旋转
     * @param object 调用者对象
     * @param isOpen 是否开启方向调整
     */
    public void setAutoRotation(Object object, boolean isOpen, int layoutDirect) {
        if (orientationDetector == null) {
            orientationDetector = new OrientationDetector();
        }

        orientationDetector.setLayoutDirect(layoutDirect);

        orientationDetector.autoOrientationAdjust(object, isOpen);
    }

    /**
     * 视频角度探测类
     * 用于设备在旋转时，调整摄相头采集方向，以及视频窗口显示方向
     * 1.根据角度划4象限:竖向上【0-45、315-360】；横向右【45-135】；竖向下【135-225】；横向左【225-315】
     * 2.根据返回的页面布局方向分三种场景：横向布局、竖向布局、横向布局翻转
     * 3.根据摄像头情况分两种场景：前置摄像头、后置摄像头
     * 4.根据视频窗口情况分两种场景：远端窗口（对方看到自己的图像）、本端窗口（本端右下角小视频窗口）
     */
    private class OrientationDetector {
        /**
         * 未知角度，无象限所属
         */
        private static final int ORIENTATION_UNKNOWN = -1;

        /**
         * 竖屏象限（摄像头在上）
         */
        private static final int ORIENTATION_PORTRAIT_UP = 0;

        /**
         * 横屏象限（摄像头在左）
         */
        private static final int ORIENTATION_LANDSCAPE_LEFT = 1;

        /**
         * 横屏象限（摄像头在右）
         */
        private static final int ORIENTATION_LANDSCAPE_RIGHT = 2;

        /**
         * 竖屏象限（摄像头在下）
         */
        private static final int ORIENTATION_PORTRAIT_DOWN = 3;

        /**
         * 竖向布局
         */
        //private static final int LAYOUT_PORTRAIT = 1;

        //private static final int LAYOUT_LANDSCAPE = 2;

        /**
         * 布局方向，默认为竖向布局
         */
        private int layoutDirect = LAYOUT_PORTRAIT;

        /**
         * 摄相头采集旋转方向，一般是设置本端显示方向选择
         * 比如摄像头在上时，远端和本端窗口显示的图像都是图像朝下的。
         * 修改此参数可以旋转本端窗口显示状态。（0:0度，1:90度，2:180度，3:270度）
         */
        private int cameraCaptureRotation;

        /**
         * 视频窗口显示旋转方向,一般是设置远端显示方向选择
         * 比如摄像头在上时，远端和本端窗口显示的图像都是图像朝下的。
         * 修改此参数可以旋转远端窗口显示状态。（0:0度，1:90度，2:180度，3:270度）
         */
        private int windowsDisplayRotation;

        /**
         * 记录上一次的旋转方向
         * 默认unknown
         */
        private int lastOrientation = ORIENTATION_UNKNOWN;

        /**
         * 当前角度
         */
        private int curOriginalOrientation;

        /**
         * 设备摄像头旋转角度监听
         */
        private OrientationEventListener orientationEventListener;

        /**
         * 监听列表
         */
        private final List<Object> orientationEventListenerList = new ArrayList<>();

        /**
         * 构造方法
         */
        public OrientationDetector() {
            // 创建监听
            //createOrientationListener();
        }

        /**
         * 设置布局的方向
         *
         * @param layoutDirect 布局方向值
         */
        public void setLayoutDirect(int layoutDirect) {
            this.layoutDirect = layoutDirect;
        }

        /**
         * 设置视频自动方向调整
         *
         * @param object 调用者对象
         * @param isOpen 是否开启方向调整
         */
        public void autoOrientationAdjust(Object object, boolean isOpen) {
            if (isOpen) {
                if (orientationEventListenerList.size() == 0) {
                    // 创建监听
                    createOrientationListener();
                }
                // 添加调用者到监听列表
                if (!orientationEventListenerList.contains(object)) {
                    orientationEventListenerList.add(object);
                }

                updateRotation(true);
            } else {
                this.lastOrientation = ORIENTATION_UNKNOWN;

                // 去注册监听，移除摄像头旋转角度监听
                orientationEventListenerList.remove(object);
                if (orientationEventListenerList.size() == 0) {
                    destroyOrientationListener();
                }
            }
        }


        /**
         * 更新摄相头采集方向和视频窗口显示方向
         * 为解决平放手机无法监听设备角度问题，添加参数isForce，不管是否平放，先强制主动旋转一次
         *
         * @param isForce 是否是强制更新
         */
        public void updateRotation(boolean isForce) {
            int deviceOrientation = getOrientation(curOriginalOrientation);

            // 强制设置旋转，或与上一次不一样的区间，则进行更新设置旋转角度
            if (isForce || deviceOrientation != lastOrientation) {
                // 更新旋转角度, 包括摄相头和显示窗口
                updateRotation(deviceOrientation);

                // 根据旋转角度，调用TUP接口旋转视频方向
                setRotation(cameraCaptureRotation, windowsDisplayRotation);
            }
        }

        /**
         * 创建并启动设备旋转监听
         */
        private void createOrientationListener() {
            // 启一个新的监听，监听设备旋转角度
            orientationEventListener = new OrientationEventListener(LocContext.getContext()) {
                @Override
                public void onOrientationChanged(int orientation) {
                    curOriginalOrientation = orientation;

                    if (curOriginalOrientation > 360 || curOriginalOrientation < 0) {
                        return;
                    }

                    // 旋转处理，更新摄相头采集角度和视频窗口显示角度
                    if (!orientationEventListenerList.isEmpty()) {
                        updateRotation(false);
                    }
                }
            };

            // 启动监听
            orientationEventListener.enable();
        }

        /**
         * 停止并销毁设备旋转监听
         */
        private void destroyOrientationListener() {
            if (orientationEventListener != null) {
                orientationEventListener.disable();
            }
            orientationEventListener = null;
        }

        /**
         * 根据捕捉到的摄像头方向划分四个象限，不属于四象限则返回unknown不做处理
         * 四个象限分别是：竖向上【0-45、315-360】；横向右【45-135】；竖向下【135-225】；横向左【225-315】
         *
         * @param orientation 捕捉到的设备摄像头角度
         * @return 角度所属象限
         */
        private int getOrientation(int orientation) {
            if ((orientation < 45 && orientation >= 0) || (orientation >= 315 && orientation <= 360)) {

                return ORIENTATION_PORTRAIT_UP;
            } else if (orientation >= 45 && orientation < 135) {
                return ORIENTATION_LANDSCAPE_RIGHT;
            } else if (orientation >= 135 && orientation < 225) {
                return ORIENTATION_PORTRAIT_DOWN;
            } else if (orientation >= 225 && orientation < 315) {
                return ORIENTATION_LANDSCAPE_LEFT;
            } else {
                return ORIENTATION_UNKNOWN;
            }
        }

        /**
         * 根据不同的象限区间设置旋转角度
         *
         * @param deviceOrientation 象限区间
         */
        private void updateRotation(int deviceOrientation) {
            switch (deviceOrientation) {
                case ORIENTATION_LANDSCAPE_LEFT:
                    setOrientationLandscapeLeft();
                    break;
                case ORIENTATION_LANDSCAPE_RIGHT:
                    setOrientationLandscapeRight();
                    break;
                case ORIENTATION_PORTRAIT_UP:
                    setOrientationPortraitUp();
                    break;
                case ORIENTATION_PORTRAIT_DOWN:
                    setOrientationPortraitDown();
                    break;
                default:
                    break;
            }

            // 记录上一次的象限区间
            lastOrientation = deviceOrientation;
        }

        /**
         * 设备横向，摄像头在左时的旋转角度设置
         */
        private void setOrientationLandscapeLeft() {
            if (isLayoutPortrait()) {
                // 前置后置摄像头旋转角度一致
                cameraCaptureRotation = 0;
                windowsDisplayRotation = 1;
            } else {
                // 前置后置摄像头旋转角度一致
                cameraCaptureRotation = 0;
                windowsDisplayRotation = 0;
            }
        }

        /**
         * 设备横向，摄像头在右时的旋转角度设置
         */
        private void setOrientationLandscapeRight() {
            if (isLayoutPortrait()) {
                // 前置后置摄像头旋转角度一致
                cameraCaptureRotation = 2;
                windowsDisplayRotation = 3;
            } else {
                // 前置后置摄像头旋转角度一致
                cameraCaptureRotation = 2;
                windowsDisplayRotation = 0;
            }
        }

        /**
         * 设备竖向，摄像头在上时的旋转角度设置
         */
        private void setOrientationPortraitUp() {
            if (isLayoutPortrait()) {
                if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                    cameraCaptureRotation = 3;
                    windowsDisplayRotation = 0;
                } else {
                    cameraCaptureRotation = 1;
                    windowsDisplayRotation = 0;
                }
            } else {
                if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                    cameraCaptureRotation = 3;
                    windowsDisplayRotation = 3;
                } else {
                    cameraCaptureRotation = 1;
                    windowsDisplayRotation = 3;
                }
            }
        }

        /**
         * 设备竖向，摄像头在下时的旋转角度设置
         */
        private void setOrientationPortraitDown() {
            if (isLayoutPortrait()) {
                if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                    cameraCaptureRotation = 1;
                    windowsDisplayRotation = 2;
                } else {
                    cameraCaptureRotation = 3;
                    windowsDisplayRotation = 2;
                }
            }
            else {
                if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                    cameraCaptureRotation = 1;
                    windowsDisplayRotation = 1;
                } else {
                    cameraCaptureRotation = 3;
                    windowsDisplayRotation = 1;
                }
            }
        }

        /**
         * 判断是否是竖向布局
         *
         * @return true：是；false：否
         */
        private boolean isLayoutPortrait() {
            return layoutDirect == LAYOUT_PORTRAIT;
        }

        /**
         * 设置旋转角度
         *
         * @param cameraCaptureRotation  摄像头采集方向
         * @param windowsDisplayRotation 窗口显示方向
         */
        private void setRotation(int cameraCaptureRotation, int windowsDisplayRotation) {

            currentCallId = VideoMgr.getInstance().getCurrentCallId();

            if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                // 1表示前置摄像头
                setCaptureRotation(1, cameraCaptureRotation);
                setLocalVideoDisplayRotation(1, windowsDisplayRotation);
            } else if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.BACK_CAMERA) {
                // 0表示后置摄像头
                setCaptureRotation(0, cameraCaptureRotation);
                setLocalVideoDisplayRotation(0, windowsDisplayRotation);
            } else
            {
                // -1表示摄相头关闭
                // do nothing
            }

            setRemoteVideoDisplayRotation(windowsDisplayRotation);

        }


        /**
         * This method is used to set camera capture rotation
         * 设置视频采集方向
         * @param index
         * @param rotation
         * @return
         */
        public boolean setCaptureRotation(int index, int rotation)
        {
            TsdkCall tsdkCall = TsdkManager.getInstance().getCallManager().getCallByCallId(currentCallId);
            if (null == tsdkCall)
            {
                return false;
            }

            tsdkCall.setCaptureRotation(index, rotation);

            return true;
        }


        /**
         * This method is used to set local video window display rotation
         * 设置本地视频窗口显示方向
         * @param cameraIndex
         * @param rotation
         * @return
         */
        public boolean setLocalVideoDisplayRotation(int cameraIndex, int rotation)
        {
            TsdkCall tsdkCall = TsdkManager.getInstance().getCallManager().getCallByCallId(currentCallId);
            if (null == tsdkCall)
            {
                return false;
            }

            // 前置摄像头
            if (1 == cameraIndex)
            {
                // 窗口镜像模式 0:不做镜像(默认值) 1:上下镜像(目前未支持) 2:左右镜像
                // 本地视频前置摄像头做左右镜像，所以设置mirror type为 2
                TsdkVideoRenderInfo videoRenderInfo = new TsdkVideoRenderInfo();
                videoRenderInfo.setRenderType(TsdkVideoWndType.TSDK_E_VIDEO_WND_LOCAL);
                videoRenderInfo.setMirrorType(TsdkVideoWndMirrorType.TSDK_E_VIDEO_WND_MIRROR_HORIZONTAL);
                videoRenderInfo.setDisplayType(TsdkVideoWndDisplayMode.TSDK_E_VIDEO_WND_DISPLAY_CUT);

                tsdkCall.setVideoRender(videoRenderInfo);
            }
            else{
                TsdkVideoRenderInfo videoRenderInfo = new TsdkVideoRenderInfo();
                videoRenderInfo.setRenderType(TsdkVideoWndType.TSDK_E_VIDEO_WND_LOCAL);
                videoRenderInfo.setMirrorType(TsdkVideoWndMirrorType.TSDK_E_VIDEO_WND_MIRROR_DEFAULE);
                videoRenderInfo.setDisplayType(TsdkVideoWndDisplayMode.TSDK_E_VIDEO_WND_DISPLAY_CUT);

                tsdkCall.setVideoRender(videoRenderInfo);
            }

            tsdkCall.setDisplayRotation(TsdkVideoWndType.TSDK_E_VIDEO_WND_LOCAL, rotation);

            return true;
        }

        /**
         * This method is used to set remote video window display rotation
         * 设置远端视频显示方向
         * @param rotation
         * @return
         */
        public boolean setRemoteVideoDisplayRotation(int rotation)
        {
            TsdkCall tsdkCall = TsdkManager.getInstance().getCallManager().getCallByCallId(currentCallId);
            if (null == tsdkCall)
            {
                return false;
            }

            TsdkVideoRenderInfo remoteVideoRenderInfo = new TsdkVideoRenderInfo();

            if (isLayoutPortrait()) {
                remoteVideoRenderInfo.setDisplayType(TsdkVideoWndDisplayMode.TSDK_E_VIDEO_WND_DISPLAY_CUT);
            } else {
                remoteVideoRenderInfo.setDisplayType(TsdkVideoWndDisplayMode.TSDK_E_VIDEO_WND_DISPLAY_CUT);
            }

            remoteVideoRenderInfo.setRenderType(TsdkVideoWndType.TSDK_E_VIDEO_WND_REMOTE);
            remoteVideoRenderInfo.setMirrorType(TsdkVideoWndMirrorType.TSDK_E_VIDEO_WND_MIRROR_DEFAULE);

            tsdkCall.setVideoRender(remoteVideoRenderInfo);

            tsdkCall.setDisplayRotation(TsdkVideoWndType.TSDK_E_VIDEO_WND_REMOTE, rotation);

            return true;
        }

    }

}
