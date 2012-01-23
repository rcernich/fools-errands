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
package org.example.modular_mvp.base.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.example.modular_mvp.base.client.plugin.ExtensionDefinition;
import org.example.modular_mvp.base.client.plugin.ExtensionManager;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * ExtensionManagerGenerator
 * 
 * <p/>
 * Simple generator that builds an ExtensionManager implementation that serves
 * up NavigatorItems for extensions using the ExtensionDefinition annotation.
 * 
 * @author Rob Cernich
 */
public class ExtensionManagerGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
            throws UnableToCompleteException {
        TypeOracle typeOracle = context.getTypeOracle();
        JClassType extensionManager = typeOracle.findType(typeName);
        if (extensionManager == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
            throw new UnableToCompleteException();
        }
        if (extensionManager.isInterface() == null) {
            logger.log(TreeLogger.ERROR, extensionManager.getQualifiedSourceName() + " is not an interface", null);
            throw new UnableToCompleteException();
        }

        List<ExtensionDefinition> extensions = new ArrayList<ExtensionDefinition>();
        for (JClassType type : typeOracle.getTypes()) {
            if (type.isAnnotationPresent(ExtensionDefinition.class)) {
                extensions.add(type.getAnnotation(ExtensionDefinition.class));
            }
        }

        String packageName = extensionManager.getPackage().getName();
        String className = extensionManager.getSimpleSourceName() + "Impl";

        generateClass(logger, context, packageName, className, extensions);

        return packageName + "." + className;
    }

    private void generateClass(TreeLogger logger, GeneratorContext context, String packageName, String className,
            List<ExtensionDefinition> extensions) {
        PrintWriter pw = context.tryCreate(logger, packageName, className);
        if (pw == null) {
            return;
        }

        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);

        // imports
        composerFactory.addImport(ArrayList.class.getCanonicalName());
        composerFactory.addImport(List.class.getCanonicalName());
        composerFactory.addImport(ExtensionDefinition.class.getCanonicalName());
        composerFactory.addImport(ExtensionManager.class.getCanonicalName());

        // interface
        composerFactory.addImplementedInterface(ExtensionManager.class.getCanonicalName());

        SourceWriter sw = composerFactory.createSourceWriter(context, pw);

        // begin class definition
        sw.indent();

        // fields
        sw.println("private final List<NavigatorItem> items = new ArrayList<NavigatorItem>();");

        // constructor
        sw.println("public " + className + "() {");
        sw.indent();
        for (ExtensionDefinition extension : extensions) {
            for (ExtensionDefinition.NavigatorItem item : extension.value()) {
                sw.println("items.add(new NavigatorItem(\"%s\", \"%s\"));", escape(item.name()), escape(item.token()));
            }
        }
        sw.outdent();
        sw.println("}");

        // methods
        // getNavigatorItems
        sw.println("public List<NavigatorItem> getNavigatorItems() {");
        sw.indentln("return items;");
        sw.println("}");

        // close it out
        sw.outdent();
        sw.println("}");

        context.commit(logger, pw);
    }
}
