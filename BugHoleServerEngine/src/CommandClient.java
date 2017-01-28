import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import game.IGameState;
import util.Logger;

public class CommandClient {
	
	private final static int MaxBuffer = 1024;
	
	private DataInputStream stream_in;
	private DataOutputStream stream_out;
	
	private byte[] reading_buffer;
	private byte[] writing_buffer;
	
	private IGameState _gameState;

	public CommandClient(Socket clientSocket, long timeBase, IGameState gameState) {
		_gameState = gameState;
		
		try {
			Logger.Info("Initialize CommandClient");
			stream_in =  new DataInputStream(clientSocket.getInputStream());
			stream_out = new DataOutputStream(clientSocket.getOutputStream());			
			reading_buffer = new byte[MaxBuffer];
			writing_buffer = new byte[MaxBuffer];
			
		} catch (Exception e) {
			Logger.Error("Initialise command client streams failed!", e);
		}
	}
	
	public void StartReadingThread() {
		Thread reading_thread = new Thread() {
			public void run() {
				Logger.Info("CommandClient: StartReadingThread id=" + Thread.currentThread().getId());
				try {
					while (true) {
						int numBytes = stream_in.readInt();						
						stream_in.readFully(reading_buffer, 0, numBytes);						
						_gameState.HandleCommands(reading_buffer, numBytes);
					}
				} catch (Exception e) {
					Logger.Error("CommandClient: Error while reading from inputstream", e);
					try {
						stream_in.close();
					} catch (Exception ex) {
						Logger.Error("CommandClient: Error closing input stream", e);
					}
				}
			}
		};
		
		reading_thread.setPriority(Thread.MAX_PRIORITY);
		reading_thread.start();
	}
	
	public void StartWritingThread() {
		Thread writing_thread = new Thread() {
			public void run() {
				Logger.Info("CommandClient: StartWritingThread id=" + Thread.currentThread().getId());
				
				try
				{
					while(true) {
						int bufferSizeInBytes = _gameState.TransferEventsIntoBuffer(writing_buffer);						
						if (bufferSizeInBytes > 0) {
							stream_out.writeInt(bufferSizeInBytes);
							stream_out.write(writing_buffer, 0, bufferSizeInBytes);
						}
					}
				} catch (Exception e) {
						Logger.Error("CommandClient: Error while writing to output stream", e);
					try {
						stream_out.close();
					} catch (Exception ex) {
						Logger.Error("CommandClient: Error closing output stream", e);
					}
				}
			}
		};
		
		writing_thread.setPriority(Thread.MAX_PRIORITY);
		writing_thread.start();
	}
}
