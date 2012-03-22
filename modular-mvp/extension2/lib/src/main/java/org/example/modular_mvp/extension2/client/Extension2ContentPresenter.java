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
package org.example.modular_mvp.extension2.client;

import org.example.modular_mvp.base.client.presenters.MainPresenter;
import org.example.modular_mvp.extension2.client.Extension2ContentPresenter.Extension2ContentProxy;
import org.example.modular_mvp.extension2.client.Extension2ContentPresenter.Extension2ContentView;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

/**
 * Extension2ContentPresenter
 * 
 * <p/>
 * A content presenter that will be contributed as an extension to the base
 * application.
 * 
 * @author Rob Cernich
 */
public class Extension2ContentPresenter extends Presenter<Extension2ContentView, Extension2ContentProxy> {

    public interface Extension2ContentView extends View {
    }

    @ProxyCodeSplit
    @NameToken("extension2")
    public interface Extension2ContentProxy extends ProxyPlace<Extension2ContentPresenter> {
    }

    @Inject
    public Extension2ContentPresenter(EventBus eventBus, Extension2ContentView view, Extension2ContentProxy proxy,
            PlaceManager placeManager) {
        super(eventBus, view, proxy);
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), MainPresenter.TYPE_Content, this);
    }

}
