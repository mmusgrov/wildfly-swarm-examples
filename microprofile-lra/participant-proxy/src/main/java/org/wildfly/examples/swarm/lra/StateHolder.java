package org.wildfly.examples.swarm.lra;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class StateHolder implements Serializable {
    private AtomicInteger completedCount = new AtomicInteger(0);
    private AtomicInteger compensatedCount = new AtomicInteger(0);

    public int getCompletedCount() {
        return completedCount.get();
    }

    public int incrCompletedCount() {
        return completedCount.incrementAndGet();
    }

    public int getCompensatedCount() {
        return compensatedCount.get();
    }

    public int incrCompensatedCount() {
         return compensatedCount.incrementAndGet();
    }
}
