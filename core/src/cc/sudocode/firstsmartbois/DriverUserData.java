package cc.sudocode.firstsmartbois;

public class DriverUserData {

    private final int arraySize = 200;

    public boolean stopped = false;

    //overall checkpoints past
    public int checkpoints = 0;
    public int[] checkpointsPast = new int[arraySize];

    //reset every lap
    private int checkPointIndex = 0;

    private int laps = 0;
    private int checkpointsSum = 0;
    private int minimumLapsBeforeTrigger = 5;

    public boolean hasCheckpont(int id){

        if(checkPointIndex < 1){
            return false;
        }

        for(int i = 0; i < checkPointIndex; i++){
            if(checkpointsPast[i] == id){
                return true;
            }
        }

        return false;
    }

    public void addCheckpoint(int id){
        checkpointsPast[checkPointIndex] = id;
        checkPointIndex++;

        updateCheckpoints();
    }

    private void updateCheckpoints(){
        checkpoints = checkPointIndex + checkpointsSum;

        //so checkpoints on higher laps are worth more
        checkpoints *= (laps + 1);
    }

    //if the car crosses the lap marker
    public void crossLapMarker(){

        if(checkPointIndex > minimumLapsBeforeTrigger) {
            checkpointsSum += checkPointIndex;
            laps++;
            checkPointIndex = 0;
            clearCheckpointsPast();
        }

        addCheckpoint(Util.lapMarkerID);

    }

    private void clearCheckpointsPast(){

        for(int i = 0; i < arraySize; i++){
            checkpointsPast[i] = 0;
        }
    }

    public int getLaps(){
        return laps;
    }

}
