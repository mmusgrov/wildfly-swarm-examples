package org.wildfly.examples.swarm.lra;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import io.narayana.lra.client.internal.proxy.ParticipantProxyResource;
import org.eclipse.microprofile.lra.participant.LRAManagement;

@ApplicationPath("/")
public class JaxRsActivator2 extends Application {
    private final Set<Class<?>> classes = new HashSet<>();

    @Inject
    private LRAManagement lraManagement;

    @Inject
    private ParticipantDeserializer participantDeserializer;

    @PostConstruct
    private void postConstruct() {
        lraManagement.registerDeserializer(participantDeserializer);
    }

    @Override
    public Set<Class<?>> getClasses() {
        // TODO the lra swarm fraction should configure resteasy to scan dependent modules
        // for JAX-RS resources
        classes.add(MyResource2.class);
        classes.add(LRAMgmtEgController.class);
        classes.add(ParticipantProxyResource.class);

        return classes;
    }

    public void unregisterDeserializer() {
        lraManagement.unregisterDeserializer(participantDeserializer);
    }
}
