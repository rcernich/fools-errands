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
package org.example.modular_mvp.base.rebind;

import java.util.ArrayList;
import java.util.List;

import org.example.modular_mvp.base.client.plugin.ExtensionDefinition;
import org.example.modular_mvp.base.client.plugin.ExtensionManager;

/**
 * ExtensionManagerImpl
 * 
 * Example implementation for generator. This is what the implementation created
 * by ExtensionManagerGenerator should look like.
 * 
 * @author Rob Cernich
 */
public class ExtensionManagerImpl implements ExtensionManager {

    private final List<NavigatorItem> items = new ArrayList<NavigatorItem>();

    /**
     * Create a new ExtensionManagerImpl.
     */
    @SuppressWarnings("null")
    public ExtensionManagerImpl() {
        ExtensionDefinition exdef = null;
        for (ExtensionDefinition.NavigatorItem item : exdef.value()) {
            // The generator will be adding these statements. The above code
            // represents the structure used by the generator for iterating the
            // extension annotations.
            items.add(new NavigatorItem(item.name(), item.token()));
        }
    }

    @Override
    public List<NavigatorItem> getNavigatorItems() {
        return items;
    }

}
