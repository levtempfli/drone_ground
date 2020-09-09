package levtempfli.drone_ground;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class Controller {
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

    public void init(){
        checkbox_connected.setDisable(true);
    }
}
