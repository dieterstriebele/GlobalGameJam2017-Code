package ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl;

import ggj2k15.bughole.bugholegraphicsengine.interfaces.IGeometryInformation;

public class GeometryInformationStubs implements IGeometryInformation, Runnable {

    //9 brainmines
    //3 intestines
    private int m_NumberOfObjects = 9 + 6;
    private float[] m_ObjectXPositions;
    private float[] m_ObjectYPositions;
    private float[] m_ObjectZPositions;
    private float[] m_ObjectXRotations;
    private float[] m_ObjectYRotations;
    private float[] m_ObjectZRotations;
    private float[] m_ObjectXScalings;
    private float[] m_ObjectYScalings;
    private float[] m_ObjectZScalings;
    private int[] m_ObjectModelIdentification;
    private float m_Time;
    private float m_IntestineScrollingOffset;

    public GeometryInformationStubs() {
        m_ObjectXPositions = new float[m_NumberOfObjects];
        m_ObjectYPositions = new float[m_NumberOfObjects];
        m_ObjectZPositions = new float[m_NumberOfObjects];
        m_ObjectXRotations = new float[m_NumberOfObjects];
        m_ObjectYRotations = new float[m_NumberOfObjects];
        m_ObjectZRotations = new float[m_NumberOfObjects];
        m_ObjectXScalings = new float[m_NumberOfObjects];
        m_ObjectYScalings = new float[m_NumberOfObjects];
        m_ObjectZScalings = new float[m_NumberOfObjects];
        m_ObjectModelIdentification = new int[m_NumberOfObjects];
    }

    public void SynchronizeState() {
        m_Time += 0.01f;
        //generate data for the brain mines
        int i=0;
        //generates a waving flag object pattern of 10x10 objects
        for (float y=-0.5f; y<0.5f+0.5f; y+=0.5f) {
            for (float x = -0.5f; x < 0.5f+0.5f; x += 0.5f) {
                m_ObjectXPositions[i] = x;
                m_ObjectYPositions[i] = y;
                m_ObjectZPositions[i] = (float)Math.sin(((x+y)*1.0)*0.4+m_Time*0.5)*2.0f;
                m_ObjectXRotations[i] = (m_Time+((x+y)*1.05f)) * 0.1f;
                m_ObjectYRotations[i] = m_Time  * 0.2f;
                m_ObjectZRotations[i] = m_Time * 0.3f;
                m_ObjectXScalings[i] = 0.2f;
                m_ObjectYScalings[i] = 0.2f;
                m_ObjectZScalings[i] = 0.2f;
                m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE;
                i++;
            }
        }
        //generate data for the tunnel segments
        m_IntestineScrollingOffset %= 8.0f;
        m_IntestineScrollingOffset -= 0.025f;

        m_ObjectXPositions[i] = 0;
        m_ObjectYPositions[i] = 0;
        m_ObjectZPositions[i] = 24.0f+m_IntestineScrollingOffset;
        m_ObjectXRotations[i] = 0;
        m_ObjectYRotations[i] = 1.570796f;
        m_ObjectZRotations[i] = 0;
        m_ObjectXScalings[i] = 1.0f;
        m_ObjectYScalings[i] = 1.0f;
        m_ObjectZScalings[i] = 1.0f;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
        i++;
        m_ObjectXPositions[i] = 0;
        m_ObjectYPositions[i] = 0;
        m_ObjectZPositions[i] = 16.0f+m_IntestineScrollingOffset;
        m_ObjectXRotations[i] = 0;
        m_ObjectYRotations[i] = 1.570796f;
        m_ObjectZRotations[i] = 0;
        m_ObjectXScalings[i] = 1.0f;
        m_ObjectYScalings[i] = 1.0f;
        m_ObjectZScalings[i] = 1.0f;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
        i++;
        m_ObjectXPositions[i] = 0;
        m_ObjectYPositions[i] = 0;
        m_ObjectZPositions[i] = 8.0f+m_IntestineScrollingOffset;
        m_ObjectXRotations[i] = 0;
        m_ObjectYRotations[i] = 1.570796f;
        m_ObjectZRotations[i] = 0;
        m_ObjectXScalings[i] = 1.0f;
        m_ObjectYScalings[i] = 1.0f;
        m_ObjectZScalings[i] = 1.0f;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
        i++;
        m_ObjectXPositions[i] = 0;
        m_ObjectYPositions[i] = 0;
        m_ObjectZPositions[i] = 0.0f+m_IntestineScrollingOffset;
        m_ObjectXRotations[i] = 0;
        m_ObjectYRotations[i] = 1.570796f;
        m_ObjectZRotations[i] = 0;
        m_ObjectXScalings[i] = 1.0f;
        m_ObjectYScalings[i] = 1.0f;
        m_ObjectZScalings[i] = 1.0f;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
        i++;
        m_ObjectXPositions[i] = 0;
        m_ObjectYPositions[i] = 0;
        m_ObjectZPositions[i] = -8.0f+m_IntestineScrollingOffset;
        m_ObjectXRotations[i] = 0;
        m_ObjectYRotations[i] = 1.570796f;
        m_ObjectZRotations[i] = 0;
        m_ObjectXScalings[i] = 1.0f;
        m_ObjectYScalings[i] = 1.0f;
        m_ObjectZScalings[i] = 1.0f;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
        i++;
        m_ObjectXPositions[i] = 0;
        m_ObjectYPositions[i] = 0;
        m_ObjectZPositions[i] = -16.0f+m_IntestineScrollingOffset;
        m_ObjectXRotations[i] = 0;
        m_ObjectYRotations[i] = 1.570796f;
        m_ObjectZRotations[i] = 0;
        m_ObjectXScalings[i] = 1.0f;
        m_ObjectYScalings[i] = 1.0f;
        m_ObjectZScalings[i] = 1.0f;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
        i++;
    }

    public void SwapState() { }

    public int GetNumberOfObjects() {
        return m_NumberOfObjects;
    }

    public float GetObjectXPosition(int inObjectIndex) { return m_ObjectXPositions[inObjectIndex]; }

    public float GetObjectYPosition(int inObjectIndex) { return m_ObjectYPositions[inObjectIndex]; }

    public float GetObjectZPosition(int inObjectIndex) {
        return m_ObjectZPositions[inObjectIndex];
    }

    public float GetObjectXRotation(int inObjectIndex) {
        return m_ObjectXRotations[inObjectIndex];
    }

    public float GetObjectYRotation(int inObjectIndex) {
        return m_ObjectYRotations[inObjectIndex];
    }

    public float GetObjectZRotation(int inObjectIndex) {
        return m_ObjectZRotations[inObjectIndex];
    }

    public float GetObjectXScaling(int inObjectIndex) { return m_ObjectXScalings[inObjectIndex]; }

    public float GetObjectYScaling(int inObjectIndex) { return m_ObjectYScalings[inObjectIndex]; }

    public float GetObjectZScaling(int inObjectIndex) { return m_ObjectZScalings[inObjectIndex]; }

    public int GetObjectModelIdentification(int inObjectIndex) { return m_ObjectModelIdentification[inObjectIndex]; }

    public void run() {
        //Zzz ...
    }
}
