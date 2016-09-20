package enemies;

import game.Settings;

public class Spiral extends EnemySwarm {

	public Spiral(long timeBase) {
		super(timeBase);

        Init((int)(Math.random() * 4 + 4.0), Settings.SpiralId, Settings.SpiralScaling);
	}
	
	public boolean IsFinished(long currentTime) {
		return _persistentPositions[0].mZPos < -Settings.SpiralZStart;
	};

	@Override
	public void SynchronizeState(long currentTime) {
        long delta = currentTime - mTimeBase;

        double radiusOffset = Math.sin(delta * Settings.SpiralRadiusChangeSpeed) * Settings.SpiralRadiusChangeDist;
        
        for (int i = 0; i < _swarmMemberCount; i++) {
        	_persistentPositions[i].set((float)(Math.cos(delta * Settings.SpiralAngleSpeed - i) * (Settings.SpiralRadius + radiusOffset)),
        			(float)(Math.sin(delta * Settings.SpiralAngleSpeed - i) * (Settings.SpiralRadius + radiusOffset)),
        			Settings.SpiralZStart - delta * Settings.SpiralZSpeed + i * Settings.SpiralZDist);
        }
        
        super.SynchronizeState(currentTime);
	}
}
