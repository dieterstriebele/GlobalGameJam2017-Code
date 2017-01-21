package geometryInfo;

import game.Settings;

public class Tunnel extends GeometryInformationBase {
	private static final int NumberOfCameraSegments = 6;
	
	public float m_IntestineScrollingOffset = 0f;
	
	public Tunnel(long timeBase) {
		super(timeBase);
		
		Init(NumberOfCameraSegments, IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE, Settings.TunnelScaling);
		
		for (int i = 0; i < NumberOfCameraSegments; i++) {
			_positions.get(i).mXPos = 0f;
			_positions.get(i).mYPos = 0f;			
			_rotations.get(i).mYPos = 1.570796f;
		}
	}
	
    public int GetObjectModelIdentification(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return Settings.TunnelId;
    	}
    	
    	if (_childGeometry != null) {
    		return _childGeometry.GetObjectModelIdentification(inObjectIndex - _positions.size());
    	}
    	
    	return 0;
    }
    
    public void SynchronizeState(long currentTime) {
    	
    	long diffTime = currentTime - _timeBase;
    	int i = 0;
        m_IntestineScrollingOffset -= (float)diffTime * Settings.TunnelSpeed;
         
        if (m_IntestineScrollingOffset < -8f)
        {
        	m_IntestineScrollingOffset = 0f;
        }
        
    	_timeBase = currentTime;

        _positions.get(i++).mZPos = 24.0f  + m_IntestineScrollingOffset;
        _positions.get(i++).mZPos = 16.0f  + m_IntestineScrollingOffset;
        _positions.get(i++).mZPos = 8.0f   + m_IntestineScrollingOffset;
        _positions.get(i++).mZPos = 0.0f   + m_IntestineScrollingOffset;
        _positions.get(i++).mZPos = -8.0f  + m_IntestineScrollingOffset;
        _positions.get(i++).mZPos = -16.0f + m_IntestineScrollingOffset;
        
        super.SynchronizeState(currentTime);
    }


	@Override
	public boolean IsFinished(long currentTime) {
		return false;
	}
}
