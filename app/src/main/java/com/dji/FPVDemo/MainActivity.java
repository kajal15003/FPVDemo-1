package com.dji.FPVDemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView.SurfaceTextureListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import dji.common.camera.CameraSystemState;
import dji.common.camera.DJICameraSettingsDef;
import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightControllerControlMode;
import dji.common.flightcontroller.DJIFlightControllerRemoteControllerFlightMode;
import dji.common.flightcontroller.DJIFlightFailsafeOperation;
import dji.common.flightcontroller.DJIFlightOrientationMode;
import dji.common.flightcontroller.DJIIMUState;
import dji.common.flightcontroller.DJILocationCoordinate2D;
import dji.common.flightcontroller.DJIVirtualStickFlightControlData;
import dji.common.flightcontroller.DJIVirtualStickFlightCoordinateSystem;
import dji.common.flightcontroller.DJIVirtualStickRollPitchControlMode;
import dji.common.flightcontroller.DJIVirtualStickVerticalControlMode;
import dji.common.flightcontroller.DJIVirtualStickYawControlMode;
import dji.common.product.Model;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.camera.DJICamera;
import dji.sdk.camera.DJICamera.CameraReceivedVideoDataCallback;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJIFlightControllerDelegate;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

import static com.dji.FPVDemo.FPVDemoApplication.getProductInstance;


public class MainActivity extends Activity implements SurfaceTextureListener,OnClickListener{

    private static final String TAG = MainActivity.class.getName();
    public native static String KCFTracker(long addr,float a,float b,float c,float d);
    EditText text;
    Mat tmp;
    private Handler mHandler;
    Timer t;
    Double p_a=0.0;
    Double p_b=0.0;
    Button p_control;
    ImageView result;
    private SmoothBluetooth mSmoothBluetooth;
    protected DJICamera.CameraReceivedVideoDataCallback mReceivedVideoDataCallBack = null;
    Double controla=0.0028;
    Double controlb=0.003;
    TextView hello;
    TextView hello1;
    TextView hello2;
    //int s1,s2,s3,s4;
    Bitmap bmp;
    Bitmap bm;
    int count=0;
    //int init=0;
    //int x1,y1;
    protected DJICodecManager mCodecManager = null;
    int recordbit=0;
    protected TextureView mVideoSurface = null;
    private Button mCaptureBtn, mShootPhotoModeBtn, mRecordVideoModeBtn;
    private ToggleButton mRecordBtn;
    private TextView recordingTime;
    //private OutputStream outputStream;
    //private InputStream inStream;


