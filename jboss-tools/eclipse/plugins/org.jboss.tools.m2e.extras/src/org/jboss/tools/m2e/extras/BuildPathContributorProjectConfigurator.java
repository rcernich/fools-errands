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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.lifecyclemapping.model.PluginExecutionMetadata;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

/**
 * BuildPathContributorProjectConfigurator
 * 
 * A project configurator which adds source and resource folder to the project
 * classpath. The folder used is retrieved from a property on the execution. By
 * default, the plugin property is "outputDirectory" but an alternate property
 * name can be set in the lifeclycle mapping metadata.
 * 
 * @author Rob Cernich
 */
public class BuildPathContributorProjectConfigurator extends AbstractJavaProjectConfigurator {

    private static final String OUTPUT_DIRECTORY_PROPERTY = "outputDirectoryProperty";
    private static final String OUTPUT_DIRECTORY_DEFAULT = "outputDirectory";

    private static final String CONFIGURATOR_ID = "org.jboss.tools.m2e.extras.genericSourceConfigurator";

    /**
     * Adds a source or resource folder to the classpath depending on the
     * lifecycle phase of the execution: *-sources map to a source folder;
     * *-resources map to a resource folder. The output folder is set to the
     * test output folder if the phase matches "-test-".
     */
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
        return new RefreshOutputDirectoryBuildParticipant(this, execution, (PluginExecutionMetadata) executionMetadata);
    }

    protected File[] getSourceFolders(ProjectConfigurationRequest request, MojoExecution execution)
            throws CoreException {
        IPluginExecutionMetadata executionMetadata = Util.getPluginExecutionMetadata(CONFIGURATOR_ID,
                request.getMavenProjectFacade(), execution);
        if (executionMetadata == null) {
            return new File[0];
        }
        File sourceFolder = getSourceFolder(request.getMavenSession(), execution, executionMetadata);
        if (sourceFolder == null) {
            return new File[0];
        }
        return new File[] { sourceFolder };
    }

    protected File getSourceFolder(MavenSession session, MojoExecution execution,
            IPluginExecutionMetadata executionMetadata) throws CoreException {
        String outputDirectory;
        Xpp3Dom outputDirectoryProperty = ((PluginExecutionMetadata) executionMetadata).getConfiguration().getChild(
                OUTPUT_DIRECTORY_PROPERTY);
        if (outputDirectoryProperty == null || outputDirectoryProperty.getValue() == null) {
            outputDirectory = OUTPUT_DIRECTORY_DEFAULT;
        } else {
            outputDirectory = outputDirectoryProperty.getValue();
        }
        return getParameterValue(outputDirectory, File.class, session, execution);
    }

}
