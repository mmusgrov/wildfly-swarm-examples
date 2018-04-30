package org.wildfly.examples.swarm.lra;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.LRA;
import org.eclipse.microprofile.lra.client.LRAClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.eclipse.microprofile.lra.client.LRAClient.LRA_HTTP_HEADER;
import static org.eclipse.microprofile.lra.client.LRAClient.LRA_HTTP_RECOVERY_HEADER;
import static org.wildfly.examples.swarm.lra.LRAMgmtEgController.LRAM_PATH;
import static org.wildfly.examples.swarm.lra.LRAMgmtEgController.LRAM_WORK;

@Path("/")
@LRA(LRA.Type.SUPPORTS)
public class MyResource2 {

    @Inject
    private LRAClient lraClient;

    @Context
    private UriInfo context;

    @GET
    @Produces("text/plain")
    @LRA(LRA.Type.NOT_SUPPORTED)
    public String get() {
        return String.format("%d completed and %d compensated", Participant.getCompensatedCount(), Participant.getCompensatedCount());
    }

    @PostConstruct
    private void postConstruct() {
        int servicePort = Integer.getInteger("service.http.port", TEST_SWARM_PORT);

        try {
            microserviceBaseUrl = new URL("http://localhost:" + servicePort);

            // setting up the client
            msClient = ClientBuilder.newClient();

            msTarget = msClient.target(URI.create(new URL(microserviceBaseUrl, "/").toExternalForm()));
        } catch (MalformedURLException e) {
            System.err.printf("WARN: unabled to construct URL: %s%n", e.getMessage());
        }
    }


    private static Client msClient;
    private static final int TEST_SWARM_PORT = 8080;
    private static URL microserviceBaseUrl;

    private WebTarget msTarget;

    @GET
    @Path("/work")
    @Produces("text/plain")
    public Response testLRAMgmt() {
        URL lraId = lraClient.startLRA("testStartLRA", 120L, TimeUnit.SECONDS);

        Response response = msTarget.path(LRAM_PATH).path(LRAM_WORK)
                .queryParam("lraId", lraId.toExternalForm())
                .request().put(Entity.text(""));

        String joinUrl = checkStatusAndClose(response, Response.Status.OK.getStatusCode(), true);

        try {
            lraClient.closeLRA(lraId);
        } catch (Error e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Close LRA failed: " + e.getMessage()).build();
        }

        return Response.ok(String.format("%d completed and %d compensated",
                Participant.getCompensatedCount(), Participant.getCompensatedCount())).build();
    }

    private String checkStatusAndClose(Response response, int expected, boolean readEntity) {
        try {
            if (expected != -1 && response.getStatus() != expected)
                throw new WebApplicationException(response);

            if (readEntity)
                return response.readEntity(String.class);
        } finally {
            response.close();
        }

        return null;
    }
}
