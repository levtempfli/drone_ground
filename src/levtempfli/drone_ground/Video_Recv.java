package levtempfli.drone_ground;

public class Video_Recv implements Runnable{
    public void run() {
        while (true) {
            System.out.println("Hey, I am the video thread!");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
