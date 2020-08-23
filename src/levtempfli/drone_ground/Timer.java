package levtempfli.drone_ground;

public class Timer {
    private long CounterStart = 0L;

    public void StartCounter() {
        CounterStart = System.currentTimeMillis();
    }

    public long GetCounter() {
        return System.currentTimeMillis() - CounterStart;
    }
}
