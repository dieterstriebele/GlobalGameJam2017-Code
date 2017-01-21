package geometryInfo;

import java.util.ArrayList;

import util.Vector3D;

public abstract class GeometryInformationBase implements IGeometryInformation{
	
	protected ArrayList<Vector3D> _positions;
	protected ArrayList<Vector3D> _rotations;
	protected ArrayList<Vector3D> _scaling;
	
    protected long _timeBase;
    
    protected IGeometryInformation _childGeometry;
    
    private int _graphicsId;
    
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
    }
    
    public void SynchronizeState(long currentTime) {
    	if (_childGeometry != null) {
        	_childGeometry.SynchronizeState(currentTime);
        }
    }
    
    public void RemoveFinished(long currentTime) {
        if (_childGeometry != null) {
            while (_childGeometry.IsFinished(currentTime)) {
                _childGeometry = _childGeometry.GetChild();

                if (_childGeometry == null) {
                    break;
                }
            }
        }

        if (_childGeometry != null) {
        	_childGeometry.RemoveFinished(currentTime);
        }
    }
    
    public IGeometryInformation GetChild() {
    	return _childGeometry;
    }
    
	public int GetNumberOfObjects() {
    	int numObjects = _positions.size();
    	
    	if (_childGeometry != null) {
    		numObjects += _childGeometry.GetNumberOfObjects();
    	}
    	
        return numObjects;
    }
    
    public void PropagateGeometryInformation(IGeometryInformation geometryInformation) {
    	if (_childGeometry == null) {
    		_childGeometry = geometryInformation;
    	}
    	else {
    		_childGeometry.PropagateGeometryInformation(geometryInformation);
    	}
    }

    public boolean CollidesWith(Vector3D roundPos) {    	
    	boolean collides = false;
    	
		for (int i = 0; i < _positions.size(); i++) {
			if (_positions.get(i).squareDistance(roundPos) < 0.5f) {
				collides = true;
				_positions.remove(i);
			}
		}
    	
    	if (_childGeometry != null && !collides) {
    		collides |= _childGeometry.CollidesWith(roundPos);
    	}
    	
    	return collides;
    }

    public Vector3D GetObjectPosition(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return _positions.get(inObjectIndex);
    	}
    	
    	if (_childGeometry != null) {
    		return _childGeometry.GetObjectPosition(inObjectIndex - _positions.size());
    	}

    	return null;
    }

    public Vector3D GetObjectRotation(int inObjectIndex) {
    	if (inObjectIndex < _rotations.size()) {
    		return _rotations.get(inObjectIndex);
    	}

    	if (_childGeometry != null) {
    		return _childGeometry.GetObjectRotation(inObjectIndex - _rotations.size());
    	}

    	return null;
    }

    public Vector3D GetObjectScaling(int inObjectIndex) {
    	if (inObjectIndex < _scaling.size()) {
    		return _scaling.get(inObjectIndex);
    	}
    	
    	if (_childGeometry != null) {
    		return _childGeometry.GetObjectScaling(inObjectIndex - _scaling.size());
    	}
    	
    	return null;
    }

    public int GetObjectModelIdentification(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return _graphicsId;
    	}
    	
    	if (_childGeometry != null) {
    		return _childGeometry.GetObjectModelIdentification(inObjectIndex - _positions.size());
    	}
    	
    	return 0;
    }

    public void SetDecorator(IGeometryInformation geometryInformation) {
    	_childGeometry = geometryInformation;
    }
}
