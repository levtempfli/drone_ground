package levtempfli.drone_ground;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            Data drone_data = new Data();
            Controller controller = new Controller(drone_data);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("window.fxml"));
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 1600, 900);
            scene.addEventHandler(KeyEvent.ANY, controller.keyboardEventHandler);

            primaryStage.setTitle("Drone Ground Station");
            primaryStage.setScene(scene);
            primaryStage.show();

            controller.init();

            Thread map_thread = new Thread(new ThreadMap());
            map_thread.setDaemon(true);
            map_thread.start();

            Thread video_recv_thread = new Thread(new ThreadVideoRecv());
            video_recv_thread.setDaemon(true);
            video_recv_thread.start();

            Thread tcp_com_thread = new Thread(new ThreadTCPComm(drone_data));
            tcp_com_thread.setDaemon(true);
            tcp_com_thread.start();

            Thread param_update_thread = new Thread(new ThreadParamUpdate(controller, drone_data));
            param_update_thread.setDaemon(true);
            param_update_thread.start();

        } catch (IOException e) {
            System.out.println("FXML loading failed");
            e.printStackTrace();
        }


    }
}
