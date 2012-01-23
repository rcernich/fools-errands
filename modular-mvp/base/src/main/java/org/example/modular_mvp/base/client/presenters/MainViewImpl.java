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

import org.example.modular_mvp.base.client.plugin.ExtensionManager;
import org.example.modular_mvp.base.client.plugin.ExtensionManager.NavigatorItem;
import org.example.modular_mvp.base.client.presenters.MainPresenter.MainPresenterView;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * MainViewImpl
 * 
 * <p/>
 * The View implementation associated with the MainPresenter. This
 * implementation uses the ExtensionManager to add entries to the navigator for
 * content supplied by extensions.
 * 
 * @author Rob Cernich
 */
public class MainViewImpl extends ViewImpl implements MainPresenterView {

    private DockLayoutPanel panel;
    private LayoutPanel contentPanel;

    /**
     * Create a new MainViewImpl.
     */
    @Inject
    public MainViewImpl(ExtensionManager extensionManager) {
        contentPanel = new LayoutPanel();
        contentPanel.setStyleName("main-content-panel");

        LayoutPanel headerPanel = new LayoutPanel();
        headerPanel.setStyleName("header-panel");

        LayoutPanel footerPanel = new LayoutPanel();
        footerPanel.setStyleName("footer-panel");

        panel = new DockLayoutPanel(Style.Unit.PX);
        panel.addNorth(headerPanel, 64);
        panel.addSouth(footerPanel, 30);
        panel.addWest(createNavigator(extensionManager), 200);
        panel.add(contentPanel);

        headerPanel.add(new ContentHeaderLabel("Modular MVP Example"));
    }

    public Widget asWidget() {
        return panel;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == MainPresenter.TYPE_Content) {
            contentPanel.clear();
            if (content != null) {
                contentPanel.add(content);
            }
        }
    }

    private Widget createNavigator(ExtensionManager extensionManager) {
        VerticalPanel stack = new VerticalPanel();
        stack.setStyleName("fill-layout");

        // Create a section for the default, built-in content.
        DisclosurePanel panel = new DisclosureStackPanel("Built-in", true).asWidget();
        LHSNavTree tree = new LHSNavTree("navigator");
        tree.addItem(new LHSNavTreeItem("Built-in Panel", "main/builtin"));
        panel.setContent(tree);
        stack.add(panel);

        // Create a section for extended content.
        panel = new DisclosureStackPanel("Extensions").asWidget();
        tree = new LHSNavTree("navigator");
        for (NavigatorItem item : extensionManager.getNavigatorItems()) {
            tree.addItem(new LHSNavTreeItem(item.getName(), "main/" + item.getToken()));
        }
        // only add the section if there are extensions.
        if (tree.getItemCount() > 0) {
            panel.setContent(tree);
            stack.add(panel);
        }

        return stack;
    }
}
