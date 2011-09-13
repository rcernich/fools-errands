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

import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

public class CleanProjectBuildParticipant extends MojoExecutionBuildParticipant {

    public CleanProjectBuildParticipant(MojoExecution execution) {
        super(execution, false);
    }

    @Override
    public Set<IProject> build(int kind, IProgressMonitor monitor) throws Exception {
        // we only clean.
        return null;
    }

    @Override
    public void clean(IProgressMonitor monitor) throws CoreException {
        try {
            super.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
        } catch (CoreException e) {
            throw e;
        } catch (Exception e) {
            throw new CoreException(new Status(Status.ERROR, Util.PLUGIN_ID,
                    "Error occurred executing maven-clean-plugin.", e));
        }
    }

}
