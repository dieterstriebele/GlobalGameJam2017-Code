package ggj2k15.bughole.bugholegraphicsengine.geometryinformation.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.SystemClock;
import android.util.Log;

import ggj2k15.bughole.bugholegraphicsengine.Settings;
import ggj2k15.bughole.bugholegraphicsengine.interfaces.IGeometryInformation;
import ggj2k15.bughole.bugholegraphicsengine.util.BufferConvert;
import ggj2k15.bughole.bugholegraphicsengine.util.ServiceLocateUDP;

public class GeometryInformationNetwork implements IGeometryInformation, Runnable {

    private DataOutputStream out;
    private DataInputStream in;
    private Socket socket;
    private PrintWriter printWriter;
    //private BufferedReader bufferedReader;
    //private Thread readingThread;

    private int m_CurrentNumberOfObjects = 0;
    private int m_MaxNumberOfObjects = 100;
    private float[] m_ObjectXPositions;
    private float[] m_ObjectYPositions;
    private float[] m_ObjectZPositions;
    private float[] m_ObjectXRotations;
    private float[] m_ObjectYRotations;
    private float[] m_ObjectZRotations;
    private float[] m_ObjectXScalings;
    private float[] m_ObjectYScalings;
    private float[] m_ObjectZScalings;
    private int[] m_ObjectModelIdentification;

    private int m_CurrentNumberOfObjects_Backbuffer = 0;
    private float[] m_ObjectXPositions_Backbuffer;
    private float[] m_ObjectYPositions_Backbuffer;
    private float[] m_ObjectZPositions_Backbuffer;
    private float[] m_ObjectXRotations_Backbuffer;
    private float[] m_ObjectYRotations_Backbuffer;
    private float[] m_ObjectZRotations_Backbuffer;
    private float[] m_ObjectXScalings_Backbuffer;
    private float[] m_ObjectYScalings_Backbuffer;
    private float[] m_ObjectZScalings_Backbuffer;
    private int[] m_ObjectModelIdentification_Backbuffer;

    public GeometryInformationNetwork() {
        m_ObjectXPositions = new float[m_MaxNumberOfObjects];
        m_ObjectYPositions = new float[m_MaxNumberOfObjects];
        m_ObjectZPositions = new float[m_MaxNumberOfObjects];
        m_ObjectXRotations = new float[m_MaxNumberOfObjects];
        m_ObjectYRotations = new float[m_MaxNumberOfObjects];
        m_ObjectZRotations = new float[m_MaxNumberOfObjects];
        m_ObjectXScalings = new float[m_MaxNumberOfObjects];
        m_ObjectYScalings = new float[m_MaxNumberOfObjects];
        m_ObjectZScalings = new float[m_MaxNumberOfObjects];
        m_ObjectModelIdentification = new int[m_MaxNumberOfObjects];
        m_ObjectXPositions_Backbuffer = new float[m_MaxNumberOfObjects];
        m_ObjectYPositions_Backbuffer = new float[m_MaxNumberOfObjects];
        m_ObjectZPositions_Backbuffer = new float[m_MaxNumberOfObjects];
        m_ObjectXRotations_Backbuffer = new float[m_MaxNumberOfObjects];
        m_ObjectYRotations_Backbuffer = new float[m_MaxNumberOfObjects];
        m_ObjectZRotations_Backbuffer = new float[m_MaxNumberOfObjects];
        m_ObjectXScalings_Backbuffer = new float[m_MaxNumberOfObjects];
        m_ObjectYScalings_Backbuffer = new float[m_MaxNumberOfObjects];
        m_ObjectZScalings_Backbuffer = new float[m_MaxNumberOfObjects];
        m_ObjectModelIdentification_Backbuffer = new int[m_MaxNumberOfObjects];
    }

    //update position and rotation of all objects (will be called every frame)
    public void SynchronizeState() {
        write("SynchronizeState");
    }

