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

    private GLSurfaceView  GLES20SurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("BugHoleGraphicsEngine.onCreate()", "OnCreate called on activity!");
        super.onCreate(savedInstanceState);

        //force landscape orientation and disable re-orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //hide the title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //assume GLES20 rendering is available on the device
        //setup the SurfaceView for the activity
        GLES20SurfaceView = new GLSurfaceView(this);
        //GLES20SurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 0);
        GLES20SurfaceView.setEGLContextClientVersion(2);
        GLES20SurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
        GLES20Renderer tGLES20Renderer = new GLES20Renderer(this);
        GLES20SurfaceView.setRenderer(tGLES20Renderer);

        //https://developer.android.com/training/system-ui/immersive.html
        GLES20SurfaceView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        GLES20SurfaceView.setOnTouchListener(tGLES20Renderer);

        setContentView(GLES20SurfaceView);
    }

    @Override
    public void onPause() {
        Log.d("BugHoleGraphicsEngine.onPause()", "OnPause called on activity!");
        super.onPause();
        Log.d("BugHoleGraphicsEngine.onResume()", "Forewarding onPause() call to the GLES20SurfaceView");
        GLES20SurfaceView.onPause();
    }

    @Override
    public void onResume() {
        Log.d("BugHoleGraphicsEngine.onResume()", "OnResume called on activity!");
        super.onResume();

        //the system ui visibility state gets lost during pause, we need to configure the visibilty
        //again to get into "immersive fullscreen"
        Log.d("BugHoleGraphicsEngine.onResume()", "Resetting system ui visibility state to 'immersive fullscreen'");
        //https://developer.android.com/training/system-ui/immersive.html
        GLES20SurfaceView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Log.d("BugHoleGraphicsEngine.onResume()", "Forewarding onResume() call to the GLES20SurfaceView (to reinitialize the new GLContext)");
        GLES20SurfaceView.onResume();
    }
}
