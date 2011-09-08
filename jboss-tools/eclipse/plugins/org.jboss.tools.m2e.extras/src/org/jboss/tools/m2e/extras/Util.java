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
package org.jboss.tools.m2e.extras;

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.m2e.core.internal.lifecyclemapping.model.PluginExecutionMetadata;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.MojoExecutionKey;

public class Util {

    public static IPluginExecutionMetadata getPluginExecutionMetadata(String configuratorId,
            IMavenProjectFacade projectFacade, MojoExecution execution) {
        Map<MojoExecutionKey, List<IPluginExecutionMetadata>> executionMappings = projectFacade
                .getMojoExecutionMapping();
        if (executionMappings == null) {
            return null;
        }
        List<IPluginExecutionMetadata> executionMetadatas = executionMappings.get(new MojoExecutionKey(execution));
        if (executionMetadatas == null || executionMetadatas.size() == 0) {
            return null;
        }
        for (IPluginExecutionMetadata executionMetadata : executionMetadatas) {
            Xpp3Dom configuration = ((PluginExecutionMetadata) executionMetadata).getConfiguration();
            if (configuration == null) {
                continue;
            }
            Xpp3Dom id = configuration.getChild("id");
            if (id == null) {
                continue;
            }
            if (configuratorId == null || configuratorId.equals(id.getValue())) {
                return executionMetadata;
            }
        }
        return null;
    }

    private Util() {
    }

}
