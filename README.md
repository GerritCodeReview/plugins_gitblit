GitBlit plugin
==============

Overview
--------
Purpose of this plugin is to use Gitblit as web-based viewer
(i.e. GitWeb replacement) on top of Gerrit Code Review.

How to build
------------
As pre-requisites you need to make a custom-build of Gitblit
and Wicket: the standard JARs downloaded from a public Maven
repository aren't enough as they are missing some specific
build parameters and constraints (i.e. shaded-jar) that are
needed for a Gerrit plugin to work properly.

### Gitblit

You need to clone Gitblit from GitHub and build it locally
using the installMaven ANT target.

    $ git clone https://github.com/gitblit/gitblit.git
    $ cd gitblit
    $ git fetch origin refs/pull/1168/head && git merge --no-edit FETCH_HEAD
    $ ant -DresourceFolderPrefix=static installMaven

### Gitblit plugin

This gitblit plugin is built with Bazel.
Only the Gerrit in-tree build is supported.

Clone or link this plugin to the plugins directory of Gerrit's source
tree.

```
  git clone https://gerrit.googlesource.com/gerrit
  git clone https://gerrit.googlesource.com/plugins/gitblit
  cd gerrit/plugins
  ln -s ../../gitblit .
  rm external_plugin_deps.bzl
  ln -s gitblit/external_plugin_deps.bzl .
  cd ../
```

From Gerrit source tree issue the command:

```
  bazel build plugins/gitblit
```

The output is created in

```
  bazel-genfiles/plugins/gitblit/gitblit.jar
```

This project can be imported into the Eclipse IDE.
Add the plugin name to the `CUSTOM_PLUGINS` set in
Gerrit core in `tools/bzl/plugins.bzl`, and execute:

```
  ./tools/eclipse/project.py
```

Configuration
-------------
In order to use GitBlit as GitWeb replacement, run the Gerrit init
again and answer 'Y' when asked during the Gitblit-specific initialisation.

Example:

```
*** GitBlit Integration
***

Do you want to use GitBlit as your GitWeb viewer? [Y/n]? y
Link name                      [GitBlit]:
"Repositories" submenu title   [Repositories]:
"Activity" submenu title       [Activity]:
"Documentation" submenu title  [Documentation]:
"Search" submenu title (makes only sense to set if some projects are indexed in GitBlit; single dash unsets) [Search]:
"Browse" submenu title for the "Projects" top-level menu [Browse]:
```
