package levtempfli.drone_ground;

public class TCP_Comm implements Runnable{
    public void run() {
        while (true) {
            System.out.println("Hey, I am the TCP thread!");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
