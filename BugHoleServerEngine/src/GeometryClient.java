import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import game.IGameState;
import util.Logger;
import geometryInfo.IGeometry_Information;

public class GeometryClient {
	
	private DataInputStream in;
	private DataOutputStream out;
	private String clientIp;
	private BufferedReader bufferedReader;
	
	private byte[] _buffer;
	
	private IGameState _gameState;
	
	public GeometryClient(Socket clientSocket, long timeBase, IGameState gameState) {
		try {
			_gameState = gameState;
			
			//m_ClientSocket = clientSocket;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			bufferedReader = new BufferedReader(new InputStreamReader(in));	
			
//			geometryInformation.Decorate(new Geometry_Information_Spiral(System.currentTimeMillis()));
			
			int sizeOfFloat = 4;
	    	int sizeOfInt = Integer.SIZE / Byte.SIZE;
	    	int numberOfBytesPerObject = 9 * sizeOfFloat + sizeOfInt;  // 6 = 3xfloat (position) + 3xfloat (rotation). 
			
			_buffer = new byte[sizeOfInt + IGeometry_Information.MaxObjects * numberOfBytesPerObject];

		} catch (Exception e) {
			LogClientError("Initialise client streams failed!", e);
		}
		clientIp = clientSocket.getRemoteSocketAddress().toString();
	}

	public void write(String message) {
		PrintWriter printWriter = new PrintWriter(out);
		//LogClientInfo("write to client: " + message);
		printWriter.println(message);
		printWriter.flush();
	}

	public void StartReadingThread() {
		Thread read = new Thread() {
			public void run() {
				LogClientInfo("GeometryClient: StartReadingThread id=" + Thread.currentThread().getId());
				try {
					while (true) {
						String receivedLine = bufferedReader.readLine();
						
						long currentTime = System.currentTimeMillis();

						_gameState.SpawnSwarms(currentTime);
						
						if (receivedLine != null && receivedLine.length() > 0) {
							LogClientInfo("received: " + receivedLine);
							handleIncommingCommands(receivedLine, currentTime);
						} else {
							LogClientError("Client aborted connection! Shutting down Client Processing Thread!");
							break;
						}
						
						//http://stackoverflow.com/questions/3484972/java-socketchannel-doesnt-detect-disconnection
						
						/*
						
						Usually if you turn off OS level networking, writes to socket should throw exceptions, so you know the connection is broken.
						However more generally, we can't be sure if a packet is delivered. In java (probably C too), there is no way to check if a
						packet is ACK'ed. Even if we can check TCP ACKs, it doesn't guarantee that the server received or processed the packet. It
						only means that the target machine received the packet and buffered it in memory. Many things can go wrong after that.
						So if you really want to sure, you can't rely on transport protocol. You must have application level ACK, that is, the 
						server application writes back an ACK message after it received and processed a message from client. From client point of
						view, it writes a message to server, then tries to read ACK from server. If it gets it, it can be certain that its message
						is received and processed. If it fails to get ACK, well, it has no idea what has happened. Empirically, most likely TCP 
						failed. Next possiblity is that server crashed. It's also possible that everything went OK, except the ACK couldn't reach
						the client.

					    A socket channel can be connected by invoking its connect method; once connected, a socket channel remains connected until
					    it is closed. The channel is not closed when the server is not available anymore, due to a broken physical connection or a
					    server failure. So once a connection has been established, isConnected() will be returning true until you close the channel
					    on your side. If you want to check, if the server is still available, send a byte to the sockets outputstream. If you get 
					    an Exception, then the server is unavailable (connection lost).
						*/
						/*
						LogClientInfo(
								"Reading next line .. "+
								" m_ClientSocket.isConnected()="+m_ClientSocket.isConnected()+
								" m_ClientSocket.isOutputShutdown()="+m_ClientSocket.isOutputShutdown()+
								" m_ClientSocket.isInputShutdown()="+m_ClientSocket.isInputShutdown()+
								" m_ClientSocket.isClosed()="+m_ClientSocket.isClosed()
								);
						if (m_ClientSocket.isInputShutdown() || m_ClientSocket.isOutputShutdown()|| m_ClientSocket.isClosed() || !m_ClientSocket.isConnected()) {
							LogClientError("Client aborted connection! Shutting down Client Processing Thread!");
							break;
						}
						*/
					}
				} catch (Exception e) {
					LogClientError("GeometryClient: Error while reading from inputstream", e);
					try {
					in.close();
					out.close();
					} catch (Exception ex) {
						LogClientError("GeometryClient: Error closing streams", e);
					}
				}
			}
		};
		read.setPriority(Thread.MAX_PRIORITY);
		//read.setDaemon(true);
		read.start();
	}

	private void handleIncommingCommands(String message, long currentTime) throws Exception {
		// handle connect command
		if (message.equals(new String("SynchronizeState"))) {
			
			int numberOfBytesToWrite = _gameState.UpdateAndGetStateAndNumberOfBytesToWrite(_buffer, currentTime);
						
			out.writeInt(numberOfBytesToWrite);
			out.flush();
			
			if (numberOfBytesToWrite != 0) {
	
				//LogClientInfo("Sending "+buf.length+" bytes to client!");
				//System.out.println(Arrays.toString(buf));
				
				out.write(_buffer, 0, numberOfBytesToWrite);
				out.flush();
			}
		}	
	}

	public String getIp() {
		return clientIp;
	}

	private void LogClientInfo(String message) {
		Logger.Info(clientIp + "|" + message);
	}

	private void LogClientError(String message) {
		Logger.Error(clientIp + "|" + message);
	}
	
	private void LogClientError(String message, Exception exception) {
		Logger.Error(clientIp + "|" + message, exception);
	}

}
