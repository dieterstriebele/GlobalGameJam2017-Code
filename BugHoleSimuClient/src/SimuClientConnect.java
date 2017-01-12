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
					geometry_printer.println(cargs[0]);
					break;
					
				case "shoot":
					if(cargs.length < 4)
					{
						Logger.Error("Not enough arguments for \"shoot\" command.");
						break;
					}
					if(cargs.length > 4)
					{
						Logger.Error("Too many arguments for \"shoot\" command.");
						break;
					}
					
					command_out.writeInt(20);
					byte[] buf_out = new byte[20];
					Arrays.fill(buf_out, (byte)0);
					
					//first four bytes are for the command (integer), fire command is 1 = 00000000 00000000 00000000 00000001, skipping first three bytes
					buf_out[3] = (byte)(1);
					
					//x coordinate
					buf_out[4]  = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[1])) >> 24);
					buf_out[5]  = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[1])) >> 16);
					buf_out[6]  = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[1])) >> 8);
					buf_out[7]  = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[1])));
					
					//y coordinate
					buf_out[8]  = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[2])) >> 24);
					buf_out[9]  = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[2])) >> 16);
					buf_out[10] = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[2])) >> 8);
					buf_out[11] = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[2])));
					
					//z coordinate
					buf_out[12] = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[3])) >> 24);
					buf_out[13] = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[3])) >> 16);
					buf_out[14] = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[3])) >> 8);
					buf_out[15] = (byte)(Float.floatToRawIntBits(Float.parseFloat(cargs[3])));
					
					command_out.write(buf_out, 0, buf_out.length);
					
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
