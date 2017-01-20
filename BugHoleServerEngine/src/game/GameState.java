package game;

import java.util.LinkedList;
import java.util.List;

import geometryInfo.IGeometry_Information;
import geometryInfo.ShotsAtEnemies;
import geometryInfo.ShotsAtPlayer;
import geometryInfo.Tunnel;
import util.BufferConvert;
import util.Logger;
import util.Vector3D;

public class GameState implements IGameState {
	private final static int FireCommand = 1;

	private Vector3D _playerDirection;
	private int _playerHitPoints;
	private int _playerScore;

	private ShotsAtPlayer  _shotsAtPlayer;
	private ShotsAtEnemies _shotsAtEnemies;

	private IGeometry_Information _geometryInformation;

	private SpawnScheduler _spawnScheduler;
	
	private List<Integer> _gameEvents;

	public GameState() {
		_playerHitPoints = 100;
		_playerScore = 0;
		_playerDirection = new Vector3D();		
		_gameEvents = new LinkedList<Integer>();
		// initialize the geometric information
	}

	public void Init() {
		long currentTime = System.currentTimeMillis();

		_geometryInformation = new Tunnel(currentTime);
		_shotsAtPlayer = new ShotsAtPlayer(currentTime, this);
		_shotsAtEnemies = new ShotsAtEnemies(currentTime, this);

		_geometryInformation.PropagateGeometryInformation(_shotsAtPlayer);
		_geometryInformation.PropagateGeometryInformation(_shotsAtEnemies);

		_spawnScheduler = new SpawnScheduler(currentTime);
	}

	public synchronized void HandleCommands(byte[] buffer, int bufferLength) {
		int sizeofInt = 4;
		int sizeofFloat = 4;

		if (bufferLength > 0) {

			int index = 0;

			int command = BufferConvert.ReadIntFromBufferAtOffset(buffer, 0);
			index += sizeofInt;
			
			Logger.Info("received command: " + command);

			if (command == FireCommand) {

				float x = BufferConvert.ReadIntFromBufferAtOffsetAndConvertToFloat(buffer, index);
				index += sizeofFloat;
				float y = BufferConvert.ReadIntFromBufferAtOffsetAndConvertToFloat(buffer, index);
				index += sizeofFloat;
				float z = BufferConvert.ReadIntFromBufferAtOffsetAndConvertToFloat(buffer, index);
				index += sizeofFloat;
				
				// The direction is written as vec4.
				BufferConvert.ReadIntFromBufferAtOffsetAndConvertToFloat(buffer, index);
				index += sizeofFloat;

				// I hope I'll never have to explain this...
					//... but you should have! *+&$=?!
				_playerDirection.mXPos = -x;
				_playerDirection.mYPos = z;
				_playerDirection.mZPos = y;

				Logger.Info("shooting at " + _playerDirection.mXPos + " " + _playerDirection.mYPos + " " + _playerDirection.mZPos);
				_shotsAtEnemies.EmitShot(Vector3D.Zero, _playerDirection);
				
			}
		}
	}

	public synchronized void SpawnSwarms(long currentTime) {
		Logger.Info("Trying to spawn...");
		if (_spawnScheduler != null) {
			IGeometry_Information newSwarm = _spawnScheduler.Update(_shotsAtPlayer);

			// Currently the root element are the shots which are never removed
			if (newSwarm != null) {
				_geometryInformation.PropagateGeometryInformation(newSwarm);
			}

			// Remove finished
			_geometryInformation.RemoveFinished(currentTime);
			
			Logger.Info("Spawning");
		}
	}

	public int UpdateAndGetStateAndNumberOfBytesToWrite(byte[] buffer, long currentTime) {
		int currentNumberOfObjects = 0;

		if (_geometryInformation != null) {
			_geometryInformation.SynchronizeState(currentTime);
			currentNumberOfObjects = _geometryInformation.GetNumberOfObjects();
		}

		int sizeOfFloat = 4;
		int sizeOfInt   = 4;
		int numberOfBytesPerObject = 9 * sizeOfFloat + sizeOfInt;

		for (int i = 0; i < currentNumberOfObjects; i++) {
			Vector3D rotation = _geometryInformation.GetObjectRotation(i);
			Vector3D scaling  = _geometryInformation.GetObjectScaling(i);
			Vector3D position = _geometryInformation.GetObjectPosition(i);

			// Position
			BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(position.mXPos, buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 0));
			BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(position.mYPos, buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 1));
			BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(position.mZPos, buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 2));
			
			// Rotation
			BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(rotation.mXPos, buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 3));
			BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(rotation.mYPos, buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 4));
			BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(rotation.mZPos, buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 5));
			
			// Scaling
			BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(scaling.mXPos,  buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 6));
			BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(scaling.mYPos,  buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 7));
			BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(scaling.mZPos,  buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 8));
			
			// ID
			BufferConvert.WriteIntToBufferAtOffset(_geometryInformation.GetObjectModelIdentification(i), buffer,
					(i * numberOfBytesPerObject) + (sizeOfFloat * 9));
		}

		return currentNumberOfObjects * numberOfBytesPerObject;
	}
	
	public synchronized void EmitGameEvent(int eventId) {
		_gameEvents.add(new Integer(eventId));
		
		if (eventId == IGameState.GameEventEnemyHitsPlayer) {
			_playerHitPoints--;
		} else if (eventId == IGameState.GameEventPlayerHitsEnemy) {
			_playerScore++;
		}
	}

	public synchronized int TransferEventsIntoBuffer(byte[] buffer)
	{
		int offset = 0;
		int sizeofInt = 4;

		for (Integer eventId : _gameEvents) {
			BufferConvert.WriteIntToBufferAtOffset(eventId.intValue(), buffer, offset);
			offset += sizeofInt;
			
			if (eventId.intValue() == IGameState.GameEventEnemyHitsPlayer) {
				BufferConvert.WriteIntToBufferAtOffset(_playerHitPoints, buffer, offset);
				offset += sizeofInt;
			} else if (eventId.intValue() == IGameState.GameEventPlayerHitsEnemy) {
				BufferConvert.WriteIntToBufferAtOffset(_playerScore, buffer, offset);
				offset += sizeofInt;
			}
		}
		
		_gameEvents.clear();
		
		return offset;
	}
}
