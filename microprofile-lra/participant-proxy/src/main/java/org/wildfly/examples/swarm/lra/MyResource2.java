package org.wildfly.examples.swarm.lra;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.lra.client.LRAClient;
import org.eclipse.microprofile.lra.participant.LRAManagement;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.wildfly.examples.swarm.lra.LRAMgmtEgController.LRAM_PATH;
import static org.wildfly.examples.swarm.lra.LRAMgmtEgController.LRAM_WORK;

@Path("/")
@ApplicationScoped
public class MyResource2 {

    @Inject
    StateHolder stats;

    @Inject
    private LRAManagement lraManagement;

    @Inject
    private LRAClient lraClient;

    @Inject
    private JaxRsActivator2 application;

    private static Client msClient;
    private WebTarget msTarget;

    @GET
    @Produces("text/plain")
    public String get() {
        return String.format("%d completed and %d compensated", stats.getCompletedCount(), stats.getCompensatedCount());
    }

    @PostConstruct
    private void postConstruct() {
        int servicePort = Integer.getInteger("service.http.port", 8080);

        try {
            URL microserviceBaseUrl = new URL("http://localhost:" + servicePort);

            // setting up the client
            msClient = ClientBuilder.newClient();

            msTarget = msClient.target(URI.create(new URL(microserviceBaseUrl, "/").toExternalForm()));
        } catch (MalformedURLException e) {
            System.err.printf("WARN: unabled to construct URL: %s%n", e.getMessage());
        }
    }

    @PreDestroy
    private void preDestroy() {
        application.unregisterDeserializer();
    }

    @GET
    @Path("/work")
    @Produces("text/plain")
    public Response testLRAMgmt() {
        int requestCount = 2;
        URL lraId = lraClient.startLRA("testStartLRA", 120L, TimeUnit.SECONDS);
        int completionCount = stats.getCompletedCount();
        IntStream.rangeClosed(1, requestCount).forEach(i -> doWork(lraId));

        try {
            lraClient.closeLRA(lraId);
        } catch (Error e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Close LRA failed: " + e.getMessage()).build();
        }

        if (completionCount + requestCount != stats.getCompletedCount())
            return Response.ok(String.format("%d completions expected but only %s",
                    completionCount + requestCount, get())).build();

        return Response.ok(get()).build();
    }

    private void doWork(URL lraId) {
        Response response = msTarget.path(LRAM_PATH).path(LRAM_WORK)
                .queryParam("lraId", lraId.toExternalForm())
                .request().put(Entity.text(""));

        try {
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new WebApplicationException(response);
            }
        } finally {
            response.close();
        }
    }
}
