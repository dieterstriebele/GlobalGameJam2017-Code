package game;

public interface IGameState {
	
	public static final int GameEventNone = 0;
	public static final int GameEventPlayerHitsEnemy = 1;
	public static final int GameEventEnemyHitsPlayer = 2;
		
	public void SpawnSwarms(long currentTime);
	public int UpdateAndGetStateAndNumberOfBytesToWrite(byte[] buffer, long currentTime);
	
	public void HandleCommands(byte[] buffer, int bufferLength);
	
	public void EmitGameEvent(int eventId);
	public int TransferEventsIntoBuffer(byte[] buffer);
}
