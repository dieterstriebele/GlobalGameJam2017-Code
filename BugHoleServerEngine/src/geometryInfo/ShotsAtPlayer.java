package geometryInfo;

import game.IGameState;
import util.Vector3D;

public class ShotsAtPlayer extends ShotsBase {
	private IGameState _gameState;

	public ShotsAtPlayer(long timeBase, IGameState gameState)
	{
		super(timeBase);
		_gameState = gameState;
	}
	
	protected void CheckForCollision() {
		for (Vector3D pos : _positions) {
			// What? Dirty, but good enough for now, I guess... Hoppe at his best
			if (pos.squareDistance(Vector3D.Zero) < 1.f)
			{
				_gameState.EmitGameEvent(IGameState.GameEventEnemyHitsPlayer);
				
				// remove shot via genius 'trick'
					// WTF?
				pos.mZPos = Float.MIN_VALUE;
			}
		}
	}
}
