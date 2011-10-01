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

import static org.jboss.tools.m2e.extras.AptProjectConfigurator.JAVA_INCLUDES;
import static org.jboss.tools.m2e.extras.AptProjectConfigurator.OUTPUT_DIRECTORY;
import static org.jboss.tools.m2e.extras.AptProjectConfigurator.SOURCE_OUTPUT_DIRECTORY;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.codehaus.plexus.util.Scanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * AptBuildParticipant
 * 
 * Invokes mojo and refreshes output directories. Compatible with
 * incremental/automatic builds.
 * 
 * @author Rob Cernich
 */
public class AptBuildParticipant extends MojoExecutionBuildParticipant {

    private static final String COMPILE_SOURCE_ROOTS = "compileSourceRoots";
    private static final String ADDITIONAL_SOURCE_ROOTS = "additionalSourceRoots";
    private static final String CLASSPATH_ELEMENTS = "classpathElements";
    private static final String PDE_PLUGIN_NATURE = "org.eclipse.pde.PluginNature";
    private static final IPath PDE_CLASSPATH_CONTAINER = new Path("org.eclipse.pde.core.requiredPlugins");
    private static final String[] DEFAULT_INCLUDES = new String[] {JAVA_INCLUDES };

    public AptBuildParticipant(MojoExecution execution) {
        super(execution, true);
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        if (!appliesToBuildKind(kind)) {
            return null;
        }

        if (IncrementalProjectBuilder.FULL_BUILD == kind) {
            // don't waste time scanning
            return performBuild(kind, monitor);
        }

        // scan source paths for changes
        boolean build = false;
        BuildContext context = getBuildContext();
        String[] excludes = getExcludes();
        String[] includes = getIncludes();
        if (includes == null || includes.length == 0) {
            includes = DEFAULT_INCLUDES;
        }
        for (File sourceRoot : getSourcePaths()) {
            Scanner scanner = context.newScanner(sourceRoot);
            if (excludes != null && excludes.length > 0) {
                scanner.setExcludes(excludes);
            }
            scanner.setIncludes(includes);
            scanner.scan();
            if (scanner.getIncludedFiles().length > 0) {
                build = true;
                break;
            }
        }
        if (build) {
            return performBuild(kind, monitor);
        }
        return null;
    }

    private Set<IProject> performBuild(int kind, IProgressMonitor monitor) throws Exception {
        final boolean patchedClasspath;
        final Xpp3Dom origClasspathElementsDom;
        if (getMavenProjectFacade().getProject().hasNature(PDE_PLUGIN_NATURE)
                && getMavenProjectFacade().getMavenProject().getPackaging().startsWith("eclipse-")) {
            // XXX: massive hackery. tycho m2e doesn't process dependencies so
            // compileClasspathElements only contains entries directly
            // contributed by the project.
            patchedClasspath = true;
            origClasspathElementsDom = patchClasspathElements();
        } else {
            patchedClasspath = false;
            origClasspathElementsDom = null;
        }

        // perform the build
        Set<IProject> result;
        try {
            result = super.build(kind, monitor);
        } finally {
            if (patchedClasspath) {
                revertConfiguration(origClasspathElementsDom);
            }
        }

        // refresh output directories.
        File outputDirectory = MavenPlugin.getMaven().getMojoParameterValue(getSession(), getMojoExecution(),
                SOURCE_OUTPUT_DIRECTORY, File.class);
        if (outputDirectory != null) {
            getBuildContext().refresh(outputDirectory);
        }
        outputDirectory = MavenPlugin.getMaven().getMojoParameterValue(getSession(), getMojoExecution(),
                OUTPUT_DIRECTORY, File.class);
        if (outputDirectory != null) {
            getBuildContext().refresh(outputDirectory);
        }
        return result;
    }

    private List<File> getSourcePaths() throws CoreException {
        List<File> sourcePaths = new ArrayList<File>();
        sourcePaths.addAll(getCompileSourceRoots());
        sourcePaths.addAll(getAdditionalSourceRoots());
        return sourcePaths;
    }

    private List<File> getCompileSourceRoots() throws CoreException {
        return convertProjectPathsToFileList(MavenPlugin.getMaven().getMojoParameterValue(getSession(),
                getMojoExecution(), COMPILE_SOURCE_ROOTS, String[].class));
    }

    private List<File> getAdditionalSourceRoots() throws CoreException {
        return convertProjectPathsToFileList(MavenPlugin.getMaven().getMojoParameterValue(getSession(),
                getMojoExecution(), ADDITIONAL_SOURCE_ROOTS, String[].class));
    }

    private String[] getIncludes() throws CoreException {
        return MavenPlugin.getMaven().getMojoParameterValue(getSession(), getMojoExecution(), "includes",
                String[].class);
    }

