package enemies;

import game.Settings;

public class DeathCircle extends EnemySwarm {

	private final static int NumObjects = 5;
	public final static float ApproachTargetPos = 8f;

	private enum State {
		Approach,
		Extend,
		Rotate,
		Collapse,
		Retreat,
		Finished
	}
	
	private State _state;
	
	private float _angle;
	
	public DeathCircle(long timeBase) {
		super(timeBase);
        
        Init(NumObjects, Settings.DeathCircleId, Settings.DeathCircleScale);
        
        _state = State.Approach;
	}

	@Override
	public boolean IsFinished(long currentTime) {

		if (_state == State.Finished) {
			return true;
		}
		
		return super.IsFinished(currentTime);
	}

	@Override
	public void SynchronizeState(long currentTime) {
		long delta = currentTime - _timeBase;
		
        if (_state == State.Approach) {
	        for (int i = 0; i < _swarmMemberCount; i++) {
	        	_persistentPositions[i].mZPos = Settings.DeathCircleZStart - delta * Settings.DeathCircleSpeed;	
	        }
	        
	        if (_persistentPositions[0].mZPos < ApproachTargetPos) {
	        	_state = State.Extend;
	        	_timeBase = currentTime;
	        }
        } else if (_state == State.Extend) {
        	for (int i = 1; i < _swarmMemberCount; i++) {
        		float speed = i % 2 == 1 ? Settings.DeathCircleSpeed : -Settings.DeathCircleSpeed;
        		speed = i < 3 ? speed / 2 : speed;
        		
        		_persistentPositions[i].mXPos = delta * speed;
        	}
        	
        	if (Math.abs(_persistentPositions[4].mXPos) >= Settings.DeathCircleRadius)
        	{
        		_state = State.Rotate;
        		_timeBase = currentTime;
        	}
        } else if (_state == State.Rotate) {
    		_angle = delta * Settings.DeathCircleAngleSpeed;

    		for (int i = 1; i < _swarmMemberCount; i++) {
        		float angle = i % 2 == 1 ? _angle : _angle + (float)Math.PI;
        		float radius = i < 3 ? Settings.DeathCircleRadius / 2f : Settings.DeathCircleRadius;
        		_persistentPositions[i].mXPos = (float)Math.cos(angle) * radius;
        		_persistentPositions[i].mYPos = (float)Math.sin(angle) * radius;
        	}
        	
        	if (_angle >= Settings.DeathCircleRotationLimit) {
        		_state = State.Retreat;
        		_timeBase = currentTime;
        	}
        } else if (_state == State.Retreat) {
    		_angle = delta * Settings.DeathCircleAngleSpeed;

    		for (int i = 0; i < _swarmMemberCount; i++) {
    			
    			if (i > 0) {
	        		float angle = i % 2 == 1 ? _angle : _angle + (float)Math.PI;
	        		float radius = i < 3 ? Settings.DeathCircleRadius / 2f : Settings.DeathCircleRadius;
	        		_persistentPositions[i].mXPos = (float)Math.cos(angle) * radius;
	        		_persistentPositions[i].mYPos = (float)Math.sin(angle) * radius;
    			}

	        	_persistentPositions[i].mZPos = ApproachTargetPos + delta * Settings.DeathCircleSpeed;	
    		}
	        
	        if (_persistentPositions[0].mZPos > Settings.DeathCircleZStart) {
	        	_state = State.Finished;
	        }
        }
        
        super.SynchronizeState(currentTime);
	}
}
