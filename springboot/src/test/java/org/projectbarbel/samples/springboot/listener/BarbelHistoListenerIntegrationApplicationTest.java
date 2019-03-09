package org.projectbarbel.samples.springboot.listener;

import static org.junit.Assert.assertTrue;

import java.net.ConnectException;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.core.NestedCheckedException;

public class BarbelHistoListenerIntegrationApplicationTest {

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    @Test
    public void testDefaultSettings() throws Exception {
        try {
            BarbelHistoListenerIntegrationApplication.main(new String[0]);
        } catch (IllegalStateException ex) {
            if (serverNotRunning(ex)) {
                return;
            }
        }
        String output = this.outputCapture.toString();
        assertTrue("Wrong output: " + output, output.contains("clientId=1234"));
    }

    private boolean serverNotRunning(IllegalStateException ex) {
        @SuppressWarnings("serial")
        NestedCheckedException nested = new NestedCheckedException("failed", ex) {
        };
        if (nested.contains(ConnectException.class)) {
            Throwable root = nested.getRootCause();
            if (root.getMessage().contains("Connection refused")) {
                return true;
            }
        }
        return false;
    }
}
