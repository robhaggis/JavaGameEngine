package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	private Player player;


	private final Vector3f position = new Vector3f(0,0,0);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;
	
	public Camera(Player p){
		this.player = p;
	}
	
	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();
		float hD = calculateHorizontalDistance();
		float vD = calculateVerticalDistance();
		this.yaw = 180-(player.getRotY() + angleAroundPlayer);
		calculateCamPosition(vD, hD);

	}

	private void calculateCamPosition(float v, float h){
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (h * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (h * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y +v;


	}

	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(+Math.toRadians(pitch)));
	}
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(+Math.toRadians(pitch)));
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
	
	private void calculateZoom(){
		float zoomLevel = Mouse.getDWheel() * 0.1f;//TODO sensitivity settings
		distanceFromPlayer -= zoomLevel;//TODO invert mouse scroll option in settings
	}

	private void calculatePitch(){
		if(Mouse.isButtonDown(1)){		//NOTE right button pressed. Move to settings
			float pitchChange = Mouse.getDY() * 0.1f; //TODO sensitivity settings
			pitch -= pitchChange;
		}
	}

	private void calculateAngleAroundPlayer(){
		if(Mouse.isButtonDown(0)) {//NOTE left button pressed. MOve to settings
			float angleChange = Mouse.getDX() * 0.3f;//TODO sensitivity settings
			angleAroundPlayer -= angleChange;
		}
	}

}
