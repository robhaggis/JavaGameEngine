package textures;

public class ModelTexture {

    private int textureID;

    private float shineDamper = 1f;

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    private float reflectivity = 0f;

    public ModelTexture(int id){
        this.textureID = id;
    }
    public int getTextureID() {
        return textureID;
    }

}
