/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

import org.eclipse.microprofile.lra.participant.LRAParticipant;
import org.eclipse.microprofile.lra.participant.TerminationException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.NotFoundException;
import java.io.Serializable;
import java.net.URL;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Participant implements LRAParticipant, Serializable {

    private StateHolder stats;

    public Participant(StateHolder stats) {
        this.stats = stats;
    }

    public int getCompletedCount() {
        return stats.getCompletedCount();
    }

    public int getCompensatedCount() {
        return stats.getCompensatedCount();
    }

    public Future<Void> completeWork(URL lraId) throws NotFoundException, TerminationException {
        int cnt = stats.incrCompletedCount();

        System.out.printf("%d participant completions%n", cnt);

        return null;
    }

    public Future<Void> compensateWork(URL lraId) throws NotFoundException, TerminationException {
        int cnt = stats.incrCompensatedCount();

        System.out.printf("%d participant completions%n", cnt);

        return null;
    }
}
