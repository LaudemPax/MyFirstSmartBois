package cc.sudocode.firstsmartbois;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class RaceCar{

    private Sprite sprite;
    private Sprite altSprite;
    private Vector2 velocity;
    private Vector2 pos;
    private float speed = 800;
    private float angleChange  = 0f;
    private boolean stop = false;
    private Body physicsBody; //used only for collision detection
    private boolean hide = false;

    private int checkpointsReached = 0;
    private float sumOfDistances =  0;

    public RaceCar(World world,float x, float y, boolean bestLastGen){

        if(!bestLastGen)
            sprite = new Sprite(Util.carTexture);
        else
            sprite = new Sprite(Util.altCarTexture);

        sprite.setRotation(90);
        velocity = new Vector2(0,1);
        pos = new Vector2(x,y);
        initPhysicsBody(world);
    }

    private void initPhysicsBody(World world){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(getPos());
        bdef.angle = getSpriteAngleInRad();

        physicsBody = world.createBody(bdef);

        //true when car collides with boundary
        physicsBody.setUserData(new DriverUserData());

        PolygonShape collisionBox = new PolygonShape();
        collisionBox.setAsBox(15,40);

        FixtureDef fdef = new FixtureDef();
        fdef.filter.categoryBits = MainGdxGame.CAR;
        fdef.filter.maskBits = MainGdxGame.BOUNDARY;
        fdef.shape = collisionBox;
        physicsBody.createFixture(fdef);

        collisionBox.dispose();

    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public Vector2 getPos(){
        return pos;
    }

    public void adjustAngle(float angle){
        angleChange = angle;
    }

    public void update(float delta){

        if(!stop) {

            sprite.setRotation(sprite.getRotation() + angleChange * delta);
            //sprite angle has downwards set to 0 degrees while libgdx rotates according to x-axis
            float angle = sprite.getRotation() - 90;
            velocity.x = MathUtils.cos(degToRad(angle));
            velocity.y = MathUtils.sin(degToRad(angle));

            velocity.scl(speed * delta);

            pos.add(velocity);
        }

        stop = ((DriverUserData) physicsBody.getUserData()).stopped;
        physicsBody.setTransform(pos.x,pos.y,getSpriteAngleInRad());

    }

    public float getSpriteAngleInRad(){
        return degToRad(sprite.getRotation());
    }

    private float degToRad(float deg){
        return deg*(MathUtils.PI / 180);
    }

    public void draw(SpriteBatch batch){

        if(!hide) {

            //set sprite position adjusted to its center
            if(!stop) {
                    sprite.setPosition(pos.x - sprite.getWidth() / 2, pos.y - sprite.getHeight() / 2);
                    sprite.draw(batch);
            }
        }
    }

    public float getWidth(){
        return sprite.getWidth();
    }

    public float getHeight(){
        return sprite.getHeight();
    }

    public void stopCar(){
        stop = true;
    }

    public int getCheckpointsReached(){
        return checkpointsReached;
    }

    public boolean isStopped(){
        return stop;
    }

    public void setHide(boolean b){
        hide = b;
    }

    public Body getPhysicsBody(){
        return physicsBody;
    }

    public Sprite getSprite(){
        return  sprite;
    }
}
