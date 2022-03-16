GitBlit plugin
==============

Overview
--------
Purpose of this plugin is to use Gitblit as web-based viewer
(i.e. GitWeb replacement) on top of Gerrit Code Review.

How to build
------------

This gitblit plugin is built with Bazel.
Only the Gerrit in-tree build is supported.

Clone or link this plugin to the plugins directory of Gerrit's source
tree.

```
  git clone https://gerrit.googlesource.com/gerrit
  cd gerrit/plugins
  git clone https://gerrit.googlesource.com/plugins/gitblit
  ln -sf gitblit/external_plugin_deps.bzl .
  cd ../
```

From Gerrit source tree issue the command:

```
  bazel build plugins/gitblit
```

The output is created in

```
  bazel-bin/plugins/gitblit/gitblit.jar
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
