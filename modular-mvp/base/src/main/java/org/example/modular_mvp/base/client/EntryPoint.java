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
package org.example.modular_mvp.base.client;

import org.example.modular_mvp.base.client.gin.BaseGinjector;
import org.example.modular_mvp.base.client.gin.GinjectorSingleton;

import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

/**
 * EntryPoint
 * 
 * <p/>
 * Simple EntryPoint which creates the Ginjector, initializes GWT Platform MVP,
 * and reveals the default place.
 * 
 * @author Rob Cernich
 */
public class EntryPoint implements com.google.gwt.core.client.EntryPoint {

    @Override
    public void onModuleLoad() {
        // We're using double indirection so extensions can provide a Ginjector
        // that includes their presenters.
        BaseGinjector ginjector = GWT.<GinjectorSingleton> create(GinjectorSingleton.class).instance();
        // Bind the Ginjector (standard GWT platform stuff).
        DelayedBindRegistry.bind(ginjector);
        // Reveal the default place.
        ginjector.getPlaceManager().revealDefaultPlace();
    }

}
