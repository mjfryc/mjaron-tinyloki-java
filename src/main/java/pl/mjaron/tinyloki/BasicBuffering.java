package pl.mjaron.tinyloki;

import java.util.ArrayList;
import java.util.List;

/**
 * Default buffering implementation. Provides a set of buffers which try to keep as mandy messages as possible.
 *
 * @since 0.4.0
 */
public class BasicBuffering implements IBuffering {

    private ILogCollector logCollector;
    private int maxMessageSize = IBuffering.DEFAULT_MAX_MESSAGE_SIZE;
    private int maxBuffersCount = IBuffering.DEFAULT_MAX_BUFFERS_COUNT;
    private IExecutor executor;
    private ILogMonitor logMonitor;

    private final List<byte[]> buffers = new ArrayList<>();
    private int alreadyBufferedLogsSize = 0;

    /**
     * Default constructor.
     *
     * @since 0.4.0
     */
    public BasicBuffering() {
    }

    /**
     * Allows configuring the object.
     *
     * @param maxMessageSize  Max allowed message size.
     * @param maxBuffersCount Max buffers count.
     * @since 0.4.0
     */
    public BasicBuffering(final int maxMessageSize, final int maxBuffersCount) {
        if (maxMessageSize > 0) {
            this.maxMessageSize = maxMessageSize;
        }
        if (maxBuffersCount > 0) {
            this.maxBuffersCount = maxBuffersCount;
        }
    }

    public void configure(final ILogCollector logCollector, final int maxMessageSize, final int maxBuffersCount, final IExecutor executor, final ILogMonitor logMonitor) {
        this.logCollector = logCollector;
        if (maxMessageSize > 0) {
            this.maxMessageSize = maxMessageSize;
        }
        if (maxBuffersCount > 0) {
            this.maxBuffersCount = maxBuffersCount;
        }
        this.executor = executor;
        this.logMonitor = logMonitor;
    }

    /**
     * Provides container of internal buffers.
     * For diagnostic and testing purposes only.
     * This method is not thread safe.
     *
     * @return Container of internal buffers.
     * @since 0.4.0
     */
    public List<byte[]> getBuffers() {
        return buffers;
    }

    private void addBuffer(final byte[] collected) {
        if (collected == null) {
            return;
        }
        buffers.add(collected);
        if (buffers.size() > maxBuffersCount) {
            logMonitor.logError("Removing buffer due to max buffer count overflow: [" + buffers.size() + "].");
            buffers.remove(0);
        }

        if (buffers.size() > 1) {
            executor.flush();
        }
    }

    @Override
    public boolean beforeLog(final int logCandidateSize) {
        if (logCandidateSize > maxMessageSize) {
            logMonitor.logError("Dropping the log due to size: [" + logCandidateSize + "] which exceeds max size: [" + maxMessageSize + "].");
            return false; // Ignore the log due to message size limit.
        }

        final int bufferCandidateSize = logCandidateSize + alreadyBufferedLogsSize;
        if (bufferCandidateSize > maxMessageSize) {
            alreadyBufferedLogsSize = 0;
            final byte[] collected = logCollector.collect();
            this.addBuffer(collected);
            return true; // Let's accept the message (to the new buffer).
        }

        // The log size is normal, and it doesn't exceed the max size, just accept the logged message.
        return true;
    }

    @Override
    public void logAccepted(final int logSize) {
        alreadyBufferedLogsSize += logSize;
    }

    @Override
    public byte[][] collectAll() {
        alreadyBufferedLogsSize = 0;
        final byte[] collected = logCollector.collect();
        this.addBuffer(collected);

        if (buffers.isEmpty()) {
            return null;
        }

        final byte[][] exported = new byte[buffers.size()][];
        for (int i = 0; i < buffers.size(); ++i) {
            exported[i] = buffers.get(i);
        }
        buffers.clear();
        return exported;
    }
}
