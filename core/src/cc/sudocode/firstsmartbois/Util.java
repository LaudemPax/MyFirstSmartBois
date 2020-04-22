package cc.sudocode.firstsmartbois;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

//helper class with a bunch of static objects
public class Util {

    public static Texture carTexture;
    public static Texture altCarTexture;
    public static final int lapMarkerID = 9999;

    //load texture here so it only loads once instead of every time a new RaceCar is generated
    public Util(){
        carTexture = new Texture(Gdx.files.internal("race_car.png"));
        altCarTexture = new Texture(Gdx.files.internal("race_car_y.png"));
    }

    public static float degToRad(float deg){
        return deg * MathUtils.degreesToRadians;
    }
}
