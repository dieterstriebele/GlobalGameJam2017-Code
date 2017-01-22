package geometryInfo;

import java.util.ArrayList;

import util.Vector3D;

public abstract class GeometryInformationBase implements IGeometryInformation{
	
	protected ArrayList<Vector3D> _positions;
	protected ArrayList<Vector3D> _rotations;
	protected ArrayList<Vector3D> _scaling;
	
    protected long _timeBase;
    
    protected static float _timePoint; //temp static
    
    protected int _graphicsId;

	protected int _numObjects;
    
    public GeometryInformationBase(long timeBase) {
    	_timeBase = timeBase;
    }
    
    protected void Init(int numObjects, int graphicsId, Vector3D scaling) {
    	_graphicsId = graphicsId;
    	
    	_positions = new ArrayList<Vector3D>(numObjects);
    	_rotations = new ArrayList<Vector3D>(numObjects);
    	_scaling   = new ArrayList<Vector3D>(numObjects);
    	
    	for (int i = 0; i < numObjects; i++) {
    		_positions.add(new Vector3D());
    		_rotations.add(new Vector3D());
    		_scaling.add(scaling);
    	}

    	_numObjects = numObjects;
    }
    
    public void SynchronizeState(long currentTime) {
    	_timePoint += 8.0f;
    }

	public int GetNumberOfObjects() {
		return _numObjects;
    }

    public boolean CollidesWith(Vector3D roundPos) {    	
    	boolean collides = false;
    	
		for (int i = 0; i < _positions.size(); i++) {
			if (_positions.get(i).squareDistance(roundPos) < 0.5f) {
				collides = true;
				_positions.remove(i);
			}
		}
    	
    	return collides;
    }

    public Vector3D GetObjectPosition(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return _positions.get(inObjectIndex);
    	}

    	return null;
    }

    public Vector3D GetObjectRotation(int inObjectIndex) {
    	if (inObjectIndex < _rotations.size()) {
    		return _rotations.get(inObjectIndex);
    	}

    	return null;
    }

    public Vector3D GetObjectScaling(int inObjectIndex) {
    	if (inObjectIndex < _scaling.size()) {
    		return _scaling.get(inObjectIndex);
    	}
    	
    	return null;
    }

    public int GetObjectModelIdentification(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return _graphicsId;
    	}
    	
    	return 0;
    }
}
