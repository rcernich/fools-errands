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
package org.jboss.tools.forge;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.forge.console.ForgeConsole;
import org.osgi.cdi.api.integration.CDIContainer;
import org.osgi.cdi.api.integration.CDIContainerFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

    /** The PLUGIN_ID. */
    public static final String PLUGIN_ID = "org.jboss.tools.forge.console"; //$NON-NLS-1$
    /** The FORGE_CONSOLE_TYPE. */
    public static final String FORGE_CONSOLE_TYPE = PLUGIN_ID + ".ForgeConsoleType"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private CDIContainer _forgeCDIContainer;
    private ForgeConsole _forgeConsole;
    private ColorManager _colorManager;

    /**
     * The constructor.
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        _colorManager = new ColorManager();

        ServiceReference<CDIContainerFactory> cdiContainerFactoryServiceReference = context.getServiceReference(CDIContainerFactory.class);
        CDIContainerFactory cdiContainerFactory = context.getService(cdiContainerFactoryServiceReference);
        _forgeCDIContainer = cdiContainerFactory.container(getForgeRuntimeBundle(context));
        context.ungetService(cdiContainerFactoryServiceReference);
        //_forgeCDIContainer.initialize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        if (_forgeConsole != null) {
            _forgeConsole.shutdown();
            _forgeConsole = null;
        }
        _colorManager.dispose();
        _colorManager = null;
        _forgeCDIContainer.shutdown();
        _forgeCDIContainer = null;
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * @return the Weld container for this plug-in.
     */
    public CDIContainer getForgeCDIContainer() {
        return _forgeCDIContainer;
    }

    /**
     * @return the Forge console.
     */
    public synchronized ForgeConsole getForgeConsole() {
        if (_forgeConsole == null) {
            try {
                _forgeConsole = new ForgeConsole();
            } catch (CoreException e) {
                getLog().log(e.getStatus());
            }
        }
        return _forgeConsole;
    }

    /**
     * @return the ColorManager.
     */
    public ColorManager getColorManager() {
        return _colorManager;
    }

    private Bundle getForgeRuntimeBundle(BundleContext context) {
        for (Bundle bundle : context.getBundles()) {
            if ("org.jboss.tools.forge.runtime".equals(bundle.getSymbolicName())) {
                return bundle;
            }
        }
        return null;
    }
}
