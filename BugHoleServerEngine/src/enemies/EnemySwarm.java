package enemies;

import game.Settings;
import geometryInfo.Geometry_Information;
import geometryInfo.IShotEmitter;
import util.Vector3D;

public abstract class EnemySwarm extends Geometry_Information {
	// contains the same object as _positions in the super class. 
	// But killed objects are not removed but replaced by a null reference.
	// That's required for enemy swarm position control of scripted enemies.
	protected Vector3D[] _persistentPositions;
	protected int _swarmMemberCount;

	private float[] _shootingPropability;
	
	private IShotEmitter _shotEmitter;

	public EnemySwarm(long timeBase) {
		super(timeBase);
	}
	
	public void SetShotEmitter(IShotEmitter shotEmitter) {
		_shotEmitter = shotEmitter;
	}
	
	public void Init(int numObjects, int graphicsId, Vector3D scaling) {
		super.Init(numObjects, graphicsId, scaling);
		
		_swarmMemberCount = numObjects;
		_persistentPositions = new Vector3D[numObjects];
		_shootingPropability = new float[numObjects];
		
		
		for (int i = 0; i < numObjects; i++) {
			_persistentPositions[i] = _positions.get(i);
			_shootingPropability[i] = Settings.EnemyShotPropability;
			
			_scaling.get(i).set(0.5f, 0.5f, 0.5f);
		}
	}
	
	public void SynchronizeState(long currentTime) {
		for (int i = 0; i < _swarmMemberCount; i++) {
			if (_persistentPositions[i] != null && Math.random() < _shootingPropability[i]) {
				Vector3D dir = new Vector3D(_persistentPositions[i]);
				dir.mul(-1f);
				_shotEmitter.EmitShot(_persistentPositions[i], dir);
			}
		}
		
		super.SynchronizeState(currentTime);
	}

	public boolean IsFinished(long currentTime) {
		return _positions.size() == 0;
	}
}