    public void SwapState() {
        m_ObjectXPositions = m_ObjectXPositions_Backbuffer;
        m_ObjectYPositions = m_ObjectYPositions_Backbuffer;
        m_ObjectZPositions = m_ObjectZPositions_Backbuffer;
        m_ObjectXRotations = m_ObjectXRotations_Backbuffer;
        m_ObjectYRotations = m_ObjectYRotations_Backbuffer;
        m_ObjectZRotations = m_ObjectZRotations_Backbuffer;
        m_ObjectXScalings = m_ObjectXScalings_Backbuffer;
        m_ObjectYScalings = m_ObjectYScalings_Backbuffer;
        m_ObjectZScalings = m_ObjectZScalings_Backbuffer;
        m_ObjectModelIdentification = m_ObjectModelIdentification_Backbuffer;
    }

    public int GetNumberOfObjects() {
        return m_CurrentNumberOfObjects;
    }

    public float GetObjectXPosition(int inObjectIndex) { return m_ObjectXPositions[inObjectIndex]; }

    public float GetObjectYPosition(int inObjectIndex) { return m_ObjectYPositions[inObjectIndex]; }

    public float GetObjectZPosition(int inObjectIndex) { return m_ObjectZPositions[inObjectIndex]; }

    public float GetObjectXRotation(int inObjectIndex) {
        return m_ObjectXRotations[inObjectIndex];
    }

    public float GetObjectYRotation(int inObjectIndex) {
        return m_ObjectYRotations[inObjectIndex];
    }

    public float GetObjectZRotation(int inObjectIndex) {
        return m_ObjectZRotations[inObjectIndex];
    }

    public float GetObjectXScaling(int inObjectIndex) { return m_ObjectXScalings[inObjectIndex]; }

    public float GetObjectYScaling(int inObjectIndex) { return m_ObjectYScalings[inObjectIndex]; }

    public float GetObjectZScaling(int inObjectIndex) { return m_ObjectZScalings[inObjectIndex]; }

    public int GetObjectModelIdentification(int inObjectIndex) { return m_ObjectModelIdentification[inObjectIndex]; }

    private boolean IsReady = false;

    private boolean AnswerReceived = false;

