package de.swagner.ld22;

import com.badlogic.gdx.math.Vector3;

public class Player extends Renderable {	
	
	public enum STATE {
		IDLE,FORWARD,BACKWARD,LEFT,RIGHT;
	}
	
	public float headMovement = 0.f;
	public boolean headMovementDownUp = true; 
	
	public Vector3 direction = new Vector3(0,0,-1);
	public Vector3 position = new Vector3(0,0,0);	
	public STATE state = STATE.IDLE;
	
}
