package com.sudoprototype.firstsmartbois;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import javax.swing.*;

public class Population {

    private int size = 100;
    private Driver drivers[];
    private Vector2 spawnPoint;
    private int generation = 1;
    private float fitnessOfBestDriver = 0;
    private int stepsTakenByBestDriver = 0;
    private int laps = 0;
    private World world;

    //to draw the last location of the best driver in the last gen
    private Sprite bestDriverLastGen = null;

    public Population(int size,World world, float x, float y){
        this.size = size;
        this.world = world;
        drivers = new Driver[size];
        spawnPoint = new Vector2(x,y);

        for (int i = 0; i < size; i++){
            drivers[i] = new Driver(world,x,y);
        }
    }

    public int getSize(){
        return size;
    }

    public void step(float delta){

        if(!allCarsStopped()) {
            for (int i = 0; i < size; i++) {
                drivers[i].step(delta);
            }
        }else{

            calculateFitness();
            setBestDriver();
            reset();

        }
    }

    public void draw(SpriteBatch batch){

        //draw driver in array pos 1 to (size -1) first
        for(int i = 1; i < size; i++){
            drivers[i].draw(batch);
        }

        //draw driver at drivers[0] (best driver of the last gen to be drawn on top of the others)
        drivers[0].draw(batch);

        if(bestDriverLastGen != null)
            bestDriverLastGen.draw(batch);
    }

    private boolean allCarsStopped(){

        for (Driver driver : drivers) {
            if (!driver.getRaceCar().isStopped()) {
                return false;
            }
        }

        return true;

    }

    private void calculateFitness(){


        for(Driver driver : drivers){
            driver.calculateFitness();
        }

    }

    private void setBestDriver(){

        float bestFitness = 0;
        int index = 0;

        for (int i = 0; i < drivers.length; i++) {

            if(drivers[i].getFitness() > bestFitness){
                bestFitness = drivers[i].getFitness();
                index = i;
            }

        }

        bestDriverLastGen = drivers[index].getRaceCar().getSprite();
        fitnessOfBestDriver = drivers[index].getFitness();
        stepsTakenByBestDriver = drivers[index].getSteps();
        laps = drivers[index].getLaps();
        drivers[index].isTheBest = true;

    }

    private void reset(){

        //destroy all physics bodies
        for(int i = 0; i < drivers.length; i++){
            //destroy all physics bodies
            world.destroyBody(drivers[i].getRaceCar().getPhysicsBody());
        }

        //find index of best driver
        int index = 0;

        while(!drivers[index].isTheBest){
            index++;
        }

        Driver[] nextGen = new Driver[size];


        //the best driver from the last generation lives to drive another day
        nextGen[0] = drivers[index].gimmeBaby(world,spawnPoint.x,spawnPoint.y, true);
        nextGen[0].getRaceCar();

        //natural selection
        for(int i = 1; i <  nextGen.length; i++){
            nextGen[i] = selectParent().gimmeBaby(world,spawnPoint.x,spawnPoint.y, false);
        }

        //replace the old with the new
        for(int i = 0; i < drivers.length; i++){
            drivers[i] = nextGen[i];
        }

        //randomly modify the new generation
        mutateDrivers();

        generation++;

    }

    private Driver selectParent() {

        float fitnessSum = 0;
        for(Driver driver :  drivers){
            fitnessSum += driver.getFitness();
        }

        //random float between 0 and fitnessSum
        float random = (float) Math.random() * (fitnessSum);
        float runningSum = 0;


        for (int i = 0; i < drivers.length; i++) {
            runningSum += drivers[i].getFitness();
            if(runningSum > random) {
                return drivers[i];
            }
        }

        //return the last driver in the array
        return drivers[drivers.length - 1];

    }

    private void mutateDrivers(){

        //mutate all except the best driver
        for(int i = 1; i < drivers.length;i++){
            drivers[i].mutate();
        }

    }

    public int getGeneration(){
        return generation;
    }

    public float getFitnessOfBestDriver(){
        return fitnessOfBestDriver;
    }

    public int getStepsTakenByBestDriver(){
        return stepsTakenByBestDriver;
    }

    public int getLaps(){
        return laps;
    }

}
