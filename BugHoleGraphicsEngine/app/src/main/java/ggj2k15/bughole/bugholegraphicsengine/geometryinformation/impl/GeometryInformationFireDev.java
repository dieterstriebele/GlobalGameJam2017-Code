package ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl;

import java.util.ArrayList;
import java.util.Collections;

import ggj2k15.bughole.bugholegraphicsengine.BuildConfig;
import ggj2k15.bughole.bugholegraphicsengine.clientinformation.impl.ClientInformationFireDev;
import ggj2k15.bughole.bugholegraphicsengine.interfaces.IGeometryInformation;

public class GeometryInformationFireDev implements IGeometryInformation, Runnable {

    private ClientInformationFireDev m_clientInformationFireDev;

    private int m_NumBrainMinesX = 3;
    private int m_NumBrainMinesY = 1;
    private int m_NumBrainMinesTotal = m_NumBrainMinesX * m_NumBrainMinesY;
    private int m_NumTunnelSegments = 12;
    private int m_NumberOfObjects = m_NumBrainMinesTotal + m_NumTunnelSegments;
    private ArrayList<GeometryInformationFireDevObject> m_objects;

    private float m_Time;
    private float m_TimeInterval = 0.02f;
    private float m_IntestineScrollingOffset;

    public GeometryInformationFireDev()
    {
        //setup initial scene configuration, m_objects will be altered dynamically later
        m_objects = new ArrayList<>(m_NumberOfObjects);
        for(int i=0; i<m_NumberOfObjects; i++)
        {
            m_objects.add(new GeometryInformationFireDevObject());
        }
    }

    public void SynchronizeState()
    {
        m_Time += m_TimeInterval;

        int num_scene_objects = 0;
        num_scene_objects = MakeBrainMines(num_scene_objects);
        num_scene_objects = MakeTunnelSegments(num_scene_objects);

        if(BuildConfig.DEBUG && (num_scene_objects != m_NumberOfObjects))
        {
            throw new AssertionError("incorrect number of objects in scene");
        }
    }

    private int MakeBrainMines(int i_offset)
    {
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
                m_objects.get(object_index).pos[0]   = x;
                m_objects.get(object_index).pos[1]   = y;
                m_objects.get(object_index).pos[2]   = (float)Math.sin(((x + y) * 1.0) * 0.4 + m_Time * 0.5) * 20.0f;
                m_objects.get(object_index).rot[0]   = (m_Time + ((x + y) * 1.05f)) * 0.1f;
                m_objects.get(object_index).rot[1]   = m_Time * 0.2f;
                m_objects.get(object_index).rot[2]   = m_Time * 0.3f;
                m_objects.get(object_index).scale[0] = 0.2f;
                m_objects.get(object_index).scale[1] = 0.2f;
                m_objects.get(object_index).scale[2] = 0.2f;
                m_objects.get(object_index).modelTypeIdentifier = IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE;

                object_index++;
            }
        }

        return object_index;
    }

    private int MakeTunnelSegments(int i_offset)
    {
        int object_index = i_offset;

        float scroll_offset = 8.0f;

        //generate data for the tunnel segments
        m_IntestineScrollingOffset %= scroll_offset;
        m_IntestineScrollingOffset -= 0.025f;

        float z_offset = m_NumTunnelSegments / 2 * scroll_offset;
        float z_offset_decrement = scroll_offset;

        for(int i = 0; i < m_NumTunnelSegments; i++)
        {
            m_objects.get(object_index).pos[0]   = 0;
            m_objects.get(object_index).pos[1]   = 0;
            m_objects.get(object_index).pos[2]   = z_offset + m_IntestineScrollingOffset;
            m_objects.get(object_index).rot[0]   = 0;
            m_objects.get(object_index).rot[1]   = 1.570796f;
            m_objects.get(object_index).rot[2]   = 0;
            m_objects.get(object_index).scale[0] = 1.0f;
            m_objects.get(object_index).scale[1] = 1.0f;
            m_objects.get(object_index).scale[2] = 1.0f;
            m_objects.get(object_index).modelTypeIdentifier = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;

            z_offset -= z_offset_decrement;
            object_index++;
        }

        return object_index;
    }

    public void SwapState()
    {
        //...
    }

    public int GetNumberOfObjects()
    {
        return m_NumberOfObjects;
    }

    public float GetObjectXPosition(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).pos[0];
    }

    public float GetObjectYPosition(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).pos[1];
    }

    public float GetObjectZPosition(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).pos[2];
    }

    public float GetObjectXRotation(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).rot[0];
    }

    public float GetObjectYRotation(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).rot[1];
    }

    public float GetObjectZRotation(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).rot[2];
    }

    public float GetObjectXScaling(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).scale[0];
    }

    public float GetObjectYScaling(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).scale[1];
    }

    public float GetObjectZScaling(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).scale[2];
    }

    public int GetObjectModelIdentification(int inObjectIndex)
    {
        return m_objects.get(inObjectIndex).modelTypeIdentifier;
    }

    public void run()
    {
        //Zzz ...
    }

    public void RegisterClientInformation(ClientInformationFireDev clientInformationFireDev)
    {
        m_clientInformationFireDev = clientInformationFireDev;
    }

    //todo: returns index of the object that was hit by the shot, otherwise returns -1
    //returns index of the object that was hit by the shot, otherwise returns -1
    public int FireAt(float[] cameraDirection)
    {
        boolean collisionDetected = false;
        if(collisionDetected)
        {
            int objectIdToDestroy = 0;
            DestroyObject(objectIdToDestroy);
            return objectIdToDestroy;
        }

        return -1;
    }

    //todo: remove object from arrays
    public void DestroyObject(int objectIndex)
    {
        //todo: this is a real problem! we create objects procedurally every single time that SynchronizeState is called, that needs to change!
    }
}
