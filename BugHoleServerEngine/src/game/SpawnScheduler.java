package game;

import java.util.LinkedList;

import enemies.DeathCircle;
import enemies.EnemySwarm;
import enemies.Spiral;
import enemies.Howler;
import enemies.Wesp;
import enemies.Flocking;

import geometryInfo.IGeometryInformation;
import geometryInfo.IShotEmitter;

public class SpawnScheduler {
	
	private class Swarm
	{
		public Swarm(long deltaTime, EnemyType swarmId) {
			_deltaTime = deltaTime;
			_swarmId = swarmId;
		}
		
		public long _deltaTime;
		public EnemyType _swarmId;
	}
	
	enum EnemyType
	{
		Spiral,
		DeathCircle,
		Howler,
		Wesp,
		Flocking
	}
	
	private long _lastSpawnTime;
	
	private LinkedList<Swarm> _swarmSchedule;
	
	public SpawnScheduler(long timeBase) {
		_lastSpawnTime = timeBase;
		_swarmSchedule = new LinkedList<Swarm>();		
		
		_swarmSchedule.add(new Swarm(5000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(6000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(2000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.DeathCircle));
		_swarmSchedule.add(new Swarm(10000,EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(100,  EnemyType.Howler));
		_swarmSchedule.add(new Swarm(100,  EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(100,  EnemyType.Howler));
		_swarmSchedule.add(new Swarm(100,  EnemyType.Howler));
		_swarmSchedule.add(new Swarm(100,  EnemyType.Howler));
		_swarmSchedule.add(new Swarm(4000, EnemyType.DeathCircle));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(5000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(5000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(5000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(6000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(4000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(6000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(4000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(4000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(4000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(4000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(4000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(4000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(4000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(100,  EnemyType.Howler));
		_swarmSchedule.add(new Swarm(100,  EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(2000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(6000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Wesp));
		_swarmSchedule.add(new Swarm(5000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(5000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(2000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(5000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(2000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(1000, EnemyType.Spiral));
		_swarmSchedule.add(new Swarm(4000, EnemyType.Howler));
		_swarmSchedule.add(new Swarm(5000, EnemyType.DeathCircle));
	}
	
	public IGeometryInformation Update(IShotEmitter shotEmitter) {
		IGeometryInformation swarmInstance = null;
		
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
	
	private IGeometryInformation CreateSwarm(EnemyType id, long timeBase, IShotEmitter shotEmitter) {
		EnemySwarm swarm = null;
		
		switch(id)
		{
		case Spiral:
			swarm = new Spiral(timeBase);
			break;
		case DeathCircle:
			swarm = new DeathCircle(timeBase);
			break;
		case Howler:
			swarm = new Howler(timeBase);
			break;
		case Wesp:
			swarm = new Wesp(timeBase);
			break;
		case Flocking:
			swarm = new Flocking(timeBase);
			break;
		default:
			swarm = new Spiral(timeBase);
			break;
		}
		
		swarm.SetShotEmitter(shotEmitter);
		
		return swarm;
	}
}
