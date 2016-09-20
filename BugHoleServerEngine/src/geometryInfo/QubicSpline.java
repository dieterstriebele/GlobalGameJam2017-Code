package geometryInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import util.Logger;
import util.Vector3D;

public class QubicSpline {
	private ArrayList<Vector3D> _controlPoints;
	private float _speed;
	
	long _timeBase;
	
	public QubicSpline(long timeBase) {
		_timeBase = timeBase;
		_controlPoints = new ArrayList<Vector3D>();
	}
	
	public void Add(Vector3D controlPoint) {
		_controlPoints.add(controlPoint);
	}
	
	public void SetSpeed(float speed) {
		_speed = speed;
	}
	
	public Vector3D GetPosition(long currentTime) {
		Vector3D position = null;
		
		float delta = (currentTime - _timeBase) * _speed;

		if (_controlPoints.size() >= 3) {
			position = new Vector3D();
			
			// (1–t)² P0+  2(1–t)tP1+t2P2
			float a = (1-delta) * (1-delta);
			Vector3D p0 = new Vector3D(_controlPoints.get(0));
			p0.mul(a);
			
			Vector3D p1 = new Vector3D(_controlPoints.get(1));
			p1.mul(2 * (1 - delta) * delta);
		
			Vector3D p2 = new Vector3D(_controlPoints.get(2));
			p2.mul(delta);
			
			p0.add(p1);
			p0.add(p2);
			position.set(p0);
			
	
			if (delta > 1) {
				_controlPoints.remove(0);
				_controlPoints.remove(0);
				position.set(_controlPoints.get(0));
				_timeBase = currentTime;
			}
		}

		return position;
	}
}
