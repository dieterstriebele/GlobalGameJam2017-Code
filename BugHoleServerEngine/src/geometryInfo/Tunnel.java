package geometryInfo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.file.Paths;

import game.Settings;

public class Tunnel extends GeometryInformationBase {
	
	private static final int NumberOfCameraSegments = 6;
	
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
	}
	
	private float[] _LoadTunnelPathFromFile(String inputPath)
	{
		float[] positions = null;
		
		try {
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(inputPath)));
			
			int num_positions = (int)inputStream.readFloat();
			positions = new float[num_positions];
			
			for(int i=0; i<num_positions; i++)
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
        super.SynchronizeState(currentTime);
    }
}
