package ggj2k15.bughole.bugholegraphicsengine.camerainformation.impl;

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

import ggj2k15.bughole.bugholegraphicsengine.interfaces.ICamera_Information;

public class Camera_Information_Legacy implements ICamera_Information {
    private SensorManager _sensorManager;
    private Context mContext;

    private float[] mOrientation = new float[3];
    private float[] mViewDirection = new float[4];
    private final float[] mViewRotationMatrix = new float[16];
    private float[] mRemapedViewRotationForDisplayMatrix = new float[16];
    private float[] mRemapedViewRotationForViewDirectionMatrix = new float[16];

    private float[] m_ModelViewProjectionMatrix = new float[16];
    private float[] m_ModelViewMatrix = new float[16];
    private float[] m_ProjectionMatrix = new float[16];
    private float[] m_ModelingMatrix1 = new float[16];
    private float[] m_ModelingMatrix2 = new float[16];
    private float[] m_ModelingMatrix3 = new float[16];
    private float[] m_TemporaryHelperMatrix = new float[16];

    public Camera_Information_Legacy(Context context) {
        mContext = context;
        //Get the default sensor for the sensor type from the SenorManager
        _sensorManager = (SensorManager) mContext.getSystemService(Activity.SENSOR_SERVICE);
        //sensorType is either Sensor.TYPE_STEP_COUNTER or Sensor.TYPE_STEP_DETECTOR
        List<Sensor> deviceSensors = _sensorManager.getSensorList(Sensor.TYPE_ALL);
        Sensor sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        final boolean batchMode = _sensorManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_GAME, 0);
    }

    public void GenerateCameraMatrix(float inEyeX, float inEyeY, float inEyeZ, float inCenterX, float inCenterY, float inCenterZ, int inViewportWidth, int inViewportHeight) {
        Matrix.setIdentityM(m_ModelViewMatrix, 0);
        Matrix.setLookAtM(m_ModelViewMatrix, 0,
                inEyeX, inEyeY, inEyeZ,
                inCenterX, inCenterY, inCenterZ,
                0f, 1.0f, 0.0f);
        Matrix.setIdentityM(m_ModelingMatrix1, 0);
        Matrix.setIdentityM(m_ModelingMatrix2, 0);
        Matrix.setIdentityM(m_ModelingMatrix3, 0);
        //create matrices from the rotation angles
        Matrix.setRotateM(m_ModelingMatrix1, 0, -mOrientation[1] / (float) Math.PI * 180f, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(m_ModelingMatrix2, 0, mOrientation[0] / (float) Math.PI * 180f, 0.0f, 1.0f, 0.0f);
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

        public void onSensorChanged(SensorEvent event) {
            //_sensorManager.getRotationMatrixFromVector(mViewRotationMatrix, lowPass(event.values, sensorValues));
            _sensorManager.getRotationMatrixFromVector(mViewRotationMatrix, event.values);
            // _sensorManager.getRotationMatrixFromVector(mViewRotationMatrix, getSmoothedSensorDirection(event.values));

            // By default, remap the axes as if the front of the
            // device screen was the instrument panel.
            int worldAxisForDeviceAxisX = SensorManager.AXIS_X;
            int worldAxisForDeviceAxisY = SensorManager.AXIS_Y;

            // Adjust the rotation matrix for the device orientation
            int screenRotation = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            if (screenRotation == Surface.ROTATION_0) {
                worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_Y;
            } else if (screenRotation == Surface.ROTATION_90) {
                worldAxisForDeviceAxisX = SensorManager.AXIS_Y;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
            } else if (screenRotation == Surface.ROTATION_180) {
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Y;
            } else if (screenRotation == Surface.ROTATION_270) {
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Y;
                worldAxisForDeviceAxisY = SensorManager.AXIS_X;
            }

            //_sensorManager.remapCoordinateSystem(mViewRotationMatrix, worldAxisForDeviceAxisX, worldAxisForDeviceAxisY, mRemapedViewRotationForDisplayMatrix);

            _sensorManager.remapCoordinateSystem(mViewRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, mRemapedViewRotationForViewDirectionMatrix);
            _sensorManager.getOrientation(mRemapedViewRotationForViewDirectionMatrix, mOrientation);

            calcViewDirection();
        }

        private void calcViewDirection() {
            float[] screenDirection = new float[] {0f, 1f, 0f, 1f};

            float[] rotationZ = new float[16];
            float[] rotationX = new float[16];

            Matrix.setRotateM(rotationZ, 0, -mOrientation[0] / (float) Math.PI * 180f, 0f, 0f, 1f);
            Matrix.setRotateM(rotationX, 0, -mOrientation[1] / (float) Math.PI * 180f, 1f, 0f, 0f);
            Matrix.multiplyMM(rotationX, 0, rotationZ, 0, rotationX, 0);

            Matrix.multiplyMV(mViewDirection, 0, rotationX, 0, screenDirection, 0);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    };
}