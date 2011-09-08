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

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.internal.lifecyclemapping.model.PluginExecutionMetadata;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

public class RefreshOutputDirectoryBuildParticipant extends MojoExecutionBuildParticipant {

    private BuildPathContributorProjectConfigurator configurator;
    private PluginExecutionMetadata pluginExecutionMetadata;

    public RefreshOutputDirectoryBuildParticipant(BuildPathContributorProjectConfigurator configurator,
            MojoExecution execution, PluginExecutionMetadata pluginExecutionMetadata) {
        super(execution, false);
        this.configurator = configurator;
        this.pluginExecutionMetadata = pluginExecutionMetadata;
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        if (appliesToBuildKind(kind)) {
            Set<IProject> result = super.build(kind, monitor);
            File generated = configurator.getSourceFolder(getSession(), getMojoExecution(), pluginExecutionMetadata);
            if (generated != null) {
                getBuildContext().refresh(generated);
            }
            return result;
        }
        return null;
    }

}
