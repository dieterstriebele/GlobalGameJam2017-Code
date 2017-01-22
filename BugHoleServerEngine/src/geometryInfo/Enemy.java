package geometryInfo;

import java.nio.file.Paths;
import util.PathLoader;
import game.Settings;

/**
 * Created by adieling on 21.01.17.
 * jajaaja, blub blub blub
 */
public class Enemy extends GeometryInformationBase {
	
	public enum EnemyType
	{
		Alpha,
		Beta,
//		Gamma,
//		Delta
	}
	
	private float[] m_enemy_positions = null;
	private String m_path = null;
	private float m_time_factor = 1.0f;

    public Enemy(long timeBase, EnemyType enemyType) {
        super(timeBase);
        
        //every enemy represents exactly one object in the scene!
        Init(1, IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE, Settings.BrainmineScaling);   
        
        _positions.get(1).mXPos = 0.0f;
        _positions.get(1).mYPos = 0.0f;
        _rotations.get(1).mYPos = 1.0f;
        
        m_time_factor = _GetEnemyTimeFactor(enemyType);        
        m_path = _GetEnemyTypePath(enemyType);
        m_enemy_positions = PathLoader.LoadPathFromFile(m_path);
    }
    
    private String _GetEnemyTypePath(EnemyType enemyType)
    {
    	String path = null;
    	String rel_path = null;
    	
    	switch(enemyType)
    	{
    	case Alpha:
    		rel_path = "res/intestines_triplepath_002_kbap.bin";
    		break;
    	case Beta:
    		rel_path = "res/intestines_triplepath_003_kbap.bin";
    		break;
    	default:
    		rel_path = "intestines_triplepath_002_kbap.bin";
    		break;
    	}
    	
    	path = Paths.get(rel_path).toAbsolutePath().toString();    	
    	return path;
    }
    
    private float _GetEnemyTimeFactor(EnemyType enemyType)
    {
    	float factor = 1.0f;
    	
    	switch(enemyType)
    	{
    	case Alpha:
    		factor = 2.0f;
    		break;
    	case Beta:
    		factor = 3.0f;
    		break;
    	default:
    		factor = 2.0f;
    		break;
    	}
    	
    	return factor;
    }

    public int GetObjectModelIdentification(int inObjectIndex) {
    	if(inObjectIndex < _positions.size())
    	{
    		return IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE;
    	}
        return 0;
    }

    public void SynchronizeState(long currentTime) {
    	super.SynchronizeState(currentTime);
    	
    	//Logger.Info("In SynchronizeState of Enemy");    	
    	
		int path_segment_offset = ((int)(_timePoint * m_time_factor) % (m_enemy_positions.length / 3)) * 3;
		
		_positions.get(0).mXPos =  m_enemy_positions[path_segment_offset + 0];
		_positions.get(0).mYPos =  m_enemy_positions[path_segment_offset + 2];
		_positions.get(0).mZPos = -m_enemy_positions[path_segment_offset + 1];
		_rotations.get(0).mXPos = 0.0f;
		_rotations.get(0).mYPos = 0.0f;
		_rotations.get(0).mZPos = 0.0f;
		_scaling.get(0).mXPos = 1.0f;
		_scaling.get(0).mYPos = 1.0f;
		_scaling.get(0).mZPos = 1.0f;
		
		
    }
}
