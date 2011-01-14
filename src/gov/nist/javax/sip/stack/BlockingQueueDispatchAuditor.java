package gov.nist.javax.sip.stack;

import gov.nist.core.CommonLogger;
import gov.nist.core.LogLevels;
import gov.nist.core.StackLogger;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class BlockingQueueDispatchAuditor extends TimerTask {
	private Timer timer = new Timer();
	private static StackLogger logger = CommonLogger.getLogger(BlockingQueueDispatchAuditor.class);
    private long totalReject = 0;     
    private boolean started = false;
    private Queue<? extends Runnable> queue;
    private int timeout = 8000;
    public BlockingQueueDispatchAuditor(Queue<? extends Runnable> queue) {
    	this.queue = queue;
    }
    
    public void start(int interval) {
    	if(started) stop();
    	started = true;
    	timer = new Timer();
    	timer.scheduleAtFixedRate(this, interval, interval);
    }
    
    public int getTimeout() {
    	return timeout;
    }
    
    public void setTimeout(int timeout) {
    	this.timeout = timeout;
    }

    public void stop() {
    	try {
    		timer.cancel();
    		timer = null;
    	} catch (Exception e) {
    		//not important
    	} finally {
    		started = false;
    	}
    }

	public void run() {
		try {
			QueuedMessageDispatchBase runnable =(QueuedMessageDispatchBase) this.queue.peek();
			int removed = 0;
			while(runnable != null) {
				QueuedMessageDispatchBase d = (QueuedMessageDispatchBase) runnable;
				if(System.currentTimeMillis() - d.getReceptionTime() > timeout) {
					queue.poll();
					runnable = (QueuedMessageDispatchBase) this.queue.peek();
					removed ++;
				} else {
					runnable = null;
				}
			}
			if(removed>0) {
				totalReject+=removed;
				if(logger != null && logger.isLoggingEnabled(LogLevels.TRACE_WARN))
					logger.logWarning("Removed stuck messages=" + removed +
							" total rejected=" + totalReject + " stil in queue=" + this.queue.size());
			}
		} catch (Exception e) {
			if(logger != null && logger.isLoggingEnabled(LogLevels.TRACE_WARN)) {
				logger.logWarning("Problem reaping old requests. This is not a fatal error." + e);
			}
		}
	}
}