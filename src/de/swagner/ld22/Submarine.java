package de.swagner.ld22;

import com.badlogic.gdx.math.Vector3;

public class Submarine extends Renderable {
		
	public Submarine(Vector3 position, float scale, float angleX, float angleY) {
		this.position = position.cpy();
		this.collisionPosition.set(position);
		this.collisionPosition.y = 0;
		this.collisionPosition.z -= scale;
		this.scale = scale;
		this.angleX = angleX;
		this.angleY = angleY;
	}
}
