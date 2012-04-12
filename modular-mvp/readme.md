#Extensible GWT-Platform Application Example

This project provides an example of how one might go about creating an
extensible application using GWT-Platform.

##Overview

It's relatively simple to create modular applications using GWT and GWT-Platform.  Typically,
this involves creating components and assembling the desired set into an application.  With GWT-Platform, the
application project would include all the necessary modules, Ginjector definition and entry point.

This is all well and good for an application defining a static set of components.  Things get tricky
when the application needs to support a dynamic set of components, especially when using GWT-Platform.
This is due to some of the constraints imposed on how an application is structured.  Briefly, these are:

* Ginjector definition with get methods for EventBus, PlaceManager and all Presenter types.
  This is required during generation of the Proxy implementations for the presenters.
* Ginjector creation, typically using `GWT.create(MyGinjector.class)`.
* Ginjector binding (i.e. `DelayedBindRegistry.bind(ginjector)`).  This binds the Ginjector
  instance to the Presenter Proxy implementations.

The above constraints make it difficult to dynamically construct a Ginjector interface, which
aggregates all Ginjector interfaces into one.  The biggest constraint is the Proxy generation,
which requires the Ginjecter interface be known when the Proxy implementations are generated.

To make an application extensible, two problems need to be overcome:

* An extension mechanism must be created, enabling extra functionality to be plugged in (i.e. a plug-in API).
* A mechanism allowing the Ginjector used by the core application to be swapped out.

This project provides an example of how both of these problems might be solved.

##The Solution

###What It Is and Isn't

The solution presented here provides a mechanism for compile-time resolution/composition of
desired extensions.  It provides an API through which extensions can be discovered, as
well as a mechanism for supplanting the Ginjector used by the application with an extended
version.  

This solution does not show how one might support a truly dynamic, pluggable GWT Platform
solution (e.g. dropping in a JAR).

###The `GinjectorSingleton`

The `GinjectorSingleton` is what enables the base application to use an extended Ginjector.  This "double indirection,"
gives the extended application the ability to instantiate the specific Ginjector type that will be used
by the application.  The main difference here is that instead of using `GWT.create(MyGinjector.class)` to
resolve the Ginjector, you would use `GWT.<GinjectorSingleton>(GinjectorSingleton.class).instance()`.

###The Extension API

The extension API is rather crude, but gets the job done.  A simple Annotation (`ExtensionDefinition`) is defined, which
allows extensions to add content by adding the annotation to a class definition.  Typically,
the annotation would be added to the Ginjector definition used by the extension, but it can
be applied to any class or interface.

The `ExtensionDefinition` simply allows extensions to define some display text, along with the
place token that should be used to resolve the presenter (i.e. the place token should match the `@NameToken` defined on
the Presenter's proxy).

A "manager" class is created using GWT code generation to implement an `ExtensionManager`.  This interface
simply provides the base application with access to the defined extensions.  The base application uses
the `NavigatorItem` list when constructing the "Extensions" section of the navigator.

Boiling it down, this extension mechanism simply provides links (`NameToken`s) for resolving the extended
functionality.

###The "Extended" Application

The extended application then needs to do the following:

* Define a Ginjector interface that "mixes in" the Ginjector interfaces for the base application, plus all extensions.
* Define a `GinjectorSingleton` implementation that creates an instance of the extended Ginjector.
* Define a module that includes the modules for the base application, plus all extensions.
* Define a `replace-with` for `GinjectorSingleton`.
* Define a `gin.ginjector` property, set to the extended Ginjector.

###The "Bundle" Project

This project provides an example for how a complex application might be managed, where extensions may
have their own release cycle, independent of the base or other extensions.  Its version may be updated
as the base and/or extensions are added, removed or updated.

It also provides a module that may be used as a parent project for creating specialized versions
of the application.  For example,

* Overriding versions of specific extensions (see modular-mvp-extension-app)
* Creating customized bundles which aggregate different sets of extensions (see modular-mvp-bundle2).

###Project Structure

This project is composed of the following parts:

* **modular-mvp-parent** - parent project which aggregates the individual child projects.
* **modular-mvp-base** (base) the parent project for the "base".
  * **modular-mvp-base-lib** (base/lib) - packaged as a module, this project contains the base application code, including:
     * Presenters
     * EntryPoint
     * Ginjector definition
     * Plug-in/extension API
  * **modular-mvp-base-app** (base/app) - this project simply packages modular-mvp-base as a WAR. 
* **modular-mvp-extension** (extension) the parent project for an extension.
  * **modular-mvp-extension-lib** (extension/lib) - packaged as a module, this project defines an extension to the base application, including:
     * Additional Presenters
     * Extension Ginjector definition
     * Extension definition
  * **modular-mvp-extension-app** (extension/app) - packaged as a WAR, this project is parented by modular-mvp-bundle-app-parent, overriding:
     * The version of modular-mvp-extension included in the bundle
* **modular-mvp-extension2** (extension2) the parent project for another extension.
  * **modular-mvp-extension-lib** (extension2/lib) - packaged as a module, this project defines an extension to the base application, including:
     * Additional Presenters
     * Extension Ginjector definition
     * Extension definition
* **modular-mvp-bundle** (bundle) the parent project for the default application bundle.
  * **modular-mvp-bundle-gin** (bundle/gin) - packaged as a module, this project defines the composite application, including:
     * Aggregate Ginjector interface
     * Aggregate GinjectorSingleton implementation
     * GWT module definition, inheriting base and all extension modules
  * **modular-mvp-bundle-app-parent** (bundle/app-parent) - parent project providing configuration for building bundles, including:
     * Dependencies
     * Build configuration
  * **modular-mvp-bundle-app** (bundle/app) - packages bundle as a WAR, this project is parented by modular-mvp-bundle-app-parent.
* **modular-mvp-bundle2** (bundle2) the parent project for the custom application bundle.
  * **modular-mvp-bundle2-gin** (bundle2/gin) - packaged as a module, this project defines the custom composite application.
  * **modular-mvp-bundle2-app** (bundle2/app) - packages the customized bundle as a WAR, this project is parented by modular-mvp-bundle-app-parent, overriding:
     * GWT module being compiled to "Application2" module defined in modular-mvp-bundle2-gin
     * The name of the WAR.

## Running the Examples

### Eclipse

Modify the GWT "Web Application" settings to use the exploded war directory in the output folders, e.g. target/application.

When prompted for the HTML page to use (after selecting Run As -> Web Application), make sure "Show Derived Resources" is checked (if you don't see any html files, this is the problem).

You may need to manually adjust the run configuration by adding the module name to the end of the program arguments, e.g. org.example.modular_mvp.bundle.Application.  (I believe this is because the "module" is configured in the parent pom.)
