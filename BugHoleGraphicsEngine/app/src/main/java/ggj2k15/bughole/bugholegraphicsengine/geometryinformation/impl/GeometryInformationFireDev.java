package ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl;

import ggj2k15.bughole.bugholegraphicsengine.interfaces.IGeometryInformation;

public class GeometryInformationFireDev implements IGeometryInformation, Runnable {

    private int m_NumBrainMines = 9;
    private int m_NumTunnelSegments = 6;

    private int m_NumberOfObjects = m_NumBrainMines + m_NumTunnelSegments;
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

    public GeometryInformationFireDev() {
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

        int i=0;
        i = MakeBrainMines(i);
        i = MakeTunnelSegments(i);
    }

    private int MakeBrainMines(int i_offset) {
        int object_index = i_offset;

        //generates a waving object pattern
        for (float y=-0.5f; y<0.5f+0.5f; y+=0.5f)
        {
            for (float x = -0.5f; x < 0.5f+0.5f; x += 0.5f)
            {
                m_ObjectXPositions[object_index] = x;
                m_ObjectYPositions[object_index] = y;
                m_ObjectZPositions[object_index] = (float)Math.sin(((x+y)*1.0)*0.4+m_Time*0.5)*20.0f;
                m_ObjectXRotations[object_index] = (m_Time+((x+y)*1.05f)) * 0.1f;
                m_ObjectYRotations[object_index] = m_Time  * 0.2f;
                m_ObjectZRotations[object_index] = m_Time * 0.3f;
                m_ObjectXScalings[object_index] = 0.2f;
                m_ObjectYScalings[object_index] = 0.2f;
                m_ObjectZScalings[object_index] = 0.2f;
                m_ObjectModelIdentification[object_index] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE;

                object_index++;
            }
        }

        return object_index;
    }

    private int MakeTunnelSegments(int i_offset) {
        int object_index = i_offset;

        //generate data for the tunnel segments
        m_IntestineScrollingOffset %= 8.0f;
        m_IntestineScrollingOffset -= 0.025f;

        float z_offset = 24.0f;
        float z_offset_decrement = 8.0f;

        for(int i=0; i<m_NumTunnelSegments; i++)
        {
            m_ObjectXPositions[object_index] = 0;
            m_ObjectYPositions[object_index] = 0;
            m_ObjectZPositions[object_index] = z_offset + m_IntestineScrollingOffset;
            m_ObjectXRotations[object_index] = 0;
            m_ObjectYRotations[object_index] = 1.570796f;
            m_ObjectZRotations[object_index] = 0;
            m_ObjectXScalings[object_index] = 1.0f;
            m_ObjectYScalings[object_index] = 1.0f;
            m_ObjectZScalings[object_index] = 1.0f;
            m_ObjectModelIdentification[object_index] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;

            z_offset -= z_offset_decrement;
            object_index++;
        }

        return object_index;
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
