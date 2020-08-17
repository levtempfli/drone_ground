package levtempfli.drone_ground;

public class Param_Update implements Runnable{
    public void run() {
        while (true) {
            System.out.println("Hey, I am the paramupdate thread!");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}