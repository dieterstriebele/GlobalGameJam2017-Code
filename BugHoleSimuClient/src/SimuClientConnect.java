import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SimuClientConnect {
	private Socket geometrySocket;
	private Socket clientSocket;
	
	//public static String ipAddress = "192.168.0.123";
	public static String ipAddress = "127.0.0.1";
	
	public void Connect()
	{
		Logger.Info("SimuClient trying to set up connection to BugHoleServer...");
		try
		{	
			Logger.Info("IP-Address: " + ipAddress);
			InetAddress serverAdress = InetAddress.getByName(ipAddress);
			serverAdress.isReachable(2000);
			
			Logger.Info("Setting up Geometry Socket");
			geometrySocket = new Socket();			
			geometrySocket.connect(new InetSocketAddress(serverAdress, 9090), 15000);
			geometrySocket.setKeepAlive(true);
            geometrySocket.setPerformancePreferences(0, 1, 2);
            geometrySocket.setTcpNoDelay(true);
            
            Logger.Info("Setting up Client Socket");
            clientSocket = new Socket();			
            clientSocket.connect(new InetSocketAddress(serverAdress, 9091), 15000);
            clientSocket.setKeepAlive(true);
            clientSocket.setPerformancePreferences(0, 1, 2);
            clientSocket.setTcpNoDelay(true);
            
            Logger.Info("Connected to BugHoleServer!");
		}
		catch (Exception e)
		{
			Logger.Error("Error while trying to connect to BugHoleServer.", e);
			e.printStackTrace();
		}
	}
	
	public void doSomeCrazyWork()
	{
		try
		{
			InputStreamReader in = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(in);
			
			DataOutputStream out = new DataOutputStream(geometrySocket.getOutputStream());
			PrintWriter printer = new PrintWriter(out);
			
			String str;
			
			boolean cancelled = false;
			
			while(true)
	        {
				Logger.Print("Type a command: ");
				str = br.readLine();
				
				switch(str)
				{
				case "help":
					Logger.Info("There's no help, bitch!");
					break;
					
				case "up":
				case "down":
				case "left":
				case "right":
					printer.println(str);
					break;
					
				case "shoot":
					break;
					
				case "quit":
					cancelled = true;
					break;
					
				default:
					Logger.Error("Unknown command");
					break;
				}
				
				if(cancelled)
				{
					break;
				}
				
				printer.flush();
	        }
			
			br.close();
			
			Logger.Info("Closing connection to BugHoleServer...");
			geometrySocket.close();
			clientSocket.close();
			Logger.Info("Connection to BugHoleServer closed!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
