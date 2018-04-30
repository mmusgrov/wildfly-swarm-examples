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

    1 completed and 0 compensated

## Use

Since WildFly Swarm apps tend to support one deployment per executable, it
automatically adds a `jboss-web.xml` to the deployment if it doesn't already
exist.  This is used to bind the deployment to the root of the web-server,
instead of using the `.war`'s own name as the application context.

    http://localhost:8080/

Be aware that you will notice an exception in the logs when accessing the page.
This is simply an overly verbose message from WildFly that the 'favicon.ico' file couldn't be found.
