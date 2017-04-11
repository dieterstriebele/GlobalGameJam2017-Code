package ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import ggj2k15.bughole.bugholegraphicsengine.R;
import ggj2k15.bughole.bugholegraphicsengine.interfaces.IGeometryInformation;

public class GeometryInformationStubsPath implements IGeometryInformation, Runnable {

    //1 brainmines (position from path)
    //6 intestines
    private int m_NumberOfObjects = 1; // + 6;
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

    public GeometryInformationStubsPath(Context a_Context) {
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

        m_ObjectPathPositions_00 = LoadObjectPositionsFromInputStream(a_Context.getResources().openRawResource(R.raw.metaball_intestines_middlepath));
        //m_ObjectPathPositions_01 = LoadObjectPositionsFromInputStream(a_Context.getResources().openRawResource(R.raw.intestines_triplepath_001_kbap));
        //m_ObjectPathPositions_02 = LoadObjectPositionsFromInputStream(a_Context.getResources().openRawResource(R.raw.intestines_triplepath_002_kbap));
        //m_ObjectPathPositions_03 = LoadObjectPositionsFromInputStream(a_Context.getResources().openRawResource(R.raw.intestines_triplepath_003_kbap));

        Log.d("GeometryInformationStubsPath.LoadObjectPositionsFromInputStream()", "Loading object positions from input stream!");

    }

    private float[] m_ObjectPathPositions_00 = null;
    private float[] m_ObjectPathPositions_01 = null;
    private float[] m_ObjectPathPositions_02 = null;
    private float[] m_ObjectPathPositions_03 = null;

    protected float[] LoadObjectPositionsFromInputStream(InputStream inInputStream) {
        Log.d("GeometryInformationStubsPath.LoadObjectPositionsFromInputStream()", "Loading object positions from input stream!");
        float[] tObjectPathPositions = null;
        try {
            DataInputStream obfInputStream = new DataInputStream(new BufferedInputStream(inInputStream));
            int tObjectPathPositionsCount = (int)obfInputStream.readFloat();
            tObjectPathPositions = new float[tObjectPathPositionsCount * 3];
            for (int i=0; i<tObjectPathPositions.length; i++) {
                tObjectPathPositions[i] = obfInputStream.readFloat();
            }
        } catch (IOException e) {
            Log.e("GeometryInformationStubsPath.LoadObjectPositionsFromInputStream()", "Constructor failed!", e);
        }
        return tObjectPathPositions;
    }

    private float movecounter = 0.0f;

    //BEWARE: Exported pathes from Blender are in Blender coordinate system, which is different from the one OpenGL is using.
    //        See here for further details: https://www.blender.org/forum/viewtopic.php?t=26417
    //        In Essence:
    //        Say Blender coords are (X, Y, Z), OpenGL are (X', Y', Z'). Then the coordinate orientations are:
    //        Blender: X to right, Y away from you, Z up.
    //        OpenGL: X' to right, Y' up, Z' towards you.
    //        Therefore:
    //        X' = X
    //        Y' = Z
    //        Z' = -Y