    public void run() {
        try {

            ServiceLocateUDP.locateServerByBroadcast();

            Info("Creating inetadress ...");
            //InetAddress serverAdress = InetAddress.getByName("192.168.0.123");
            InetAddress serverAdress = InetAddress.getByName(Settings.Ip);
            Info("testing reachability");
            serverAdress.isReachable(2000);
            Info("adress reachable!");
            Info("Creating socket ...");
            socket = new Socket();


            //while(true) {
                Info("Trying to connect to socket ...");
            //    try {
                    socket.connect(new InetSocketAddress(serverAdress, 9090), 15000);
            //        break;
            //    } catch (Exception e) {
            //        Info("Connecting to socket failed! Retrying ... " + e.getMessage());
            //    }
            //}

            socket.setKeepAlive(true);
            //http://stackoverflow.com/questions/8780667/socket-setperformancepreferences
            socket.setPerformancePreferences(0, 1, 2);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(2000);
            Info("Configured socket!");
            Info("Obtaining DataInputStream from socket ...");
            in =  new DataInputStream(socket.getInputStream());
            Info("Obtaining DataOutputStream from socket ...");
            out = new DataOutputStream(socket.getOutputStream());
            Info("Wrapping stream in reader/writer");
            printWriter = new PrintWriter(out);
            //bufferedReader = new BufferedReader(new InputStreamReader( in ));



            Info("Connection to server socket established!");


            IsReady = true;

            //int numberOfObjects = 1000;
            int sizeOfFloat = 4;
            int sizeOfInt = 4;
            int numberOfFloatsPerObject = 3 + 3 + 3 + 1;
            //3 for position
            //3 for rotation
            //3 for scaling
            //1 for identification

            int sizeIntForNumberOfObjects = 4;




            //byte buf[] = new byte[(m_MaxNumberOfObjects * numberOfFloatsPerObject * sizeOfFloat) + sizeIntForNumberOfObjects];
            byte buf[] = new byte[m_MaxNumberOfObjects * numberOfFloatsPerObject * sizeOfFloat];


            while(true){
                try{


                    //http://en.wikipedia.org/wiki/Large_segment_offload
                    //For example, a unit of 64kB (65,536 bytes) of data is usually segmented to 46 segments of 1448 bytes each before it is sent through the NIC and over the network.
                    //int numberOfReadBytes = in.read(buf, 0, buf.length);
                    //Info("buf.length="+buf.length+" numberOfReadBytes="+numberOfReadBytes);

                    //we have to use readfully to stay synchronized and avoid the hassle of handling TCP segmentation
                    long startTime = SystemClock.elapsedRealtime();
                    //hoppe suxx ass!
                    // fu hoshi. I'll commit my stuff RIGHT NOW!
                    m_CurrentNumberOfObjects = in.readInt()/(numberOfFloatsPerObject * sizeOfFloat);
                    Info("m_CurrentNumberOfObjects="+m_CurrentNumberOfObjects);

                    if (m_CurrentNumberOfObjects != 0) {
                        in.readFully(buf, 0, numberOfFloatsPerObject * sizeOfFloat * m_CurrentNumberOfObjects);
                        long endTime = SystemClock.elapsedRealtime();
                        Log.d("GeometryInformationNetwork", "readFully() took " + (endTime - startTime) + " ms");

                        //m_CurrentNumberOfObjects = ConvertBytesAtBufferOffsetToInt(buf,0);



                        for (int i=0; i<m_CurrentNumberOfObjects; i++) {
                            m_ObjectXPositions_Backbuffer[i] = BufferConvert.ConvertBytesAtBufferOffsetToIntAndThenFloat(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 0));
                            m_ObjectYPositions_Backbuffer[i] = BufferConvert.ConvertBytesAtBufferOffsetToIntAndThenFloat(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 1));
                            m_ObjectZPositions_Backbuffer[i] = BufferConvert.ConvertBytesAtBufferOffsetToIntAndThenFloat(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 2));
                            m_ObjectXRotations_Backbuffer[i] = BufferConvert.ConvertBytesAtBufferOffsetToIntAndThenFloat(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 3));
                            m_ObjectYRotations_Backbuffer[i] = BufferConvert.ConvertBytesAtBufferOffsetToIntAndThenFloat(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 4));
                            m_ObjectZRotations_Backbuffer[i] = BufferConvert.ConvertBytesAtBufferOffsetToIntAndThenFloat(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 5));
                            m_ObjectXScalings_Backbuffer[i]  = BufferConvert.ConvertBytesAtBufferOffsetToIntAndThenFloat(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 6));
                            m_ObjectYScalings_Backbuffer[i]  = BufferConvert.ConvertBytesAtBufferOffsetToIntAndThenFloat(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 7));
                            m_ObjectZScalings_Backbuffer[i]  = BufferConvert.ConvertBytesAtBufferOffsetToIntAndThenFloat(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 8));
                            m_ObjectModelIdentification_Backbuffer[i]   = BufferConvert.ConvertBytesAtBufferOffsetToInt(buf, (i * sizeOfFloat * numberOfFloatsPerObject) + (sizeOfFloat * 9));



                        }

                        //Info("m_ObjectXPositions_Backbuffer");
                        //Info(m_ObjectXPositions_Backbuffer);
                        //Info("m_ObjectYPositions_Backbuffer");
                        //Info(m_ObjectYPositions_Backbuffer);
                        //Info("m_ObjectZPositions_Backbuffer");
                        //Info(m_ObjectZPositions_Backbuffer);

                        //Info("m_ObjectXRotations_Backbuffer");
                        //Info(m_ObjectXRotations_Backbuffer);
                        //Info("m_ObjectYRotations_Backbuffer");
                        //Info(m_ObjectYRotations_Backbuffer);
                        //Info("m_ObjectZRotations_Backbuffer");
                        //Info(m_ObjectZRotations_Backbuffer);

                        //Info("m_ObjectXScalings_Backbuffer");
                        //Info(m_ObjectXScalings_Backbuffer);
                        //Info("m_ObjectYScalings_Backbuffer");
                        //Info(m_ObjectYScalings_Backbuffer);
                        //Info("m_ObjectZScalings_Backbuffer");
                        //Info(m_ObjectZScalings_Backbuffer);

                        //Info("m_ObjectModelIdentification_Backbuffer");
                        //Info(m_ObjectModelIdentification_Backbuffer);


                        //!!! tNumberOfObjects must be used in renderer

                    /*
                    String receivedLine = bufferedReader.readLine();
                    if (receivedLine != null && receivedLine.length() > 0)
                    {
                        //Info("received: " + receivedLine);
                        handleIncomingCommand(receivedLine);
                    }
                    */

                    }
                    AnswerReceived = true;

                }
                catch(Exception e)
                {
                    Error("Error occured while reading from inputstream!", e);
                    break;
                }
            }

        } catch (Exception e) {
            Error("Trying connect to server", e);
            e.printStackTrace();
        }


    }

    private void write(String message)
    {
        if (IsReady) {
            try {
                //Info("Trying to write: " + message);
                AnswerReceived = false;

                printWriter.println(message);
                printWriter.flush();

//                while(!AnswerReceived) {
                    //Info("Waiting");
                    //Thread.sleep(1);
//                }

            } catch (Exception exception) {
                Error("Error occured while writing to server!", exception);
            }
        }
    }
