package levtempfli.drone_ground;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ThreadTCPComm implements Runnable {
    private final int drone_port = 325;

    private final long mainloop_wait = 50;
    private final long connloop_wait = 10;
    private final int conn_msg_timeout = 5000;
    private final int msg_send_period = 100;
    private final int in_buffer_size = 64000;
    private final int out_buffer_size = 256;
    private final int slv_buffer_size = 4098;
    private final int msg_types_max = 3;

    private byte[] in_buffer = new byte[in_buffer_size];
    private byte[] out_buffer = new byte[out_buffer_size];
    private byte[] slv_buffer = new byte[slv_buffer_size];
    private ByteBuffer out_buffer_ByteBuffer = ByteBuffer.wrap(out_buffer);
    private ByteBuffer in_buffer_ByteBuffer = ByteBuffer.wrap(in_buffer);


    private int msg_types_curr = 1;
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
        int rec = 0;
        try {
            in_buffer_ByteBuffer.clear();
            rec = tcp_cl.read(in_buffer_ByteBuffer);
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
            out_buffer_ByteBuffer.clear();
            out_buffer_ByteBuffer.limit(to_st);
            try {
                sent = tcp_cl.write(out_buffer_ByteBuffer);
                if (sent == to_st) {
                    msg_send_timer.StartCounter();
                    msg_types_curr++;
                    if (msg_types_curr > msg_types_max) msg_types_curr = 1;
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
            char_rec = (char) in_buffer[i];
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
                            slv_buffer[slv_buff_i] = (byte) char_rec;
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
                            if (in_chd_1 == calculate_checksum(slv_buffer, 1, slv_buff_i))
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
            int len = create_message(msg_types_curr);
            out_buffer[0] = '@';
            int chs = calculate_checksum(out_buffer, 1, len);
            len++;
            out_buffer[len] = '~';
            len++;
            if (chs / 16 < 10) out_buffer[len] = (byte) (48 + (chs / 16));
            else out_buffer[len] = (byte) ((chs / 16) - 10 + 65);
            len++;
            if (chs % 16 < 10) out_buffer[len] = (byte) (48 + (chs % 16));
            else out_buffer[len] = (byte) ((chs % 16) - 10 + 65);
            return len + 1;
        } else return 0;
    }

    private void solve_message(int len, boolean correct) {
        System.out.print(correct);
        for (int i = 1; i <= len; i++) {
            System.out.print((char) slv_buffer[i]);//DEBUG
        }
        System.out.println();
    }

    private int create_message(int type) {
        switch (type) {
            case 1:
                System.arraycopy("Rhello1Z".getBytes(), 0, out_buffer, 0, 8);
                return 7;
            case 2:
                System.arraycopy("Rhello2Z".getBytes(), 0, out_buffer, 0, 8);
                return 7;
            case 3:
                System.arraycopy("Rhello3Z".getBytes(), 0, out_buffer, 0, 8);
                return 7;
        }
        return 0;
    }

    private int calculate_checksum(byte[] buff, int begin, int end) {
        int chs = 0;
        for (int i = begin; i <= end; i++) {
            chs = chs ^ buff[i];
        }
        return chs;
    }

}