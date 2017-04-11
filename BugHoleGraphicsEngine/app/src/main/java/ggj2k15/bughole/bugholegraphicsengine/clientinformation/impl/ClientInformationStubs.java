package ggj2k15.bughole.bugholegraphicsengine.clientinformation.impl;

import android.util.Log;

import java.util.Vector;

import ggj2k15.bughole.bugholegraphicsengine.interfaces.IClientInformation;

public class ClientInformationStubs implements IClientInformation, Runnable {

    private Vector<Integer> m_UserActions = new Vector<Integer>(10);
    private int m_Score = 0;
    private int m_HitPoints = 10000;

    public void SynchronizeState() {
        Info("SynchronizeState()");

        //do something here
        m_Score++;
        m_HitPoints--;

        m_UserActions.removeAllElements();
    }

    public void AddUserAction(int inUserAction) {
        Info("AddUserAction() inUserAction=" + inUserAction);
        m_UserActions.add(inUserAction);
    }

    public int GetNumberOfUserActions() {
        int tSize = m_UserActions.size();
        Info("GetNumberOfUserActions() returning " + tSize);
        return tSize;
    }

    public int GetUserAction(int inUserActionIndex) {
        int tUserAction = m_UserActions.elementAt(inUserActionIndex);
        Info("GetUserAction() inUserActionIndex=" + inUserActionIndex+" returning " + tUserAction);
        return tUserAction;
    }

    public void SetCameraDirectionVector(float[] inCameraDirectionVector) {
        Info("SetCameraDirectionVector() inCameraDirectionVector=" + inCameraDirectionVector);
        m_ViewDirection = inCameraDirectionVector;
    }

    private float[] m_ViewDirection = new float[] { 0.0f, 1.0f, 0.0f, 0.0f };

    public float[] GetCameraDirectionVector() {
        Info("GetCameraDirectionVector() returning " + m_ViewDirection);
        return m_ViewDirection;
    }

    public int GetUserScore() {
        Info("GetUserScore() returning " + m_Score);
        return m_Score;
    }

    public int GetUserHitpoints() {
        Info("GetUserHitpoints() returning " + m_HitPoints);
        return m_HitPoints;
    }

    public void run() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {}
    }

    private void Info(String message)
    {
        Log.d("[Client]", message);
    }

}
