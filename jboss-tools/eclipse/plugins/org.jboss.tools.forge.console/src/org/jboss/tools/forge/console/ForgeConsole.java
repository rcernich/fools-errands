/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.jboss.tools.forge.console;

import javax.enterprise.event.Event;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.events.AcceptUserInput;
import org.jboss.forge.shell.events.PostStartup;
import org.jboss.forge.shell.events.PreStartup;
import org.jboss.forge.shell.events.Startup;
import org.jboss.tools.forge.Activator;
import org.osgi.cdi.api.integration.CDIContainer;

/**
 * ForgeConsole
 * 
 * A console for Forge.
 * 
 * @author Rob Cernich
 */
public class ForgeConsole extends IOConsole {

    private Shell _shell;
    private boolean _visible;
    private IOConsoleOutputStream _consoleOutputStream;
    private IOConsoleOutputStream _executionOutputStream;

    /**
     * Create a new ForgeConsole.
     * 
     * @throws CoreException if an error occurs initializing the shell.
     */
    public ForgeConsole() throws CoreException {
        super("Forge Console", Activator.FORGE_CONSOLE_TYPE, ImageDescriptor.getMissingImageDescriptor(), false);
        initializeForgeShell();
    }

    /**
     * Show the Forge console.
     */
    public void showConsole() {
        ensureExistence();
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(this);
    }

    /**
     * Close the Forge console.
     */
    public void closeConsole() {
        if (_visible) {
            IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
            manager.removeConsoles(new IConsole[] {this });
            // removed during the above call
            manager.addConsoleListener(new ConsoleLifecycleListener());
        }
    }

    /**
     * Dispose the console for good.
     * 
     * @throws CoreException if an error occurs.
     */
    public void shutdown() throws CoreException {
        if (_visible) {
            ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] {this });
        }
        try {
            Activator.getDefault().getForgeCDIContainer();
        } catch (Exception e) {
            throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error initializing Forge.", e));
        } finally {
            super.dispose();
        }
    }

    protected void init() {
        super.init();
        _visible = true;
    }

    @Override
    protected void dispose() {
        _visible = false;
    }

    private void initializeForgeShell() throws CoreException {
        if (_shell == null) {
            try {
                CDIContainer container = Activator.getDefault().getForgeCDIContainer();
                _shell = container.getInstance().select(Shell.class).get();

                if (_shell == null) {
                    throw new Exception("Could not locate Forge Shell implemetation.");
                }

                initializeStreams();

                Event<Object> event = container.getEvent();
                event.select(PreStartup.class).fire(new PreStartup());

                // set the streams
                _shell.setInputStream(getInputStream());
                _shell.setOutputStream(_consoleOutputStream);

                event.select(Startup.class).fire(new Startup(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), false));
                event.select(PostStartup.class).fire(new PostStartup());
                event.select(AcceptUserInput.class).fire(new AcceptUserInput());
            } catch (Exception e) {
                throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Error initializing Forge.", e));
            }
        }
    }

    private void initializeStreams() {
        _consoleOutputStream = newOutputStream();
        _executionOutputStream = newOutputStream();

        _consoleOutputStream.setColor(Activator.getDefault().getColorManager().getConsoleColor());
        _executionOutputStream.setColor(Activator.getDefault().getColorManager().getExecutionColor());

        // install font
        setFont(JFaceResources.getFontRegistry().get("pref_console_font")); //$NON-NLS-1$
    }

    private void ensureExistence() {
        if (_visible) {
            return;
        }
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {this });
    }

    private class ConsoleLifecycleListener implements org.eclipse.ui.console.IConsoleListener {

        public void consolesAdded(IConsole[] consoles) {
            for (int i = 0; i < consoles.length; i++) {
                IConsole console = consoles[i];
                if (console == ForgeConsole.this) {
                    init();
                }
            }

        }

        public void consolesRemoved(IConsole[] consoles) {
            for (int i = 0; i < consoles.length; i++) {
                IConsole console = consoles[i];
                if (console == ForgeConsole.this) {
                    ConsolePlugin.getDefault().getConsoleManager().removeConsoleListener(this);
                    dispose();
                }
            }
        }
    }

}
