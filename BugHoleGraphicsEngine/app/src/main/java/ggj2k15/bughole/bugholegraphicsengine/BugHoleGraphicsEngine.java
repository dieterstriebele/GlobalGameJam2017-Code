package ggj2k15.bughole.bugholegraphicsengine;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.opengl.GLSurfaceView;
import android.view.Window;

import ggj2k15.bughole.bugholegraphicsengine.gles20.GLES20Renderer;

public class BugHoleGraphicsEngine extends Activity {

    private GLSurfaceView  m_surfaceView;
    private BugHoleGameController m_gameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("BugHoleGraphicsEngine.onCreate()", "OnCreate called on activity!");
        super.onCreate(savedInstanceState);

        m_gameController = new BugHoleGameController();

        //force landscape orientation and disable re-orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //assume GLES20 rendering is available on the device
        //setup the SurfaceView for the activity
        m_surfaceView = new GLSurfaceView(this);
        //m_surfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 0);
        m_surfaceView.setEGLContextClientVersion(2);
        m_surfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
        GLES20Renderer tGLES20Renderer = new GLES20Renderer(this, m_gameController);
        m_surfaceView.setRenderer(tGLES20Renderer);

        //https://developer.android.com/training/system-ui/immersive.html
        m_surfaceView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //m_surfaceView.setOnTouchListener(tGLES20Renderer);
        m_surfaceView.setOnTouchListener(m_gameController);

        setContentView(m_surfaceView);
    }

    @Override
    public void onPause() {
        Log.d("BugHoleGraphicsEngine.onPause()", "OnPause called on activity!");
        super.onPause();
        Log.d("BugHoleGraphicsEngine.onResume()", "Forewarding onPause() call to the m_surfaceView");
        m_surfaceView.onPause();
    }

    @Override
    public void onResume() {
        Log.d("BugHoleGraphicsEngine.onResume()", "OnResume called on activity!");
        super.onResume();

        //the system ui visibility state gets lost during pause, we need to configure the visibilty
        //again to get into "immersive fullscreen"
        Log.d("BugHoleGraphicsEngine.onResume()", "Resetting system ui visibility state to 'immersive fullscreen'");
        //https://developer.android.com/training/system-ui/immersive.html
        m_surfaceView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Log.d("BugHoleGraphicsEngine.onResume()", "Forewarding onResume() call to the m_surfaceView (to reinitialize the new GLContext)");
        m_surfaceView.onResume();
    }
}
