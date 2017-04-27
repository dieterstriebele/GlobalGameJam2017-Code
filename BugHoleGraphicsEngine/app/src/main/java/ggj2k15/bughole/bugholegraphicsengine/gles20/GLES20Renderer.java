package ggj2k15.bughole.bugholegraphicsengine.gles20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import ggj2k15.bughole.bugholegraphicsengine.BugHoleGameController;

public class GLES20Renderer implements GLSurfaceView.Renderer{

    private GLES20Content mGLES20Content;

    public GLES20Renderer(Context context, BugHoleGameController controller) {
        Log.d("GLES20Renderer.GLES20Renderer()", "Constructor called!");
        mGLES20Content = new GLES20Content(context, controller);
    }

    @Override
    public void onDrawFrame(GL10 a_glUnused) {
        //Log.d("GLES20Renderer.onDrawFrame()", "a_glUnused="+a_glUnused);
        mGLES20Content.render();
    }

    @Override
    public void onSurfaceChanged(GL10 a_glUnused, int a_Width, int a_Height) {
        Log.d("GLES20Renderer.onSurfaceChanged()", "a_glUnused="+a_glUnused+" a_Width="+a_Width+" a_Height="+a_Height);
         mGLES20Content.Initialize(a_Width, a_Height);
    }

    @Override
    public void onSurfaceCreated(GL10 a_glUnused, EGLConfig a_EGLConfig) {
        Log.d("GLES20Renderer.onSurfaceCreated()", "a_glUnused=" + a_glUnused + " a_EGLConfig=" + a_EGLConfig);
    }
}

