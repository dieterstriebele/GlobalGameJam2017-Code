import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class SimuClientConnect {
	private Socket geometrySocket;
	private Socket commandSocket;
	
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
            
            Logger.Info("Setting up Command Socket");
            commandSocket = new Socket();			
            commandSocket.connect(new InetSocketAddress(serverAdress, 9091), 15000);
            commandSocket.setKeepAlive(true);
            commandSocket.setPerformancePreferences(0, 1, 2);
            commandSocket.setTcpNoDelay(true);
            
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
			
			DataOutputStream geometry_out = new DataOutputStream(geometrySocket.getOutputStream());
			DataOutputStream command_out = new DataOutputStream(commandSocket.getOutputStream());
			
			PrintWriter geometry_printer = new PrintWriter(geometry_out);
			
			boolean cancelled = false;
			
			while(true)
	        {
				Logger.Print("Type a command: ");
				String str = br.readLine();				
				String[] cargs = str.split("\\s+");				

				switch(cargs[0])
				{
				case "help":
					Logger.Info("There's no help, bitch!");
					break;
					
				case "up":
				case "down":
				case "left":
				case "right":
					geometry_printer.println(str);
					break;
					
				case "shoot":
					command_out.writeInt(20);
					byte[] buf_out = new byte[20];
					buf_out[0] = 1 >> 24;
					buf_out[1] = 1 >> 16;
					buf_out[2] = 1 >> 8;
					buf_out[3] = 1;
					for(int i=4; i<20; ++i)
					{
						buf_out[i] = 0;
					}
					command_out.write(buf_out, 0, buf_out.length);
					
					for(int j=1; j<cargs.length; ++j)
					{
						Logger.Info("additional argument: " + cargs[j]);
					}
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
				
				geometry_printer.flush();
				command_out.flush();
	        }
			
			br.close();
			
			Logger.Info("Closing connection to BugHoleServer...");
			geometrySocket.close();
			commandSocket.close();
			Logger.Info("Connection to BugHoleServer closed!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
