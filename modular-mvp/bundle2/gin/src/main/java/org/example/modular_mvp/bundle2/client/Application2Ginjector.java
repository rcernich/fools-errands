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
package org.example.modular_mvp.bundle2.client;

import org.example.modular_mvp.base.client.gin.BaseGinjector;
import org.example.modular_mvp.extension2.client.gin.Extension2Ginjector;

/**
 * Application2Ginjector
 * 
 * <p/>
 * This interface is used to mix-in the base and extended Ginjector definitions.
 * The modules are defined on the respective Ginjector definitions.
 * 
 * <p/>
 * If the module configured on the base Ginjector does not include bindings for
 * some elements (e.g. EventBus), a module configuring bindings for those items
 * must be included here. See {@link BaseGinjector} for more details on how
 * Ginjector definitions should be configured so extended applications can
 * override specific bindings.
 * 
 * @author Rob Cernich
 */
public interface Application2Ginjector extends BaseGinjector, Extension2Ginjector {

}
