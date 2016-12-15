package ggj2k15.bughole.bugholegraphicsengine;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.opengl.GLSurfaceView;
import android.view.Window;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class BugHoleGraphicsEngine extends Activity {

    private GLSurfaceView  GLES20SurfaceView;

    public static void locateServerByBroadcast()
    {
        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                c.send(sendPacket);
                System.out.println(">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }

                    System.out.println( ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            System.out.println( ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            System.out.println( ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
                //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
//                Controller_Base.setServerIp(receivePacket.getAddress());
                Settings.Ip = receivePacket.getAddress().getHostAddress();
                Log.d(">>>> Foud IP Address: ", Settings.Ip);
            }

            //Close the port!
            c.close();
        } catch (IOException ex) {
//            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
