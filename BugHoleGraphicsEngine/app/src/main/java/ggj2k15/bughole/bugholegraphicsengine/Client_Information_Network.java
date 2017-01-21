package ggj2k15.bughole.bugholegraphicsengine;

import android.util.Log;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Vector;

import ggj2k15.bughole.bugholegraphicsengine.util.BufferConvert;
import ggj2k15.bughole.bugholegraphicsengine.util.ServiceLocateSubnetScan;
import ggj2k15.bughole.bugholegraphicsengine.util.ServiceLocateUDP;

public class Client_Information_Network implements IClient_Information, Runnable {

    private static final int ReadingBufferMaxLength = 1024;

    public static final int GameEventNone = 0;
    public static final int GameEventPlayerHitsEnemy = 1;
    public static final int GameEventEnemyHitsPlayer = 2;

    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;

    private boolean IsReady = false;
    private boolean NeedsSynchronization = false;

    private Vector<Integer> m_UserActions = new Vector<Integer>(10);
    private int m_Score = 0;
    private int m_HitPoints = 10000;

    private byte[] m_ReadingBuffer;

    public void SynchronizeState() {
        Info("SynchronizeState()");
        NeedsSynchronization = true;
        /*
        while(!NeedsSynchronization) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                Error("Error occured while waiting for synchronization!", e);
                break;
            }
        }
        */
    }

    public void AddUserAction(int inUserAction) {
        Info("AddUserAction() inUserAction=" + inUserAction);
        m_UserActions.add(inUserAction);
    }

    public int GetNumberOfUserActions() {
        int tSize = m_UserActions.size();
        return tSize;
    }

