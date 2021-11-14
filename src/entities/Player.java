package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Player extends Entity{

    //TODO move to setting file
    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;

    private float currentSpeed = 0;
    private float currentTurn = 0;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(){
        checkInputs();
        super.increaseRotation(0,currentTurn* DisplayManager.getDT(),0);
        float distance = currentSpeed * DisplayManager.getDT();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx,0,dz);
    }

    private void checkInputs(){

        //TODO keybindings file
        //Forward and Back
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            this.currentSpeed = RUN_SPEED;
        }else if (Keyboard.isKeyDown(Keyboard.KEY_S)){
            this.currentSpeed = -RUN_SPEED;
        }else{
            currentSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            this.currentTurn = -TURN_SPEED;
        }else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            this.currentTurn = TURN_SPEED;
        }else{
            currentTurn = 0;
        }
    }
}
