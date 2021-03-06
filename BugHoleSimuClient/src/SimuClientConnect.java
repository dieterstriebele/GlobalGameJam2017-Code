import java.io.BufferedReader;
import java.io.DataInputStream;
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
	
	private boolean running = true;
	
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
	
	public void StartCommandThread()
	{
		Thread command_thread = new Thread() {
			public void run() {
				try
				{
					InputStreamReader in_stream_reader = new InputStreamReader(System.in);
					BufferedReader buf_reader = new BufferedReader(in_stream_reader);
					DataOutputStream command_out = new DataOutputStream(commandSocket.getOutputStream());									
					
					while(running)
			        {
						Logger.Print("Type a command: ");
						String str = buf_reader.readLine();				
						String[] cargs = str.split("\\s+");				
	
						switch(cargs[0])
						{							
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
						case "exit":
						case "end":
							running = false;
							break;
							
						default:
							Logger.Error("Unknown command");
							break;
						}

						command_out.flush();
			        }
					
					buf_reader.close();
					commandSocket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		//command_thread.setPriority(Thread.MAX_PRIORITY);
		command_thread.start();		
	}
	
	public void StartGeometryThread()
	{
		Thread geometry_thread = new Thread() {
			public void run() {
				try
				{			
					DataInputStream geometry_in = new DataInputStream(geometrySocket.getInputStream()); 
					DataOutputStream geometry_out = new DataOutputStream(geometrySocket.getOutputStream());					
					PrintWriter geometry_printer = new PrintWriter(geometry_out);
								
					while(running)
			        {
						//request synchronization
						geometry_printer.println("SynchronizeState");
						geometry_printer.flush();
						
						//consume date from buffer
						int count = geometry_in.readInt();
						byte[] buf = new byte[3600];
						geometry_in.readFully(buf, 0, count);
						
						//wait a little (16 milliseconds ~time to render a frame)
						Thread.sleep(16);
			        }
					
					geometrySocket.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		geometry_thread.setPriority(Thread.MAX_PRIORITY);
		geometry_thread.start();	
	}
}
