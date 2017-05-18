package ggj2k15.bughole.bugholegraphicsengine;

import android.view.MotionEvent;
import android.view.View;

import ggj2k15.bughole.bugholegraphicsengine.camerainformation.impl.CameraInformationDevice;
import ggj2k15.bughole.bugholegraphicsengine.camerainformation.impl.CameraInformationLegacy;
import ggj2k15.bughole.bugholegraphicsengine.camerainformation.impl.CameraInformationStubs;
import ggj2k15.bughole.bugholegraphicsengine.camerainformation.impl.CameraInformationTouch;
import ggj2k15.bughole.bugholegraphicsengine.camerainformation.impl.CameraInformationWithoutDrift;
import ggj2k15.bughole.bugholegraphicsengine.clientinformation.impl.ClientInformationFireDev;
import ggj2k15.bughole.bugholegraphicsengine.clientinformation.impl.ClientInformationNetwork;
import ggj2k15.bughole.bugholegraphicsengine.clientinformation.impl.ClientInformationStubs;
import ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl.GeometryInformationFireDev;
import ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl.GeometryInformationNetwork;
import ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl.GeometryInformationStubs;
import ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl.GeometryInformationStubsPath;
import ggj2k15.bughole.bugholegraphicsengine.interfaces.ICameraInformation;
import ggj2k15.bughole.bugholegraphicsengine.interfaces.IClientInformation;
import ggj2k15.bughole.bugholegraphicsengine.interfaces.IGeometryInformation;

/**
 * Created by ewers on 25.04.2017.
 */

public class BugHoleGameController implements View.OnTouchListener {

    private IGeometryInformation m_GeometryInformation;
    private ICameraInformation m_CameraInformation;
    private IClientInformation m_ClientInformation;

    private final float TOUCH_SCALE_FACTOR = 0.15f;
    private float mPreviousX;
    private float mPreviousY;
    private float m_RotateX = 0.0f;
    private float m_RotateY = 360.0f;
    private float m_RotateZ = 0.0f;

    public BugHoleGameController()
    {
        //TODO: exchange for networking/device sensory test
        GeometryInformationFireDev geoFireDev = new GeometryInformationFireDev();

        //m_GeometryInformation = new GeometryInformationStubs();
        //m_GeometryInformation = new GeometryInformationNetwork();
        //m_GeometryInformation = new GeometryInformationStubsPath(m_Context.getResources().openRawResource(R.raw.brainmine_path));
        //m_GeometryInformation = new GeometryInformationStubsPath(m_Context);

        //m_CameraInformation = new CameraInformationStubs();
        //m_CameraInformation = new CameraInformationDevice(m_Context);
        //m_CameraInformation = new CameraInformationLegacy(m_Context);
        //m_CameraInformation = new CameraInformationWithoutDrift(m_Context);
        m_CameraInformation = new CameraInformationTouch();

        //m_ClientInformation = new ClientInformationStubs();
        //m_ClientInformation = new ClientInformationNetwork();
        ClientInformationFireDev clientFireDev = new ClientInformationFireDev();

        geoFireDev.RegisterClientInformation(clientFireDev);
        clientFireDev.RegisterGeometryInformation(geoFireDev);

        m_ClientInformation = clientFireDev;
        m_GeometryInformation = geoFireDev;
    }

    public IGeometryInformation GetGeometryInformation()
    {
        return m_GeometryInformation;
    }

    public ICameraInformation GetCameraInformation()
    {
        return m_CameraInformation;
    }

    public IClientInformation GetClientInformation()
    {
        return m_ClientInformation;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        //check if events for mouse based camera need to be processed
        if (m_CameraInformation instanceof CameraInformationTouch) {
            CameraInformationTouch tCameraInformationTouch = (CameraInformationTouch) m_CameraInformation;
            float x = event.getX();
            float y = event.getY();
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                m_RotateX = m_RotateX - (dy * TOUCH_SCALE_FACTOR);
                m_RotateY = m_RotateY + (dx * TOUCH_SCALE_FACTOR);
                tCameraInformationTouch.SetRotationX(m_RotateX);
                tCameraInformationTouch.SetRotationY(m_RotateY);
                tCameraInformationTouch.SetRotationZ(m_RotateZ);
            }
            mPreviousX = x;
            mPreviousY = y;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            m_ClientInformation.AddUserAction(IClientInformation.cACTIONIDENTIFICATION_FIRE);
            m_ClientInformation.SetCameraDirectionVector(m_CameraInformation.GetCameraDirectionVector());
            m_ClientInformation.SynchronizeState();

            // Most annoying feature ever!
            // POC Vibration! Uh Ah! TODO: Vibrate on collision.
            //Vibrator v = (Vibrator) m_Context.getSystemService(Context.VIBRATOR_SERVICE);

            // Vibrate for 500 milliseconds
            //v.vibrate(500);
            // Start without a delay
            // Vibrate for 100 milliseconds
            // Each element then alternates between vibrate, sleep, vibrate, sleep...
            //long[] pattern = {0, 200, 100, 150, 250, 300, 200, 10, 300};
            //v.vibrate(pattern, -1);
        }
        return true;
    }
}
