package ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl;

import ggj2k15.bughole.bugholegraphicsengine.BuildConfig;
import ggj2k15.bughole.bugholegraphicsengine.interfaces.IGeometryInformation;

public class GeometryInformationFireDev implements IGeometryInformation, Runnable {

    private int m_NumBrainMinesX = 1;
    private int m_NumBrainMinesY = 1;
    private int m_NumTunnelSegments = 6;

    private int m_NumberOfObjects = (m_NumBrainMinesX * m_NumBrainMinesY) + m_NumTunnelSegments;
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
    private float m_TimeInterval = 0.02f;
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
        m_Time += m_TimeInterval;

        int num_scene_objects = 0;
        num_scene_objects = MakeBrainMines(num_scene_objects);
        num_scene_objects = MakeTunnelSegments(num_scene_objects);

        if(BuildConfig.DEBUG && (num_scene_objects != m_NumberOfObjects))
        {
            throw new AssertionError("incorrect number of objects in scene");
        }
    }

    private int MakeBrainMines(int i_offset) {
        int object_index = i_offset;

        //generates a waving object pattern
        float obj_distance = 0.4f;

        float y_start = 0.0f - (float)Math.floor((float)m_NumBrainMinesY / 2) * obj_distance + (((m_NumBrainMinesY+1) % 2) * (obj_distance / 2));
        float y_end = (float)Math.floor((float)m_NumBrainMinesY / 2) * obj_distance;

        float x_start = 0.0f - (float)Math.floor((float)m_NumBrainMinesX / 2) * obj_distance + (((m_NumBrainMinesX+1) % 2) * (obj_distance / 2));
        float x_end = (float)Math.floor((float)m_NumBrainMinesX / 2) * obj_distance;

        for (float y = y_start; y <= y_end; y += obj_distance)
        {
            for (float x = x_start; x <= x_end; x += obj_distance)
            {
                m_ObjectXPositions[object_index] = x;
                m_ObjectYPositions[object_index] = y;
                m_ObjectZPositions[object_index] = (float)Math.sin(((x + y) * 1.0) * 0.4 + m_Time * 0.5) * 20.0f;
                m_ObjectXRotations[object_index] = (m_Time + ((x + y) * 1.05f)) * 0.1f;
                m_ObjectYRotations[object_index] = m_Time * 0.2f;
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

        for(int i = 0; i < m_NumTunnelSegments; i++)
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
