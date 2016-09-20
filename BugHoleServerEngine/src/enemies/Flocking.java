package enemies;

import java.util.ArrayList;

import geometryInfo.IGeometry_Information;
import geometryInfo.QubicSpline;
import util.Logger;
import util.Vector3D;

public class Flocking extends EnemySwarm {
	private static final int NumEnemies = 4;	
	
	private static final float CohesionFactor = 1.2f;
	private static final float SeparationFactor = 1.2f;
	private static final float AlignmentFactor = 1.8f;
	private static final float FlockingSpeed = 0.0001f;
	private static final Vector3D FlockingScaling = new Vector3D(0.1f, 0.1f, 0.1f);
	
	private Vector3D[] _alignment;
	
	private Vector3D[] _prevPositions;
	
	QubicSpline _masterSpline;
	
	public Flocking(long timeBase) {
		super(timeBase);
		Init(NumEnemies, IGeometry_Information.cOBJECTMODELIDENTIFICATION_BRAINMINE, FlockingScaling);
		
		_masterSpline = new QubicSpline(timeBase);
		_masterSpline.SetSpeed(FlockingSpeed);
		
		_masterSpline.Add(new Vector3D(0f, 0f, 40f));
		_masterSpline.Add(new Vector3D(-0.9f, 0.9f, 20f));
		_masterSpline.Add(new Vector3D(0.9f, 0f, 10f));

		_masterSpline.Add(new Vector3D(0f, -0.2f, 12f));
		_masterSpline.Add(new Vector3D(-0.4f, 0f, 15f));

		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 8f));
		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 4f));

		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 9f));
		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 14f));

		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 10f));
		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 6f));

		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 10f));
		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 15f));

		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 20f));
		_masterSpline.Add(new Vector3D((float)(Math.random() * 1.8 - 0.9), (float)(Math.random() * 1.8 - 0.9), 40f));

		_alignment = new Vector3D[NumEnemies];
		
		for (int i = 0; i < NumEnemies; i++) {
			_alignment[i] = new Vector3D(0f, 0f, -1f);
		}
		
		_prevPositions = new Vector3D[NumEnemies];
		
		// Start position of the spline controlled master
		_prevPositions[0] = new Vector3D(0f, 0f, 40f);
		
		for (int i = 1; i < NumEnemies; i++) {
			_prevPositions[i] = new Vector3D((float)(Math.random() * 1.8 - 0.9),
					(float)(Math.random() * 1.8 - 0.9),
					40f);
			_persistentPositions[i].set(_prevPositions[i]);
		}
	}

	public void SynchronizeState(long currentTime) {
		
		// Calculate master position and speed
		Vector3D newMasterPosition = _masterSpline.GetPosition(currentTime) ;
		
		if (newMasterPosition != null) {
			float speed = 0f;
			_persistentPositions[0].set(newMasterPosition);
			Vector3D speedVec =  new Vector3D(_persistentPositions[0]);
			speedVec.sub(_prevPositions[0]);
			speed = speedVec.len();

			if (speed > 0) {
				_prevPositions[0].set(_persistentPositions[0]);
			
				for (int i = 1; i < NumEnemies; i++) {
					Vector3D cohesion = cohesion(i);
					Vector3D alignment = alignment(i);
					Vector3D separation = separation(i);
					
					cohesion.mul(CohesionFactor);
					alignment.mul(AlignmentFactor);
					separation.mul(SeparationFactor);
	
					Vector3D flockingDir = new Vector3D();
					
					flockingDir.add(cohesion);
					//flockingDir.add(alignment);
					flockingDir.add(separation);
					
					flockingDir.normalize();
					flockingDir.mul(speed);
					
					_alignment[i].set(flockingDir);
					
					_prevPositions[i].add(_alignment[i]);
					
					_persistentPositions[i].set(_prevPositions[i]);
				}
			}
		}
		else
		{
			_prevPositions[0] = null;
		}

		
		super.SynchronizeState(currentTime);

	}
	
	public boolean IsFinished(long currentTime) {
		return _prevPositions[0] == null;
	}
	
    private Vector3D cohesion(int clientIndex) {
        Vector3D center = new Vector3D(Vector3D.Zero);

        for (int i = 0; i < NumEnemies; i++) {
        	if (i == clientIndex) {
        		continue;
        	}
        	
    		center.add(_persistentPositions[i]);
        }
        
        center.div(NumEnemies-1);
        
        Vector3D dir = new Vector3D(center);
        
        dir.sub(_persistentPositions[clientIndex]);        
        
        dir.normalize();

        return dir;
    }
    

    private Vector3D separation(int clientIndex)
	{
		Vector3D dir = new Vector3D();

		for (int i = 0; i < NumEnemies; i++) {
			if (i == clientIndex) {
				continue;
			}
			
			if (i != clientIndex) {
				Vector3D delta = new Vector3D(_persistentPositions[i]);
				delta.sub(_persistentPositions[clientIndex]);
				dir.add(delta);
			}
		}
		
		dir.div(NumEnemies);
		dir.mul(-1f);
		dir.normalize();
		
		return dir;
	}


    private Vector3D alignment(int clientIndex) 
	{
		Vector3D alignment = new Vector3D();
		
		for (int i = 0; i < NumEnemies; i++) {
			if (i == clientIndex) {
				continue;
			}
			
			alignment.add(_alignment[i]);
		}

		alignment.div(NumEnemies - 1);
		alignment.normalize();
		
		return alignment;
	}

}
