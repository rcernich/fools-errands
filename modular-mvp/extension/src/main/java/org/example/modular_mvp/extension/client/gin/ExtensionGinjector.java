/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.example.modular_mvp.extension.client.gin;

import org.example.modular_mvp.base.client.plugin.ExtensionDefinition;
import org.example.modular_mvp.base.client.plugin.ExtensionDefinition.NavigatorItem;
import org.example.modular_mvp.extension.client.ExtensionContentPresenter;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * ExtensionGinjector
 * 
 * <p/>
 * Ginjector definition for extended content. Note that only the presenters are
 * listed here as the core GWT Platform MVP requirements (e.g. EventBus) are defined on
 * BaseGinjector in the base project.
 * 
 * @author Rob Cernich
 */
@ExtensionDefinition(@NavigatorItem(name = "Extension", token = "extension"))
@GinModules(ExtensionModule.class)
public interface ExtensionGinjector extends Ginjector {

    /**
     * @return the "extension" content presenter.
     */
    public AsyncProvider<ExtensionContentPresenter> getExtensionContent();
}
