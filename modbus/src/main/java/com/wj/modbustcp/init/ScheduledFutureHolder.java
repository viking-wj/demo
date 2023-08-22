package com.wj.modbustcp.init;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by IntelliJ IDEA
 *
 * @author wj
 * @date 2023/8/22 13:48
 */
public class ScheduledFutureHolder {
    private ScheduledFuture<?> scheduledFuture;

    private Class<? extends Runnable> runnableClass;

    private String corn;

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public Class<? extends Runnable> getRunnableClass() {
        return runnableClass;
    }

    public void setRunnableClass(Class<? extends Runnable> runnableClass) {
        this.runnableClass = runnableClass;
    }

    public String getCorn() {
        return corn;
    }

    public void setCorn(String corn) {
        this.corn = corn;
    }

    @Override
    public String toString() {
        return "ScheduledFutureHolder{" +
                "scheduledFuture=" + scheduledFuture +
                ", runnableClass=" + runnableClass +
                ", corn='" + corn + '\'' +
                '}';
    }
}
