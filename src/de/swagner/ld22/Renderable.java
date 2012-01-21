package de.swagner.ld22;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Renderable implements Comparable<Renderable> {
	
	public Vector3 position = new Vector3();
	public Vector3 collisionPosition = new Vector3();
	public float sortPosition;
	
	public float scale;
	public float angleX;
	public float angleY;
	
	private Matrix4 tmp = new Matrix4();
	public Matrix4 model = new Matrix4();
	public Matrix4 normal = new Matrix4();
	
	@Override
	public int compareTo(Renderable o) {
		if(!(o instanceof Renderable)) return -1;
		if((o).sortPosition<this.sortPosition) return -1;
		return 1;
	}
	
	public void calculateMatrix() {
		tmp.idt();
		model.idt();
		
		tmp.setToTranslation(position.x, position.y, position.z);
		model.mul(tmp);
		
		tmp.setToScaling(scale, scale, scale);
		model.mul(tmp);	
		
		tmp.setToRotation(Vector3.X, angleX);
		model.mul(tmp);
		
		tmp.setToRotation(Vector3.Y, angleY);
		model.mul(tmp);	

		model.getTranslation(collisionPosition);
		collisionPosition.y = 0;
		
		normal.set(model.cpy().toNormalMatrix());
		
	}

}
