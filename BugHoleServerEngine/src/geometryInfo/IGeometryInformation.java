package geometryInfo;

import util.Vector3D;

public interface IGeometryInformation {
	public static final int MaxObjects = 100;
    public static final int cOBJECTMODELIDENTIFICATION_BRAINMINE = 0;
    public static final int cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE = 1;
    public static final int cOBJECTMODELIDENTIFICATION_ROUND = 2;

    public void SynchronizeState(long currentTime);

    public int GetNumberOfObjects();
    
    public boolean CollidesWith(Vector3D pos);
    
    public Vector3D GetObjectPosition(int inObjectIndex);

    public Vector3D GetObjectRotation(int inObjectIndex);

    public Vector3D GetObjectScaling(int inObjectIndex);
    
    public int GetObjectModelIdentification(int inObjectIndex);
    
}
