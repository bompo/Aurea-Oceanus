package de.swagner.ld22;

import com.badlogic.gdx.math.Vector3;

public class Item extends Renderable {
		
	public Item(Vector3 position, float scale, float angleX, float angleY) {
		this.position = position.cpy();
		this.collisionPosition.set(position);
		this.collisionPosition.y = 0;
		this.scale = scale;
		this.angleX = angleX;
		this.angleY = angleY;
	}
}
