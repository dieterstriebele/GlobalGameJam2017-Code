package geometryInfo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.file.Paths;

import game.Settings;
import util.Logger;

import static java.lang.Math.abs;

public class Tunnel extends GeometryInformationBase {

	private float m_Time;

	private static final int NumberOfCameraSegments = 3;
	
	public float m_IntestineScrollingOffset = 0f;
	
	private float[] m_tunnel_positions = null;
	
	public Tunnel(long timeBase) {
		super(timeBase);
		
		Init(NumberOfCameraSegments, IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE, Settings.TunnelScaling);
		
		for (int i = 0; i < NumberOfCameraSegments; i++) {
			_positions.get(i).mXPos = 0f;
			_positions.get(i).mYPos = 0f;			
			_rotations.get(i).mYPos = 1.570796f;
		}
		
		m_tunnel_positions = _LoadTunnelPathFromFile(Paths.get("res/intestines_triplepath_001_kbap.bin").toAbsolutePath().toString());

//        float x_d = abs(m_tunnel_positions[0] - m_tunnel_positions[m_tunnel_positions.length-3]);
//        float y_d = abs(m_tunnel_positions[2] - m_tunnel_positions[m_tunnel_positions.length-1]);
//        float z_d = abs(m_tunnel_positions[1] - m_tunnel_positions[m_tunnel_positions.length-2]);
	}

	private float[] _LoadTunnelPathFromFile(String inputPath)
	{
		float[] positions = null;

		try {
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(inputPath)));
			
			int num_positions = (int)inputStream.readFloat();
			positions = new float[num_positions * 3];
			
			for(int i=0; i<positions.length; i++)
			{
				positions[i] = inputStream.readFloat();
			}
			
			inputStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return positions;
	}
	
    public int GetObjectModelIdentification(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return Settings.TunnelId;
    	}
    	
    	return 0;
    }
    
    public void SynchronizeState(long currentTime) {
    	//TODO: update _positions, _rotations, _scaling, _numObjects

		//Logger.Info("In SynchronizeState of Tunnel");

		m_Time += 16.00f;
		int i=0;

		int m_PathSegmentOffset_01 = (int)m_Time*1 % (m_tunnel_positions.length/3);
		m_PathSegmentOffset_01 *= 3;

		_positions.get(i).mXPos = m_tunnel_positions[m_PathSegmentOffset_01 + 0];
		_positions.get(i).mYPos = m_tunnel_positions[m_PathSegmentOffset_01 + 2];
		_positions.get(i).mZPos = -m_tunnel_positions[m_PathSegmentOffset_01 + 1];
		_rotations.get(i).mXPos = 0.0f;
		_rotations.get(i).mYPos = 0.0f;
		_rotations.get(i).mZPos = 0.0f;
		_scaling.get(i).mXPos = 1.0f;
		_scaling.get(i).mYPos = 1.0f;
		_scaling.get(i).mZPos = 1.0f;
		i++;

        int m_PathSegmentOffset_02 = (int)m_Time*1 % (m_tunnel_positions.length/3);
        m_PathSegmentOffset_02 *= 3;
        _positions.get(i).mXPos = m_tunnel_positions[m_PathSegmentOffset_02 + 0];
        _positions.get(i).mYPos = m_tunnel_positions[m_PathSegmentOffset_02 + 2];
        _positions.get(i).mZPos = -m_tunnel_positions[m_PathSegmentOffset_02 + 1] + 100.0f;
        _rotations.get(i).mXPos = 0.0f;
        _rotations.get(i).mYPos = 0.0f;
        _rotations.get(i).mZPos = 0.0f;
        _scaling.get(i).mXPos = 1.0f;
        _scaling.get(i).mYPos = 1.0f;
        _scaling.get(i).mZPos = 1.0f;
        i++;

        int m_PathSegmentOffset_03 = (int)m_Time*1 % (m_tunnel_positions.length/3);
        m_PathSegmentOffset_03 *= 3;
        _positions.get(i).mXPos = m_tunnel_positions[m_PathSegmentOffset_03 + 0];
        _positions.get(i).mYPos = m_tunnel_positions[m_PathSegmentOffset_03 + 2];
        _positions.get(i).mZPos = -m_tunnel_positions[m_PathSegmentOffset_03 + 1] - 100.0f;
        _rotations.get(i).mXPos = 0.0f;
        _rotations.get(i).mYPos = 0.0f;
        _rotations.get(i).mZPos = 0.0f;
        _scaling.get(i).mXPos = 1.0f;
        _scaling.get(i).mYPos = 1.0f;
        _scaling.get(i).mZPos = 1.0f;
        i++;


//		_graphicsId = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
//		_numObjects = 1;

        super.SynchronizeState(currentTime);
    }
}
