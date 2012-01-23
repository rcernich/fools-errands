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
import org.jboss.ballroom.client.spi.Framework;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

/**
 * FrameworkImpl
 * 
 * Simple Framework implementation that serves up the EventBus and PlaceManager
 * registered in our Ginjector.
 * 
 * @author Rob Cernich
 */
@SuppressWarnings("deprecation")
public class FrameworkImpl implements Framework {

    // Using double indirection to retrieve the Ginjector implementation. Note,
    // this will return the same Ginjector instance that is in the EntryPoint.
    private BaseGinjector ginjector = GWT.<GinjectorSingleton> create(GinjectorSingleton.class).instance();

    @Override
    public EventBus getEventBus() {
        return ginjector.getEventBus();
    }

    @Override
    public PlaceManager getPlaceManager() {
        return ginjector.getPlaceManager();
    }

    @Override
    public AutoBeanFactory getBeanFactory() {
        return new AutoBeanFactory() {
            @Override
            public <T, U extends T> AutoBean<T> create(Class<T> clazz, U delegate) {
                return null;
            }

            @Override
            public <T> AutoBean<T> create(Class<T> clazz) {
                return null;
            }
        };
    }

}