    public int GetUserAction(int inUserActionIndex) {
        int tUserAction = m_UserActions.elementAt(inUserActionIndex);
        Info("GetUserAction() inUserActionIndex=" + inUserActionIndex + " returning " + tUserAction);
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

            ServiceLocateUDP.locateServerByBroadcast();

            Info("Creating inetadress ...");
            InetAddress serverAdress = InetAddress.getByName(Settings.Ip);
            Info("testing reachability");
            serverAdress.isReachable(2000);
            Info("Adress reachable!");
            Info("Creating socket ...");
            socket = new Socket();

            //while(true) {
            Info("Trying to connect to socket ...");
            //    try {
            socket.connect(new InetSocketAddress(serverAdress, 9091), 15000);
            //        break;
            //    } catch (Exception e) {
            //        Info("Connecting to socket failed! Retrying ... " + e.getMessage());
            //    }
            //}

            socket.setKeepAlive(true);
            //http://stackoverflow.com/questions/8780667/socket-setperformancepreferences
            socket.setPerformancePreferences(0, 1, 2);
            socket.setTcpNoDelay(true);
//            socket.setSoTimeout(2000);
            Info("Configured socket!");
            Info("Obtaining DataInputStream from socket ...");
            in = new DataInputStream(socket.getInputStream());
            Info("Obtaining DataOutputStream from socket ...");
            out = new DataOutputStream(socket.getOutputStream());
            Info("Connection to server socket established!");

            IsReady = true;

            StartWritingThread();
            StartReadingThread();

        } catch (Exception e) {
            Error("Trying connect to server", e);
            e.printStackTrace();
        }
    }

    public void ConvertFloatToIntAndWriteToBufferAtOffset(float inValue, byte[] buf, int offset) {
        WriteIntToBufferAtOffset(Float.floatToRawIntBits(inValue), buf, offset);
    }

    public void WriteIntToBufferAtOffset(int inValue, byte[] buf, int offset) {
        buf[offset + 0] = (byte) (inValue >> 24);
        buf[offset + 1] = (byte) (inValue >> 16) ;
        buf[offset + 2] = (byte) (inValue >> 8);
        buf[offset + 3] = (byte) (inValue);
    }

    private void StartWritingThread()
    {
        Thread thread = new Thread() {
            public void run() {
                int sizeOfFloat = 4;
                int sizeOfInt = 4;

                while (true) {
                    try {
                        //currently only "fire" commands with 1 int and 4 floats is supported
                        //do some for-loop here to accumulate size for different actions
                        int numberOfUserActions = GetNumberOfUserActions();


                        if (numberOfUserActions > 0) {
                            int numberOfBytesForUserActions = (numberOfUserActions * sizeOfInt) + (numberOfUserActions * sizeOfFloat * 4);

                            //byte bytebuffer[] = new byte[numberOfBytesForUserActions];

                            int numberOfBytesForOneUserAction = sizeOfInt + sizeOfFloat * 4;

                            out.writeInt(numberOfBytesForUserActions);

                            byte[] tByteArray = new byte[numberOfBytesForUserActions];
                            for (int i = 0; i < numberOfUserActions; i++) {
                                WriteIntToBufferAtOffset(GetUserAction(i), tByteArray, (i * numberOfBytesForUserActions) + (sizeOfFloat * 0));
                                float[] tCameraDirectionVector = GetCameraDirectionVector();
                                ConvertFloatToIntAndWriteToBufferAtOffset(tCameraDirectionVector[0], tByteArray, (i * numberOfBytesForOneUserAction) + (sizeOfFloat * 1));
                                ConvertFloatToIntAndWriteToBufferAtOffset(tCameraDirectionVector[1], tByteArray, (i * numberOfBytesForOneUserAction) + (sizeOfFloat * 2));
                                ConvertFloatToIntAndWriteToBufferAtOffset(tCameraDirectionVector[2], tByteArray, (i * numberOfBytesForOneUserAction) + (sizeOfFloat * 3));
                                ConvertFloatToIntAndWriteToBufferAtOffset(tCameraDirectionVector[3], tByteArray, (i * numberOfBytesForOneUserAction) + (sizeOfFloat * 4));
                            }
                            out.write(tByteArray, 0, tByteArray.length);
                            m_UserActions.removeAllElements();
                        }
                        //this needs to be slowed down to wait for the next command !
                        //while(!NeedsSynchronization) {
                        Thread.sleep(5);
                        //}

                    } catch (Exception e) {
                        Error("Error occured in client information writing thread!", e);
                        break;
                    }
                }
            }
        };

        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public void StartReadingThread() {
        Thread thread = new Thread() {
            public void run() {
                int sizeofInt = 4;
                int sizeofFloat = 4;

                m_ReadingBuffer = new byte[ReadingBufferMaxLength];
                while (true) {
                    try {
                        int bufferSize = in.readInt();
                        in.readFully(m_ReadingBuffer, 0, bufferSize);

                        int bytesParsed = 0;

                        while (bytesParsed < bufferSize) {
                            // First read out the server event id:
                            int eventId = BufferConvert.ConvertBytesAtBufferOffsetToInt(m_ReadingBuffer, bytesParsed);
                            bytesParsed += sizeofInt;

                            // parse the next bytes depending on the event
                            if (eventId == GameEventPlayerHitsEnemy) {
                                // read out the current player score
                                m_Score = BufferConvert.ConvertBytesAtBufferOffsetToInt(m_ReadingBuffer, bytesParsed);
                                bytesParsed += sizeofInt;
                            } else if (eventId == GameEventEnemyHitsPlayer) {
                                // Read out player hit points
                                m_HitPoints = BufferConvert.ConvertBytesAtBufferOffsetToInt(m_ReadingBuffer, bytesParsed);
                                bytesParsed += sizeofInt;
                            }
                        }
                    } catch (Exception e) {
                        Error("Error occured in client information reading thread!", e);
                        break;
                    }
                }
            }
        };

        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    private void Info(String message)
    {
        Log.d("[Client]", message);
    }

    private void Error(String message)
    {
        Log.e("[Client]", message);
    }

    private void Error(String message, Exception exception)
    {
        Log.e("[Client]", message + "|" + exception.getMessage());
    }
}
