package org.wildfly.swarm.it.lra;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.swarm.it.AbstractIntegrationTest;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class LRAApplicationIT extends AbstractIntegrationTest {

    @Drone
    WebDriver browser;

    @Test
    public void testIt() {
        browser.navigate().to("http://localhost:8080/work");
        browser.navigate().to("http://localhost:8080");
        assertThat(browser.getPageSource()).contains("1 completed and 0 compensated");
    }
}
