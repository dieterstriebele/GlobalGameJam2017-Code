import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import game.IGameState;
import util.Logger;

public class CommandClient {
	
	private final static int MaxBuffer = 1024;
	
	private DataInputStream _in;
	private DataOutputStream _out;
	private String clientIp;
	
	private byte[] _readingBuffer;
	private byte[] _writingBuffer;
	
	private IGameState _gameState;

	public CommandClient(Socket clientSocket, long timeBase, IGameState gameState) {
		_gameState = gameState;
		
		try {

			Logger.Info("Initialize CommandClient");
			_in = new DataInputStream(clientSocket.getInputStream());
			_out = new DataOutputStream(clientSocket.getOutputStream());
			
			_readingBuffer = new byte[MaxBuffer];
			_writingBuffer = new byte[MaxBuffer];
		} catch (Exception e) {
			Logger.Error("Initialise command client streams failed!", e);
		}
		clientIp = clientSocket.getRemoteSocketAddress().toString();
	}
	
	public void StartReadingThread() {
		Thread read = new Thread() {
			public void run() {
				Logger.Info("CommandClient: StartReadingThread id=" + Thread.currentThread().getId());
				try {
					while (true) {
						int numBytes = _in.readInt();
						
						_in.readFully(_readingBuffer, 0, numBytes);
						
						_gameState.HandleCommands(_readingBuffer, numBytes);
					}
				} catch (Exception e) {
					Logger.Error("CommandClient: Error while reading from inputstream", e);
					try {
					_in.close();
					} catch (Exception ex) {
						Logger.Error("CommandClient: Error closing input stream", e);
					}
				}
			}
		};
		
		read.setPriority(Thread.MAX_PRIORITY);
		//read.setDaemon(true);
		read.start();
	}
	
	public void StartWritingThread() {
		Thread write = new Thread() {
			public void run() {
				Logger.Info("CommandClient: StartWritingThread id=" + Thread.currentThread().getId());
				
				try
				{
					while(true) {
						int bufferSizeInBytes = _gameState.TransferEventsIntoBuffer(_writingBuffer);
						
						if (bufferSizeInBytes > 0) {
							_out.writeInt(bufferSizeInBytes);
							_out.write(_writingBuffer, 0, bufferSizeInBytes);
						}
					}
				} catch (Exception e) {
					Logger.Error("CommandClient: Error while writing to output stream", e);
					try {
						_out.close();
					} catch (Exception ex) {
						Logger.Error("CommandClient: Error closing output stream", e);
					}
				}
			}
		};
		
		write.setPriority(Thread.MAX_PRIORITY);
		write.start();
	}
}
