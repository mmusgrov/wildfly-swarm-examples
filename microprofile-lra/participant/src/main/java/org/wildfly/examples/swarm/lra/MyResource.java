package org.wildfly.examples.swarm.lra;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.lra.annotation.Compensate;
import org.eclipse.microprofile.lra.annotation.Complete;
import org.eclipse.microprofile.lra.annotation.LRA;
import org.eclipse.microprofile.lra.client.LRAClient;

import java.util.concurrent.atomic.AtomicInteger;

import static org.eclipse.microprofile.lra.client.LRAClient.LRA_HTTP_HEADER;
import static org.eclipse.microprofile.lra.client.LRAClient.LRA_HTTP_RECOVERY_HEADER;

@Path("/")
@LRA(LRA.Type.SUPPORTS)
public class MyResource {
    private static final AtomicInteger COMPLETED_COUNT = new AtomicInteger(0);
    private static final AtomicInteger COMPENSATED_COUNT = new AtomicInteger(0);

    @Inject
    private LRAClient lraClient;

    @Context
    private UriInfo context;

    @GET
    @Produces("text/plain")
    @LRA(LRA.Type.NOT_SUPPORTED)
    public String get() {
        return String.format("%d completed and %d compensated", COMPLETED_COUNT.get(), COMPENSATED_COUNT.get());
    }

    @GET
    @Path("/work")
    @Produces("text/plain")
    @LRA(LRA.Type.REQUIRED)
    public Response activityWithLRA(@HeaderParam(LRA_HTTP_RECOVERY_HEADER) String rcvId,
                                    @HeaderParam(LRA_HTTP_HEADER) String lraId) {
        return Response.ok("Check LRA finished using HTTP GET on " + context.getBaseUri().toString()).build();
    }

    @PUT
    @Path("/complete")
    @Produces(MediaType.APPLICATION_JSON)
    @Complete
    public Response completeWork(@HeaderParam(LRA_HTTP_HEADER) String lraId, String userData)
            throws NotFoundException {
        COMPLETED_COUNT.incrementAndGet();

        return Response.ok().build();
    }

    @PUT
    @Path("/compensate")
    @Produces(MediaType.APPLICATION_JSON)
    @Compensate
    public Response compensateWork(@HeaderParam(LRA_HTTP_HEADER) String lraId, String userData)
            throws NotFoundException {
        COMPENSATED_COUNT.incrementAndGet();

        return Response.ok().build();
    }
}
