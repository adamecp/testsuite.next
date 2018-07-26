package org.jboss.hal.testsuite.test.deployment;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.creaper.ResourceVerifier;
import org.jboss.hal.testsuite.fragment.DialogFragment;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.WizardFragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.operations.Address;

import static org.jboss.hal.dmr.ModelDescriptionConstants.*;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class StandaloneDeploymentTest extends AbstractDeploymentTest {

    @Test
    public void uploadDeployment() throws Exception {
        page.navigate();
        Deployment deployment = createSimpleDeployment();

        WizardFragment wizard = page.uploadStandaloneDeployment();
        wizard.getUploadForm().uploadFile(deployment.getDeploymentFile());
        wizard.next(Ids.UPLOAD_NAMES_FORM);
        wizard.finishStayOpen();
        wizard.verifySuccess();
        wizard.close();

        new ResourceVerifier(deployment.getAddress(), client).verifyExists();
        assertTrue("The deployment does not contain expected " + INDEX_HTML + " file.",
                deploymentOps.deploymentContainsPath(deployment.getName(), INDEX_HTML));
    }

    @Test
    public void addUnmanagedDeployment() throws Exception {
        page.navigate();
        Deployment deployment = createSimpleDeployment();
        String
            nameValue = deployment.getName(),
            runtimeNameValue = Random.name(),
            pathValue = deployment.getDeploymentFile().getAbsolutePath();

        DialogFragment dialog = page.addUnmanagedStandaloneDeployment();
        FormFragment form = dialog.getForm(Ids.UNMANAGED_FORM);
        form.text(NAME, nameValue);
        form.text(RUNTIME_NAME, runtimeNameValue);
        form.text(PATH, pathValue);
        form.flip(ARCHIVE, true);
        dialog.primaryButton();

        new ResourceVerifier(deployment.getAddress(), client).verifyExists()
            .verifyAttribute(NAME, nameValue)
            .verifyAttribute(RUNTIME_NAME, runtimeNameValue);
        assertEquals(pathValue, deploymentOps.getDeploymentPath(nameValue));
        assertEquals(false, deploymentOps.deploymentIsExploded(nameValue));
    }

    @Test
    public void enableDeployment() throws Exception {
        String enableActionName = "Enable";
        Deployment deployment = createSimpleDeployment();
        ResourceVerifier deploymentVerifier = new ResourceVerifier(deployment.getAddress(), client);

        client.apply(deployment.deployEnabledCommand());
        deploymentVerifier.verifyExists().verifyAttribute(ENABLED, true);
        page.navigate();
        assertFalse(enableActionName + " action should not be available for already enabled deployment.",
                page.isActionOnStandaloneDeploymentAvailable(deployment.getName(), enableActionName));

        client.apply(deployment.disableCommand());
        deploymentVerifier.verifyExists().verifyAttribute(ENABLED, false);

        page.navigate();
        page.callActionOnStandaloneDeployment(deployment.getName(), "Enable");
        deploymentVerifier.verifyAttribute(ENABLED, true);
    }

    @Test
    public void disableDeployment() throws Exception {
        String disableActionName = "Disable";
        Deployment deployment = createSimpleDeployment();
        ResourceVerifier deploymentVerifier = new ResourceVerifier(deployment.getAddress(), client);

        client.apply(deployment.deployEnabledCommand());
        deploymentVerifier.verifyExists().verifyAttribute(ENABLED, true);

        page.navigate();
        page.callActionOnStandaloneDeployment(deployment.getName(), disableActionName);
        deploymentVerifier.verifyAttribute(ENABLED, false);

        page.navigate();
        assertFalse(disableActionName + " action should not be available for already disabled deployment.",
                page.isActionOnStandaloneDeploymentAvailable(deployment.getName(), disableActionName));
    }

    @Test
    public void explodeDeployment() throws Exception {
        String explodeActionName = "Explode";
        Deployment deployment = createSimpleDeployment();

        client.apply(deployment.deployEnabledCommand());
        new ResourceVerifier(deployment.getAddress(), client).verifyExists();

        page.navigate();
        assertFalse(explodeActionName + " action should not be available for enabled deployment.",
                page.isActionOnStandaloneDeploymentAvailable(deployment.getName(), explodeActionName));

        client.apply(deployment.disableCommand());
        assertFalse("Deployment should not be exploded yet.", deploymentOps.deploymentIsExploded(deployment.getName()));

        page.navigate();
        page.callActionOnStandaloneDeployment(deployment.getName(), explodeActionName);
        assertTrue("Deployment should be exploded.", deploymentOps.deploymentIsExploded(deployment.getName()));

        page.navigate();
        assertFalse(explodeActionName + " action should not be available for already exploded deployment.",
                page.isActionOnStandaloneDeploymentAvailable(deployment.getName(), explodeActionName));
    }

    @Test
    public void removeDeployment() throws Exception {
        Deployment deployment = createSimpleDeployment();
        ResourceVerifier deploymentVerifier = new ResourceVerifier(deployment.getAddress(), client);

        client.apply(deployment.deployEnabledCommand());
        deploymentVerifier.verifyExists();

        page.navigate();
        page.callActionOnStandaloneDeployment(deployment.getName(), "Remove");
        console.confirmationDialog().confirm();

        deploymentVerifier.verifyDoesNotExist();
    }

    @Test
    public void createEmptyDeployment() throws Exception {
        String deploymentName = createSimpleDeployment().getName();
        page.navigate();
        DialogFragment dialog = page.addEmptyStandaloneDeployment();
        dialog.getForm(Ids.DEPLOYMENT_EMPTY_FORM).text(NAME, deploymentName);
        dialog.primaryButton();
        new ResourceVerifier(Address.deployment(deploymentName), client).verifyExists();
    }

}