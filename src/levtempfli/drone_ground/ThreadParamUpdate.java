package levtempfli.drone_ground;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class ThreadParamUpdate implements Runnable {
    private final long update_loop_wait = 50;
    private final long max_debug_in_len = 12000;
    private Controller controller;
    private Data drone_data;

    public ThreadParamUpdate(Controller controller, Data drone_data) {
        this.controller = controller;
        this.drone_data = drone_data;
    }

    public void run() {
        while (true) {
            update_misc();
            update_incoming();
            update_outgoing();
            update_debug();
            try {
                Thread.sleep(update_loop_wait);
            } catch (InterruptedException e) {
                System.out.println("Thread.sleep failed: " + e.toString());
            }
        }
    }

    private void update_misc() {
        update_label(controller.label_ip, drone_data.getDrone_ip());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.checkbox_connected.setSelected(drone_data.isConnected());
            }
        });
    }

    private void update_incoming() {
        update_label(controller.label_press, drone_data.incoming.getPressure() + " Pa");
        update_label(controller.label_temp, drone_data.incoming.getTemperature() + " C°");
        update_label(controller.label_baro_alt, drone_data.incoming.getBaro_alti() + " m");
        update_label(controller.label_GPS_lat, drone_data.incoming.getGPS_lat() + "");
        update_label(controller.label_GPS_lon, drone_data.incoming.getGPS_lon() + "");
        update_label(controller.label_GPS_alt, drone_data.incoming.getGPS_alt() + " m");
        update_label(controller.label_GPS_fix, drone_data.incoming.getGPS_fix() + "");
        update_label(controller.label_GPS_speed, drone_data.incoming.getGPS_speed() + " km/h");
        update_label(controller.label_GPS_sats, drone_data.incoming.getGPS_sats() + "");
        update_label(controller.label_GPS_PDOP, drone_data.incoming.getGPS_PDOP() + "");
        update_label(controller.label_home_lat, drone_data.incoming.getHome_lat() + "");
        update_label(controller.label_home_lon, drone_data.incoming.getHome_lon() + "");
        update_label(controller.label_GPS_lat_set, drone_data.incoming.getLat_set() + "");
        update_label(controller.label_GPS_lon_set, drone_data.incoming.getLon_set() + "");
        update_label(controller.label_alt_set, drone_data.incoming.getAlt_set() + "");
        update_label(controller.label_IMUX_set, drone_data.incoming.getIMUX_set() + "°");
        update_label(controller.label_IMUY_set, drone_data.incoming.getIMUY_set() + "°");
        update_label(controller.label_IMUZ_set, drone_data.incoming.getIMUZ_set() + "°");
        update_label(controller.label_IMUX, drone_data.incoming.getIMUX() + "°");
        update_label(controller.label_IMUY, drone_data.incoming.getIMUY() + "°");
        update_label(controller.label_IMUZ, drone_data.incoming.getIMUZ() + "°");
        update_label(controller.label_volt, drone_data.incoming.getVolt() + "V");
        update_label(controller.label_curr, drone_data.incoming.getCurr() + "A");
        update_label(controller.label_est_bat, drone_data.incoming.getEst_bat() + " %");
        update_label(controller.label_est_rem, drone_data.incoming.getEst_rem() + " min");
        update_label(controller.label_mode, getModeName(drone_data.incoming.getMode()));
        update_label(controller.label_sonar_state, drone_data.incoming.getSonar_state() + "");
        update_label(controller.label_I2C_err, drone_data.incoming.getI2C_err() + "");
        update_label(controller.label_timer_err, drone_data.incoming.getTimer_err() + "");
    }

    private void update_outgoing() {
        update_label(controller.label_mode_set, getModeName(drone_data.outgoing.getMode_set()));
        update_label(controller.label_control_x, drone_data.outgoing.getControl_x() + "");
        update_label(controller.label_control_y, drone_data.outgoing.getControl_y() + "");
        update_label(controller.label_control_zr, drone_data.outgoing.getControl_zr() + "");
        update_label(controller.label_control_a, drone_data.outgoing.getControl_a() + "");
        update_label(controller.label_control_s, drone_data.outgoing.getControl_s() + "");
    }

    private void update_debug() {
        if (drone_data.debug.getDebug_in().length() > max_debug_in_len) drone_data.debug.resetDebug_in();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.textarea_debug.setText(drone_data.debug.getDebug_in());
            }
        });
    }

    private void update_label(Label label, String string) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label.setText(string);
            }
        });
    }

    private String getModeName(short mode) {
        String ret = "";
        switch (mode) {
            case 1:
                ret = "1 - Standing";
                break;
            case 2:
                ret = "2 - Manual";
                break;
            case 3:
                ret = "3 - GPS hold";
                break;
            case 4:
                ret = "4 - Go to home";
                break;
            case 5:
                ret = "5 - Landing";
                break;
        }
        return ret;
    }


}