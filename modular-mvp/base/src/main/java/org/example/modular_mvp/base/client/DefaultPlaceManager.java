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

import org.jboss.ballroom.client.util.Places;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

/**
 * DefaultPlaceManager
 * 
 * <p/>
 * Simple implementation PlaceManager implementation. Required for GWT Platform
 * MVP.
 * 
 * @author Rob Cernich
 */
public class DefaultPlaceManager extends PlaceManagerImpl {

    /**
     * Create a new DefaultPlaceManager.
     * 
     * @param eventBus
     * @param tokenFormatter
     */
    @Inject
    public DefaultPlaceManager(EventBus eventBus, TokenFormatter tokenFormatter) {
        super(eventBus, tokenFormatter);
    }

    @Override
    public void revealDefaultPlace() {
        revealPlaceHierarchy(Places.fromString("main/builtin"));
    }

}