/*
    private void handleIncomingCommand(String message) {
        //Info("handleIncomingCommand message.length=" + message.length());
        StringTokenizer tStringTokenizer = new StringTokenizer(message,";");
        int i=0;
        while (tStringTokenizer.hasMoreTokens()) {
            //String currentToken =  tStringTokenizer.nextToken();
            //Info("currentToken="+currentToken);
            m_ObjectXPositions_Backbuffer[i] = Float.parseFloat(tStringTokenizer.nextToken());
            m_ObjectYPositions_Backbuffer[i] = Float.parseFloat(tStringTokenizer.nextToken());
            m_ObjectZPositions_Backbuffer[i] = Float.parseFloat(tStringTokenizer.nextToken());
            m_ObjectXRotations_Backbuffer[i] = Float.parseFloat(tStringTokenizer.nextToken());
            m_ObjectYRotations_Backbuffer[i] = Float.parseFloat(tStringTokenizer.nextToken());
            m_ObjectZRotations_Backbuffer[i] = Float.parseFloat(tStringTokenizer.nextToken());
            i++;
        }
    }
*/
    private void Info(float[] inFloatArray) {
        StringBuffer tStringBuffer = new StringBuffer();
        Info("Logging float array inFloatArray.lenght="+inFloatArray.length);
        for (int i=0; i<inFloatArray.length; i++) {
            tStringBuffer.append(" ["+i+"]="+inFloatArray[i]);
        }
        Info(tStringBuffer.toString());
    }

    private void Info(int[] inIntegerArray) {
        StringBuffer tStringBuffer = new StringBuffer();
        Info("Logging int array inIntegerArray.length="+inIntegerArray.length);
        for (int i=0; i<inIntegerArray.length; i++) {
            tStringBuffer.append(" ["+i+"]="+inIntegerArray[i]);
        }
        Info(tStringBuffer.toString());
    }

    private void Info(String message)
    {
        Log.d("[Socket]",message);
    }

    private void Error(String message)
    {
        Log.e("[Socket]",message);
    }

    private void Error(String message, Exception exception)
    {
        Log.e("[Socket]",message  + "|" + exception.getMessage());
    }

}
