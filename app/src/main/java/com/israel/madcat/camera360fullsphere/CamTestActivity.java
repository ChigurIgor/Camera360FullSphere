package com.israel.madcat.camera360fullsphere;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CamTestActivity extends Activity implements SensorEventListener, OnClickListener {
    private static AdView adViewBaner;

    private static final String TAG = "CamTestActivity";
    private static Preview preview;
    private Button buttonClick,buttonApply;
    private Camera camera;
    private Activity act;
    private static Context ctx;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int actualX,actualY,actualZ;
    private static final int SENSOR_DELAY_MICROS = 16 * 1000; // 16ms
    private TextView textViewDebug,textViewDebug2,textViewDebug3,textViewDebug4;
    private Button imageButtonSettings,imageButtonInstructions;
    private LinearLayout linearLayoutSettings,linearLayoutInstructions;
    private ScrollView scrollViewInstructions;
    private EditText editTextBelts,editTextSectors;
    private Button imageViewLeft,imageViewRight,imageViewTop,imageViewBotom,imageViewOk,imageViewDone;

    private ArrayList<Point> pointList;
    private int currentPoint=0;
    private Point point;
    private int imprecision=2;
    private int coordX=0;
    private int coordY=0;
    private int width=200;
    private int height=100;
    private static final int PERMISSION_REQUEST_CODE = 1;

    float Rot[]=null; //for gravity rotational data
    //don't use R because android uses that for other stuff
    float I[]=null; //for magnetic rotational data
    float accels[]=new float[3];
    float mags[]=new float[3];
    float[] values = new float[3];

    float azimuth;
    float pitch;
    float roll;
    private float currentDegree = 0f;

    private ShutterCallback shutterCallback;
    private PictureCallback rawCallback;
    private  PictureCallback jpegCallback;
    private boolean safeToTakePicture = false;
    private String DEB_TAG= "DEB_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this,getResources().getString(R.string.YOUR_ADMOB_APP_ID));


        ctx = this;
        act = this;

//        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
//        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

//        buttonClick.setText("");


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);



        permisionRequest();

//        pointList=new ArrayList<>();

        textViewDebug=findViewById(R.id.textViewDebug);
        textViewDebug2=findViewById(R.id.textViewDebug2);
        textViewDebug3=findViewById(R.id.textViewDebug3);
        textViewDebug4=findViewById(R.id.textViewDebug4);
        imageButtonSettings=findViewById(R.id.imageButtonSettings);
        imageButtonInstructions=findViewById(R.id.imageButtonInstructions);
        buttonApply=findViewById(R.id.buttonApply);
        linearLayoutSettings=findViewById(R.id.linearLayoutSettings);
        linearLayoutInstructions=findViewById(R.id.linearLayoutInstructions);
        scrollViewInstructions=findViewById(R.id.scrollViewInstructions);
        editTextBelts=findViewById(R.id.editTextBelts);
        editTextSectors=findViewById(R.id.editTextSectors);
        buttonClick = (Button) findViewById(R.id.btnCapture);

        imageViewLeft=findViewById(R.id.imageViewLeft);
        imageViewRight=findViewById(R.id.imageViewRight);
        imageViewTop=findViewById(R.id.imageViewTop);
        imageViewBotom=findViewById(R.id.imageViewBotom);
        imageViewOk=findViewById(R.id.imageViewOk);
        imageViewDone=findViewById(R.id.imageViewDone);

        imageButtonSettings.setOnClickListener(this);
        imageButtonInstructions.setOnClickListener(this);
        buttonApply.setOnClickListener(this);
//        textViewDebug.setText(""+currentPoint+"/"+pointList.size());


