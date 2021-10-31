package pl.mjaron.tinyloki;

import java.util.Map;

public class LogController {

    private static final long LOG_WAIT_TIME = 100;
    private static final long EXIT_WAIT_TIME = 200;

    private final ILogCollector logCollector;
    private final LogSender logSender;
    private Thread workerThread = null;
    private boolean softFinishing = false;

    public LogController(final ILogCollector logCollector, final LogSender logSender) {
        this.logCollector = logCollector;
        this.logSender = logSender;
        this.logSender.getSettings().setContentType(logCollector.contentType());
    }

    public ILogStream createStream(Map<String, String> labels) {
        return logCollector.createStream(labels);
    }

    public LogController start() {
        //noinspection AnonymousHasLambdaAlternative
        workerThread = new Thread() {
            @Override
            public void run() {
                workerLoop();
            }
        };
        workerThread.start();
        return this;
    }

    synchronized public LogController softStop() {
        softFinishing = true;
        return this;
    }

    public boolean waitForStop() {
        return waitForStop(EXIT_WAIT_TIME);
    }

    public boolean waitForStop(final long timeout) {
        try {
            workerThread.join(timeout);
            return (workerThread.getState() == Thread.State.TERMINATED);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void workerLoop() {
        boolean doLastCheck = false;
        while (true) {
            synchronized (this) {
                if (softFinishing) {
                    doLastCheck = true;
                }
            }
            boolean anyLogs = false;
            try {
                if (doLastCheck) {
                    anyLogs = logCollector.waitForLogs(1);
                }
                else {
                    anyLogs = logCollector.waitForLogs(LOG_WAIT_TIME);
                }
            } catch (InterruptedException e) {
                return;
            }

            if (anyLogs) {
                final byte[] logs = logCollector.collect();
                if (logs != null) {
                    logSender.send(logs);
                }
            }
            if (doLastCheck) {
                return;
            }
        }
    }
}
