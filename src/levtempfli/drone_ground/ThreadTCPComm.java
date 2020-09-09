package levtempfli.drone_ground;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ThreadTCPComm implements Runnable {
    private final int drone_port = 325;

    private final long mainloop_wait = 50;
    private final long connloop_wait = 20;
    private final int conn_msg_timeout = 5000;
    private final int msg_send_period = 125;
    private final int in_buffer_size = 64000;
    private final int out_buffer_size = 256;
    private final int slv_buffer_size = 4098;
    private final int msg_types_max = 3;

    private static class Buffer {
        private byte[] array;
        private ByteBuffer byteBuffer;
        private int index;

        public Buffer(final int size) {
            array = new byte[size];
            byteBuffer = ByteBuffer.wrap(array);
            index = 0;
        }

        public byte getAtIndex() {
            return array[index];
        }

        public void setAtIndex(byte t) {
            array[index] = t;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public byte[] getArray() {
            return array;
        }

        public ByteBuffer getByteBuffer() {
            return byteBuffer;
        }

    }

    Buffer in_buffer = new Buffer(in_buffer_size);
    Buffer out_buffer = new Buffer(out_buffer_size);
    Buffer slv_buffer = new Buffer(slv_buffer_size);


    private int out_msg_types_curr = 0;
    private int in_chd_1, in_chd_2;
    private int in_status = 0, slv_buff_i = 0;
    private boolean connected = false;

    SocketChannel tcp_cl;
    Timer msg_send_timer = new Timer();
    Timer conn_msg_timer = new Timer();


    private Data drone_data;

    private void socket_disconnect() {
        connected = false;
        drone_data.setConnected(false);
        try {
            tcp_cl.close();
        } catch (IOException ignored) {
        }
    }

    public ThreadTCPComm(Data drone_data) {
        this.drone_data = drone_data;
    }

    public void run() {

        while (true) {
            main_loop();
            try {
                Thread.sleep(mainloop_wait);
            } catch (InterruptedException e) {
                System.out.println("Thread.sleep failed: " + e.toString());
            }
        }

    }

    private void main_loop() {
        try {
            tcp_cl = SocketChannel.open(new InetSocketAddress(drone_data.getDrone_ip(), drone_port));
            tcp_cl.configureBlocking(false);
            connected = true;
            drone_data.setConnected(true);
            conn_msg_timer.StartCounter();
            msg_send_timer.StartCounter();
        } catch (IOException ignored) {
        }
        while (connected) {
            connected_loop();
            try {
                Thread.sleep(connloop_wait);
            } catch (InterruptedException e) {
                System.out.println("Thread.sleep failed: " + e.toString());
            }
        }
    }

    private void connected_loop() {
        int rec;
        try {
            in_buffer.byteBuffer.clear();
            rec = tcp_cl.read(in_buffer.byteBuffer);
        } catch (IOException e) {
            socket_disconnect();
            return;
        }
        if (rec == -1) {
            socket_disconnect();
            return;
        } else if (rec != 0) {
            decode_message(rec);
            conn_msg_timer.StartCounter();
        }


        int sent, to_st = encode_message();
        if (to_st != 0) {
            out_buffer.getByteBuffer().clear();
            out_buffer.getByteBuffer().limit(to_st);
            try {
                sent = tcp_cl.write(out_buffer.getByteBuffer());
                if (sent == to_st) {
                    msg_send_timer.StartCounter();
                    out_msg_types_curr++;
                    if (out_msg_types_curr >= msg_types_max) out_msg_types_curr = 0;
                }
            } catch (IOException e) {
                socket_disconnect();
            }
        }


        if (conn_msg_timer.GetCounter() > conn_msg_timeout) {
            socket_disconnect();
        }

    }

    private void decode_message(int rec) {
        char char_rec;
        for (int i = 0; i < rec; i++) {
            char_rec = (char) in_buffer.getArray()[i];
            switch (char_rec) {
                case '@':
                    in_status = 1;
                    slv_buff_i = 0;
                    break;
                case '~':
                    if (in_status != 1) break;
                    in_status = 2;
                    break;
                default:
                    switch (in_status) {
                        case 1:
                            if (slv_buff_i == slv_buffer_size - 1) {
                                in_status = 0;
                                break;
                            }
                            slv_buff_i++;
                            slv_buffer.getArray()[slv_buff_i] = (byte) char_rec;
                            break;
                        case 2:
                            if (char_rec < 58) in_chd_1 = char_rec - 48;
                            else in_chd_1 = char_rec - 55;
                            in_status = 3;
                            break;
                        case 3:
                            if (char_rec < 58) in_chd_2 = char_rec - 48;
                            else in_chd_2 = char_rec - 55;
                            in_chd_1 = in_chd_1 * 16 + in_chd_2;
                            if (in_chd_1 == calculate_checksum(slv_buffer.getArray(), 1, slv_buff_i))
                                solve_message(slv_buff_i, true);
                            else solve_message(slv_buff_i, false);
                            in_status = 0;
                            break;
                    }
                    break;
            }
        }
    }


    private int encode_message() {
        if (msg_send_timer.GetCounter() > msg_send_period) {
            int len = create_message(out_msg_types_curr);
            if (len == 0) {
                out_msg_types_curr++;
                if (out_msg_types_curr >= msg_types_max) out_msg_types_curr = 0;
                return 0;
            }
            out_buffer.getArray()[0] = '@';
            int chs = calculate_checksum(out_buffer.getArray(), 1, len);
            len++;
            out_buffer.getArray()[len] = '~';
            len++;
            if (chs / 16 < 10) out_buffer.getArray()[len] = (byte) (48 + (chs / 16));
            else out_buffer.getArray()[len] = (byte) ((chs / 16) - 10 + 65);
            len++;
            if (chs % 16 < 10) out_buffer.getArray()[len] = (byte) (48 + (chs % 16));
            else out_buffer.getArray()[len] = (byte) ((chs % 16) - 10 + 65);
            return len + 1;
        } else return 0;
    }

    private void solve_message(int len, boolean correct) {
        slv_buffer.setIndex(1);
        byte type_identifier = (byte) (slv_buffer.getAtIndex() - '0');
        MSG_Parser.field_skip(slv_buffer);
        switch (type_identifier) {
            case 0://Barometric data
                if (!correct) return;
                drone_data.incoming.setPressure((int) MSG_Parser.field_get_long(slv_buffer));
                drone_data.incoming.setTemperature(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setBaro_alti(MSG_Parser.field_get_double(slv_buffer));
                break;
            case 1://Gps data
                if (!correct) return;
                drone_data.incoming.setGPS_lat(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setGPS_lon(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setGPS_fix((short) MSG_Parser.field_get_long(slv_buffer));
                drone_data.incoming.setGPS_alt(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setGPS_speed(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setGPS_sats((short) MSG_Parser.field_get_long(slv_buffer));
                drone_data.incoming.setGPS_PDOP(MSG_Parser.field_get_double(slv_buffer));
                break;
            case 2://Sets data
                if (!correct) return;
                drone_data.incoming.setHome_lat(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setHome_lon(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setLat_set(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setLon_set(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setAlt_set(MSG_Parser.field_get_double(slv_buffer));
                break;
            case 3://Imu data
                if (!correct) return;
                drone_data.incoming.setIMUX_set(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setIMUY_set(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setIMUZ_set(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setIMUX(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setIMUY(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setIMUZ(MSG_Parser.field_get_double(slv_buffer));
                break;
            case 4://Battery data
                if (!correct) return;
                drone_data.incoming.setVolt(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setCurr(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setEst_bat(MSG_Parser.field_get_double(slv_buffer));
                drone_data.incoming.setEst_rem(MSG_Parser.field_get_double(slv_buffer));
                break;
            case 5://Misc data
                if (!correct) return;
                drone_data.incoming.setMode((short) MSG_Parser.field_get_long(slv_buffer));
                drone_data.incoming.setSonar_state(MSG_Parser.field_get_long(slv_buffer) == 1);
                drone_data.incoming.setI2C_err(MSG_Parser.field_get_long(slv_buffer));
                drone_data.incoming.setTimer_err(MSG_Parser.field_get_long(slv_buffer));
                break;
            case 6://Debug data
                drone_data.debug.addDebug_in(MSG_Parser.field_get_String(slv_buffer));
                break;
        }
    }

    private int create_message(int type) {
        StringBuilder out = new StringBuilder();
        out.append('D');

        switch (type) {
            case 0:
                out.append("0CN#");
                out.append(drone_data.outgoing.getMode_set());
                out.append('#');
                out.append(drone_data.outgoing.getControl_x());
                out.append('#');
                out.append(drone_data.outgoing.getControl_y());
                out.append('#');
                out.append(drone_data.outgoing.getControl_zr());
                out.append('#');
                out.append(drone_data.outgoing.getControl_a());
                out.append('#');
                break;
            case 1:
                String debug_line_out = drone_data.debug.getDebug_line_out();
                if (debug_line_out.equals("") || debug_line_out.length() > out_buffer_size - 3) return 0;
                out.append("1DC#");
                out.append(debug_line_out);
                out.append('#');
                break;
            default:
                return 0;
        }
        System.arraycopy(out.toString().getBytes(), 0, out_buffer.getArray(), 0, out.length());
        return out.length();
    }

    private int calculate_checksum(byte[] buff, int begin, int end) {
        int chs = 0;
        for (int i = begin; i <= end; i++) {
            chs = chs ^ buff[i];
        }
        return chs;
    }

    private static class MSG_Parser {
        public static void field_skip(Buffer buffer) {
            while (buffer.getAtIndex() != '#') {
                buffer.setIndex(buffer.getIndex() + 1);
            }
            buffer.setIndex(buffer.getIndex() + 1);
        }

        public static long field_get_long(Buffer buffer) {
            long num = 0;
            while (buffer.getAtIndex() != '#') {
                num = num * 10 + (buffer.getAtIndex() - 48);
                buffer.setIndex(buffer.getIndex() + 1);
            }
            buffer.setIndex(buffer.getIndex() + 1);
            return num;
        }

        public static double field_get_double(Buffer buffer) {
            double num = 0;
            boolean neg = false;
            if (buffer.getAtIndex() == '-') {
                neg = true;
                buffer.setIndex(buffer.getIndex() + 1);
            }
            while (buffer.getAtIndex() != '.' && buffer.getAtIndex() != '#') {
                num = num * 10 + (buffer.getAtIndex() - 48);
                buffer.setIndex(buffer.getIndex() + 1);
            }
            if (buffer.getAtIndex() == '#') {
                buffer.setIndex(buffer.getIndex() + 1);
                if (neg)
                    num = -num;
                return num;
            }
            buffer.setIndex(buffer.getIndex() + 1);
            int div = 1;
            while (buffer.getAtIndex() != '#') {
                num = num * 10 + (buffer.getAtIndex() - 48);
                div *= 10;
                buffer.setIndex(buffer.getIndex() + 1);
                ;
            }
            num /= div;
            buffer.setIndex(buffer.getIndex() + 1);
            if (neg)
                num = -num;
            return num;
        }

        public static char field_get_char(Buffer buffer) {
            byte c = buffer.getAtIndex();
            field_skip(buffer);
            return (char) c;
        }

        public static String field_get_String(Buffer buffer) {
            StringBuilder ret = new StringBuilder();
            while (buffer.getAtIndex() != '#') {
                ret.append((char) buffer.getAtIndex());
                buffer.setIndex(buffer.getIndex() + 1);
            }
            buffer.setIndex(buffer.getIndex() + 1);
            return ret.toString();
        }
    }

}