package ggj2k15.bughole.bugholegraphicsengine.camerainformation.impl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import ggj2k15.bughole.bugholegraphicsengine.interfaces.ICameraInformation;

public class CameraInformationStubs implements ICameraInformation {

    //rotation angle in X and Y axis
    private float m_RotateX = 0.0f;
    private float m_RotateY = 360.0f;
    private float m_RotateZ = 0.0f;

    private float[] m_ModelViewMatrix = new float[16];
    private float[] m_ModelViewProjectionMatrix = new float[16];
    private float[] m_ProjectionMatrix = new float[16];
    private float[] m_ModelingMatrix1 = new float[16];
    private float[] m_ModelingMatrix2 = new float[16];
    private float[] m_ModelingMatrix3 = new float[16];
    private float[] m_ModelingMatrix4 = new float[16];
    private float[] m_TemporaryHelperMatrix = new float[16];
    private float[] m_ViewDirection = new float[4];

    public float[] GetModelViewMatrix() {
        return m_ModelViewMatrix;
    }

    public float[] GetModelViewProjectionMatrix() {
        return m_ModelViewProjectionMatrix;
    }

    public void GenerateCameraMatrix(float inEyeX, float inEyeY, float inEyeZ, float inCenterX, float inCenterY, float inCenterZ, int inViewportWidth, int inViewportHeight) {
        /*
        What the function is really doing is setting up a view matrix for you. It needs the eye position to establish
        where the camera will be. After that it will subtract eye position from center and normalize it to get a
        forward vector. Then it will cross the forward vector with the up vector to get a right vector. After
        normalizing all three, it can construct a matrix from those x,y,z vectors giving you a basic model view matrix.
        It just discretizes the math for you.
        */
        //setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ)
        Matrix.setIdentityM(m_ModelViewMatrix, 0);
        Matrix.setLookAtM(m_ModelViewMatrix, 0,
                inEyeX, inEyeY, inEyeZ,
                inCenterX, inCenterY, inCenterZ,
                0f, 1.0f, 0.0f);
        Matrix.setIdentityM(m_ModelingMatrix1, 0);
        Matrix.setIdentityM(m_ModelingMatrix2, 0);
        Matrix.setIdentityM(m_ModelingMatrix3, 0);
        Matrix.setIdentityM(m_ModelingMatrix4, 0);
        //create matrices from the rotation angles
        Matrix.setRotateM(m_ModelingMatrix1, 0, m_RotateX, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(m_ModelingMatrix2, 0, m_RotateY, 0.0f, 1.0f, 0.0f);
        Matrix.setRotateM(m_ModelingMatrix3, 0, m_RotateZ, 0.0f, 1.0f, 0.0f);

        //combine the matrices into the model matrix
        Matrix.multiplyMM(m_ModelingMatrix4, 0, m_ModelingMatrix1, 0, m_ModelingMatrix2, 0);
        Matrix.multiplyMM(m_TemporaryHelperMatrix, 0, m_ModelingMatrix3, 0, m_ModelingMatrix4, 0);
        System.arraycopy(m_TemporaryHelperMatrix, 0, m_ModelingMatrix4, 0, m_TemporaryHelperMatrix.length);

        //native implementation of multiplyMM: http://androidxref.com/source/xref/frameworks/base/core/jni/android/opengl/util.cpp
        //BEWARE!: Native implementation has different semantics on ARM MALI/NVIDIA K1. One implementation multiplies in place the other
        //         on an stack intermediate. This leads to completely different results (it's broken on ARM MALI).
        //    FIX: ALWAYS ENSURE THAT ALL MATRICES PASSED ARE DISTINCT ARRAY REFERENCES! DO NOT PASS A REFERENCE USED AS RESULT ALSO AS OPERAND!
        Matrix.multiplyMM(m_TemporaryHelperMatrix, 0, m_ModelViewMatrix, 0, m_ModelingMatrix4, 0);
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

    public void Update() {
        this.m_RotateX += 0.1f;
        this.m_RotateY += 0.2f;
        this.m_RotateZ += 0.5f;
    }

    public float[] GetCameraDirectionVector() {
        return m_ViewDirection;
    }
}
