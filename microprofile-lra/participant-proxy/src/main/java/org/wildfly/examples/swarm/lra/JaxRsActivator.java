package org.wildfly.examples.swarm.lra;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import io.narayana.lra.client.internal.proxy.ParticipantProxyResource;

@ApplicationPath("/")
public class JaxRsActivator extends Application {
    private final Set<Class<?>> classes = new HashSet<>();

    @Override
    public Set<Class<?>> getClasses() {
        classes.add(MyResource2.class);
        classes.add(LRAMgmtEgController.class);
        classes.add(ParticipantProxyResource.class);

        return classes;
    }
}
