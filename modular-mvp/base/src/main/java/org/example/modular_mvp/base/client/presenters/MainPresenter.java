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
package org.example.modular_mvp.base.client.presenters;

import org.example.modular_mvp.base.client.presenters.MainPresenter.MainPresenterProxy;
import org.example.modular_mvp.base.client.presenters.MainPresenter.MainPresenterView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

/**
 * MainPresenter
 * 
 * <p/>
 * The main presenter for the application. It provides the navigator and content
 * area.
 * 
 * @author Rob Cernich
 */
public class MainPresenter extends Presenter<MainPresenterView, MainPresenterProxy> {

    public interface MainPresenterView extends View {
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_Content = new GwtEvent.Type<RevealContentHandler<?>>();

    @ProxyCodeSplit
    @NameToken("main")
    public interface MainPresenterProxy extends ProxyPlace<MainPresenter> {
    }

    @Inject
    public MainPresenter(EventBus eventBus, MainPresenterView view, MainPresenterProxy proxy, PlaceManager placeManager) {
        super(eventBus, view, proxy);
    }

    @Override
    protected void revealInParent() {
        RevealRootLayoutContentEvent.fire(getEventBus(), this);
    }

}
