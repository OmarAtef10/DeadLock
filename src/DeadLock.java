import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class DeadLock {
    int resource_num;
    int process_num;
    int[] totalResources;

    int[] originalAvailable;
    int[][] originalAllocated;
    int[][] originalNeed;
    int[][] originalMaximum;

    int[] available; //the available amount of each resource
    int[][] maximum; //the maximum demand of each process
    int[][] allocation; //the amount currently allocated to each process
    int[][] need; //the remaining needs of each process
    ArrayList<String> safeSequence;


    public static void main(String[] args) {
        DeadLock deadLock = new DeadLock();
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the number of processes: ");
        int process_num = input.nextInt();
        System.out.println("Enter number of resources: ");
        int resource_num = input.nextInt();

        deadLock.initializeSystem(process_num, resource_num);
        deadLock.viewCurrentState();

        int choice;
        while (true){
            deadLock.menu();
            choice = input.nextInt();
            if(choice==4){
                break;
            }
            if(choice == 1){
                deadLock.bankersAlgorithm();
                deadLock.viewCurrentState();
            }

            if(choice==2){
                deadLock.RQ();
            }
            if(choice==3){
                deadLock.RL();
            }


            if(choice>4 || choice<1){
                System.out.println("Invalid choice!");
            }

        }
    }

    public void menu(){
        System.out.println("1- Bankers Algorithm (Recovery included).");
        System.out.println("2- Request");
        System.out.println("3- Release");
        System.out.println("4-Quit");
    }

    public void initializeSystem(int process_num, int resource_num) {
        Scanner input = new Scanner(System.in);

        this.resource_num = resource_num;
        this.process_num = process_num;

        safeSequence = new ArrayList<>();

        this.totalResources = new int[resource_num];

        this.originalAvailable = new int[resource_num];
        this.available = new int[]{0,0,0};


        this.maximum = new int[][]{
                {0,1,0},
                {4,0,2},
                {3,0,3},
                {3,1,1},
                {0,0,4}
        };

        this.allocation = new int[][]{
                {0, 1,0},
                {2,0,0},
                {3,0,3},
                {2,1,1},
                {0,0,2}
        };

        this.need = new int[process_num][resource_num];

//
//        this.totalResources = new int[resource_num];
//        this.available = new int[resource_num];
//        this.maximum = new int[process_num][resource_num];
//        this.allocation = new int[process_num][resource_num];
//        this.need = new int[process_num][resource_num];
//
//        System.out.println("Enter current available resources (Available Matrix): ");
//        for(int i=0; i<resource_num; i++){
//            this.available[i] = input.nextInt();
//        }
//        for(int i=0; i<available.length; i++){
//            this.originalAvailable[i] = available[i];
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

        this.originalNeed = new int[process_num][resource_num];
        this.originalAllocated = new int[process_num][resource_num];
        this.originalMaximum = new int[process_num][resource_num];
        for(int i=0; i<process_num; i++){
            for(int j=0; j<resource_num; j++){
                this.originalAllocated[i][j] = allocation[i][j];
                this.originalNeed[i][j] = need[i][j];
                this.originalMaximum[i][j] = maximum[i][j];
            }
        }

        calculateNeedMatrix();
        calculateTotalResources();
        viewCurrentState();

    }

    public void resetStateOriginal(){
        this.safeSequence = new ArrayList<>();
        this.allocation = originalAllocated;
        this.maximum = originalMaximum;
        this.available = originalAvailable;
        this.need = originalNeed;
        calculateTotalResources();
    }

    public void calculateNeedMatrix(){
        for (int i = 0; i < process_num; i++) {
            for (int j = 0; j < resource_num; j++) {
                this.need[i][j] = maximum[i][j] - allocation[i][j];
            }
        }
    }

    public void calculateTotalResources(){
        for (int i = 0; i < process_num; i++) {
            for (int j = 0; j < resource_num; j++) {
                totalResources[j] += allocation[i][j];
            }
        }

        for (int i = 0; i < resource_num; i++) {
            totalResources[i] += available[i];
        }
    }

    public void viewCurrentState() {
        System.out.println("Available resources");
        for (int i = 0; i < resource_num; i++) {
            System.out.print(this.available[i] + " ");
        }
        System.out.println();

        System.out.println("Allocated resources");
        for (int i = 0; i < process_num; i++) {
            for (int j = 0; j < resource_num; j++) {
                System.out.print(this.allocation[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Maximum resources");
        for (int i = 0; i < process_num; i++) {
            for (int j = 0; j < resource_num; j++) {
                System.out.print(this.maximum[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Need Matrix");
        for (int i = 0; i < process_num; i++) {
            for (int j = 0; j < resource_num; j++) {
                System.out.print(this.need[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Total Resources");
        for (int i = 0; i < resource_num; i++) {
            System.out.print(totalResources[i] + " ");
        }
        System.out.println();
    }

    public void recoverAlgorithm() {
        int p_num = killMaxResourceProcess();
        safeSequence = new ArrayList<>();
        this.available = originalAvailable;
        for (int i = 0; i < resource_num; i++) {
            available[i] += allocation[p_num][i];
            need[p_num][i] += allocation[p_num][i];
            allocation[p_num][i] = 0;
        }
        calculateTotalResources();
        viewCurrentState();
    }

    public int killMaxResourceProcess() {
        int totalAllocated = 0;
        int maxAllocated = 0;
        int p_num = 0;
        for (int i = 0; i < process_num; i++) {
            for (int j = 0; j < resource_num; j++) {
                totalAllocated += allocation[i][j];
            }
            if (totalAllocated > maxAllocated) {
                maxAllocated = totalAllocated;
                p_num = i;
            }
            totalAllocated = 0;
        }
        System.out.println("Killed P"+p_num);
        return p_num;
    }

    public void bankersAlgorithm() {
        ArrayList<Boolean> state = new ArrayList<>();
        for (int i = 0; i < process_num; i++) {
            state.add(false);
        }

        int false_count = process_num;
        int prevFalseCount = 0;
        while (true) {
            prevFalseCount = false_count;
            for (int i = 0; i < process_num; i++) {
                if (!state.get(i)) {
                    if (compareNeedAvailable(i)) {
                        System.out.println("P" + i + " finished");
                        safeSequence.add("P" + i);
                        state.set(i, true);
                        false_count--;
                    } else {
                        System.out.println("P" + i + " no needed res");
                        state.set(i, false);
                    }
                }
            }
            boolean hasFalse = state.contains(false);

            if (!hasFalse) {
                getSafeSequence();
                break;
            }

            if (false_count == prevFalseCount) {
                System.out.println("Deadlock, no safe sequence could be obtained!, Entering Recovery Algorithm...");
                recoverAlgorithm();
                false_count=process_num;
                prevFalseCount=0;
                state = new ArrayList<>();
                for (int i = 0; i < process_num; i++) {
                    state.add(false);
                }
            }
        }
    }

    public void getSafeSequence() {
        System.out.print("<");
        for (int i = 0; i < safeSequence.size(); i++) {
            System.out.print(safeSequence.get(i) + ",");
        }
        System.out.print(">\n");
    }

    public boolean compareNeedAvailable(int need_row) {
        boolean sufficient = true;
        for (int i = 0; i < available.length; i++) {
            if (available[i] < need[need_row][i]) {
                sufficient = false;
            }
        }

        if (sufficient) {
            for (int i = 0; i < resource_num; i++) {
                this.available[i] += this.allocation[need_row][i];
            }
        }

        return sufficient;
    }

    public void RQ(){
        resetStateOriginal();
        Scanner input = new Scanner(System.in);

        System.out.println("Enter process number: ");
        int p_num = input.nextInt();
        System.out.println("Enter resources: ");
        for(int i=0; i<resource_num; i++){
            maximum[p_num][i] += input.nextInt();
        }
        calculateNeedMatrix();
        calculateTotalResources();
        bankersAlgorithm();
    }

    public void RL(){}

}
