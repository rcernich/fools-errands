#Extensible GWT Platform MVP Application Example

This project provides an example of how one might go about creating an
extensible application using GWT Platform MVP.

##Overview

It's relatively simple to create modular applications using GWT and GWT Platform MVP.  Typically,
this involves creating components and assembling the desired set into an application.  In MVP, the
application project would include all the necessary modules, Ginjector definition and entry point.

This is all well and good for an application defining a static set of components.  Things get tricky
when the application needs to support a dynamic set of components, especially when using GWT Platform MVP.
This is due to some of the constraints MVP imposes on how an application is structured.  Briefly, these are:

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

This solution does not show how one might support a truly dynamic, pluggable GWT Platform MVP
solution (e.g. dropping in a JAR).

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

###The `GinjectorSingleton`

The `GinjectorSingleton` is what enables the base application to use an extended Ginjector.  This "double indirection,"
gives the extended application the ability to instantiate the specific Ginjector type that will be used
by the application.

###The "Extended" Application

The extended application then needs to do the following:

* Define a Ginjector interface that "mixes in" the Ginjector interfaces for the base application, plus all extensions.
* Define a `GinjectorSingleton` implementation that creates an instance of the extended Ginjector.
* Define a module that includes the modules for the base application, plus all extensions.
* Define a `replace-with` for `GinjectorSingleton`.
* Define a `gin.ginjector` property, set to the extended Ginjector.

###Project Structure

This project is composed of the following parts:

* **modular-mvp-parent** - parent project defining modules, dependencies, etc.
* **modular-mvp-base** (base) - packaged as a module, this project contains the base application code, including:
  * Presenters
  * EntryPoint
  * Ginjector definition
  * Plug-in/extension API
* **modular-mvp-base-app** (base-app) - this project simply packages modular-mvp-base as a WAR. 
* **modular-mvp-extension** (extension) - packaged as a module, this project defines an extension to the base application, including:
  * Additional Presenters
  * Extension Ginjector definition
  * Extension definition
* **modular-mvp-extended-app** (extended-app) - packaged as a WAR, this project provides the settings required for execution, including:
  * An extended, mix-in type Ginjector definition
  * gin.ginjector property setting
  * Ginjector creation (i.e. `ExtendedApplicationSingleton`)
