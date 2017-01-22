package geometryInfo;

import java.nio.file.Paths;
import game.Settings;
import util.PathLoader;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;

public class Tunnel extends GeometryInformationBase {

	//should always be an uneven number (for now)!
	private static final int NumberTunnelSegments = 3;
	
//	private float m_time;
	private float[] m_tunnel_positions = null;
	
	private float m_tunnel_z_dim;
	
	public Tunnel(long timeBase) {
		super(timeBase);
		
		Init(NumberTunnelSegments, IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE, Settings.TunnelScaling);
		
		for (int i = 0; i < NumberTunnelSegments; i++) {
			_positions.get(i).mXPos = 0f;
			_positions.get(i).mYPos = 0f;			
			_rotations.get(i).mYPos = 1.570796f;
		}
		
		m_tunnel_positions = PathLoader.LoadPathFromFile(Paths.get("res/intestines_triplepath_001_kbap.bin").toAbsolutePath().toString());

		//Keep this to calculate dimensions of tunnel segments in case the geometry is replaced
         m_tunnel_z_dim = abs(m_tunnel_positions[1] - m_tunnel_positions[m_tunnel_positions.length-2]);
//       float x_d = abs(m_tunnel_positions[0] - m_tunnel_positions[m_tunnel_positions.length-3]);
//       float y_d = abs(m_tunnel_positions[2] - m_tunnel_positions[m_tunnel_positions.length-1]);
	}
	
    public int GetObjectModelIdentification(int inObjectIndex) {
		return IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
    }
    
    public void SynchronizeState(long currentTime) {
    	super.SynchronizeState(currentTime);
    	
    	_timePoint += 8;
    	
		//Logger.Info("In SynchronizeState of Tunnel");

		//m_time += 8.00f;
		
		float z_offset_start = (float)ceil((float)NumberTunnelSegments / 2) * m_tunnel_z_dim;	
		float z_offset = z_offset_start;		
		
		for(int i=0; i<NumberTunnelSegments; i++)
		{
			z_offset -= m_tunnel_z_dim;
//			int path_segment_offset = ((int)m_time % (m_tunnel_positions.length / 3)) * 3;
			int path_segment_offset = ((int)_timePoint % (m_tunnel_positions.length / 3)) * 3;
			
			_positions.get(i).mXPos =  m_tunnel_positions[path_segment_offset + 0];
			_positions.get(i).mYPos =  m_tunnel_positions[path_segment_offset + 2];
			_positions.get(i).mZPos = -m_tunnel_positions[path_segment_offset + 1] + z_offset;
			_rotations.get(i).mXPos = 0.0f;
			_rotations.get(i).mYPos = 0.0f;
			_rotations.get(i).mZPos = 0.0f;
			_scaling.get(i).mXPos = 1.0f;
			_scaling.get(i).mYPos = 1.0f;
			_scaling.get(i).mZPos = 1.0f;
		}
    }
}
