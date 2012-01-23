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

import org.example.modular_mvp.base.client.DefaultPlaceManager;
import org.example.modular_mvp.base.client.plugin.ExtensionManager;
import org.example.modular_mvp.base.client.presenters.BuiltInContentPresenter;
import org.example.modular_mvp.base.client.presenters.BuiltInContentPresenter.BuiltInContentProxy;
import org.example.modular_mvp.base.client.presenters.BuiltInContentPresenter.BuiltInContentView;
import org.example.modular_mvp.base.client.presenters.BuiltInContentViewImpl;
import org.example.modular_mvp.base.client.presenters.MainPresenter;
import org.example.modular_mvp.base.client.presenters.MainPresenter.MainPresenterProxy;
import org.example.modular_mvp.base.client.presenters.MainPresenter.MainPresenterView;
import org.example.modular_mvp.base.client.presenters.MainViewImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

/**
 * BaseModule
 * 
 * <p/>
 * Bindings for the base application. See {@link BaseGinjector} for details on
 * how to structure modules if you want to give extensions the ability to
 * override bindings (e.g. EventBus).
 * 
 * @author Rob Cernich
 */
public class BaseModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        // gwt platform requirements
        bind(EventBus.class).to(SimpleEventBus.class).asEagerSingleton();
        bind(PlaceManager.class).to(DefaultPlaceManager.class).asEagerSingleton();
        bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);
        bind(RootPresenter.class).asEagerSingleton();

        // our presenters
        bindPresenter(MainPresenter.class, MainPresenterView.class, MainViewImpl.class, MainPresenterProxy.class);
        bindPresenter(BuiltInContentPresenter.class, BuiltInContentView.class, BuiltInContentViewImpl.class,
                BuiltInContentProxy.class);

        // other bindings
        bind(ExtensionManager.class).in(Singleton.class);
    }

}
