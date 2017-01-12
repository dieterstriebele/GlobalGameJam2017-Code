
import java.net.ServerSocket;
import java.net.Socket;

import game.GameState;
import util.Logger;
import util.DiscoveryThread;

public class BugHoleServerMain {

	private static final int SERVERPORT_GEOMETRY_CLIENT = 9090;
	private static final int SERVERPORT_COMMAND_CLIENT = 9091;
	
    private static ServerSocket geometryServerSocket;
    private static ServerSocket commandServerSocket;
    private static long timeBase = System.currentTimeMillis();
	
	public static void main(String[] args) {
		System.out.println("Starting BugHoleServerEngine ...");

        try {        	       
        	DiscoveryThread discoveryThread = DiscoveryThread.getInstance();
        	(new Thread(discoveryThread)).start();
        	
        	geometryServerSocket = new ServerSocket(SERVERPORT_GEOMETRY_CLIENT);
            Logger.Info("Started geomtery server on Port:" + SERVERPORT_GEOMETRY_CLIENT);
            //http://stackoverflow.com/questions/8780667/socket-setperformancepreferences
            geometryServerSocket.setPerformancePreferences(0, 1, 2);
            
            commandServerSocket = new ServerSocket(SERVERPORT_COMMAND_CLIENT);
            Logger.Info("Started command server on Port:" + SERVERPORT_COMMAND_CLIENT);
            //http://stackoverflow.com/questions/8780667/socket-setperformancepreferences
            commandServerSocket.setPerformancePreferences(0, 1, 2);            
            
            GameState gameState = new GameState();
            
            while(true)
            {
                Logger.Info("Wait for incoming client connection");
                
                Socket geometrySocket = geometryServerSocket.accept();
                Logger.Info("Accepting connection from " + geometrySocket);
                geometrySocket.setTcpNoDelay(true);
                geometrySocket.setKeepAlive(true);
                //geometrySocket.setSoTimeout(15000);
                
                GeometryClient geometryClient = new GeometryClient(geometrySocket,timeBase, gameState);
                geometryClient.StartReadingThread();
                
                Socket commandSocket = commandServerSocket.accept();
                Logger.Info("Accepting connection from " + commandSocket);
                commandSocket.setTcpNoDelay(true);
                commandSocket.setKeepAlive(true);
                
                CommandClient commandClient = new CommandClient(commandSocket, timeBase, gameState);
                commandClient.StartReadingThread();
                commandClient.StartWritingThread();
                
                gameState.Init();
                //Thread.sleep(10000000);
            }
                       
        } catch (Exception e) {
            Logger.Error("Could not start game server", e);
        }
	}

}