    private String[] getExcludes() throws CoreException {
        return MavenPlugin.getMaven().getMojoParameterValue(getSession(), getMojoExecution(), "excludes",
                String[].class);
    }

    private List<File> convertProjectPathsToFileList(String[] paths) {
        if (paths == null || paths.length == 0) {
            return Collections.emptyList();
        }
        List<File> files = new ArrayList<File>(paths.length);
        for (String path : paths) {
            files.add(new File(path));
        }
        return files;
    }

    private Xpp3Dom patchClasspathElements() {
        IProject project = getMavenProjectFacade().getProject();
        Xpp3Dom config = getMojoExecution().getConfiguration();
        Xpp3Dom origClasspathElementsDom = config.getChild(CLASSPATH_ELEMENTS);

        // remove the current classpathElemenets entry
        if (origClasspathElementsDom != null) {
            for (int i = 0, count = config.getChildCount(); i < count; ++i) {
                if (config.getChild(i) == origClasspathElementsDom) {
                    config.removeChild(i);
                    break;
                }
            }
        }

        // add the "patched" entry
        Xpp3Dom newClasspathElementsDom = new Xpp3Dom(CLASSPATH_ELEMENTS);
        config.addChild(newClasspathElementsDom);

        try {
            // add the project classpath elements
            for (IClasspathEntry ice : JavaCore.create(project).getRawClasspath()) {
                try {
                    processClasspathElement(ice, project, newClasspathElementsDom);
                } catch (Exception e) {
                }
            }
            System.err.println(Arrays.toString(MavenPlugin.getMaven().getMojoParameterValue(getSession(),
                    getMojoExecution(), CLASSPATH_ELEMENTS, String[].class)));
        } catch (Exception e) {
        }
        return origClasspathElementsDom;
    }

    private void processClasspathElement(IClasspathEntry ice, IProject containingProject,
            Xpp3Dom newClasspathElementsDom) throws JavaModelException {
        IPath path;
        switch (ice.getEntryKind()) {
        case IClasspathEntry.CPE_SOURCE: {
            path = ice.getOutputLocation();
            if (path == null) {
                path = JavaCore.create(containingProject).getOutputLocation();
            }
            break;
        }
        case IClasspathEntry.CPE_PROJECT: {
            IProject referenceProject = containingProject.getWorkspace().getRoot()
                    .getProject(ice.getPath().toPortableString());
            for (IClasspathEntry resolvedIce : JavaCore.create(referenceProject).getRawClasspath()) {
                // we're only concerned with exported libraries and the project
                // output
                if (resolvedIce.isExported() || resolvedIce.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                    try {
                        processClasspathElement(resolvedIce, referenceProject, newClasspathElementsDom);
                    } catch (JavaModelException e) {
                    }
                }
            }
            return;
        }
        case IClasspathEntry.CPE_CONTAINER: {
            // we're only concerned with the PDE container
            if (!PDE_CLASSPATH_CONTAINER.equals(ice.getPath())) {
                return;
            }
            IClasspathContainer icc = JavaCore.getClasspathContainer(ice.getPath(), JavaCore.create(containingProject));
            if (icc == null) {
                return;
            }
            for (IClasspathEntry resolvedIce : icc.getClasspathEntries()) {
                try {
                    processClasspathElement(resolvedIce, containingProject, newClasspathElementsDom);
                } catch (JavaModelException e) {
                }
            }
            return;
        }
        case IClasspathEntry.CPE_LIBRARY:
            path = ice.getPath();
            break;
        case IClasspathEntry.CPE_VARIABLE:
            ice = JavaCore.getResolvedClasspathEntry(ice);
            if (ice == null) {
                return;
            }
            path = ice.getPath();
            break;
        default:
            return;
        }
        // make sure we have an absolute file system path
        Xpp3Dom child = new Xpp3Dom("#");
        IResource resource = containingProject.getWorkspace().getRoot().findMember(path);
        if (resource == null) {
            child.setValue(ice.getPath().toPortableString());
        } else {
            child.setValue(resource.getLocation().toPortableString());
        }
        newClasspathElementsDom.addChild(child);
    }

    private void revertConfiguration(Xpp3Dom origClasspathElementsDom) {
        Xpp3Dom config = getMojoExecution().getConfiguration();
        Xpp3Dom compileElementsDom = config.getChild(CLASSPATH_ELEMENTS);
        for (int i = 0, count = config.getChildCount(); i < count; ++i) {
            if (config.getChild(i) == compileElementsDom) {
                config.removeChild(i);
                break;
            }
        }
        if (origClasspathElementsDom != null) {
            config.addChild(origClasspathElementsDom);
        }
    }
}