    static{
        if(OpenCVLoader.initDebug())
        {
            System.loadLibrary("KCF");
            Log.d(TAG,"Successfully Loaded");
        }
        else
        {
            Log.d(TAG,"OpenCV Not Loaded");
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler(Looper.getMainLooper());
        mSmoothBluetooth = new SmoothBluetooth(this);
        mSmoothBluetooth.setListener(mListener);
        mSmoothBluetooth.doDiscovery();
        mSmoothBluetooth.tryConnection();

        initUI();
        t = new Timer();
        /*DJIFlightController flightController=new DJIFlightController() {
            @Override
            public boolean isIntelligentFlightAssistantSupported() {
                return false;
            }

            @Override
            public boolean isLandingGearMovable() {
                return false;
            }

            @Override
            public boolean isRtkSupported() {
                return false;
            }

            @Override
            public void setFlightFailsafeOperation(DJIFlightFailsafeOperation djiFlightFailsafeOperation, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getFlightFailsafeOperation(DJICommonCallbacks.DJICompletionCallbackWith<DJIFlightFailsafeOperation> djiCompletionCallbackWith) {

            }

            @Override
            public void takeOff(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void cancelTakeOff(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void autoLanding(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void cancelAutoLanding(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void turnOnMotors(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void turnOffMotors(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void goHome(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void cancelGoHome(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void setHomeLocation(DJILocationCoordinate2D djiLocationCoordinate2D, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getHomeLocation(DJICommonCallbacks.DJICompletionCallbackWith<DJILocationCoordinate2D> djiCompletionCallbackWith) {

            }

            @Override
            public void setGoHomeAltitude(float v, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getGoHomeAltitude(DJICommonCallbacks.DJICompletionCallbackWith<Float> djiCompletionCallbackWith) {

            }

            @Override
            public boolean isOnboardSDKDeviceAvailable() {
                return false;
            }

            @Override
            public void sendDataToOnboardSDKDevice(byte[] bytes, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void setLEDsEnabled(boolean b, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getLEDsEnabled(DJICommonCallbacks.DJICompletionCallbackWith<Boolean> djiCompletionCallbackWith) {

            }

            @Override
            public void setFlightOrientationMode(DJIFlightOrientationMode djiFlightOrientationMode, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void lockCourseUsingCurrentDirection(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public boolean isVirtualStickControlModeAvailable() {
                return true;
            }

            @Override
            public void sendVirtualStickFlightControlData(DJIVirtualStickFlightControlData djiVirtualStickFlightControlData, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void enableVirtualStickControlMode(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void disableVirtualStickControlMode(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void setGoHomeBatteryThreshold(int i, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getGoHomeBatteryThreshold(DJICommonCallbacks.DJICompletionCallbackWith<Integer> djiCompletionCallbackWith) {

            }

            @Override
            public void setLandImmediatelyBatteryThreshold(int i, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getLandImmediatelyBatteryThreshold(DJICommonCallbacks.DJICompletionCallbackWith<Integer> djiCompletionCallbackWith) {

            }

            @Override
            public void setHomeLocationUsingAircraftCurrentLocation(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void setOnIMUStateChangedCallback(DJIFlightControllerDelegate.FlightControllerIMUStateChangedCallback flightControllerIMUStateChangedCallback) {

            }

            @Override
            public void startIMUCalibration(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void startIMUCalibration(int i, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public int getNumberOfIMUs() {
                return 0;
            }

            @Override
            public void setControlMode(DJIFlightControllerControlMode djiFlightControllerControlMode, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getControlMode(DJICommonCallbacks.DJICompletionCallbackWith<DJIFlightControllerControlMode> djiCompletionCallbackWith) {

            }

            @Override
            public void setTripodModeEnabled(Boolean aBoolean, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getTripodModeEnabled(DJICommonCallbacks.DJICompletionCallbackWith<Boolean> djiCompletionCallbackWith) {

            }

            @Override
            public void setAutoQuickSpinEnabled(Boolean aBoolean, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getQuickSpinEnabled(DJICommonCallbacks.DJICompletionCallbackWith<Boolean> djiCompletionCallbackWith) {

            }

            @Override
            public void getMultiSideIMUCalibrationStatus(DJICommonCallbacks.DJICompletionCallbackWith<DJIIMUState> djiCompletionCallbackWith) {

            }

            @Override
            public void setTerrainFollowModeEnabled(Boolean aBoolean, DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void getTerrainFollowModeEnable(DJICommonCallbacks.DJICompletionCallbackWith<Boolean> djiCompletionCallbackWith) {

            }

            @Override
            public void confirmLanding(DJICommonCallbacks.DJICompletionCallback djiCompletionCallback) {

            }

            @Override
            public void isLandingConfirmationNeeded(DJICommonCallbacks.DJICompletionCallbackWith<Boolean> djiCompletionCallbackWith) {

            }

            @Override
            public void getRemoteControllerFlightModeMappingWithCompletion(DJICommonCallbacks.DJICompletionCallbackWith<DJIFlightControllerRemoteControllerFlightMode[]> djiCompletionCallbackWith) {

            }
        };
*/
        FPVDemoApplication.getAircraftInstance().getFlightController().enableVirtualStickControlMode(new DJICommonCallbacks.DJICompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {

            }
        });

        FPVDemoApplication.getAircraftInstance().getFlightController().isVirtualStickControlModeAvailable();
        FPVDemoApplication.getAircraftInstance().getFlightController().setHorizontalCoordinateSystem(DJIVirtualStickFlightCoordinateSystem.Body);
        FPVDemoApplication.getAircraftInstance().getFlightController().setVerticalControlMode(DJIVirtualStickVerticalControlMode.Velocity);
        FPVDemoApplication.getAircraftInstance().getFlightController().setRollPitchControlMode(DJIVirtualStickRollPitchControlMode.Velocity);
        FPVDemoApplication.getAircraftInstance().getFlightController().setYawControlMode(DJIVirtualStickYawControlMode.AngularVelocity);


        mReceivedVideoDataCallBack = new CameraReceivedVideoDataCallback() {

            @Override
            public void onResult(byte[] videoBuffer, int size) {
                if(mCodecManager != null){

                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }else {
                    Log.e(TAG, "mCodecManager is null");
                }
            }
        };

        DJICamera camera = FPVDemoApplication.getCameraInstance();

        if (camera != null) {

            camera.setDJICameraUpdatedSystemStateCallback(new DJICamera.CameraUpdatedSystemStateCallback() {
                @Override
                public void onResult(CameraSystemState cameraSystemState) {
                    if (null != cameraSystemState) {

                        int recordTime = cameraSystemState.getCurrentVideoRecordingTimeInSeconds();
                        int minutes = (recordTime % 3600) / 60;
                        int seconds = recordTime % 60;

                        final String timeString = String.format("%02d:%02d", minutes, seconds);
                        final boolean isVideoRecording = cameraSystemState.isRecording();

                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                recordingTime.setText(timeString);

                                if (isVideoRecording){
                                    recordingTime.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            });

        }

        /*result.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                x1 = (int) event.getX();
                y1 = (int) event.getY();
                x1 = (int) ((double) x1 * ((double) bmp.getWidth()/(double) result.getWidth()));
                y1 = (int) ((double) y1 * ((double) bmp.getHeight() / (double) result.getHeight()));
                init++;
                Toast.makeText(getApplicationContext(),x1+"-"+y1,Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/



    }






    protected void onProductChange() {
        initPreviewer();
    }

    @Override
    public void onResume() {
        //Log.e(TAG, "onResume");
        super.onResume();
        initPreviewer();
        onProductChange();
        FPVDemoApplication.getAircraftInstance().getFlightController().enableVirtualStickControlMode(new DJICommonCallbacks.DJICompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {

            }
        });
        FPVDemoApplication.getAircraftInstance().getFlightController().setHorizontalCoordinateSystem(DJIVirtualStickFlightCoordinateSystem.Body);
        FPVDemoApplication.getAircraftInstance().getFlightController().setVerticalControlMode(DJIVirtualStickVerticalControlMode.Velocity);
        FPVDemoApplication.getAircraftInstance().getFlightController().setRollPitchControlMode(DJIVirtualStickRollPitchControlMode.Velocity);
        FPVDemoApplication.getAircraftInstance().getFlightController().setYawControlMode(DJIVirtualStickYawControlMode.AngularVelocity);
        if(mVideoSurface == null) {
            Log.e(TAG, "mVideoSurface is null");
        }
    }

    @Override
    public void onPause() {
       // Log.e(TAG, "onPause");
        uninitPreviewer();
        super.onPause();
    }

    @Override
    public void onStop() {
        //Log.e(TAG, "onStop");
        super.onStop();
    }

  /*  public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }*/

    @Override
    protected void onDestroy() {
       // Log.e(TAG, "onDestroy");
        uninitPreviewer();
        super.onDestroy();
    }

    private void initUI() {


        mVideoSurface = (TextureView)findViewById(R.id.video_previewer_surface);
        hello=(TextView)findViewById(R.id.kunal);
        hello1=(TextView)findViewById(R.id.kunal1);
        hello2=(TextView)findViewById(R.id.kunal2);
        result=(ImageView)findViewById(R.id.result);
        recordingTime = (TextView) findViewById(R.id.timer);
        mCaptureBtn = (Button) findViewById(R.id.btn_capture);
        mRecordBtn = (ToggleButton) findViewById(R.id.btn_record);
        mShootPhotoModeBtn = (Button) findViewById(R.id.btn_shoot_photo_mode);
        mRecordVideoModeBtn = (Button) findViewById(R.id.btn_record_video_mode);
        text=(EditText)findViewById(R.id.text);
        p_control=(Button)findViewById(R.id.proportional);


        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }


        p_control.setOnClickListener(this);
        mCaptureBtn.setOnClickListener(this);
        mRecordBtn.setOnClickListener(this);
        mShootPhotoModeBtn.setOnClickListener(this);
        mRecordVideoModeBtn.setOnClickListener(this);

        recordingTime.setVisibility(View.INVISIBLE);

        mRecordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startRecord();
                } else {
                    stopRecord();
                }
            }
        });

    }





    private void initPreviewer() {

        DJIBaseProduct product = getProductInstance();

        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.disconnected));
        } else {
            if (null != mVideoSurface) {
                mVideoSurface.setSurfaceTextureListener(this);
            }
            if (!product.getModel().equals(Model.UnknownAircraft)) {
                DJICamera camera = product.getCamera();
                if (camera != null){
                    camera.setDJICameraReceivedVideoDataCallback(mReceivedVideoDataCallBack);
                }
            }
        }
    }

    private void uninitPreviewer() {
        DJICamera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null){
            FPVDemoApplication.getCameraInstance().setDJICameraReceivedVideoDataCallback(null);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //Log.e(TAG, "onSurfaceTextureAvailable");
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(this, surface, width, height);

        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
       // Log.e(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
       // Log.e(TAG,"onSurfaceTextureDestroyed");
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }

        return false;
    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        if(recordbit!=0) {
            bmp = mVideoSurface.getBitmap();
            int bytes = bmp.getByteCount();
            ByteBuffer buffer = ByteBuffer.allocate(bytes);
            bmp.copyPixelsToBuffer(buffer);
            byte[] array = buffer.array();
            tmp=new Mat(bmp.getHeight(),bmp.getWidth(),CvType.CV_8UC4);
            tmp.put(0,0,array);
            Imgproc.cvtColor(tmp,tmp, Imgproc.COLOR_RGBA2RGB);

            if(count!=0) {

                hello.setText((KCFTracker(tmp.getNativeObjAddr(),0, 0, 0,0)+""));
                String s=hello.getText().toString();
                String[] parts = s.split("-");
                double a=Float.parseFloat(parts[0]);
                double b=Float.parseFloat(parts[1]);
                double c=Float.parseFloat(parts[2]);
                double d=Float.parseFloat(parts[3]);
                int scale_x=get_scale_x(c,tmp.rows()/2);
                int scale_y=get_scale_y(d,tmp.cols()/2);
                a=scale_x*controla*((a+c/2)-(tmp.cols()/2));
                b=scale_y*controlb*((tmp.rows()/2)-(b+d/2));
                hello1.setText("velociy:"+a+"-"+b);
                hello2.setText("scale: "+scale_x+"-"+scale_y);
                final float af=(float)a;
                final float bf=(float)b;
                final float height1=(float)Math.abs(p_a-((a+c/2)-(tmp.cols()/2)));
                final float height2=(float)Math.abs(p_b-((tmp.rows()/2)-(b+d/2)));
                p_a=((a+c/2)-(tmp.cols()/2));
                p_b=((tmp.rows()/2)-(b+d/2));
                final float height=Math.min(Math.max(height1,height2),2);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FPVDemoApplication.getAircraftInstance().
                                getFlightController().sendVirtualStickFlightControlData(
                                new DJIVirtualStickFlightControlData(
                                        af,bf, 0, height
                                ), new DJICommonCallbacks.DJICompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {

                                    }
                                }
                        );
                    }
                },100);



                bm = Bitmap.createBitmap(tmp.cols(), tmp.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(tmp, bm);
                result.setImageBitmap(bm);
                count++;
            }
            else
            {
                bm = Bitmap.createBitmap(tmp.cols(), tmp.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(tmp, bm);
                result.setImageBitmap(bm);
                hello.setText((KCFTracker(tmp.getNativeObjAddr(), 0, 0, 0 , 0) + ""));
                if(hello.getText().toString()!="NO" && hello.getText().toString()!="") {
                    count++;
                }
            }

            recordbit++;
        }
    }

    public int get_scale_x(double position_x, int size_x) {

        double diff=Math.abs(size_x-position_x);
        if(diff<=100)
            return 1;
        else if(diff>100 && diff<=200)
            return 2;
        else if(diff>200 && diff<=250)
            return 3;
        else if(diff>250 && diff<=300)
            return 4;
        else
            return 5;

    }


    public int get_scale_y(double position_y, int size_y) {
        double diff=Math.abs(size_y-position_y);
        if(diff<=100)
            return 1;
        else if(diff>100 && diff<=200)
            return 2;
        else if(diff>200 && diff<=250)
            return 3;
        else if(diff>250 && diff<=300)
            return 4;
        else
            return 5;
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /*case R.id.btn_capture:{
                captureAction();
                break;
            }*/
            case R.id.btn_shoot_photo_mode:{
                //switchCameraMode(DJICameraSettingsDef.CameraMode.ShootPhoto);
                recordbit=0;
                controla=0.0;
                controlb=0.0;
                break;
            }
            case R.id.btn_record_video_mode:{
                switchCameraMode(DJICameraSettingsDef.CameraMode.RecordVideo);
                break;
            }
            case R.id.proportional:{

                Toast.makeText(getApplicationContext(),"Recording stopped",Toast.LENGTH_SHORT).show();
                //switchCameraMode(DJICameraSettingsDef.CameraMode.RecordVideo); this is not working
                stopRecord();
                mSmoothBluetooth.send("1");
                break;
            }
            default:
                break;
        }
    }


    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            Toast.makeText(MainActivity.this, "Bluetooth not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onBluetoothNotEnabled() {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth,1);
        }

        @Override
        public void onConnecting(Device device) {
            Toast.makeText(getApplicationContext(),"Connecting to"+device.getName(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnected(Device device) {
            Toast.makeText(getApplicationContext(),"Connected to",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected() {
            Toast.makeText(getApplicationContext(),"Disconnected",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionFailed(Device device) {
            Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            if (device.isPaired()) {
                mSmoothBluetooth.doDiscovery();
            }
        }

        @Override
        public void onDiscoveryStarted() {
            Toast.makeText(MainActivity.this, "Searching", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryFinished() {

        }

        @Override
        public void onNoDevicesFound() {
            Toast.makeText(MainActivity.this, "No device found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {
            connectionCallback.connectTo(deviceList.get(0));
        }

        @Override
        public void onDataReceived(int data) {
            Toast.makeText(MainActivity.this, "Data Received", Toast.LENGTH_SHORT).show();
            //switchCameraMode(DJICameraSettingsDef.CameraMode.RecordVideo); this is not working
            startRecord();
        }
    };




    private void switchCameraMode(DJICameraSettingsDef.CameraMode cameraMode){

        DJICamera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            camera.setCameraMode(cameraMode, new DJICommonCallbacks.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {

                    if (error == null) {
                        showToast("Switch Camera Mode Succeeded");
                    } else {
                        showToast(error.getDescription());
                    }
                }
            });
            }

    }


  /*  private void captureAction(){

        DJICameraSettingsDef.CameraMode cameraMode = DJICameraSettingsDef.CameraMode.ShootPhoto;

        final DJICamera camera = FPVDemoApplication.getCameraInstance();

        if (camera != null) {

            DJICameraSettingsDef.CameraShootPhotoMode photoMode = DJICameraSettingsDef.CameraShootPhotoMode.Single; // Set the camera capture mode as Single mode
            camera.startShootPhoto(photoMode, new DJICommonCallbacks.DJICompletionCallback() {

                @Override
                public void onResult(DJIError error) {
                    if (error == null) {
                        showToast("take photo: success");

                    } else {
                        showToast(error.getDescription());
                    }
                }

            });
        }
    }

*/
    private void startRecord(){
        recordbit=1;
        controla=0.0028;
        controlb=0.003;
        DJICameraSettingsDef.CameraMode cameraMode = DJICameraSettingsDef.CameraMode.RecordVideo;
        final DJICamera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {
            camera.startRecordVideo(new DJICommonCallbacks.DJICompletionCallback(){
                @Override

                public void onResult(DJIError error)
                {
                    if (error == null) {

                        showToast("Record video: success");
                    }else {
                        showToast(error.getDescription());
                    }
                }
            });
        }
    }


    private void stopRecord(){

        recordbit=0;
        controla=0.0;
        controlb=0.0;

        DJICamera camera = FPVDemoApplication.getCameraInstance();
        if (camera != null) {

            camera.stopRecordVideo(new DJICommonCallbacks.DJICompletionCallback(){

                @Override
                public void onResult(DJIError error)
                {
                    if(error == null) {
                        showToast("Stop recording: success");
                    }else {
                        showToast(error.getDescription());
                    }
                }
            });
        }

    }

}
