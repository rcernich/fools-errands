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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.lifecyclemapping.model.PluginExecutionMetadata;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

/**
 * DependencyCopyDependenciesProjectConfigurator
 * 
 * Configures additional classpath entries for dependencies added through
 * maven-dependency-plugin copy-dependencies execution.
 * 
 * @author Rob Cernich
 */
public class DependencyCopyDependenciesProjectConfigurator extends AbstractJavaProjectConfigurator {

    public static final String OUTPUT_DIRECTORY = "outputDirectory";
    private static final String CONFIGURATOR_ID = "org.jboss.tools.m2e.extras.dependencyCopyDependenciesConfigurator";
    private static final String CONFIGURE_CLASSPATH_PROPERTY = "configureClasspath";

    @Override
    public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath,
            IProgressMonitor monitor) throws CoreException {
        IMavenProjectFacade facade = request.getMavenProjectFacade();

        assertHasNature(request.getProject(), JavaCore.NATURE_ID);

        for (MojoExecution mojoExecution : getMojoExecutions(request, monitor)) {
            if (!configureClasspath(request, mojoExecution)) {
                continue;
            }
            File[] libraries = getLibraries(request, mojoExecution, monitor);

            for (File library : libraries) {
                IPath libraryPath = getFullPath(facade, library);
                if (libraryPath == null) {
                    continue;
                }
                if (libraryPath != null && !classpath.containsPath(libraryPath)) {
                    classpath.addLibraryEntry(libraryPath);
                }
            }
        }
    }

    @Override
    public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution,
            IPluginExecutionMetadata executionMetadata) {
        return new RefreshDependencyOutputDirectoryBuildParticipant(execution);
    }

    private boolean configureClasspath(ProjectConfigurationRequest request, MojoExecution execution) {
        IPluginExecutionMetadata executionMetadata = Util.getPluginExecutionMetadata(CONFIGURATOR_ID,
                request.getMavenProjectFacade(), execution);
        if (executionMetadata == null) {
            return false;
        }
        Xpp3Dom configureClasspathProperty = ((PluginExecutionMetadata) executionMetadata).getConfiguration().getChild(
                CONFIGURE_CLASSPATH_PROPERTY);
        return configureClasspathProperty == null ? false : Boolean.parseBoolean(configureClasspathProperty.getValue());
    }

    /**
     * This worked for unpack, but is not correct for copy-dependencies.
     */
    private File[] getLibraries(ProjectConfigurationRequest request, MojoExecution execution, IProgressMonitor monitor)
            throws CoreException {
        if (useRepositoryLayout(request, execution)) {
            return new File[0];
        }
        File commonOutputDirectory = null;
        Xpp3Dom outputDirectoryDom = execution.getConfiguration().getChild(OUTPUT_DIRECTORY);
        if (outputDirectoryDom == null || !"${outputDirectory}".equals(outputDirectoryDom.getValue())) {
            File[] outputDirectories = super.getSourceFolders(request, execution);
            if (outputDirectories.length == 1) {
                commonOutputDirectory = outputDirectories[0];
            } else {
                return new File[0];
            }
        }
        Xpp3Dom artifactItemsDom = execution.getConfiguration().getChild("artifactItems");
        if (artifactItemsDom == null) {
            return new File[0];
        }
        Xpp3Dom[] artifactItemDoms = artifactItemsDom.getChildren("artifactItem");
        if (artifactItemDoms == null || artifactItemDoms.length == 0) {
            return new File[0];
        }
        List<File> libraries = new ArrayList<File>(artifactItemDoms.length);
        for (Xpp3Dom artifactItemDom : artifactItemDoms) {
            File outputDirectory = getMojoParameterValue(OUTPUT_DIRECTORY, File.class, artifactItemDom, request,
                    execution);
            if (outputDirectory == null) {
                outputDirectory = commonOutputDirectory;
            }
            if (outputDirectory != null) {
                String artifactFileName = getArtifactFileName(artifactItemDom, request, execution, monitor);
                if (artifactFileName != null && artifactFileName.length() > 0) {
                    libraries.add(new File(outputDirectory, artifactFileName));
                }
            }
        }
        return libraries.toArray(new File[0]);
    }

    private boolean useRepositoryLayout(ProjectConfigurationRequest request, MojoExecution execution)
            throws CoreException {
        Boolean retVal = getMojoParameterValue("useRepositoryLayout", Boolean.class, execution.getConfiguration(),
                request, execution);
        return retVal == null ? false : retVal.booleanValue();
    }

    private String getArtifactFileName(Xpp3Dom artifactItemDom, ProjectConfigurationRequest request,
            MojoExecution execution, IProgressMonitor monitor) throws CoreException {
        String groupId = getMojoParameterValue("groupId", String.class, artifactItemDom, request, execution);
        String artifactId = getMojoParameterValue("artifactId", String.class, artifactItemDom, request, execution);
        String version = getMojoParameterValue("version", String.class, artifactItemDom, request, execution);
        String type = getMojoParameterValue("type", String.class, artifactItemDom, request, execution);
        String classifier = getMojoParameterValue("classifier", String.class, artifactItemDom, request, execution);
        IMaven maven = MavenPlugin.getMaven();
        Artifact artifact = maven.resolve(groupId, artifactId, version, type, classifier,
                maven.getArtifactRepositories(), monitor);
        if (artifact == null || artifact.getFile() == null) {
            return null;
        }
        return artifact.getFile().getName();
    }

    private <T> T getMojoParameterValue(String parameter, Class<T> asType, Xpp3Dom configuration,
            ProjectConfigurationRequest request, MojoExecution execution) throws CoreException {
        PluginExecution pluginExecution = new PluginExecution();
        pluginExecution.setConfiguration(configuration);
        return MavenPlugin.getMaven().getMojoParameterValue(parameter, asType, request.getMavenSession(),
                execution.getPlugin(), pluginExecution, execution.getGoal());
    }

}
