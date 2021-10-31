package pl.mjaron.tinyloki;

public class ErrorLogMonitor implements ILogMonitor {
    @Override
    public void send(final byte[] message) {
    }

    @Override
    public void sendOk(final int status) {
    }

    @Override
    public void sendErr(final int status, final String message) {
        System.err.println("Unexpected server response status: " + status + ": " + message);
    }

    @Override
    public void onException(Exception exception) {
        exception.printStackTrace();
    }
}
