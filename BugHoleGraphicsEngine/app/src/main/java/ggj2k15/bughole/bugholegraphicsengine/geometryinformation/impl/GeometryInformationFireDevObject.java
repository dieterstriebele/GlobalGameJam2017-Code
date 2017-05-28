package ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl;

/**
 * Created by ewers on 26.05.2017.
 */

public class GeometryInformationFireDevObject {
    public float[] pos;   //x, y, z
    public float[] rot;   //x, y, z
    public float[] scale; //x, y, z
    public int modelTypeIdentifier;
    public boolean alive;

    public GeometryInformationFireDevObject()
    {
        pos = new float[3];
        rot = new float[3];
        scale = new float[3];
        modelTypeIdentifier = -1;
        alive = true;
    }
}
