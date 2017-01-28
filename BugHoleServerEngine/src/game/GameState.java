package game;

import java.util.LinkedList;
import java.util.List;

import geometryInfo.Enemy;
import geometryInfo.Enemy.EnemyType;
import geometryInfo.GeometryInformationBase;
import geometryInfo.IGeometryInformation;
import geometryInfo.Tunnel;
import util.BufferConvert;
import util.Logger;
import util.Vector3D;

public class GameState implements IGameState {
	//needs to be an integer, because we retrieve the command as an integer from the buffer
	private final static int FireCommand = 1;

	private Vector3D _playerDirection;
	private int _playerLife;
	private int _playerScore;

	private List<IGeometryInformation> _geometries;
	
	private List<Integer> _gameEvents;

	public GameState() {
		_playerLife = 100;
		_playerScore = 0;
		_playerDirection = new Vector3D();		
		_gameEvents = new LinkedList<Integer>();
		_geometries = new LinkedList<IGeometryInformation>();
	}

	public void Init() {
		long currentTime = System.currentTimeMillis();

		_geometries.add(new Tunnel(currentTime));
		_geometries.add(new Enemy(currentTime, EnemyType.Alpha));
		_geometries.add(new Enemy(currentTime, EnemyType.Beta));
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
				
				//TODO: emit shot at enemy				
			}
		}
	}
	
	//Synchronizes the state, updates the buffer and returns the number of bytes to write
	public int SynchronizeAndUpdateBuffer(byte[] buffer, long currentTime) {
		int sizeOfFloat = 4;
		int sizeOfInt   = 4;
		int numberOfBytesPerObject = 9 * sizeOfFloat + sizeOfInt;
		int numberOfBytesToWrite = 0;
		
		int totalNumberOfObjects = 0;
		
		int num_objects_written = 0;
		
		for(IGeometryInformation geo : _geometries)
		{
			((GeometryInformationBase)geo).SynchronizeState(currentTime);
			totalNumberOfObjects += geo.GetNumberOfObjects();
			
			for (int i = 0; i < geo.GetNumberOfObjects(); i++) {
				Vector3D rotation = geo.GetObjectRotation(i);
				Vector3D scaling  = geo.GetObjectScaling(i);
				Vector3D position = geo.GetObjectPosition(i);

				// Position
				BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(position.mXPos, buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 0));
				BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(position.mYPos, buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 1));
				BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(position.mZPos, buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 2));
				
				// Rotation
				BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(rotation.mXPos, buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 3));
				BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(rotation.mYPos, buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 4));
				BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(rotation.mZPos, buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 5));
				
				// Scaling
				BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(scaling.mXPos,  buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 6));
				BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(scaling.mYPos,  buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 7));
				BufferConvert.ConvertFloatToIntAndWriteToBufferAtOffset(scaling.mZPos,  buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 8));
				
				// ID
				BufferConvert.WriteIntToBufferAtOffset(geo.GetObjectModelIdentification(i), buffer,
						((num_objects_written + i) * numberOfBytesPerObject) + (sizeOfFloat * 9));
			}
			
			num_objects_written += geo.GetNumberOfObjects();
		}
		
		numberOfBytesToWrite = totalNumberOfObjects * numberOfBytesPerObject;
		return numberOfBytesToWrite;
	}
	
	public synchronized void EmitGameEvent(int eventId) {
		_gameEvents.add(new Integer(eventId));
		
		if (eventId == IGameState.GameEventEnemyHitsPlayer) {
			_playerLife--;
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
				BufferConvert.WriteIntToBufferAtOffset(_playerLife, buffer, offset);
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
