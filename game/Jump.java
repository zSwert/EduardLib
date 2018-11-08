package net.eduard.api.lib.game;

import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.eduard.api.lib.storage.Storable;

public class Jump implements Storable {
	private boolean withHigh = true;
	private boolean highFirst;
	private double force = 2;
	private double high = 0.5;
	private Sounds sound;
	private boolean useVector;
	private Vector vector;
	public Jump() {
	}
	
	public Jump(Sounds sound, Vector vector) {
		this.sound = sound;
		this.useVector = true;
		this.vector = vector;
	}
	public Jump(boolean highFirst, double force, double high,
			Sounds sound) {
		this.highFirst = highFirst;
		this.force = force;
		this.high = high;
		this.sound = sound;
		
	}
	public Jump(boolean withHigh, boolean highFirst, double force, double high,
			Sounds sound, boolean useVector, Vector vector) {
		this.withHigh = withHigh;
		this.highFirst = highFirst;
		this.force = force;
		this.high = high;
		this.sound = sound;
		this.useVector = useVector;
		this.vector = vector;
	}
	public void create(Entity entity) {

		Vector newVector = null;
		if (useVector && vector != null) {
			newVector = vector;
		}else{
			newVector = entity.getLocation().getDirection();
			if (highFirst) {
				if (withHigh) {
					newVector.setY(high);
				}
				newVector.multiply(force);
			} else {
				newVector.multiply(force);
				if (withHigh) {
					newVector.setY(high);
				}
			}

		}
		entity.setVelocity(newVector);
		if (sound != null) {
			sound.create(entity);
		}
	}
	public boolean isWithHigh() {
		return withHigh;
	}
	public void setWithHigh(boolean withHigh) {
		this.withHigh = withHigh;
	}
	public boolean isHighFirst() {
		return highFirst;
	}
	public void setHighFirst(boolean highFirst) {
		this.highFirst = highFirst;
	}
	public double getForce() {
		return force;
	}
	public void setForce(double force) {
		this.force = force;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public Sounds getSound() {
		return sound;
	}
	public void setSound(Sounds sound) {
		this.sound = sound;
	}
	public boolean isUseVector() {
		return useVector;
	}
	public void setUseVector(boolean useVector) {
		this.useVector = useVector;
	}
	public Vector getVector() {
		return vector;
	}
	public void setVector(Vector vector) {
		this.vector = vector;
	}


	@Override
	public Object restore(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void store(Map<String, Object> map, Object object) {
		// TODO Auto-generated method stub
		
	}

}
