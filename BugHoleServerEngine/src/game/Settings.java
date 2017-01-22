package game;

import geometryInfo.IGeometryInformation;
import util.Vector3D;

public class Settings {
	// Tunnel
	public final static int TunnelId = IGeometryInformation.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
	public final static float TunnelSpeed =  0.002f;
	public final static Vector3D TunnelScaling = new Vector3D(1f, 1f, 1f);
	public final static Vector3D BrainmineScaling = new Vector3D(0.5f, 0.5f, 0.5f);
	
	// Shots
//	public final static float ShotSpeed = 0.006f;
//	public final static int MaxShots = 20;
//	public final static int ShotDelayMs = 50;
//	public final static float ShotRangeSquare = 1600f;
//	public final static Vector3D ShotRotation = new Vector3D();
//	public final static Vector3D ShotScaling = new Vector3D(0.05f, 0.05f, 0.05f);
//	public final static int ShotId = IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE;
	
	// Enemies global
	public static final float EnemyShotPropability = 0.001f;

}
