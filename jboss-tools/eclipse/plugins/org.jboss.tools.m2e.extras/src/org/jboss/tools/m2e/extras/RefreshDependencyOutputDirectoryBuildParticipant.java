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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

/**
 * RefreshDependencyOutputDirectoryBuildParticipant
 * 
 * Refreshes the output folder added by a maven-dependency-plugin execution.
 * 
 * @author Rob Cernich
 */
public class RefreshDependencyOutputDirectoryBuildParticipant extends MojoExecutionBuildParticipant {

    public RefreshDependencyOutputDirectoryBuildParticipant(MojoExecution execution) {
        super(execution, false);
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        if (appliesToBuildKind(kind)) {
            Set<IProject> result = super.build(kind, monitor);
            File[] outputFolders = getOutputFolders();
            if (outputFolders != null) {
                for (File sourceFolder : outputFolders) {
                    if (sourceFolder != null) {
                        getBuildContext().refresh(sourceFolder);
                    }
                }
            }
            return result;
        }
        return null;
    }

    private File[] getOutputFolders() throws CoreException {
        Xpp3Dom outputDirectoryDom = getMojoExecution().getConfiguration().getChild(
                DependencyUnpackProjectConfigurator.OUTPUT_DIRECTORY);
        if (outputDirectoryDom == null || !"${outputDirectory}".equals(outputDirectoryDom.getValue())) {
            return new File[] { getOutputFolder(getMojoExecution().getConfiguration()) };
        }
        Xpp3Dom artifactItemsDom = getMojoExecution().getConfiguration().getChild("artifactItems");
        if (artifactItemsDom == null) {
            return new File[0];
        }
        Xpp3Dom[] artifactItemDoms = artifactItemsDom.getChildren("artifactItem");
        if (artifactItemDoms == null || artifactItemDoms.length == 0) {
            return new File[0];
        }
        List<File> sourceFolders = new ArrayList<File>(artifactItemDoms.length);
        for (Xpp3Dom artifactItemDom : artifactItemDoms) {
            PluginExecution pluginExecution = new PluginExecution();
            pluginExecution.setConfiguration(artifactItemDom);
            File sourceFolder = getOutputFolder(artifactItemDom);
            if (sourceFolder != null) {
                sourceFolders.add(sourceFolder);
            }
        }
        return sourceFolders.toArray(new File[0]);
    }

    private File getOutputFolder(Object configuration) throws CoreException {
        PluginExecution pluginExecution = new PluginExecution();
        pluginExecution.setConfiguration(configuration);
        return MavenPlugin.getMaven()
                .getMojoParameterValue(DependencyUnpackProjectConfigurator.OUTPUT_DIRECTORY, File.class, getSession(),
                        getMojoExecution().getPlugin(), pluginExecution, getMojoExecution().getGoal());
    }
}
