# MicroProfile .war Example

This example takes a normal [MicroProfile](http://microprofile.io) WAR, and wraps it into
a `-swarm` runnable jar.

> Please raise any issues found with this example in our JIRA:
> https://issues.jboss.org/browse/SWARM

## Project `pom.xml`

This project is a traditional WAR project, with maven packaging
of `war` in the `pom.xml`

    <packaging>war</packaging>

The project adds a `<plugin>` to configure `wildfly-swarm-plugin` to
create the runnable `.jar`.

    <plugin>
      <groupId>org.wildfly.swarm</groupId>
      <artifactId>wildfly-swarm-plugin</artifactId>
      <version>${version.wildfly-swarm}</version>
      <executions>
        <execution>
          <goals>
            <goal>package</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

To define the needed parts of WildFly Swarm, a dependency is added

    <dependency>
        <groupId>org.wildfly.swarm</groupId>
        <artifactId>microprofile-lra</artifactId>
        <version>${version.wildfly-swarm}</version>
    </dependency>

This dependency provides the APIs that are part of MicroProfile LRA
to your application, so the project does *not* need to specify those.

Additional application dependencies can be
specified and will be included in the normal `.war` construction and available
within the WildFly Swarm application `.jar`.

## Run

* mvn clean package && java -Dswarm.http.port=8080 -Dlra.http.port=8082 -jar target/example-microprofile-lra-swarm.jar

The example requires an exteral LRA coordinator to be running on port 8082
To start a compensatable send the HTTP request

    curl http://localhost:8080/work

and to check wether or not the resource was told to complete send the HTTP request

    curl http://localhost:8080

and expect the response

    2 completed and 0 compensated

To test that recovery works start the coordinator using a data directory that persists over
restarts (for example put it in /tmp/txlog):

 java -jar rts/lra/lra-coordinator/target/lra-coordinator-swarm.jar -Dswarm.http.port=8082 -Dswarm.transactions.object-store-path=../txlog &

Now either generate a log for recovery purposes, or use the one in this example directory
0_ffff0a3f00f9_21bb4791_5afdb3c3_14, if you are using this log then you will need to copy it
to the relevant directory in the transaction log store:
`cp 20_ffff0a3f00f9_21bb4791_5afdb3c3_14 /tmp/txlog/ShadowNoFileLockStore/defaultStore/StateManager/BasicAction/TwoPhaseCoordinator/LRA/`

To test recovery of the coordinator start the LRA coordinator with this entry in its logs.

The coordinator periodically runs a recovery scan and tries to finish the pending LRA.
You should see out put similar to the following showing that the participants have been asked to complete:

> 2018-05-17 18:01:14,999 INFO  [stdout] (default task-28) 9 participant completions
> 2018-05-17 18:01:15,023 INFO  [stdout] (default task-29) 10 participant completions                      
> 2018-05-17 18:01:15,044 INFO  [stdout] (default task-30) 11 participant completions                      
> 2018-05-17 18:01:15,065 INFO  [stdout] (default task-31) 12 participant completions                      

If you want to trigger a recovery scan immediately type the following:

> curl http://localhost:8082/lra-recovery-coordinator/recovery

This demonstrates recovery of the coordinator. To test recovery of participants. Just start the example and observe
that the coordinator attempts completion of the participants.
