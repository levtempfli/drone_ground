package levtempfli.drone_ground;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


public class Controller {
    private final short max_mode = 5;
    private final short min_mode = 1;

    private Data drone_data;

    public Controller(Data drone_data) {
        this.drone_data = drone_data;
    }

    public EventHandler<javafx.scene.input.KeyEvent> keyboardEventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (textfield_ip.isFocused() || textfield_debug.isFocused() || textfield_debug.isFocused()) return;


            if (keyEvent.getCode() == KeyCode.W) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && drone_data.outgoing.getControl_x() == 0)
                    drone_data.outgoing.setControl_x((short) 1);
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_x() > 0)
                    drone_data.outgoing.setControl_x((short) 0);
            }
            if (keyEvent.getCode() == KeyCode.S) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && drone_data.outgoing.getControl_x() == 0)
                    drone_data.outgoing.setControl_x((short) -1);
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_x() < 0)
                    drone_data.outgoing.setControl_x((short) 0);
            }
            if (keyEvent.getCode() == KeyCode.D) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && drone_data.outgoing.getControl_y() == 0)
                    drone_data.outgoing.setControl_y((short) 1);
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_y() > 0)
                    drone_data.outgoing.setControl_y((short) 0);
            }
            if (keyEvent.getCode() == KeyCode.A) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && drone_data.outgoing.getControl_y() == 0)
                    drone_data.outgoing.setControl_y((short) -1);
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_y() < 0)
                    drone_data.outgoing.setControl_y((short) 0);
            }
            if (keyEvent.getCode() == KeyCode.E) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && drone_data.outgoing.getControl_zr() == 0)
                    drone_data.outgoing.setControl_zr((short) 1);
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_zr() > 0)
                    drone_data.outgoing.setControl_zr((short) 0);
            }
            if (keyEvent.getCode() == KeyCode.Q) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && drone_data.outgoing.getControl_zr() == 0)
                    drone_data.outgoing.setControl_zr((short) -1);
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_zr() < 0)
                    drone_data.outgoing.setControl_zr((short) 0);
            }
            if (keyEvent.getCode() == KeyCode.SHIFT) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && drone_data.outgoing.getControl_a() == 0)
                    drone_data.outgoing.setControl_a((short) 1);
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_a() > 0)
                    drone_data.outgoing.setControl_a((short) 0);
            }
            if (keyEvent.getCode() == KeyCode.CONTROL) {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED && drone_data.outgoing.getControl_a() == 0)
                    drone_data.outgoing.setControl_a((short) -1);
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_a() < 0)
                    drone_data.outgoing.setControl_a((short) 0);
            }
            if (keyEvent.getCode() == KeyCode.R) {
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_s() < max_mode)
                    drone_data.outgoing.setControl_s((short) (drone_data.outgoing.getControl_s() + 1));
            }
            if (keyEvent.getCode() == KeyCode.F) {
                if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED && drone_data.outgoing.getControl_s() > min_mode)
                    drone_data.outgoing.setControl_s((short) (drone_data.outgoing.getControl_s() - 1));
            }
            if (keyEvent.getCode() == KeyCode.DIGIT1) {
                drone_data.outgoing.setMode_set((short) 1);
            }
            if (keyEvent.getCode() == KeyCode.DIGIT2) {
                drone_data.outgoing.setMode_set((short) 2);
            }
            if (keyEvent.getCode() == KeyCode.DIGIT3) {
                drone_data.outgoing.setMode_set((short) 3);
            }
            if (keyEvent.getCode() == KeyCode.DIGIT4) {
                drone_data.outgoing.setMode_set((short) 4);
            }
            if (keyEvent.getCode() == KeyCode.DIGIT5) {
                drone_data.outgoing.setMode_set((short) 5);
            }
        }
    };

    public void init() {
        checkbox_connected.setDisable(true);
        main_pane.requestFocus();
        main_pane.setOnMouseClicked(focus_loser_from_texts);
    }

    public EventHandler<javafx.scene.input.MouseEvent> focus_loser_from_texts = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            main_pane.requestFocus();
        }
    };

    public AnchorPane main_pane = null;

    public Label label_press = null;
    public Label label_temp = null;
    public Label label_baro_alt = null;
    public Label label_GPS_lat = null;
    public Label label_GPS_lon = null;
    public Label label_GPS_alt = null;
    public Label label_GPS_fix = null;
    public Label label_GPS_speed = null;
    public Label label_GPS_sats = null;
    public Label label_GPS_PDOP = null;
    public Label label_home_lat = null;
    public Label label_home_lon = null;
    public Label label_GPS_lat_set = null;
    public Label label_GPS_lon_set = null;
    public Label label_alt_set = null;
    public Label label_IMUX_set = null;
    public Label label_IMUY_set = null;
    public Label label_IMUZ_set = null;
    public Label label_IMUX = null;
    public Label label_IMUY = null;
    public Label label_IMUZ = null;
    public Label label_volt = null;
    public Label label_curr = null;
    public Label label_est_bat = null;
    public Label label_est_rem = null;
    public Label label_mode = null;
    public Label label_sonar_state = null;
    public Label label_I2C_err = null;
    public Label label_timer_err = null;
    public Label label_mode_set = null;
    public Label label_control_x = null;
    public Label label_control_y = null;
    public Label label_control_zr = null;
    public Label label_control_a = null;
    public Label label_control_s = null;

    public TextField textfield_ip = null;
    public Label label_ip = null;
    public CheckBox checkbox_connected = null;

    public TextField textfield_debug = null;
    public TextArea textarea_debug = null;


}





