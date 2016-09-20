package enemies;

import game.Settings;
import util.Vector3D;

public class Wesp extends QSplineSwarm {
	
	public Wesp(long timeBase) {
		super(timeBase);
		
		Init(2, Settings.WespId, Settings.WespScaling);		

		// Wesp 1
		_splines.get(0).Add(new Vector3D(0f, 0f, 40f));
		_splines.get(0).Add(new Vector3D(0.9f, 0.5f, 20f));
		_splines.get(0).Add(new Vector3D(-0.9f, -0.5f, 30f));

		_splines.get(0).Add(new Vector3D(-0.9f, -0.5f, 25f));		
		_splines.get(0).Add(new Vector3D(0.0f, -0.25f, 20f));

		_splines.get(0).Add(new Vector3D(0.1f, -0.1f, 25f));
		_splines.get(0).Add(new Vector3D(0.0f, -0.25f, 30f));
		
		_splines.get(0).Add(new Vector3D(-0.3f, -0.7f, 20f));
		_splines.get(0).Add(new Vector3D(-0.9f, 0.7f, 10f));

		_splines.get(0).Add(new Vector3D(0.9f, -0.7f, 15f));
		_splines.get(0).Add(new Vector3D(-0.9f, 0.7f, 20f));

		_splines.get(0).Add(new Vector3D(-0.5f, 0.5f, 8f));
		_splines.get(0).Add(new Vector3D(0.4f, -0.2f, 5f));

		_splines.get(0).Add(new Vector3D(0.0f, 0f, 5f));
		_splines.get(0).Add(new Vector3D(-0.4f, 0.2f, 5f));

		_splines.get(0).Add(new Vector3D(-0.2f, 0.2f, 20f));
		_splines.get(0).Add(new Vector3D(0f, 0f, 40f));

		_splines.get(0).SetSpeed(Settings.WespSpeed);
		
		// Wesp 2
		_splines.get(1).Add(new Vector3D(0f, 0f, 40f));
		_splines.get(1).Add(new Vector3D(-0.9f, -0.5f, 20f));
		_splines.get(1).Add(new Vector3D(0.9f, 0.5f, 30f));

		_splines.get(1).Add(new Vector3D(0.9f, 0.5f, 25f));		
		_splines.get(1).Add(new Vector3D(0.0f, 0.25f, 20f));

		_splines.get(1).Add(new Vector3D(-0.1f, 0.1f, 25f));
		_splines.get(1).Add(new Vector3D(0.0f, 0.25f, 30f));
		
		
		_splines.get(1).Add(new Vector3D(0.3f, 0.7f, 20f));
		_splines.get(1).Add(new Vector3D(0.9f, -0.7f, 10f));

		_splines.get(1).Add(new Vector3D(-0.9f, 0.7f, 15f));
		_splines.get(1).Add(new Vector3D(0.9f, -0.7f, 20f));

		_splines.get(1).Add(new Vector3D(0.5f, -0.5f, 8f));
		_splines.get(1).Add(new Vector3D(-0.4f, 0.2f, 5f));

		_splines.get(1).Add(new Vector3D(0.0f, 0f, 5f));
		_splines.get(1).Add(new Vector3D(0.4f, -0.2f, 5f));

		_splines.get(1).Add(new Vector3D(0.2f, -0.2f, 20f));
		_splines.get(1).Add(new Vector3D(0f, 0f, 40f));
		
		_splines.get(1).SetSpeed(Settings.WespSpeed);
//		
//		// Wesp 3
//		_splines.get(2).Add(new Vector3D(0f, 0f, 40f));
//		_splines.get(2).Add(new Vector3D(-0.9f, 0.5f, 20f));
//		_splines.get(2).Add(new Vector3D(0.9f, -0.5f, 30f));
//
//		_splines.get(2).Add(new Vector3D(0.9f, -0.5f, 25f));		
//		_splines.get(2).Add(new Vector3D(0.0f, -0.25f, 20f));
//
//		_splines.get(2).Add(new Vector3D(-0.1f, -0.1f, 25f));
//		_splines.get(2).Add(new Vector3D(0.0f, -0.25f, 30f));
//
//		_splines.get(2).Add(new Vector3D(0.3f, -0.7f, 20f));
//		_splines.get(2).Add(new Vector3D(0.9f, 0.7f, 10f));
//
//		_splines.get(2).Add(new Vector3D(-0.9f, -0.7f, 15f));
//		_splines.get(2).Add(new Vector3D(0.9f, 0.7f, 20f));
//
//		_splines.get(2).Add(new Vector3D(0.5f, 0.5f, 8f));
//		_splines.get(2).Add(new Vector3D(-0.4f, -0.2f, 5f));
//
//		_splines.get(2).Add(new Vector3D(0.0f, 0f, 5f));
//		_splines.get(2).Add(new Vector3D(0.4f, 0.2f, 5f));
//
//		_splines.get(2).Add(new Vector3D(0.2f, 0.2f, 20f));
//		_splines.get(2).Add(new Vector3D(0f, 0f, 40f));
//
//		_splines.get(2).SetSpeed(Settings.WespSpeed);
//		
//		// Wesp 2
//		_splines.get(3).Add(new Vector3D(0f, 0f, 40f));
//		_splines.get(3).Add(new Vector3D(-0.9f, 0.5f, 20f));
//		_splines.get(3).Add(new Vector3D(0.9f, -0.5f, 30f));
//                     
//		_splines.get(3).Add(new Vector3D(0.9f, -0.5f, 25f));		
//		_splines.get(3).Add(new Vector3D(0.0f, -0.25f, 20f));
//                     
//		_splines.get(3).Add(new Vector3D(-0.1f, -0.1f, 25f));
//		_splines.get(3).Add(new Vector3D(0.0f, -0.25f, 30f));
//		             
//		             
//		_splines.get(3).Add(new Vector3D(0.3f, -0.7f, 20f));
//		_splines.get(3).Add(new Vector3D(0.9f, 0.7f, 10f));
//                     
//		_splines.get(3).Add(new Vector3D(-0.9f, -0.7f, 15f));
//		_splines.get(3).Add(new Vector3D(0.9f, 0.7f, 20f));
//                     
//		_splines.get(3).Add(new Vector3D(0.5f, 0.5f, 8f));
//		_splines.get(3).Add(new Vector3D(-0.4f, -0.2f, 5f));
//                     
//		_splines.get(3).Add(new Vector3D(0.0f, 0f, 5f));
//		_splines.get(3).Add(new Vector3D(0.4f, 0.2f, 5f));
//                     
//		_splines.get(3).Add(new Vector3D(0.2f, 0.2f, 20f));
//		_splines.get(3).Add(new Vector3D(0f, 0f, 40f));
//		             
//		_splines.get(3).SetSpeed(Settings.WespSpeed);
	}
}
