/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.tools.forge;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * ColorManager.
 * 
 * @author Rob Cernich
 */
public class ColorManager {

    private Color _consoleColor;
    private Color _executionColor;

    /**
     * @return color used by console output.
     */
    public synchronized Color getConsoleColor() {
        if (_consoleColor == null) {
            _consoleColor = new Color(Display.getDefault(), new RGB(0, 0, 0));
        }
        return _consoleColor;
    }

    /**
     * @return color used for internally executed output.
     */
    public synchronized Color getExecutionColor() {
        if (_executionColor == null) {
            _executionColor = new Color(Display.getDefault(), new RGB(0, 0, 255));
        }
        return _executionColor;
    }

    /**
     * cleanup.
     */
    public synchronized void dispose() {
        if (_consoleColor != null) {
            _consoleColor.dispose();
            _consoleColor = null;
        }
        if (_executionColor != null) {
            _executionColor.dispose();
            _executionColor = null;
        }
    }
}
