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
import java.util.List;

import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.apt.core.util.AptConfig;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.internal.lifecyclemapping.model.PluginExecutionMetadata;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

/**
 * AptProjectConfigurator
 * 
 * Configures APT builder for the project. The user can specify whether or not
 * Eclipse's internal APT processing should be used. This is done by setting the
 * configuration property useEclipseApt to true in the lifecycle mapping. If
 * Eclipse APT processing is used, the user must ensure that the applicable
 * factories are available to Eclipse. By default, Maven APT processing is used.
 * 
 * @author Rob Cernich
 */
public class AptProjectConfigurator extends AbstractJavaProjectConfigurator {

    public static final String SOURCE_OUTPUT_DIRECTORY = "sourceOutputDirectory";
    public static final String OUTPUT_DIRECTORY = "outputDirectory";
    public static final String JAVA_INCLUDES = "**/*.java";

    private static final String CONFIGURATOR_ID = "org.jboss.tools.m2e.extras.aptConfigurator";
    private static final String USE_ECLIPSE_APT_PROPERTY = "useEclipseApt";
    private static final String RESOURCE_TARGET_PATH = "resourceTargetPath";

    @Override
    public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
        assertHasNature(request.getProject(), JavaCore.NATURE_ID);

        boolean hasUser = false;
        boolean useEclipseApt = true;
        List<MojoExecution> executions = getMojoExecutions(request, monitor);
        for (MojoExecution execution : executions) {
            boolean temp = useEclipseApt(Util.getPluginExecutionMetadata(CONFIGURATOR_ID,
                    request.getMavenProjectFacade(), execution));
            useEclipseApt = temp && useEclipseApt;
            hasUser = hasUser || temp;
        }
        if (!useEclipseApt) {
            if (hasUser) {
                Activator
                        .getDefault()
                        .getLog()
                        .log(new Status(Status.WARNING, Activator.PLUGIN_ID,
                                "Project has executions configured to use Eclipse APT and Maven APT.  Eclipse APT not configured for project: "
                                        + request.getProject().toString()));
            }
            return;
        }

        // configure Eclipse APT builder
        IMavenProjectFacade facade = request.getMavenProjectFacade();
        boolean configured = false;
        for (MojoExecution execution : executions) {
            File sourceOutputDirectory = getParameterValue(SOURCE_OUTPUT_DIRECTORY, File.class,
                    request.getMavenSession(), execution);
            if (sourceOutputDirectory == null) {
                continue;
            }
            IPath sourceOutputPath = getFullPath(facade, sourceOutputDirectory);
            IJavaProject project = JavaCore.create(request.getProject());
            AptConfig.setEnabled(project, true);
            AptConfig.setGenSrcDir(project, sourceOutputPath.toPortableString());
            configured = true;
            break;
        }
        if (configured) {
            return;
        }
        Activator
                .getDefault()
                .getLog()
                .log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to configure APT settings for project: "
                        + request.getProject().toString()));
    }

    @Override
    public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath,
            IProgressMonitor monitor) throws CoreException {
        boolean useEclipseApt = true;
        List<MojoExecution> executions = getMojoExecutions(request, monitor);
        for (MojoExecution execution : executions) {
            useEclipseApt = useEclipseApt
                    && useEclipseApt(Util.getPluginExecutionMetadata(CONFIGURATOR_ID, request.getMavenProjectFacade(),
                            execution));
        }
        if (useEclipseApt) {
            // classpath configured through APT
            return;
        }

        // Not using Eclipse APT, so we need to configure the source/resource
        // folders.
        IMavenProjectFacade facade = request.getMavenProjectFacade();
        for (MojoExecution execution : executions) {
            File sourceOutputDirectory = getParameterValue(SOURCE_OUTPUT_DIRECTORY, File.class,
                    request.getMavenSession(), execution);
            if (sourceOutputDirectory != null) {
                IPath sourcePath = getFullPath(facade, sourceOutputDirectory);
                if (sourcePath != null && !classpath.containsPath(sourcePath)) {
                    classpath.addSourceEntry(sourcePath, facade.getOutputLocation(), true);
                }
            }
            File resourceOutputDirectory = getParameterValue(OUTPUT_DIRECTORY, File.class, request.getMavenSession(),
                    execution);
            if (resourceOutputDirectory != null) {
                IPath resourcePath = getFullPath(facade, resourceOutputDirectory);
                if (resourcePath != null && !classpath.containsPath(resourcePath)) {
                    String resourceTargetPath = getParameterValue(RESOURCE_TARGET_PATH, String.class,
                            request.getMavenSession(), execution);
                    classpath.addSourceEntry(resourcePath,
                            resourceTargetPath == null || resourceTargetPath.length() == 0 ? facade.getOutputLocation()
                                    : getFullPath(facade, new File(resourceTargetPath)), new IPath[0],
                            new IPath[] {new Path(JAVA_INCLUDES) }, true);
                }
            }
        }
    }

    @Override
    public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution,
            IPluginExecutionMetadata executionMetadata) {
        if (AptConfig.isEnabled(JavaCore.create(projectFacade.getProject()))) {
            // don't run the mojo if the Eclipse APT builder is enabled
            return null;
        }
        return new AptBuildParticipant(execution);
    }

    private boolean useEclipseApt(IPluginExecutionMetadata executionMetadata) {
        Xpp3Dom useEclipseAptProperty = ((PluginExecutionMetadata) executionMetadata).getConfiguration().getChild(
                USE_ECLIPSE_APT_PROPERTY);
        return useEclipseAptProperty != null && Boolean.parseBoolean(useEclipseAptProperty.getValue());
    }
}
