package geometryInfo;

import java.util.ArrayList;

import util.Vector3D;

public abstract class Geometry_Information implements IGeometry_Information{
	
	protected ArrayList<Vector3D> _positions;
	protected ArrayList<Vector3D> _rotations;
	protected ArrayList<Vector3D> _scaling;
	
    protected long mTimeBase;
    
    protected IGeometry_Information m_child;
    
    private int _graphicsId;
    
    public Geometry_Information(long timeBase) {
    	mTimeBase = timeBase;
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
    	if (m_child != null) {
        	m_child.SynchronizeState(currentTime);
        }
    }
    
    public void RemoveFinished(long currentTime) {
        if (m_child != null) {
            while (m_child.IsFinished(currentTime)) {
                m_child = m_child.GetChild();

                if (m_child == null) {
                    break;
                }
            }
        }

        if (m_child != null) {
        	m_child.RemoveFinished(currentTime);
        }
    }
    
    public IGeometry_Information GetChild() {
    	return m_child;
    }
    
	public int GetNumberOfObjects() {
    	int numObjects = _positions.size();
    	
    	if (m_child != null) {
    		numObjects += m_child.GetNumberOfObjects();
    	}
    	
        return numObjects;
    }
    
    public void PropagateGeometryInformation(IGeometry_Information geometryInformation) {
    	if (m_child == null) {
    		m_child = geometryInformation;
    	}
    	else {
    		m_child.PropagateGeometryInformation(geometryInformation);
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
    	
    	if (m_child != null && !collides) {
    		collides |= m_child.CollidesWith(roundPos);
    	}
    	
    	return collides;
    }

    public Vector3D GetObjectPosition(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return _positions.get(inObjectIndex);
    	}
    	
    	if (m_child != null) {
    		return m_child.GetObjectPosition(inObjectIndex - _positions.size());
    	}

    	return null;
    }

    public Vector3D GetObjectRotation(int inObjectIndex) {
    	if (inObjectIndex < _rotations.size()) {
    		return _rotations.get(inObjectIndex);
    	}

    	if (m_child != null) {
    		return m_child.GetObjectRotation(inObjectIndex - _rotations.size());
    	}

    	return null;
    }

    public Vector3D GetObjectScaling(int inObjectIndex) {
    	if (inObjectIndex < _scaling.size()) {
    		return _scaling.get(inObjectIndex);
    	}
    	
    	if (m_child != null) {
    		return m_child.GetObjectScaling(inObjectIndex - _scaling.size());
    	}
    	
    	return null;
    }

    public int GetObjectModelIdentification(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return _graphicsId;
    	}
    	
    	if (m_child != null) {
    		return m_child.GetObjectModelIdentification(inObjectIndex - _positions.size());
    	}
    	
    	return 0;
    }

    public void SetDecorator(IGeometry_Information geometryInformation) {
    	m_child = geometryInformation;
    }
}
