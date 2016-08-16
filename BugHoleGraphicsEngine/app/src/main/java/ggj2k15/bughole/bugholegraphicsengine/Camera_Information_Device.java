package ggj2k15.bughole.bugholegraphicsengine;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

public class Camera_Information_Device implements ICamera_Information {
    private SensorManager _sensorManager;
    private Context mContext;

    private float[] mOrientation = new float[3];
    private float[] mViewDirection = new float[4];
    private final float[] mViewRotationMatrix = new float[16];
    private float[] mRemapedViewRotationForViewDirectionMatrix = new float[16];

    private float[] m_ModelViewProjectionMatrix = new float[16];
    private float[] m_ModelViewMatrix = new float[16];
    private float[] m_ProjectionMatrix = new float[16];
    private float[] m_ModelingMatrix1 = new float[16];
    private float[] m_ModelingMatrix2 = new float[16];
    private float[] m_ModelingMatrix3 = new float[16];
    private float[] m_TemporaryHelperMatrix = new float[16];

    private int m_WorldAxisForDeviceAxisX;
    private int m_WorldAxisForDeviceAxisY;

    public Camera_Information_Device(Context context) {

        mContext = context;


        // By default, remap the axes as if the front of the
        // device screen was the instrument panel.
        m_WorldAxisForDeviceAxisX = SensorManager.AXIS_X;
        m_WorldAxisForDeviceAxisY = SensorManager.AXIS_Y;

        // Adjust the rotation matrix for the device orientation
        int screenRotation = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        if (screenRotation == Surface.ROTATION_0) {
            m_WorldAxisForDeviceAxisX = SensorManager.AXIS_X;
            m_WorldAxisForDeviceAxisY = SensorManager.AXIS_Y;
        } else if (screenRotation == Surface.ROTATION_90) {
            m_WorldAxisForDeviceAxisX = SensorManager.AXIS_Y;
            m_WorldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
        } else if (screenRotation == Surface.ROTATION_180) {
            m_WorldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
            m_WorldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Y;
        } else if (screenRotation == Surface.ROTATION_270) {
            m_WorldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Y;
            m_WorldAxisForDeviceAxisY = SensorManager.AXIS_X;
        }


        //Get the default sensor for the sensor type from the SenorManager
        boolean manuelFusion = false;
        _sensorManager = (SensorManager) mContext.getSystemService(Activity.SENSOR_SERVICE);

        if(manuelFusion) {
            Sensor gyro_sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Sensor accelerometer_sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//TYPE_GAME_ROTATION_VECTOR vs TYPE_ROTATION_VECTOR
            _sensorManager.registerListener(mListener, gyro_sensor, SensorManager.SENSOR_DELAY_GAME, 0);
            _sensorManager.registerListener(mListener, accelerometer_sensor, SensorManager.SENSOR_DELAY_GAME, 0);
        }
        else
        {
                Sensor sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
                if(!_sensorManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_GAME, 0)) {
                    // fallback
                    Sensor fallback_rotation_sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                    _sensorManager.registerListener(mListener, fallback_rotation_sensor, SensorManager.SENSOR_DELAY_GAME, 0);
                }

        }
    }

    public void GenerateCameraMatrix(float inEyeX, float inEyeY, float inEyeZ, float inCenterX, float inCenterY, float inCenterZ, int inViewportWidth, int inViewportHeight) {
        Matrix.setIdentityM(m_ModelViewMatrix, 0);
        //Matrix.setLookAtM(m_ModelViewMatrix, 0,
        //        inEyeX, inEyeY, inEyeZ,
        //        inCenterX, inCenterY, inCenterZ,
        //        0f, 1.0f, 0.0f);
        Matrix.setIdentityM(m_ModelingMatrix1, 0);
        Matrix.setIdentityM(m_ModelingMatrix2, 0);
        Matrix.setIdentityM(m_ModelingMatrix3, 0);
        //create matrices from the rotation angles
        Matrix.rotateM(m_ModelingMatrix1, 0, mOrientation[1] / (float) Math.PI * 180f, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(m_ModelingMatrix2, 0, mOrientation[0] / (float) Math.PI * 180f, 0.0f, 1.0f, 0.0f);
        //Matrix.rotateM(m_ModelingMatrix3, 0, mOrientation[2] / (float) Math.PI * 180f, 0.0f, 0.0f, 1.0f);

        //String debugs = "rotate: " + String.valueOf(mOrientation[0]);
        //Log.d("Sensor", "rotate: " + String.valueOf(mOrientation[2]));
        //combine the matrices into the model matrix
        Matrix.multiplyMM(m_ModelingMatrix3, 0, m_ModelingMatrix1, 0, m_ModelingMatrix2, 0);

        //native implementation of multiplyMM: http://androidxref.com/source/xref/frameworks/base/core/jni/android/opengl/util.cpp
        //BEWARE!: Native implementation has different semantics on ARM MALI/NVIDIA K1. One implementation multiplies in place the other
        //         on an stack intermediate. This leads to completely different results (it's broken on ARM MALI).
        //    FIX: ALWAYS ENSURE THAT ALL MATRICES PASSED ARE DISTINCT ARRAY REFERENCES! DO NOT PASS A REFERENCE USED AS RESULT ALSO AS OPERAND!
        Matrix.multiplyMM(m_TemporaryHelperMatrix, 0, m_ModelViewMatrix, 0, m_ModelingMatrix3, 0);
        System.arraycopy(m_TemporaryHelperMatrix, 0, m_ModelViewMatrix, 0, m_TemporaryHelperMatrix.length);

        //setup with view port with the width and height and reset the projection matrix accordingly
        GLES20.glViewport(0, 0, inViewportWidth, inViewportHeight);
        //http://www.learnopengles.com/understanding-opengls-matrices/
        //create the projection matrix using the aspect ratio */
        float aspectRatio = (float) inViewportWidth / (float) inViewportHeight;
        //Matrix.frustumM(this.m_ProjectionMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 1, 10);

        Matrix.setIdentityM(m_ProjectionMatrix, 0);
        Matrix.perspectiveM(m_ProjectionMatrix, 0, 45f, aspectRatio, 0.1f, 300f);
        //create model view projection matrix
        Matrix.multiplyMM(m_ModelViewProjectionMatrix, 0, m_ProjectionMatrix, 0, m_ModelViewMatrix, 0);

    }

    public float[] GetModelViewProjectionMatrix() {
        return m_ModelViewProjectionMatrix;
    }

    public float[] GetModelViewMatrix() {
        return m_ModelViewMatrix;
    }

    public void Update() {}

    public float[] GetCameraDirectionVector() {
        return mViewDirection;
    }

    private final SensorEventListener mListener = new SensorEventListener() {
        private float[] mGravity;
        private float[] mGeomagnetic;
        float azimut, pitch,roll;

        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                    pitch = orientation[1];
                    roll = orientation[2];

                    _sensorManager.getRotationMatrixFromVector(mViewRotationMatrix, orientation);
                    _sensorManager.remapCoordinateSystem(mViewRotationMatrix, m_WorldAxisForDeviceAxisX, m_WorldAxisForDeviceAxisY, mViewRotationMatrix);
                    _sensorManager.remapCoordinateSystem(mViewRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, mViewRotationMatrix);
                    _sensorManager.getOrientation(mViewRotationMatrix, mOrientation);
                }
            }

            if(event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR
                    || event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                _sensorManager.getRotationMatrixFromVector(mViewRotationMatrix, event.values);
                _sensorManager.remapCoordinateSystem(mViewRotationMatrix, m_WorldAxisForDeviceAxisX, m_WorldAxisForDeviceAxisY, mViewRotationMatrix);

                _sensorManager.remapCoordinateSystem(mViewRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, mViewRotationMatrix);
                _sensorManager.getOrientation(mViewRotationMatrix, mOrientation);
            }

            calcViewDirection();
        }

        private void calcViewDirection() {
            float[] screenDirection = new float[] {0f, 1f, 0f, 1f};

            float[] rotationZ = new float[16];
            float[] rotationX = new float[16];
            float[] rotationY = new float[16];

            float[] rotationXYZ = new float[16];

            Matrix.setRotateM(rotationZ, 0, -mOrientation[0] / (float) Math.PI * 180f, 0f, 0f, 1f);
            Matrix.setRotateM(rotationX, 0, -mOrientation[1] / (float) Math.PI * 180f, 1f, 0f, 0f);
            //Matrix.setRotateM(rotationY, 0, -mOrientation[2] / (float) Math.PI * 180f, 0f, 1f, 0f);
            Matrix.multiplyMM(rotationXYZ, 0, rotationZ, 0, rotationX, 0);
            Matrix.multiplyMM(rotationXYZ, 0, rotationXYZ, 0, rotationY, 0);

            Matrix.multiplyMV(mViewDirection, 0, rotationXYZ, 0, screenDirection, 0);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    };
}