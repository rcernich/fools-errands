# m2e Extensions
This plugin provides m2e support for a variety of Maven mojos.

## Generic Source Generating Mojos
This extension provides support for generic source generating mojos.  It can be
used if no other extension is available.  It executes the mojo during full
builds (i.e. no incremental/automatic build support) and refreshes a single
output folder.  The output folder used by default is located in the
"outputDirectory" configuration property, but can be overriden by the
user using the "outputDirectoryProperty" in the lifecycle configuration if the
mojo uses a different property to specify its output directory.

This extension also configures the output folder as a source or resource folder,
depending on the phase to which the execution is bound.  Source folders are
configured based on the execution phase:
* *-sources phases are configured as source folders.
* *-resources phases are configured as resource folders.
Build output folders are configured based on the execution phase:
* *-test-* are configured for output to test build output.
* other folders are configured for normal build output. 

This extension does not provide any support for clean.

The extension ID: org.jboss.tools.m2e.extras.genericSourceConfigurator
Extra lifecycle configuration properties:
* outputDirectoryProperty - identifies the output directory property used by the mojo.


## Maven Dependency Plugin
Support for org.apache.maven.plugins:maven-dependency-plugin

### dependency:unpack
Adds support for dependency:unpack goal.  Behavior is similar to that of the
generic source extension: source folder configuration, no incremental build
support, no clean support.

The extension ID: org.jboss.tools.m2e.extras.dependencyUnpackConfigurator

### dependency:copy
Adds support for dependency:copy goal.  Executes plugin during full builds (i.e.
no incremental support).  Refreshes output folders used by the plugin.  Can be
configured to have copied dependencies added to build classpath using the
"configureClasspath" setting in the lifecycle mapping configuration.

The extension ID: org.jboss.tools.m2e.extras.dependencyCopyConfigurator
Extra lifecycle configuration properties:
* configureClasspath - set to true to have the copied file(s) added to the build classpath.

## Codehaus APT Plugin
Adds support for org.codehaus.mojo:apt-maven-plugin.

### apt:process
Supports incremental builds.  Adds source entries for sourceOutputDirectory and
outputDirectory.  Can be optionally configured to use Eclipse APT builder by
setting configuration property "useEclipseApt" to true.  Note that using the
Eclipse APT builder requires the user to ensure the factories are picked up by
the Eclipse builder.  Also, the Eclipse APT builder does not allow the
outputDirectory to be configured, which means it may not match what you would
get from a command line build.

No support for clean.

The extension ID: org.jboss.tools.m2e.extras.aptConfigurator
Extra lifecycle configuration properties:
* useEclipseApt - set to true to use Eclipse's built-in APT support.

