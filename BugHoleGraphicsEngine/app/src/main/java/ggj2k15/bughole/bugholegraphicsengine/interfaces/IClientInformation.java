package ggj2k15.bughole.bugholegraphicsengine.interfaces;

public interface IClientInformation {

    public void SynchronizeState();

    public void AddUserAction(int inUserAction);
    public int GetNumberOfUserActions();
    public int GetUserAction(int inUserActionIndex);

    public void SetCameraDirectionVector(float[] inCameraDirectionVector);
    public float[] GetCameraDirectionVector();

    public int GetUserScore();
    public int GetUserHitpoints();

    public static final int cACTIONIDENTIFICATION_NONE = 0;
    public static final int cACTIONIDENTIFICATION_FIRE = 1;

}
