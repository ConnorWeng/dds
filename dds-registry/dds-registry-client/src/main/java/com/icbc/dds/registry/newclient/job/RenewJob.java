package com.icbc.dds.registry.newclient.job;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.icbc.dds.registry.newclient.common.Constants;

public class RenewJob {
	private Timer timer;
	private String timerName = Constants.DEFAULT_JOB_NAME;
	private boolean isDaemon = true;
	private TimerTask renewTask;
	private long delay = Constants.DEFAULT_DELAY;
	private long period = Constants.DEFAULT_PERIOD;

	public RenewJob(TimerTask renewTask) {
		this.renewTask = renewTask;
		this.timer = new Timer(timerName, isDaemon);
	}

	public void start() {
		this.timer.scheduleAtFixedRate(renewTask, TimeUnit.SECONDS.toMillis(delay), TimeUnit.SECONDS.toMillis(period));
	}

	public void close() {
		this.timer.cancel();
	}

	public String getTimerName() {
		return timerName;
	}

	public void setTimerName(String timerName) {
		this.timerName = timerName;
	}

	public boolean isDaemon() {
		return isDaemon;
	}

	public void setDaemon(boolean isDaemon) {
		this.isDaemon = isDaemon;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}
}
