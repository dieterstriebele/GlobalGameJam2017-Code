package game;

import geometryInfo.IGeometry_Information;
import util.Vector3D;

public class Settings {
	// Tunnel
	public final static int TunnelId = IGeometry_Information.cOBJECTMODELIDENTIFICATION_INTESTINES_SIMPLE;
	public final static float TunnelSpeed =  0.002f;
	public final static Vector3D TunnelScaling = new Vector3D(1f, 1f, 1f);
	
	// Shots
	public final static float ShotSpeed = 0.006f;
	public final static int MaxShots = 20;
	public final static int ShotDelayMs = 50;
	public final static float ShotRangeSquare = 1600f;
	public final static Vector3D ShotRotation = new Vector3D();
	public final static Vector3D ShotScaling = new Vector3D(0.05f, 0.05f, 0.05f);
	public final static int ShotId = IGeometry_Information.cOBJECTMODELIDENTIFICATION_BRAINMINE;
	
	// Enemies global
	public static final float EnemyShotPropability = 0.001f;

	// Death Circle
	public final static float DeathCircleZDist = 0.1f;
	public final static float DeathCircleZStart = 40f;
	public final static float DeathCircleSpeed = 0.008f;

	public final static float DeathCircleRadius = 0.8f;
	public final static float DeathCircleAngleSpeed = (float)Math.PI / 600;
	public final static float DeathCircleRotationLimit = 10 * (float)Math.PI;
	
	public final static int DeathCircleId = IGeometry_Information.cOBJECTMODELIDENTIFICATION_BRAINMINE;
	public final static Vector3D DeathCircleScale = new Vector3D(0.1f, 0.1f, 0.1f);

	// Howler
	public static final float HowlerSpeed = 0.0002f;
	public static final int HowlerId = IGeometry_Information.cOBJECTMODELIDENTIFICATION_BRAINMINE;
	public static final Vector3D HowlerScaling = new Vector3D(0.4f, 0.4f, 0.4f);
	
	// Spiral
    public final static double SpiralAngleSpeed = Math.PI / 500.0;
    public final static double SpiralRadius = 0.6;
    public final static double SpiralRadiusChangeDist = 0.8;
    public final static double SpiralRadiusChangeSpeed = 0.001;
    
    public final static float SpiralZDist = 5f;
    public final static float SpiralZStart = 40f;
    public final static float SpiralZSpeed = 0.006f;
    public final static int SpiralId = IGeometry_Information.cOBJECTMODELIDENTIFICATION_BRAINMINE;
	public final static Vector3D SpiralScaling = new Vector3D(0.5f, 0.5f, 0.5f);

    // Wesp
	public static final float WespSpeed = 0.0005f;
    public final static int WespId = IGeometry_Information.cOBJECTMODELIDENTIFICATION_BRAINMINE;
	public final static Vector3D WespScaling = new Vector3D(0.5f, 0.5f, 0.5f);

}