//        preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
//        preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
//        preview.setKeepScreenOn(true);
//
//         shutterCallback = new ShutterCallback() {
//            public void onShutter() {
//                //			 Log.d(TAG, "onShutter'd");
//            }
//        };
//
//         rawCallback = new PictureCallback() {
//            public void onPictureTaken(byte[] data, Camera camera) {
//                //			 Log.d(TAG, "onPictureTaken - raw");
//            }
//        };
//
//         jpegCallback = new PictureCallback() {
//            public void onPictureTaken(byte[] data, Camera camera) {
//
//                if(camera!=null) {
//                    new SaveImageTask().execute(data);
//                    resetCam(camera);
//                    Log.d(TAG, "onPictureTaken - jpeg");
//                }
//            }
//        };
//
//
//        preview.setOnClickListener(this);
//        preview.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
////                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//                    if(camera!= null) {
//                        camera.autoFocus(new Camera.AutoFocusCallback() {
//                            @Override
//                            public void onAutoFocus(boolean arg0, Camera arg1) {
//                                //camera.takePicture(shutterCallback, rawCallback, jpegCallback);
////                            Toast.makeText(CamTestActivity.this,"autofocus",Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//
//        });

//        Toast.makeText(ctx, getString(R.string.take_photo_help), Toast.LENGTH_LONG).show();

//if(buttonClick!=null) {
//    buttonClick.setOnClickListener(new OnClickListener() {
//        public void onClick(View v) {
////        				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//            if (safeToTakePicture) {
//                try {
//
//
//                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//                if (currentPoint < pointList.size() - 1) {
//                    currentPoint++;
//
//                    point = pointList.get(currentPoint);
//                    coordX = point.getCoordX();
//                    coordY = point.getCoordY();
////                             textViewDebug.setText(coordX+"/"+coordY);
//                    textViewDebug.setText("" + currentPoint + "/" + pointList.size());
//                } else {
//                    imageViewDone.setVisibility(View.VISIBLE);
//                    textViewDebug.setText("done");
//                }}catch (RuntimeException rex){
//                    Log.d(TAG, "onClick error: " + rex);
//                    Toast.makeText(CamTestActivity.this,getString(R.string.camera_not_found),Toast.LENGTH_LONG).show();
//
//                    finish();
//
//                }
//            }else{
//                Toast.makeText(CamTestActivity.this,getString(R.string.camera_not_found),Toast.LENGTH_LONG).show();}
//        }
//    });
//}else{
////    Toast.makeText(CamTestActivity.this,"Launch activity again",Toast.LENGTH_LONG).show();
//}
//        		buttonClick.setOnLongClickListener(new OnLongClickListener(){
//        			@Override
//        			public boolean onLongClick(View arg0) {
//        				camera.autoFocus(new Camera.AutoFocusCallback(){
//        					@Override
//        					public void onAutoFocus(boolean arg0, Camera arg1) {
//        						//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//        					}
//        				});
//        				return true;
//        			}
//        		});
    }

    @Override
    protected void onResume() {
        super.onResume();

        adViewBaner=findViewById(R.id.adViewBaner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBaner.loadAd(adRequest);
//
//        int numCams = Camera.getNumberOfCameras();
//        if(numCams > 0){
//            try{
//                camera = Camera.open(0);
//
//                Camera.Parameters parameters=camera.getParameters();
//
//                List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
//                Camera.Size size = allSizes.get(0); // get top size
//                for (int i = 0; i < allSizes.size(); i++) {
//                    if (allSizes.get(i).width > size.width)
//                        size = allSizes.get(i);
//                }
////set max Picture Size
//
//
////                parameters.setPictureSize(width,height);
//                parameters.setPictureSize(size.width,size.height);
//                camera.setParameters(parameters);
//                camera.startPreview();
//                preview.setCamera(camera);
//            } catch (RuntimeException ex){
//                Toast.makeText(ctx, getString(R.string.camera_not_found)+"1", Toast.LENGTH_LONG).show();
//                Log.d("CAMERA_1",ex.toString());
//            }
//
////            Toast.makeText(ctx,"numCams="+numCams,Toast.LENGTH_SHORT).show();
//        }

//        startCamera();
        initialisePreview();

    }

    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        if(linearLayoutSettings.getVisibility()==(View.VISIBLE)){
            linearLayoutSettings.setVisibility(View.GONE);
            buttonClick.setVisibility(View.VISIBLE);
            imageButtonSettings.setVisibility(View.VISIBLE);
            imageButtonInstructions.setVisibility(View.VISIBLE);
            preview.setClickable(true);

            return;
        }
        if(scrollViewInstructions.getVisibility()==(View.VISIBLE)){
            scrollViewInstructions.setVisibility(View.GONE);
            buttonClick.setVisibility(View.VISIBLE);
            imageButtonSettings.setVisibility(View.VISIBLE);
            imageButtonInstructions.setVisibility(View.VISIBLE);
            preview.setClickable(true);

            return;
        }
        else{
                    super.onBackPressed();

        }


    }

    private void resetCam(Camera camera) {
        if(camera!=null){
        camera.startPreview();
        preview.setCamera(camera);
        }
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }


