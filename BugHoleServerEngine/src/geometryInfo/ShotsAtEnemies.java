package geometryInfo;

import game.IGameState;
import util.Logger;
import util.Vector3D;

public class ShotsAtEnemies extends ShotsBase {
	
	private IGameState _gameState;
	
	public ShotsAtEnemies(long timeBase, IGameState gameState)
	{
		super(timeBase);
		_gameState = gameState;
	}
	
	protected void CheckForCollision() {
		for (Vector3D pos : _positions) {
			if (_childGeometry != null) {
				if (_childGeometry.CollidesWith(pos)) {
					// we use this 'trick', now the shot will be removed by 'RemoveShots'
					pos.mXPos = Float.MAX_VALUE;
					_gameState.EmitGameEvent(IGameState.GameEventPlayerHitsEnemy);
				}
			}
		}
	}
}
