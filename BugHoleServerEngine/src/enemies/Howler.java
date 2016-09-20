package enemies;

import game.Settings;
import geometryInfo.QubicSpline;
import util.Vector3D;

public class Howler extends EnemySwarm {
	private QubicSpline _qSpline;
	private Vector3D _prevPosition;
	
	public Howler(long timeBase) {
		super(timeBase);
		
		Init(1, Settings.HowlerId, Settings.HowlerScaling);
	
		_qSpline = new QubicSpline(timeBase);
		
		_qSpline.Add(new Vector3D(0f, 0f, 40f));
		_qSpline.Add(new Vector3D((float)Math.random() * 2f - 1f, (float)Math.random() * 2f - 1f, 20f));
		_qSpline.Add(new Vector3D((float)Math.random() * 2f - 1f, (float)Math.random() * 2f - 1f, 0f));
		
		_qSpline.Add(new Vector3D((float)Math.random() * 2f - 1f, (float)Math.random() * 2f - 1f, -20f));
		_qSpline.Add(new Vector3D((float)Math.random() * 2f - 1f, (float)Math.random() * 2f	- 1f, -40f));		
		
		_qSpline.SetSpeed(Settings.HowlerSpeed);
		
		_prevPosition = new Vector3D();
	}
	
	public void SynchronizeState(long currentTime) {
		_prevPosition = _qSpline.GetPosition(currentTime);
		
		if (_prevPosition != null)
		{
			_persistentPositions[0].set(_prevPosition);
		}
		
		super.SynchronizeState(currentTime);
	}
	
	public boolean IsFinished(long currentTime) {
		return _prevPosition == null;
	}
}
