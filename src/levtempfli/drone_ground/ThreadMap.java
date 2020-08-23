package levtempfli.drone_ground;

public class ThreadMap implements Runnable{
    public void run() {
        while (true) {
            System.out.println("Hey, I am the map thread!");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
