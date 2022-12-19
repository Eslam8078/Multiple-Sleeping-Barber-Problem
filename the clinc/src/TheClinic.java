package homework5;

import java.util.concurrent.*;

public class TheClinic extends Thread {

    public static Semaphore patients = new Semaphore(0);
    public static Semaphore doctor = new Semaphore(0);
    public static Semaphore chairs = new Semaphore(1);

    // Initialize the number of chairs in the clinic to a given value
    public static final int CHAIRS = 10;
    public static int freeSeats = CHAIRS;


    class Patient extends Thread {

        //Initialize variables
        int patient;
        boolean needsExaming=true;

        public Patient(int i) {
            patient = i;
        }

        public void run() {
            //Check boolean flag to see if patient needsExaming
            while (needsExaming) {
                try {
                    //patient tries to get chair
                    chairs.acquire();
                    //Check to see if there are free seats
                    if (freeSeats > 0) {
                        //If there are free seats, patient sits.
                        System.out.println("Patient " + this.patient + " sat down.");
                        //Decrement free sits after patient takes chair
                        freeSeats--;
                        //notify the doctor that there is a customer
                        patients.release();
                        //release chair
                        chairs.release();
                        try {
                            //check status of doctor
                            doctor.acquire();
                            //if patient is examed, set flag to false and invoke get treatment method
                            needsExaming = false;
                            this.getTreatment();
                        } catch (InterruptedException ex) {}
                    }
                    else  {
                        //Handle no free seats
                        System.out.println("No free seats  Patient " + this.patient + " has left the clinic.");
                        //release chair
                        chairs.release();
                        //set flag to false, since patient will not wait for treatment
                        needsExaming=false;
                    }
                }
                catch (InterruptedException ex) {}
            }
        }

        //Create haircut method
        public void getTreatment(){
            System.out.println("Patient " + this.patient + " is getting a Treatment");
            try {
                sleep(5050);
            } catch (InterruptedException ex) {}
        }

    }


    //Create Doctor threat
    class Doctor extends Thread {
        int Doctor;

        public Doctor(int y) {
            Doctor=y;
        }

        public void run() {
            while(true) {
                try {
                    //doctor tries to get patient
                    patients.acquire();
                    //doctor takes patient
                    chairs.release();
                    //Increment number of free seats
                    freeSeats++;
                    //doctor begins cut
                    doctor.release();
                    //Unlock chairs
                    chairs.release();
                    //doctor is examing
                    this.examing();
                } catch (InterruptedException ex) {}
            }
        }

        //Cut hair method
        public void examing(){
            System.out.println("The doctor "+this.Doctor+"is examing the patient");
            try {
                sleep(5000);
            } catch (InterruptedException ex){ }
        }
    }

    public static void main(String args[]) {

        // Create instance of barber shop
        TheClinic theclinic = new TheClinic();
        //Start
        theclinic.start();
    }

    public void run(){
        for (int i=0; i<3;i++){
            Doctor b = new Doctor(i);
            b.start();

        }

        //Create scenario with x amount of patients, for this scenario set to 25
        for (int i=1; i<25; i++) {
            Patient c = new Patient(i);
            c.start();
            try {
                sleep(2000);
            } catch(InterruptedException ex) {};
        }
    }
}