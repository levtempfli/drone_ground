package levtempfli.drone_ground;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("window.fxml"));
            primaryStage.setTitle("Drone Ground Station");
            primaryStage.setScene(new Scene(root,1600,900));
            primaryStage.show();

            Thread video_recv_thread = new Thread(new Video_Recv());
            video_recv_thread.setDaemon(true);
            video_recv_thread.start();

            Thread tcp_com_thread=new Thread(new TCP_Comm());
            tcp_com_thread.setDaemon(true);
            tcp_com_thread.start();

            Thread param_update_thread=new Thread(new Param_Update());
            param_update_thread.setDaemon(true);
            param_update_thread.start();

        } catch (IOException e) {
            System.out.println("FXML loading failed");
            e.printStackTrace();
        }



    }
}
