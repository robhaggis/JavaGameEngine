package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private final Vector3f position = new Vector3f(0,25,0);
	private final float pitch = 20;
	private final float yaw = 0;
	private float roll;
	
	public Camera(){}
	
	public void move(){
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	

}
