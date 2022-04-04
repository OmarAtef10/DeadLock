import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class DeadLock {
    int resource_num;
    int process_num;
    int [] totalResources;
    int [] available; //the available amount of each resource
    int [][] maximum; //the maximum demand of each process
    int [][] allocation; //the amount currently allocated to each process
    int [][] need; //the remaining needs of each process
    ArrayList<String> safeSequence;



    public static void main(String[] args) {
        DeadLock deadLock = new DeadLock();
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the number of processes: ");
        int process_num = input.nextInt();
        System.out.println("Enter number of resources: ");
        int resource_num = input.nextInt();

        deadLock.initializeSystem(process_num,resource_num);

    }

    public void initializeSystem(int process_num,int resource_num) {
        Scanner input = new Scanner(System.in);

        this.resource_num = resource_num;
        this.process_num = process_num;

        safeSequence = new ArrayList<>();

        this.totalResources = new int[resource_num];

        this.available = new int[]{1,5,2,0};

        this.maximum = new int[][]{
                {0,0,1,2},
                {1,7,5,0},
                {2,3,5,6},
                {0,6,5,2},
                {0,6,5,6}
        };

        this.allocation = new int[][]{
                {0,0,1,2},
                {1,0,0,0},
                {1,3,5,4},
                {0,6,3,2},
                {0,0,1,4}
        };

        this.need = new int[process_num][resource_num];

//
//        this.totalResources = new int[resource_num];
//        this.available = new int[resource_num];
//        this.maximum = new int[process_num][resource_num];
//        this.allocation = new int[process_num][resource_num];
//        this.need = new int[process_num][resource_num];

//        System.out.println("Enter current available resources (Available Matrix): ");
//        for(int i=0; i<resource_num; i++){
//            this.available[i] = input.nextInt();
//        }
//
//        System.out.println("Enter Allocated resources for each process:- ");
//        for(int i=0; i<process_num; i++){
//            System.out.println("Enter allocated resources  For Process P"+i);
//            for(int j=0; j<resource_num; j++){
//                this.allocation[i][j] = input.nextInt();
//            }
//        }
//
//        System.out.println("Enter Maximum resources for each process:- ");
//        for(int i=0; i<process_num; i++){
//            System.out.println("Enter maximum amount of resources For Process P"+i);
//            for(int j=0; j<resource_num; j++){
//                this.maximum[i][j] = input.nextInt();
//            }
//        }


        for(int i=0; i<process_num; i++){
            for(int j=0; j<resource_num; j++){
                this.need[i][j] = maximum[i][j]-allocation[i][j];
            }
        }

        for(int i=0; i<process_num; i++){
            for(int j=0; j<resource_num; j++){
                totalResources[j]+= allocation[i][j];
            }
        }

        for(int i=0; i<resource_num; i++){
            totalResources[i]+= available[i];
        }
        viewCurrentState();
        bankersAlgorithm();
        viewCurrentState();
    }

    public void viewCurrentState(){
        System.out.println("Available resources");
        for(int i=0; i<resource_num; i++){
            System.out.print(this.available[i]+" ");
        }
        System.out.println();

        System.out.println("Allocated resources");
        for(int i=0; i<process_num; i++){
            for(int j=0; j<resource_num; j++){
                System.out.print(this.allocation[i][j]+" ");
            }
            System.out.println();
        }

        System.out.println("Maximum resources");
        for(int i=0; i<process_num; i++){
            for(int j=0; j<resource_num; j++){
                System.out.print(this.maximum[i][j]+" ");
            }
            System.out.println();
        }

        System.out.println("Need Matrix");
        for(int i=0; i<process_num; i++){
            for(int j=0; j<resource_num; j++){
                System.out.print(this.need[i][j]+" ");
            }
            System.out.println();
        }

        System.out.println("Total Resources");
        for(int i=0; i<resource_num; i++){
            System.out.print(totalResources[i]+" ");
        }
        System.out.println();
    }

    public void bankersAlgorithm(){
        ArrayList<Boolean> state = new ArrayList<>();
        for(int i=0; i<process_num; i++){
            state.add(false);
        }

        int false_count = process_num;
        int prevFalseCount = 0;
        while (true) {
            prevFalseCount = false_count;
            for (int i = 0; i < process_num; i++) {
                if(!state.get(i)) {
                    if (compareNeedAvailable(i)) {
                        System.out.println("P" + i + " finished");
                        safeSequence.add("P"+i);
                        state.set(i,true);
                        false_count--;
                    } else {
                        System.out.println("P" + i + " no needed res");
                        state.set(i,false);
                    }
                }
            }
            boolean hasFalse = state.contains(false);

            if(!hasFalse){
                getSafeSequence();
                break;
            }

            if(false_count == prevFalseCount){
                System.out.println("Deadlock, no safe sequence could be obtained!");
                break;
            }
        }
    }

    public void getSafeSequence(){
        System.out.print("<");
        for(int i=0; i<safeSequence.size(); i++){
            System.out.print(safeSequence.get(i)+",");
        }
        System.out.print(">\n");
    }

    public boolean compareNeedAvailable(int need_row){
        boolean sufficient = true;
        for(int i=0; i<available.length; i++){
            if(available[i] < need[need_row][i] ){
                sufficient = false;
            }
        }

        if(sufficient){
            for(int i=0; i<resource_num; i++){
                this.available[i] += this.allocation[need_row][i];
            }
        }

        return sufficient;
    }


}
