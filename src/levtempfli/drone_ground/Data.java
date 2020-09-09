package levtempfli.drone_ground;

public class Data {
    private String drone_ip = "172.27.125.242";
    private boolean connected = false;

    public synchronized String getDrone_ip() {
        return drone_ip;
    }

    public synchronized void setDrone_ip(String drone_ip) {
        this.drone_ip = drone_ip;
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public class Incoming {
        private int pressure = 0;
        private double temperature = 0;
        private double baro_alti = 0;
        private double GPS_lat = 0;
        private double GPS_lon = 0;
        private double GPS_alt = 0;
        private short GPS_fix = 0;
        private double GPS_speed = 0;
        private short GPS_sats = 0;
        private double GPS_PDOP = 0;
        private double home_lat = 0;
        private double home_lon = 0;
        private double lat_set = 0;
        private double lon_set = 0;
        private double alt_set = 0;
        private double IMUX_set = 0;
        private double IMUY_set = 0;
        private double IMUZ_set = 0;
        private double IMUX = 0;
        private double IMUY = 0;
        private double IMUZ = 0;
        private double volt = 0;
        private double curr = 0;
        private double est_bat = 0;
        private double est_rem = 0;
        private short mode = 1;
        private boolean sonar_state = false;
        private long I2C_err = 0;
        private long timer_err = 0;

        public synchronized int getPressure() {
            return pressure;
        }

        public synchronized void setPressure(int pressure) {
            this.pressure = pressure;
        }

        public synchronized double getTemperature() {
            return temperature;
        }

        public synchronized void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public synchronized double getBaro_alti() {
            return baro_alti;
        }

        public synchronized void setBaro_alti(double baro_alti) {
            this.baro_alti = baro_alti;
        }

        public synchronized double getGPS_lat() {
            return GPS_lat;
        }

        public synchronized void setGPS_lat(double GPS_lat) {
            this.GPS_lat = GPS_lat;
        }

        public synchronized double getGPS_lon() {
            return GPS_lon;
        }

        public synchronized void setGPS_lon(double GPS_lon) {
            this.GPS_lon = GPS_lon;
        }

        public synchronized double getGPS_alt() {
            return GPS_alt;
        }

        public synchronized void setGPS_alt(double GPS_alt) {
            this.GPS_alt = GPS_alt;
        }

        public synchronized short getGPS_fix() {
            return GPS_fix;
        }

        public synchronized void setGPS_fix(short GPS_fix) {
            this.GPS_fix = GPS_fix;
        }

        public synchronized double getGPS_speed() {
            return GPS_speed;
        }

        public synchronized void setGPS_speed(double GPS_speed) {
            this.GPS_speed = GPS_speed;
        }

        public synchronized short getGPS_sats() {
            return GPS_sats;
        }

        public synchronized void setGPS_sats(short GPS_sats) {
            this.GPS_sats = GPS_sats;
        }

        public synchronized double getGPS_PDOP() {
            return GPS_PDOP;
        }

        public synchronized void setGPS_PDOP(double GPS_PDOP) {
            this.GPS_PDOP = GPS_PDOP;
        }

        public synchronized double getHome_lat() {
            return home_lat;
        }

        public synchronized void setHome_lat(double home_lat) {
            this.home_lat = home_lat;
        }

        public synchronized double getHome_lon() {
            return home_lon;
        }

        public synchronized void setHome_lon(double home_lon) {
            this.home_lon = home_lon;
        }

        public synchronized double getLat_set() {
            return lat_set;
        }

        public synchronized void setLat_set(double lat_set) {
            this.lat_set = lat_set;
        }

        public synchronized double getLon_set() {
            return lon_set;
        }

        public synchronized void setLon_set(double lon_set) {
            this.lon_set = lon_set;
        }

        public synchronized double getAlt_set() {
            return alt_set;
        }

        public synchronized void setAlt_set(double alt_set) {
            this.alt_set = alt_set;
        }

        public synchronized double getIMUX_set() {
            return IMUX_set;
        }

        public synchronized void setIMUX_set(double IMUX_set) {
            this.IMUX_set = IMUX_set;
        }

        public synchronized double getIMUY_set() {
            return IMUY_set;
        }

        public synchronized void setIMUY_set(double IMUY_set) {
            this.IMUY_set = IMUY_set;
        }

        public synchronized double getIMUZ_set() {
            return IMUZ_set;
        }

        public synchronized void setIMUZ_set(double IMUZ_set) {
            this.IMUZ_set = IMUZ_set;
        }

        public synchronized double getIMUX() {
            return IMUX;
        }

        public synchronized void setIMUX(double IMUX) {
            this.IMUX = IMUX;
        }

        public synchronized double getIMUY() {
            return IMUY;
        }

        public synchronized void setIMUY(double IMUY) {
            this.IMUY = IMUY;
        }

        public synchronized double getIMUZ() {
            return IMUZ;
        }

        public synchronized void setIMUZ(double IMUZ) {
            this.IMUZ = IMUZ;
        }

        public synchronized double getVolt() {
            return volt;
        }

        public synchronized void setVolt(double volt) {
            this.volt = volt;
        }

        public synchronized double getCurr() {
            return curr;
        }

        public synchronized void setCurr(double curr) {
            this.curr = curr;
        }

        public synchronized double getEst_bat() {
            return est_bat;
        }

        public synchronized void setEst_bat(double est_bat) {
            this.est_bat = est_bat;
        }

        public synchronized double getEst_rem() {
            return est_rem;
        }

        public synchronized void setEst_rem(double est_rem) {
            this.est_rem = est_rem;
        }

        public synchronized short getMode() {
            return mode;
        }

        public synchronized void setMode(short mode) {
            this.mode = mode;
        }

        public synchronized boolean getSonar_state() {
            return sonar_state;
        }

        public synchronized void setSonar_state(boolean sonar_state) {
            this.sonar_state = sonar_state;
        }

        public synchronized long getI2C_err() {
            return I2C_err;
        }

        public synchronized void setI2C_err(long i2C_err) {
            I2C_err = i2C_err;
        }

        public synchronized long getTimer_err() {
            return timer_err;
        }

        public synchronized void setTimer_err(long timer_err) {
            this.timer_err = timer_err;
        }
    }

    public class Outgoing {
        private short mode_set = 1;
        private short control_x = 0;
        private short control_y = 0;
        private short control_zr = 0;
        private short control_a = 0;
        private short control_s = 1;

        public synchronized short getMode_set() {
            return mode_set;
        }

        public synchronized void setMode_set(short mode_set) {
            this.mode_set = mode_set;
        }

        public synchronized short getControl_x() {
            return control_x;
        }

        public synchronized void setControl_x(short control_x) {
            this.control_x = (short) (control_x * control_s);
        }

        public synchronized short getControl_y() {
            return control_y;
        }

        public synchronized void setControl_y(short control_y) {
            this.control_y = (short) (control_y * control_s);
        }

        public synchronized short getControl_zr() {
            return control_zr;
        }

        public synchronized void setControl_zr(short control_zr) {
            this.control_zr = (short) (control_zr * control_s);
        }

        public synchronized short getControl_a() {
            return control_a;
        }

        public synchronized void setControl_a(short control_a) {
            this.control_a = (short) (control_a * control_s);
        }

        public synchronized void setControl_s(short control_s) {
            control_x = (short) (control_x / this.control_s * control_s);
            control_y = (short) (control_y / this.control_s * control_s);
            control_zr = (short) (control_zr / this.control_s * control_s);
            control_a = (short) (control_a / this.control_s * control_s);
            this.control_s = control_s;
        }

        public synchronized short getControl_s() {
            return control_s;
        }
    }

    public class Debug {
        private String debug_line_out = "";
        private String debug_in = "";

        public synchronized String getDebug_line_out() {
            String tmp = debug_line_out;
            debug_line_out = "";
            return tmp;
        }

        public synchronized void setDebug_line_out(String debug_line_out) {
            this.debug_line_out = debug_line_out;
        }

        public synchronized String getDebug_in() {
            return debug_in;
        }

        public synchronized void addDebug_in(String debug_in) {
            this.debug_in += debug_in;
        }

        public synchronized void resetDebug_in() {
            debug_in = "";
        }
    }

    public Incoming incoming = new Incoming();
    public Outgoing outgoing = new Outgoing();
    public Debug debug = new Debug();

}
