package ggj2k15.bughole.bugholegraphicsengine.camerainformation.impl;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import ggj2k15.bughole.bugholegraphicsengine.interfaces.ICamera_Information;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;

/**
 * Created by vasco_000 on 31.01.2016.
 */
public class Camera_Information_WithoutDrift implements ICamera_Information {
    private SensorManager _sensorManager;
    private Context mContext;


    private float[] mSmoothOrientation = new float[3];
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

    public Camera_Information_WithoutDrift(Context context) {

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
            m_WorldAxisForDeviceAxisY = SensorManager.AXIS_X;
        } else if (screenRotation == Surface.ROTATION_180) {
            m_WorldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
            m_WorldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Y;
        } else if (screenRotation == Surface.ROTATION_270) {
            m_WorldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Y;
            m_WorldAxisForDeviceAxisY = SensorManager.AXIS_X;
        }


        //Get the default sensor for the sensor type from the SenorManager
        boolean manuelFusion = true;
        _sensorManager = (SensorManager) mContext.getSystemService(Activity.SENSOR_SERVICE);

        if(manuelFusion) {
            Sensor gyro_sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            _sensorManager.registerListener(mListener, gyro_sensor, SensorManager.SENSOR_DELAY_GAME, 0);
            if(false) {
                Sensor magnetic_field = _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                Sensor accelerometer_sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//TYPE_GAME_ROTATION_VECTOR vs TYPE_ROTATION_VECTOR
                _sensorManager.registerListener(mListener, magnetic_field, SensorManager.SENSOR_DELAY_GAME, 0);
                _sensorManager.registerListener(mListener, accelerometer_sensor, SensorManager.SENSOR_DELAY_GAME, 0);
            }
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
        Matrix.rotateM(m_ModelingMatrix1, 0, mOrientation[0] / (float) Math.PI * 180f, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(m_ModelingMatrix2, 0, mOrientation[1] / (float) Math.PI * 180f, 0.0f, 1.0f, 0.0f);
        //Matrix.rotateM(m_ModelingMatrix3, 0, mOrientation[2] / (float) Math.PI * 180f, 0.0f, 0.0f, 1.0f);

        //String debugs = "rotate: " + String.valueOf(mOrientation[0]);
        if(false) {
            Log.d("Sensor", "rotate Y: " + String.valueOf(mOrientation[2]));
            Log.d("Sensor", "rotate X: " + String.valueOf(mOrientation[0]));
        }
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

    public void Reset()
    {
        mSmoothOrientation[0] = 0;
        mSmoothOrientation[1] = 0;;//orientation[2];
        mSmoothOrientation[2] = 0;//orientation[0];
    }

    public float[] GetCameraDirectionVector() {
        return mViewDirection;
    }
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    float EPSILON = 0.001f;

    private final SensorEventListener mListener = new SensorEventListener() {
        private float[] mGravity = new float[3];;
        private float[] mGeomagnetic  = new float[3];;
        float azimut, pitch,roll;

        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            {
                // This timestep's delta rotation to be multiplied by the current rotation
                // after computing it from the gyro sample data.
                if (timestamp != 0) {
                    final float dT = (event.timestamp - timestamp) * NS2S;
                    // Axis of the rotation sample, not normalized yet.
                    float axisX = event.values[0];
                    float axisY = event.values[1];
                    float axisZ = event.values[2];

                    // Calculate the angular speed of the sample
                    float omegaMagnitude = sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

                    // Normalize the rotation vector if it's big enough to get the axis
                    if (omegaMagnitude > EPSILON) {
                        axisX /= omegaMagnitude;
                        axisY /= omegaMagnitude;
                        axisZ /= omegaMagnitude;
                    }

                    // Integrate around this axis with the angular speed by the timestep
                    // in order to get a delta rotation from this sample over the timestep
                    // We will convert this axis-angle representation of the delta rotation
                    // into a quaternion before turning it into the rotation matrix.
                    float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                    float sinThetaOverTwo = sin(thetaOverTwo);
                    float cosThetaOverTwo = cos(thetaOverTwo);
                    deltaRotationVector[0] = sinThetaOverTwo * axisX;
                    deltaRotationVector[1] = sinThetaOverTwo * axisY;
                    deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                    deltaRotationVector[3] = cosThetaOverTwo;
                }
                timestamp = event.timestamp;
                float[] deltaRotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);

                //for (int i = 0; i < mViewRotationMatrix.length; ++i) {
                //    mViewRotationMatrix[i] = mViewRotationMatrix[i] + deltaRotationMatrix[i];
                //}

                float[] deltaOrientation = new float[3];
                SensorManager.getOrientation(deltaRotationMatrix, deltaOrientation);
                mSmoothOrientation[0] += deltaOrientation[0];
                mSmoothOrientation[1] += deltaOrientation[1];
                mSmoothOrientation[2] += deltaOrientation[2];

                SensorManager.getRotationMatrixFromVector(mViewRotationMatrix, mSmoothOrientation);

                int screenRotation = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
                if (screenRotation == Surface.ROTATION_0) {
                    // Nexus 10
                    mOrientation[0] = mSmoothOrientation[1];
                    mOrientation[1] = -mSmoothOrientation[2] + (float)Math.PI; // rotate the view to fly direction
                    mOrientation[2] = mSmoothOrientation[0];
                } else if (screenRotation == Surface.ROTATION_90) {
                    mOrientation[0] = mSmoothOrientation[2];
                    mOrientation[1] = mSmoothOrientation[1] + (float)Math.PI; // rotate the view to fly direction
                    mOrientation[2] = mSmoothOrientation[0];
                } else if (screenRotation == Surface.ROTATION_180) {
                    mOrientation[0] = -mSmoothOrientation[0];
                    mOrientation[1] = -mSmoothOrientation[1] + (float)Math.PI; // rotate the view to fly direction
                    mOrientation[2] = mSmoothOrientation[2];
                } else if (screenRotation == Surface.ROTATION_270) {
                    mOrientation[0] = mSmoothOrientation[1];
                    mOrientation[1] = -mSmoothOrientation[0] + (float)Math.PI; // rotate the view to fly direction
                    mOrientation[2] = mSmoothOrientation[2];
                }

                mViewDirection[0] = mOrientation[0];
                mViewDirection[1] = mOrientation[1];
                mViewDirection[2] = mOrientation[2];
                mViewDirection[3] = 0;
                //mOrientation[1] = mSmoothOrientation[1];
                // mOrientation[2] = mSmoothOrientation[0];

                /* Rotate for every tablet generic solution, does not work :/ */
/*
                mOrientation[0] = mSmoothOrientation[0];
                mOrientation[1] = mSmoothOrientation[1];
                mOrientation[2] = mSmoothOrientation[2];


                final float[] remapedRotation1 = new float[16];
                final float[] remapedRotation2 = new float[16];
                _sensorManager.remapCoordinateSystem(mViewRotationMatrix, m_WorldAxisForDeviceAxisX, m_WorldAxisForDeviceAxisY, remapedRotation1);
                //_sensorManager.remapCoordinateSystem(remapedRotation1, SensorManager.AXIS_X, SensorManager.AXIS_Z, remapedRotation2);
                _sensorManager.getOrientation(remapedRotation1, mOrientation);*/



            }

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // System.arraycopy(event.values, 0, mGravity, 0, 3);

                final float alpha = 0.8f;

                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];

            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                System.arraycopy(event.values, 0, mGeomagnetic, 0, 3);
            }
            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, mOrientation);
                    azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                    pitch = orientation[1];
                    roll = orientation[2];

                    //orientation[1] = 0;
                    //orientation[2] = 0;
                    if(true) {
                        //_sensorManager.getOrientation(mViewRotationMatrix, orientation);
                        Log.d("Sensor", "rotate Y: " + String.valueOf(mOrientation[2]) + " X: " + String.valueOf(mOrientation[0])+ " Z: " + String.valueOf(mOrientation[1]));
                    }

                    //_sensorManager.getRotationMatrixFromVector(mViewRotationMatrix, orientation);
                    _sensorManager.remapCoordinateSystem(mViewRotationMatrix, m_WorldAxisForDeviceAxisX, m_WorldAxisForDeviceAxisY, mViewRotationMatrix);
                    //_sensorManager.remapCoordinateSystem(mViewRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, mViewRotationMatrix);
                    _sensorManager.getOrientation(mViewRotationMatrix, mOrientation);
                    mOrientation[0] = 0;
                    //mOrientation[1] = orientation[1];;//orientation[2];
                    mOrientation[2] = 0;//orientation[0];
                }
            }

            if(event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR
                    || event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                _sensorManager.getRotationMatrixFromVector(mViewRotationMatrix, event.values);
                if(false) {
                    _sensorManager.getOrientation(mViewRotationMatrix, mOrientation);
                    Log.d("Sensor", "rotate Y: " + String.valueOf(mOrientation[1]) + "rotate X: " + String.valueOf(mOrientation[0]));
                }

                _sensorManager.remapCoordinateSystem(mViewRotationMatrix, m_WorldAxisForDeviceAxisX, m_WorldAxisForDeviceAxisY, mViewRotationMatrix);

                //_sensorManager.remapCoordinateSystem(mViewRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, mViewRotationMatrix);
                _sensorManager.getOrientation(mViewRotationMatrix, mOrientation);
            }

            //calcViewDirection();
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

    };
}