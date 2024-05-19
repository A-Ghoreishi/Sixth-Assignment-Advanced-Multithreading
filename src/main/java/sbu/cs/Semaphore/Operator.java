package sbu.cs.Semaphore;
import java.util.concurrent.Semaphore;

public class Operator extends Thread {

    Semaphore theSemaphore;
    public Operator(String name,Semaphore theSemaphore) {
        super(name);
        this.theSemaphore = theSemaphore;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++)
        {
            try {
                theSemaphore.acquire();
                System.out.println(getName());
                Resource.accessResource();
            }
            catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            finally {
                theSemaphore.release();
            }

            // critical section - a Maximum of 2 operators can access the resource concurrently
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
