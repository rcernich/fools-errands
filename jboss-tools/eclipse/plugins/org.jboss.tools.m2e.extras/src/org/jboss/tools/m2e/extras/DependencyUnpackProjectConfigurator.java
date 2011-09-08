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

import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

/**
 * DependencyUnpackProjectConfigurator
 * 
 * Configures source and resource folders based on the outputDirectory used in a
 * maven-dependency-plugin unpack execution.
 * 
 * @author Rob Cernich
 */
public class DependencyUnpackProjectConfigurator extends AbstractJavaProjectConfigurator {

    public static final String OUTPUT_DIRECTORY = "outputDirectory";

    @Override
    public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath,
            IProgressMonitor monitor) throws CoreException {
        IMavenProjectFacade facade = request.getMavenProjectFacade();

        assertHasNature(request.getProject(), JavaCore.NATURE_ID);

        for (MojoExecution mojoExecution : getMojoExecutions(request, monitor)) {
            String lifecyclePhase = mojoExecution.getLifecyclePhase();
            if (lifecyclePhase == null || lifecyclePhase.isEmpty()) {
                continue;
            }
            if (lifecyclePhase.endsWith("-sources")) {
                File[] sources = getSourceFolders(request, mojoExecution);

                for (File source : sources) {
                    IPath sourcePath = getFullPath(facade, source);

                    if (sourcePath != null && !classpath.containsPath(sourcePath)) {
                        classpath.addSourceEntry(
                                sourcePath,
                                lifecyclePhase.contains("-test-") ? facade.getTestOutputLocation() : facade
                                        .getOutputLocation(), true);
                    }
                }
            } else if (lifecyclePhase.endsWith("-resources")) {
                File[] sources = getSourceFolders(request, mojoExecution);

                for (File source : sources) {
                    IPath sourcePath = getFullPath(facade, source);

                    if (sourcePath != null && !classpath.containsPath(sourcePath)) {
                        classpath.addSourceEntry(
                                sourcePath,
                                lifecyclePhase.contains("-test-") ? facade.getTestOutputLocation() : facade
                                        .getOutputLocation(), new IPath[0], new IPath[] { new Path("**") }, true);
                    }
                }
            }
        }
    }

    @Override
    public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution,
            IPluginExecutionMetadata executionMetadata) {
        return new RefreshDependencyOutputDirectoryBuildParticipant(execution);
    }

    @Override
    protected File[] getSourceFolders(ProjectConfigurationRequest request, MojoExecution execution)
            throws CoreException {
        Xpp3Dom outputDirectoryDom = execution.getConfiguration().getChild(OUTPUT_DIRECTORY);
        if (outputDirectoryDom == null || !"${outputDirectory}".equals(outputDirectoryDom.getValue())) {
            return super.getSourceFolders(request, execution);
        }
        Xpp3Dom artifactItemsDom = execution.getConfiguration().getChild("artifactItems");
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
            File sourceFolder = MavenPlugin.getMaven().getMojoParameterValue(OUTPUT_DIRECTORY, File.class,
                    request.getMavenSession(), execution.getPlugin(), pluginExecution, execution.getGoal());
            if (sourceFolder != null) {
                sourceFolders.add(sourceFolder);
            }
        }
        return sourceFolders.toArray(new File[0]);
    }

}
