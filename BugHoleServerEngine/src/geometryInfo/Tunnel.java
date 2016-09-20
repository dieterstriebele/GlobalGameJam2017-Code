package geometryInfo;

import game.Settings;

public class Tunnel extends Geometry_Information {
	private static final int NumberOfCameraSegments = 6;
	
	public float m_IntestineScrollingOffset = 0f;
	
	public Tunnel(long timeBase) {
		super(timeBase);
		
		Init(NumberOfCameraSegments, IGeometry_Information.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE, Settings.TunnelScaling);
		
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
    	
    	if (m_child != null) {
    		return m_child.GetObjectModelIdentification(inObjectIndex - _positions.size());
    	}
    	
    	return 0;
    }
    
    public void SynchronizeState(long currentTime) {
    	
    	long diffTime = currentTime - mTimeBase;
    	int i = 0;
        m_IntestineScrollingOffset -= (float)diffTime * Settings.TunnelSpeed;
         
        if (m_IntestineScrollingOffset < -8f)
        {
        	m_IntestineScrollingOffset = 0f;
        }
        
    	mTimeBase = currentTime;

        
        //System.out.println("tunnel offset = " + m_IntestineScrollingOffset);

        _positions.get(i).mZPos = 24.0f+m_IntestineScrollingOffset;

        i++;
        _positions.get(i).mZPos = 16.0f+m_IntestineScrollingOffset;

        i++;
        _positions.get(i).mZPos = 8.0f+m_IntestineScrollingOffset;

        i++;
        _positions.get(i).mZPos = 0.0f+m_IntestineScrollingOffset;

        i++;
        _positions.get(i).mZPos = -8.0f+m_IntestineScrollingOffset;

        i++;
        _positions.get(i).mZPos = -16.0f+m_IntestineScrollingOffset;
        
        super.SynchronizeState(currentTime);
    }


	@Override
	public boolean IsFinished(long currentTime) {
		return false;
	}
}
