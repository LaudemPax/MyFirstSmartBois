package cc.sudocode.firstsmartbois;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

public class Driver {

    private int maximumsteps = 5000;
    private int currentstep = 0;

    private float[] brain;

    private float maxAngleChange = 500f;

    private RaceCar raceCar;

    private float fitness = 0;

    public  boolean isTheBest = false;

    public Driver(World world, float x, float y){
        this(world,x,y,false);
    }

    public Driver(World world, float x, float y, boolean bestLastGen){
        Random random = new Random();

        brain = new float[maximumsteps];

        for(int i = 0; i < maximumsteps ; i++){
            brain[i] = ((float) Math.random() * maxAngleChange) * (random.nextBoolean() ? -1:1);
        }

        raceCar = new RaceCar(world,x,y,bestLastGen);
    }

    public void step(float delta){

        if(currentstep < maximumsteps){
            raceCar.adjustAngle(brain[currentstep]);
            currentstep++;
        }else{
            raceCar.stopCar();
        }

        raceCar.update(delta);

    }

    public void draw(SpriteBatch batch){
        raceCar.draw(batch);
    }

    public void setPos(float x, float y){

        raceCar.setPos(new Vector2(x,y));

    }

    public int getSteps(){
        return currentstep;
    }

    public void calculateFitness(){

        float checkpoints = ((DriverUserData) raceCar.getPhysicsBody().getUserData()).checkpoints;
        float laps = ((DriverUserData) raceCar.getPhysicsBody().getUserData()).getLaps();
        fitness = checkpoints * 2.0f * (1 + laps);

    }

    public int getLaps(){
        DriverUserData data = (DriverUserData) raceCar.getPhysicsBody().getUserData();
        return data.getLaps();
    }

    public void transplantBrain(float[] directions){

        for (int i = 0; i < brain.length; i++){

            this.brain[i] = directions[i];

        }

    }

    public float getFitness(){
        return fitness;
    }

    public RaceCar getRaceCar(){
        return raceCar;
    }

    //baby is an exact copy of the parent
    public Driver gimmeBaby(World world, float x, float y,boolean bestLastGen){
        Driver baby = new Driver(world,x,y,bestLastGen);
        baby.transplantBrain(this.brain);
        return baby;
    }

    public void mutate() {
        float mutationRate = 0.01f;//chance of mutation
        for (int i =0; i< brain.length; i++) {
            Random random = new Random();
            float rand = random.nextFloat();
            if (rand < mutationRate) {
                brain[i] = ((float) Math.random() * maxAngleChange) * (random.nextBoolean() ? -1:1);
            }
        }
    }

}
