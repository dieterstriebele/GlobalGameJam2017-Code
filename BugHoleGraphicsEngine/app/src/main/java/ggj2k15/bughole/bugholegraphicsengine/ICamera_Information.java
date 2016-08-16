package ggj2k15.bughole.bugholegraphicsengine;

public interface ICamera_Information {

    public void GenerateCameraMatrix(float inEyeX, float inEyeY, float inEyeZ, float inCenterX, float inCenterY, float inCenterZ, int inViewportWidth, int inViewportHeight);

    public float[] GetModelViewProjectionMatrix();

    public float[] GetModelViewMatrix();

    public void Update();

    public float[] GetCameraDirectionVector();

}
