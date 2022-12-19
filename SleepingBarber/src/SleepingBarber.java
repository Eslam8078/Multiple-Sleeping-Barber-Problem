import java.util.concurrent.*;

public class SleepingBarber extends Thread {

    public static Semaphore customers = new Semaphore(0);
    public static Semaphore barber = new Semaphore(0);
    public static Semaphore chairs = new Semaphore(1);

    // Initialize the number of chairs in the barber shop to a given value
    public static final int CHAIRS = 10;
    public static int freeSeats = CHAIRS;


    class Customer extends Thread {

        //Initialize variables
        int customer;
        boolean needsCut=true;

        public Customer(int i) {
            customer = i;
        }

        public void run() {
            //Check boolean flag to see if customer needs haircut
            while (needsCut) {
                try {
                    //Customer tries to get chair
                    chairs.acquire();
                    //Check to see if there are free seats
                    if (freeSeats > 0) {
                        //If there are free seats, customer sits.
                        System.out.println("Customer " + this.customer + " sat down.");
                        //Decrement free sits after customer takes chair
                        freeSeats--;
                        //notify the barber that there is a customer
                        customers.release();
                        //release chair
                        chairs.release();
                        try {
                            //check status of barber
                            barber.acquire();
                            //if customer is getting haircut, set flag to false and invoke get haircut method
                            needsCut = false;
                            this.getHaircut();
                        } catch (InterruptedException ex) {}
                    }
                    else  {
                        //Handle no free seats
                        System.out.println("No free seats  Customer " + this.customer + " has left the barbershop.");
                        //release chair
                        chairs.release();
                        //set flag to false, since customer will not wait for haircut
                        needsCut=false;
                    }
                }
                catch (InterruptedException ex) {}
            }
        }

        //Create haircut method
        public void getHaircut(){
            System.out.println("Customer " + this.customer + " is getting a hair cut");
            try {
                sleep(5050);
            } catch (InterruptedException ex) {}
        }

    }


    //Create barber threat
    class Barber extends Thread {
        int Barber;

        public Barber(int y) {
            Barber=y;
        }

        public void run() {
            while(true) {
                try {
                    //Barber tries to get customer
                    customers.acquire();
                    //Barber takes customer
                    chairs.release();
                    //Increment number of free seats
                    freeSeats++;
                    //Barber begins cut
                    barber.release();
                    //Unlock chairs
                    chairs.release();
                    //Barber is cutting hair
                    this.cutHair();
                } catch (InterruptedException ex) {}
            }
        }

        //Cut hair method
        public void cutHair(){
            System.out.println("The barber "+this.Barber+"is cutting hair");
            try {
                sleep(5000);
            } catch (InterruptedException ex){ }
        }
    }

    public static void main(String args[]) {

        // Create instance of barber shop
        SleepingBarber barberShop = new SleepingBarber();
        //Start
        barberShop.start();
    }

    public void run(){
        for (int i=0; i<3;i++){
            Barber b = new Barber(i);
            b.start();

        }

        //Create scenario with x amount of customers, for this scenario set to 25
        for (int i=1; i<25; i++) {
            Customer c = new Customer(i);
            c.start();
            try {
                sleep(2000);
            } catch(InterruptedException ex) {};
        }
    }
}