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

import org.example.modular_mvp.extension2.client.Extension2ContentPresenter.Extension2ContentView;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * Extension2ContentViewImpl
 * 
 * <p/>
 * View implementation associated with Extension2ContentPresenter.
 * 
 * @author Rob Cernich
 */
public class Extension2ContentViewImpl extends ViewImpl implements Extension2ContentView {

    @Override
    public Widget asWidget() {
        Panel panel = new LayoutPanel();
        panel.add(new ContentHeaderLabel("Extension2 Content"));
        return panel;
    }

}
