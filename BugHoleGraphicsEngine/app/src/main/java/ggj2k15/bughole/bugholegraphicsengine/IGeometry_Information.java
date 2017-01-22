package ggj2k15.bughole.bugholegraphicsengine;

public interface IGeometry_Information {

    //update position and rotation of all objects (will be called every frame)
    public void SynchronizeState();

    //swap internal state with recently updated one obtained wia SynchronizeState()
    public void SwapState();

    //total number of object that are synchronized
    public int GetNumberOfObjects();

    //absolute world x coordinate of the object
    public float GetObjectXPosition(int inObjectIndex);

    //absolute world y coordinate of the object
    public float GetObjectYPosition(int inObjectIndex);

    //absolute world z coordinate of the object
    public float GetObjectZPosition(int inObjectIndex);

    //absolute rotation around the x-axis of the object relative to the center of the object in degrees (0-360)
    public float GetObjectXRotation(int inObjectIndex);

    //absolute rotation around the y-axis of the object relative to the center of the object in degrees (0-360)
    public float GetObjectYRotation(int inObjectIndex);

    //absolute rotation around the z-axis of the object relative to the center of the object in degrees (0-360)
    public float GetObjectZRotation(int inObjectIndex);

    //absolute scaling in x-axis of the object relative to the center of the object
    public float GetObjectXScaling(int inObjectIndex);

    //absolute scaling in y-axis of the object relative to the center of the object
    public float GetObjectYScaling(int inObjectIndex);

    //absolute scaling in z-axis of the object relative to the center of the object
    public float GetObjectZScaling(int inObjectIndex);

    //identification of the object model
    public int GetObjectModelIdentification(int inObjectIndex);

    //static ids for the different object models
    public static final int cOBJECTMODELIDENTIFICATION_BRAINMINE = 0;
    public static final int cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE = 1;
    public static final int cOBJECTMODELIDENTIFICATION_ROUND = 2;
    public static final int cOBJECTMODELIDENTIFICATION_VIRUSMINE = 3;
    public static final int cOBJECTMODELIDENTIFICATION_MATRIXMINE = 4;

}
