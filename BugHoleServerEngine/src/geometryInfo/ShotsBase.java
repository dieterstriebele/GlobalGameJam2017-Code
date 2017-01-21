package geometryInfo;

import java.util.ArrayList;

import game.Settings;

import util.Logger;
import util.Vector3D;

public abstract class ShotsBase extends GeometryInformationBase implements IShotEmitter {

	private ArrayList<Vector3D> _directions;
	private ArrayList<Vector3D> _basePositions;
	private ArrayList<Long> _shotTime;
	
	long _previousShotTime;
   	
	public ShotsBase(long timeBase) {
		super(timeBase);

		_previousShotTime = timeBase;
		
		Init(0, Settings.ShotId, Settings.ShotScaling);
		
		_directions = new ArrayList<Vector3D>();
		_basePositions = new ArrayList<Vector3D>();
		_shotTime = new ArrayList<Long>();
	}
	
	public void EmitShot(Vector3D start, Vector3D dir) {
		long now = System.currentTimeMillis(); 
		if (now - _previousShotTime > Settings.ShotDelayMs && _positions.size() < Settings.MaxShots) {
			_previousShotTime = now; 
			
			_basePositions.add(new Vector3D(start));
			_positions.add(new Vector3D(start));
			_shotTime.add(new Long(now));

			Vector3D vec = new Vector3D(dir);
			vec.normalize();
			_directions.add(vec);
			
		}
	}
	
    public int GetObjectModelIdentification(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return Settings.ShotId;
    	}
    	
    	if (_childGeometry != null) {
    		return _childGeometry.GetObjectModelIdentification(inObjectIndex - _positions.size());
    	}
    	
    	return 0;
    }

	@Override
	public void SynchronizeState(long currentTime) {
		TranslateShots(currentTime);
		CheckForCollision();
		RemoveShots();

		super.SynchronizeState(currentTime);
	}

	@Override
	public boolean IsFinished(long currentTime) {
		return false;
	}
	
    public Vector3D GetObjectRotation(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return Settings.ShotRotation;
    	}
    	
    	if (_childGeometry != null) {
    		return _childGeometry.GetObjectRotation(inObjectIndex - _positions.size());
    	}
    	
    	return null;
    }
    
    public Vector3D GetObjectScaling(int inObjectIndex) {
    	if (inObjectIndex < _positions.size()) {
    		return Settings.ShotScaling;
    	}
    	
    	if (_childGeometry != null) {
    		return _childGeometry.GetObjectScaling(inObjectIndex - _positions.size());
    	}
    	
    	return null;
    }
	
    protected abstract void CheckForCollision();
    
	private void TranslateShots(long currentTime) {
		for (int index = 0; index < _positions.size(); index++) {
			float deltaTime = (currentTime - _shotTime.get(index).longValue()) * Settings.ShotSpeed;
			Vector3D pos = new Vector3D(_basePositions.get(index));
			Vector3D dir = new Vector3D(_directions.get(index));
			
			dir.mul(deltaTime);
			pos.add(dir);
			
			_positions.get(index).set(pos);
		}
	}
	
	private void RemoveShots() {
		boolean removed = false;
		
		do {
			removed = false;
			for (int index = 0; index < _positions.size(); index++) {
				float dist = _positions.get(index).squareDistance(_basePositions.get(index));
				if (dist > Settings.ShotRangeSquare) {
					_positions.remove(index);
					_directions.remove(index);
					_basePositions.remove(index);
					_shotTime.remove(index);
					removed = true;
					break;
				}
			}
		} while (removed);
	}
}
