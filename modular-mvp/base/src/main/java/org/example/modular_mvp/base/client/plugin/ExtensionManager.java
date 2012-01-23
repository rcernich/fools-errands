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
package org.example.modular_mvp.base.client.plugin;

import java.util.List;

/**
 * ExtensionManager
 * 
 * <p/>
 * Provides access to all the registered items.
 * 
 * @author Rob Cernich
 */
public interface ExtensionManager {

    /**
     * @return the list of registered navigator extensions.
     */
    public List<NavigatorItem> getNavigatorItems();

    /**
     * A simple structure for providing extension information.
     */
    public class NavigatorItem {

        private String name;
        private String token;

        public NavigatorItem(String name, String token) {
            this.name = name;
            this.token = token;
        }

        /**
         * @return the display name.
         */
        public String getName() {
            return name;
        }

        /**
         * @return the place token.
         */
        public String getToken() {
            return token;
        }

    }
}
