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
package org.jboss.hal.testsuite.test.configuration.logging;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.fragment.FormFragment;
import org.jboss.hal.testsuite.fragment.TableFragment;
import org.jboss.hal.testsuite.page.configuration.LoggingConfigurationPage;
import org.junit.Before;
import org.junit.Test;
import org.wildfly.extras.creaper.core.online.operations.Address;
import static org.jboss.hal.dmr.ModelDescriptionConstants.LEVEL;
import static org.jboss.hal.testsuite.test.configuration.logging.LoggingFixtures.*;

public abstract class ConsoleHandlerAbstractTest {

    @Inject protected Console console;
    @Inject private CrudOperations crud;
    private TableFragment table;
    private FormFragment form;
    protected abstract LoggingConfigurationPage getPage();
    protected abstract Address getHandlerAddress(String name);
    protected abstract TableFragment getHandlerTable();
    protected abstract FormFragment getHandlerForm();
    protected abstract void navigateToPage();

    @Before
    public void navigate() throws Exception {
        navigateToPage();
        table = getPage().getConsoleHandlerTable();
        form = getPage().getConsoleHandlerForm();
        table.bind(form);
    }

    @Test
    public void create() throws Exception {
        crud.create(getHandlerAddress(ConsoleHandler.CONSOLE_HANDLER_CREATE), table, ConsoleHandler.CONSOLE_HANDLER_CREATE);
    }

    @Test
    public void update() throws Exception {
        table.select(ConsoleHandler.CONSOLE_HANDLER_UPDATE);
        crud.update(getHandlerAddress(ConsoleHandler.CONSOLE_HANDLER_UPDATE), form,
                f -> f.select(LEVEL, "CONFIG"),
                resourceVerifier -> resourceVerifier.verifyAttribute(LEVEL, "CONFIG"));
    }

    @Test
    public void reset() throws Exception {
        table.select(ConsoleHandler.CONSOLE_HANDLER_UPDATE);
        crud.reset(getHandlerAddress(ConsoleHandler.CONSOLE_HANDLER_UPDATE), form);
    }

    @Test
    public void delete() throws Exception {
        crud.delete(getHandlerAddress(ConsoleHandler.CONSOLE_HANDLER_DELETE), table, ConsoleHandler.CONSOLE_HANDLER_DELETE);
    }
}