//------------------------------------------------  Sensor  ----------------------------------------------------------------------
    @Override
    public void onSensorChanged(SensorEvent event) {
        //below commented code - junk - unreliable is never populated
        //if sensor is unreliable, return void
        //if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        //{
        //    return;
        //}
//        if(linearLayoutSettings.getVisibility()!=View.VISIBLE && linearLayoutInstructions.getVisibility()!=View.VISIBLE) {

            switch (event.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mags = event.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    accels = event.values.clone();
                    break;
                case Sensor.TYPE_ORIENTATION:
                    currentDegree = Math.round(event.values[0]);
                    break;
            }


            if (mags != null && accels != null) {
                Rot = new float[9];
                I = new float[9];
                SensorManager.getRotationMatrix(Rot, I, accels, mags);
                // Correct if screen is in Landscape

                float[] outR = new float[9];
                SensorManager.remapCoordinateSystem(Rot, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
                SensorManager.getOrientation(outR, values);

                azimuth = Math.round(values[0] * 57.2957795f); //looks like we don't need this one
                pitch = Math.round(values[1] * 57.2957795f);
                roll = Math.round(values[2] * 57.2957795f);
                mags = null; //retrigger the loop when things are repopulated
                accels = null; ////retrigger the loop when things are repopulated
            }

//            textViewDebug.setText(""+azimuth);
            textViewDebug2.setText("" + pitch);
//            textViewDebug3.setText(""+roll);
            textViewDebug4.setText("" + currentDegree);

//            textViewDebug3.setText(""+coordX);
            if (currentDegree > coordX) {
                imageViewLeft.setVisibility(View.VISIBLE);
                imageViewRight.setVisibility(View.INVISIBLE);

            } else {
                imageViewLeft.setVisibility(View.INVISIBLE);
            }

            if (currentDegree < coordX || (currentDegree > 240 && coordX < 60)) {
                imageViewRight.setVisibility(View.VISIBLE);
                imageViewLeft.setVisibility(View.INVISIBLE);
            } else {
                imageViewRight.setVisibility(View.INVISIBLE);
            }


            if (pitch > coordY) {
                imageViewTop.setVisibility(View.VISIBLE);
            } else {
                imageViewTop.setVisibility(View.INVISIBLE);
            }

            if (pitch < coordY) {
                imageViewBotom.setVisibility(View.VISIBLE);
            } else {
                imageViewBotom.setVisibility(View.INVISIBLE);
            }

            if (currentDegree < coordX + imprecision && currentDegree > coordX - imprecision && pitch < coordY + imprecision && pitch > coordY - imprecision) {
                imageViewOk.setVisibility(View.VISIBLE);
            } else {
                imageViewOk.setVisibility(View.INVISIBLE);
            }
//        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

//------------------------------------------  calculatePoints  --------------------------------------------


    private void calculatePoints(){
        pointList=new ArrayList<Point>();
        int belts=1;
        int sectors=6;
        if(!editTextBelts.getText().toString().equals("") && !editTextBelts.getText().toString().equals("0")){
             belts=Integer.parseInt(editTextBelts.getText().toString());
        }
        if(!editTextSectors.getText().toString().equals("") && !editTextSectors.getText().toString().equals("0")) {
            sectors = Integer.parseInt(editTextSectors.getText().toString());
        }
        int count=0;
        ArrayList<Integer> coordListY= new ArrayList<>();

//        switch (belts){
//            case 1:
//                     coordListY.add(0);
//
//                break;
//            case 2:
//                    coordListY.add(45);
//                     coordListY.add(-45);
//               ;
//                break;
//            case 3: coordListY.add(0);
//                     coordListY.add(30);
//                     coordListY.add(-30);
//
//                break;
//
//            case 4:
//                coordListY.add(22);
//                coordListY.add(-22);
//                coordListY.add(67);
//                coordListY.add(-67);
//
//
//        }


        for(int i=0; i<belts;i++){
            int angle=(180/belts)/2-90+(180/belts)*i;
            coordListY.add(angle);

        }




        for (Integer coordY:coordListY) {


            for (int i = 0; i < sectors; i++) {
                int coordX = (360 / sectors) * i;
                pointList.add(new Point(coordX,coordY));

            }
            pointList.add(new Point(0,90));
            pointList.add(new Point(0,-90));
        }

        showPoints();

    }


    private void showPoints(){
        String result="";
        for(Point point:pointList){
            result=result+"/"+point.getCoordX()+","+point.getCoordY();
        }

//        textViewDebug3.setText(result);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonApply:
                calculatePoints();
                linearLayoutSettings.setVisibility(View.GONE);
                preview.setClickable(true);
                imageButtonSettings.setVisibility(View.VISIBLE);
                imageButtonInstructions.setVisibility(View.VISIBLE);
                buttonClick.setVisibility(View.VISIBLE);

                imageViewDone.setVisibility(View.INVISIBLE);
                currentPoint=0;
                point= pointList.get(currentPoint);
                coordX=point.getCoordX();
                coordY=point.getCoordY();
                break;

            case R.id.imageButtonSettings:
                linearLayoutSettings.setVisibility(View.VISIBLE);
//                textViewDebug.setText("click");
                preview.setClickable(false);
                imageButtonSettings.setVisibility(View.GONE);
                buttonClick.setVisibility(View.GONE);
                imageButtonInstructions.setVisibility(View.GONE);
                break;
            case R.id.imageButtonInstructions:
                scrollViewInstructions.setVisibility(View.VISIBLE);
//                textViewDebug.setText("click");
                preview.setClickable(false);
                imageButtonSettings.setVisibility(View.GONE);
                buttonClick.setVisibility(View.GONE);
                imageButtonInstructions.setVisibility(View.GONE);
                break;
//            case R.id.surfaceView:
//                camera.autoFocus(new Camera.AutoFocusCallback(){
//                    @Override
//                    public void onAutoFocus(boolean arg0, Camera arg1) {
//                        //camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//                    }
//                });
//                break;
        }

    }


    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/PanoramicCamera");
                dir.mkdirs();
                DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssZ");
                String date = df.format(Calendar.getInstance().getTime());

                String fileName = String.format("PanoramicCamera_"+date+".jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }


    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result=size;
                }
                else {
                    int resultArea=result.width * result.height;
                    int newArea=size.width * size.height;

                    if (newArea > resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }


    //=========================================================  permisionRequest  ===========================================================================


    public void permisionRequest() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this,"Permision granted",Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }

    }

    //ToDo==============================================   onRequestPermissionsResult   =========================================================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if(grantResults.length>0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&

                            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this,"Permision granted",Toast.LENGTH_LONG).show();

