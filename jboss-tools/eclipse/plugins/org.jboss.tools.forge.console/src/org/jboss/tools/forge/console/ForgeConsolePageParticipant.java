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

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * ForgeConsolePageParticipant
 * 
 * IConsolePageParticipant implementation.
 * 
 * @author Rob Cernich
 */
public class ForgeConsolePageParticipant implements IConsolePageParticipant {

    private IAction _removeAction;

    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        return adapter.isInstance(this) ? this : null;
    }

    @Override
    public void init(IPageBookViewPage page, IConsole console) {
        _removeAction = new ForgeConsoleRemoveAction();
        page.getSite().getActionBars().getToolBarManager().appendToGroup(IConsoleConstants.LAUNCH_GROUP, _removeAction);
    }

    @Override
    public void dispose() {
        _removeAction = null;
    }

    @Override
    public void activated() {
    }

    @Override
    public void deactivated() {
    }

}
