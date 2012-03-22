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
package org.example.modular_mvp.base.client.gin;

import org.example.modular_mvp.base.client.presenters.BuiltInContentPresenter;
import org.example.modular_mvp.base.client.presenters.MainPresenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

/**
 * BaseGinjector
 * 
 * <p/>
 * Base injection/MVP resources. All versions of any application will have this
 * functionality.
 * 
 * <p/>
 * Note, if you wish to give extensions the ability to override bindings for any
 * of these (e.g. EventBus), they cannot be bound in BaseModule. You will need
 * to split those bindings out into a separate module. That module can then be
 * included in the applications Ginjector definition.
 * 
 * @author Rob Cernich
 */
@GinModules(BaseModule.class)
public interface BaseGinjector extends Ginjector {

    /**
     * @return EventBus used by platform MVP.
     */
    public EventBus getEventBus();

    /**
     * @return PlaceManager used by platform MVP.
     */
    public PlaceManager getPlaceManager();

    /**
     * @return the main presenter.
     */
    public AsyncProvider<MainPresenter> getMainPresenter();

    /**
     * @return the "built-in" content presenter.
     */
    public AsyncProvider<BuiltInContentPresenter> getBuiltInContent();
}