//                        startCamera();
//
//                        shutterCallback = new ShutterCallback() {
//                            public void onShutter() {
//                                //			 Log.d(TAG, "onShutter'd");
//                            }
//                        };
//
//                        rawCallback = new PictureCallback() {
//                            public void onPictureTaken(byte[] data, Camera camera) {
//                                //			 Log.d(TAG, "onPictureTaken - raw");
//                            }
//                        };
//
//                        jpegCallback = new PictureCallback() {
//                            public void onPictureTaken(byte[] data, Camera camera) {
//                                if(camera!=null){
//                                new SaveImageTask().execute(data);
//                                resetCam(camera);
//                                Log.d(TAG, "onPictureTaken - jpeg");
//                                camera.startPreview();
//                            }}
//                        };

initialisePreview();
                    }

                } else {
                    finish();
                }
            }else {finish();}
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public static void restartActivity(Activity act){
        Intent intent=new Intent();
        intent.setClass(act, act.getClass());
        ((Activity)act).startActivity(intent);
        ((Activity)act).finish();
    }



    public void startCamera(){


        Log.e(DEB_TAG,"Preview startCamera");



        int numCams = Camera.getNumberOfCameras();

        if(numCams > 0){
            try{
                this.camera = Camera.open(0);

                Log.e(DEB_TAG,"Camera: "+ camera.toString());
                Log.e(DEB_TAG,"Camera getsizes: "+ camera.getParameters().getSupportedPictureSizes().toString());




                Camera.Parameters parameters=camera.getParameters();

                List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
                Camera.Size size = allSizes.get(0); // get top size
                for (int i = 0; i < allSizes.size(); i++) {
                    if (allSizes.get(i).width > size.width)
                        size = allSizes.get(i);
                }
//set max Picture Size


//                parameters.setPictureSize(width,height);
                parameters.setPictureSize(size.width,size.height);
                camera.setParameters(parameters);
                camera.startPreview();
                safeToTakePicture = true;
                preview.setCamera(camera);
                Log.e(DEB_TAG,"Preview setCamera");

                Log.e(DEB_TAG,"Preview setActivated");

            } catch (RuntimeException ex){
//                Toast.makeText(ctx, ctx.getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
                Log.e(DEB_TAG,"2"+" : "+ex);

                Log.d("CAMERA_1",ex.toString());
                finish();
            }

//            Toast.makeText(ctx,"numCams="+numCams,Toast.LENGTH_SHORT).show();
        }
    }

    //ToDo==============================================   initialisePreview   =========================================================

    private  void initialisePreview(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);

        pointList=new ArrayList<>();
        textViewDebug.setText(""+currentPoint+"/"+pointList.size());

        Log.e(DEB_TAG,"Preview");
        preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
        preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

        shutterCallback = new ShutterCallback() {
            public void onShutter() {
                //			 Log.d(TAG, "onShutter'd");
            }
        };

        rawCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                //			 Log.d(TAG, "onPictureTaken - raw");
            }
        };

        jpegCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {

                if(camera!=null) {
                    new SaveImageTask().execute(data);
                    resetCam(camera);
                    Log.d(TAG, "onPictureTaken - jpeg");
                }
            }
        };


        preview.setOnClickListener(this);
        preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                if(camera!= null ) {
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean arg0, Camera arg1) {
                            //camera.takePicture(shutterCallback, rawCallback, jpegCallback);
//                            Toast.makeText(CamTestActivity.this,"autofocus",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        });


        if(buttonClick!=null) {
            buttonClick.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
//        				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                    if (safeToTakePicture) {
                        try {


                            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                            if (currentPoint < pointList.size() - 1) {
                                currentPoint++;

                                point = pointList.get(currentPoint);
                                coordX = point.getCoordX();
                                coordY = point.getCoordY();
//                             textViewDebug.setText(coordX+"/"+coordY);
                                textViewDebug.setText("" + currentPoint + "/" + pointList.size());
                            } else {
                                    imageViewDone.setVisibility(View.VISIBLE);
                                    textViewDebug.setText("done");

                            }}catch (RuntimeException rex){
                            Log.d(TAG, "onClick error: " + rex);
//                            Toast.makeText(CamTestActivity.this,getString(R.string.camera_not_found),Toast.LENGTH_LONG).show();
                            Log.e(DEB_TAG,"1");

                            finish();

                        }
                    }else{
//                        Toast.makeText(CamTestActivity.this,getString(R.string.camera_not_found),Toast.LENGTH_LONG).show();
                        finish();
 }
                }
            });
        }else{
//    Toast.makeText(CamTestActivity.this,"Launch activity again",Toast.LENGTH_LONG).show();
        }
        startCamera();

    }

}