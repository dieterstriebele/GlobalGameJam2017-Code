package game;

import java.util.LinkedList;

import enemies.DeathCircle;
import enemies.EnemySwarm;
import enemies.Spiral;
import enemies.Howler;
import enemies.Wesp;
import enemies.Flocking;

import geometryInfo.IGeometry_Information;
import geometryInfo.IShotEmitter;

public class SpawnScheduler {
	
	private class Swarm
	{
		public Swarm(long deltaTime, int swarmId) {
			_deltaTime = deltaTime;
			_swarmId = swarmId;
		}
		
		public long _deltaTime;
		public int _swarmId;
	}
	
	private static int Spiral = 0;
	private static int DeathCircle = 1;
	private static int Howler = 2;
	private static int Wesp = 3;
	private static int Flocking = 4;
	
	private long _lastSpawnTime;
	
	private LinkedList<Swarm> _swarmSchedule;
	
	public SpawnScheduler(long timeBase) {
		_lastSpawnTime = timeBase;
		_swarmSchedule = new LinkedList<Swarm>();		
		
		_swarmSchedule.add(new Swarm(5000, Spiral));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(6000, Spiral));
		_swarmSchedule.add(new Swarm(2000, Spiral));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, DeathCircle));
		_swarmSchedule.add(new Swarm(10000,Spiral));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(100,  Howler));
		_swarmSchedule.add(new Swarm(100,  Howler));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(100,  Howler));
		_swarmSchedule.add(new Swarm(100,  Howler));
		_swarmSchedule.add(new Swarm(100,  Howler));
		_swarmSchedule.add(new Swarm(4000, DeathCircle));
		_swarmSchedule.add(new Swarm(1000, Spiral));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(5000, Spiral));
		_swarmSchedule.add(new Swarm(5000, Spiral));
		_swarmSchedule.add(new Swarm(5000, Spiral));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(6000, Wesp));
		_swarmSchedule.add(new Swarm(4000, Spiral));
		_swarmSchedule.add(new Swarm(6000, Wesp));
		_swarmSchedule.add(new Swarm(4000, Spiral));
		_swarmSchedule.add(new Swarm(4000, Spiral));
		_swarmSchedule.add(new Swarm(4000, Spiral));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(4000, Spiral));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(4000, Spiral));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(4000, Spiral));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(4000, Spiral));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(100,  Howler));
		_swarmSchedule.add(new Swarm(100,  Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(1000, Howler));
		_swarmSchedule.add(new Swarm(2000, Spiral));
		_swarmSchedule.add(new Swarm(6000, Wesp));
		_swarmSchedule.add(new Swarm(1000, Wesp));
		_swarmSchedule.add(new Swarm(5000, Howler));
		_swarmSchedule.add(new Swarm(5000, Howler));
		_swarmSchedule.add(new Swarm(2000, Howler));
		_swarmSchedule.add(new Swarm(1000, Spiral));
		_swarmSchedule.add(new Swarm(5000, Howler));
		_swarmSchedule.add(new Swarm(2000, Howler));
		_swarmSchedule.add(new Swarm(1000, Spiral));
		_swarmSchedule.add(new Swarm(4000, Howler));
		_swarmSchedule.add(new Swarm(5000, DeathCircle));
	}
	
	public IGeometry_Information Update(IShotEmitter shotEmitter) {
		IGeometry_Information swarmInstance = null;
		
		if (_swarmSchedule.size() > 0) {
			Swarm swarm = _swarmSchedule.getFirst();
			
			long now = System.currentTimeMillis();
			if (now - _lastSpawnTime > swarm._deltaTime)
			{
				_lastSpawnTime = now;
				
				swarmInstance = CreateSwarm(swarm._swarmId, now, shotEmitter);
				_swarmSchedule.removeFirst();
			}
		}
		else {
			// you win...
		}
		
		return swarmInstance;
	}
	
	private IGeometry_Information CreateSwarm(int id, long timeBase, IShotEmitter shotEmitter) {
		EnemySwarm swarm = null;
		
		if (id == Spiral) {
			swarm = new Spiral(timeBase);
		} else if (id == DeathCircle) {
			swarm = new DeathCircle(timeBase);
		} else if (id == Howler) {
			swarm = new Howler(timeBase);
		} else if (id == Wesp) {
			swarm = new Wesp(timeBase);
		} else if (id == Flocking) {
			swarm = new Flocking(timeBase);
		}	
		
		if(swarm != null) {
			swarm.SetShotEmitter(shotEmitter);
		}
		
		return swarm;
	}
}