    public void SynchronizeState() {
        m_Time += 1.00f;

        int i=0;

        int m_PathSegmentOffset_00 = (int)m_Time*1 % (m_ObjectPathPositions_00.length/3);
        m_PathSegmentOffset_00 *= 3;
/*
        //hardcode one brainmine following an exported path
        //int m_PathSegmentOffset_01 = (int)(m_Time + Math.abs(Math.sin(m_Time * 0.001f) * 1000.0f)) % (m_ObjectPathPositions_01.length/3);
        int m_PathSegmentOffset_01 = (int)m_Time*1 % (m_ObjectPathPositions_01.length/3);
        m_PathSegmentOffset_01 *= 3;
        //int m_PathSegmentOffset_02 = (int)(m_Time + Math.abs(Math.cos(m_Time * 0.001f) * 1000.0f)) % (m_ObjectPathPositions_02.length/3);
        int m_PathSegmentOffset_02 = (int)m_Time*1 % (m_ObjectPathPositions_02.length/3);
        m_PathSegmentOffset_02 *= 3;
        //int m_PathSegmentOffset_03 = (int)(m_Time + Math.abs(Math.sin(m_Time * 0.001f) * 1000.0f)) % (m_ObjectPathPositions_03.length/3);
        int m_PathSegmentOffset_03 = (int)m_Time*1 % (m_ObjectPathPositions_03.length/3);
        m_PathSegmentOffset_03 *= 3;
*/
        int m_PathSegmentOffset_04 = 0;
        m_PathSegmentOffset_04 *= 3;

        float brainmine_scaling = 1.0f;
/*
        m_ObjectXPositions[i] =  m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 0] + m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 0];
        m_ObjectYPositions[i] =  m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 2] + m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 2];
        m_ObjectZPositions[i] = -m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 1] + -m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 1];
        m_ObjectXRotations[i] = 0.0f;
        m_ObjectYRotations[i] = 0.0f;
        m_ObjectZRotations[i] = 0.0f;
        m_ObjectXScalings[i] = 1.0f;
        m_ObjectYScalings[i] = 1.0f;
        m_ObjectZScalings[i] = 1.0f;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_MATRIXMINE;
        i++;


        m_ObjectXPositions[i] = m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 0] + m_ObjectPathPositions_02[m_PathSegmentOffset_02 + 0];
        m_ObjectYPositions[i] = m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 2] + m_ObjectPathPositions_02[m_PathSegmentOffset_02 + 2];
        m_ObjectZPositions[i] = -m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 1] + -m_ObjectPathPositions_02[m_PathSegmentOffset_02 + 1];
        m_ObjectXRotations[i] = 0.0f;
        m_ObjectYRotations[i] = 0.0f;
        m_ObjectZRotations[i] = 0.0f;
        m_ObjectXScalings[i] = brainmine_scaling;
        m_ObjectYScalings[i] = brainmine_scaling;
        m_ObjectZScalings[i] = brainmine_scaling;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE;
        i++;

        m_ObjectXPositions[i] = m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 0] + m_ObjectPathPositions_03[m_PathSegmentOffset_03 + 0];
        m_ObjectYPositions[i] = m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 2] + m_ObjectPathPositions_03[m_PathSegmentOffset_03 + 2];
        m_ObjectZPositions[i] = -m_ObjectPathPositions_01[m_PathSegmentOffset_01 + 1] + -m_ObjectPathPositions_03[m_PathSegmentOffset_03 + 1];
        m_ObjectXRotations[i] = 0.0f;
        m_ObjectYRotations[i] = 0.0f;
        m_ObjectZRotations[i] = 0.0f;
        m_ObjectXScalings[i] = brainmine_scaling;
        m_ObjectYScalings[i] = brainmine_scaling;
        m_ObjectZScalings[i] = brainmine_scaling;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_VIRUSMINE;
        i++;
*/

        //m_ObjectXPositions[i] = 0.0f;
        //m_ObjectYPositions[i] = 0.0f;
        //m_ObjectZPositions[i] = 0.0f;

        //movecounter += 0.01f;
        Log.d("GeometryInformationStubsPath", "m_PathSegmentOffset_00="+m_PathSegmentOffset_00 + " m_ObjectPathPositions_00="+m_ObjectPathPositions_00.length);
        m_ObjectXPositions[i] = m_ObjectPathPositions_00[m_PathSegmentOffset_00 + 0];
        m_ObjectYPositions[i] = m_ObjectPathPositions_00[m_PathSegmentOffset_00 + 2];
        m_ObjectZPositions[i] = -m_ObjectPathPositions_00[m_PathSegmentOffset_00 + 1];
        m_ObjectXRotations[i] = 0.0f;
        m_ObjectYRotations[i] = 0.0f;
        m_ObjectZRotations[i] = 0.0f;
        m_ObjectXScalings[i] = 1.0f;
        m_ObjectYScalings[i] = 1.0f;
        m_ObjectZScalings[i] = 1.0f;
        m_ObjectModelIdentification[i] = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_METABALL;
        i++;

        /*
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
        */
        /*
        //generate data for the brain mines
        int i=0;
        //generates a waving flag object pattern of 10x10 objects
        for (float y=-0.5f; y<0.5f+0.5f; y+=0.5f) {
            for (float x = -0.5f; x < 0.5f+0.5f; x += 0.5f) {
                m_ObjectXPositions[i] = x;
                m_ObjectYPositions[i] = y;
                m_ObjectZPositions[i] = (float)Math.sin(((x+y)*4.0)*0.4+m_Time*0.5)*9.0f;
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
        */
/*
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
        */
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
