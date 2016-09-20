package enemies;

import java.util.ArrayList;

import geometryInfo.QubicSpline;
import util.Vector3D;

public abstract class QSplineSwarm extends EnemySwarm {
	protected ArrayList<QubicSpline> _splines;

	// Dirty trick to track when swarm can be removed
	private Vector3D _prevPosition;

	public QSplineSwarm(long timeBase) {
		super(timeBase);
		
		_prevPosition = new Vector3D();
		_splines = new ArrayList<QubicSpline>();
	}
	
	public void Init(int numObjects, int graphicsId, Vector3D scaling)
	{
		super.Init(numObjects, graphicsId, scaling);
		
		for (int i = 0; i < numObjects; i++) {
			_splines.add(new QubicSpline(mTimeBase));
		}
	}
	
	public void SynchronizeState(long currentTime) {
		for (int i = 0; i < _positions.size(); i++) {
			QubicSpline spline = _splines.get(i);

			_prevPosition = spline.GetPosition(currentTime);
			
			if (_prevPosition != null)
			{
				_persistentPositions[i].set(_prevPosition);
			}
		}
		
		super.SynchronizeState(currentTime);
	}
	
	public boolean IsFinished(long currentTime) {
		return _prevPosition == null;
	}

}
