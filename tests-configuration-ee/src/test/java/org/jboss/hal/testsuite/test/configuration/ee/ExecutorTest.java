/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.hal.testsuite.test.configuration.ee;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.creaper.ManagementClientProvider;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.EEPage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.JNDI_NAME;
import static org.jboss.hal.dmr.ModelDescriptionConstants.NAME;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.EXECUTOR_CREATE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.EXECUTOR_DELETE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.EXECUTOR_READ;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.EXECUTOR_UPDATE;
import static org.jboss.hal.testsuite.fixtures.EEFixtures.executorAddress;

@RunWith(Arquillian.class)
public class ExecutorTest {

    private static final OnlineManagementClient client = ManagementClientProvider.createOnlineManagementClient();
    private static final Operations operations = new Operations(client);

    @BeforeClass
    public static void beforeClass() throws Exception {
        operations.add(executorAddress(EXECUTOR_READ), Values.of(JNDI_NAME, Random.jndiName()));
        operations.add(executorAddress(EXECUTOR_UPDATE), Values.of(JNDI_NAME, Random.jndiName()));
        operations.add(executorAddress(EXECUTOR_DELETE), Values.of(JNDI_NAME, Random.jndiName()));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        operations.removeIfExists(executorAddress(EXECUTOR_CREATE));
        operations.removeIfExists(executorAddress(EXECUTOR_READ));
        operations.removeIfExists(executorAddress(EXECUTOR_UPDATE));
        operations.removeIfExists(executorAddress(EXECUTOR_DELETE));
    }

    @Inject private Console console;
    @Inject private CrudOperations crud;
    @Page private EEPage page;
    private TableFragment table;
    private FormFragment form;

    @Before
    public void setUp() throws Exception {
        page.navigate();
        console.verticalNavigation().selectSecondary(Ids.EE_SERVICES_ITEM, Ids.EE_MANAGED_EXECUTOR);

        table = page.getExecutorTable();
        form = page.getExecutorForm();
        table.bind(form);
    }

    @Test
    public void create() throws Exception {
        crud.create(executorAddress(EXECUTOR_CREATE), table, form -> {
            form.text(NAME, EXECUTOR_CREATE);
            form.text(JNDI_NAME, Random.jndiName());
        });
    }

    @Test
    public void update() throws Exception {
        table.select(EXECUTOR_UPDATE);
        crud.update(executorAddress(EXECUTOR_UPDATE), form, JNDI_NAME, Random.jndiName());
    }

    @Test
    public void reset() throws Exception {
        table.select(EXECUTOR_UPDATE);
        crud.reset(executorAddress(EXECUTOR_UPDATE), form);
    }

    @Test
    public void delete() throws Exception {
        crud.delete(executorAddress(EXECUTOR_DELETE), table, EXECUTOR_DELETE);
    }
}
