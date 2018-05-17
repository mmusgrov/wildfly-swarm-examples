/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.examples.swarm.lra;

import org.eclipse.microprofile.lra.participant.JoinLRAException;
import org.eclipse.microprofile.lra.participant.LRAManagement;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * for testing {@link LRAManagement}
 */
@Path(LRAMgmtEgController.LRAM_PATH)
public class LRAMgmtEgController {
    static final String LRAM_PATH = "lram";
    static final String LRAM_WORK = "work";

    @Inject
    private LRAManagement lraManagement;

    @Inject
    private StateHolder stats;

    @PUT
    @Path(LRAM_WORK)
    public Response joinLRA(@QueryParam("lraId") String lraId) throws MalformedURLException, JoinLRAException {
        assert lraId != null;

        String rcvUrl = lraManagement.joinLRA(
                new Participant(stats), new URL(lraId), 0L, TimeUnit.SECONDS);

        return Response.ok(rcvUrl).build();
    }
}
