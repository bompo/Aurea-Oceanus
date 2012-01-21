package de.swagner.ld22;

import javax.annotation.PostConstruct;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Shark extends Renderable {
	
	public float randomStart = 0;
	
	private float delta = 0.0f;
	
	private float speed = 2;
	private float rotateSpeed = 20;

	public Vector2 velocity = new Vector2();
	public Vector2 facing = new Vector2();
	
	public Vector2 targetPos = new Vector2();
	public Vector3 playerPos = new Vector3();
		
	public Shark(Vector3 position) {
		this.position = position.cpy();
		this.facing = new Vector2(MathUtils.random(0, 360),MathUtils.random(0, 360)).nor();
		this.collisionPosition.set(position);
		this.collisionPosition.y = 0;
		this.scale = 5;
		this.angleX = 1;
		this.angleY = 1;
		this.randomStart = MathUtils.random(1.f, 5555.f);
		
		newTarget();
	}
	
	public void newTarget() {
		
		targetPos.x = MathUtils.random(-90, 90);
		targetPos.y = MathUtils.random(-90, 90);
		rotateSpeed = 20;
		speed = 2;
//		System.out.println("new target; " + targetPos + " shark pos: " + position);
	}

	public void sharkAI() {		
		velocity.mul((float) Math.pow(0.97f, delta * 30.f));
		position.add(velocity.y * delta,0, velocity.x * delta);
		angleY= (facing.angle()+180.f);
		thrust();
		goTowardsPoint();
		
		//attack player?
		if(position.dst(playerPos)<30 && position.dst(playerPos)>10) {
			targetPos.x = playerPos.z;
			targetPos.y = playerPos.x;
//			rotateSpeed = 30;
//			speed = 3;
		}
	}
	
	public void update(Vector3 playerPos, float delta) {
		this.delta = delta;
		this.playerPos = playerPos;
		
		sharkAI();
		calculateMatrix();
	}
	
	public void turn(float direction) {		
		facing.rotate(direction * rotateSpeed * delta).nor();
	}

	public void thrust() {		
		velocity.add(facing.x * speed * delta, facing.y * 2 * delta);
	}
	
	public void goTowardsPoint() {
		Vector2 target_direction = targetPos.cpy().sub(new Vector2(position.z, position.x));
		
		if (facing.crs(target_direction) > 0) {
			turn(1);
		} else {
			turn(-1);
		}

		if (facing.dot(target_direction) > 0) {
			thrust();
		} else {
			newTarget();
		}
	}
}
