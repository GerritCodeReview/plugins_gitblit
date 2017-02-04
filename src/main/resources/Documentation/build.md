Build
=====

This gitblit plugin is built with Bazel.
Only the Gerrit in-tree build is supported.

Clone or link this plugin to the plugins directory of Gerrit's source
tree.

```
  git clone https://gerrit.googlesource.com/gerrit
  git clone https://gerrit.googlesource.com/plugins/@PLUGIN@
  cd gerrit/plugins
  ln -s ../../gitblit .
  cd ../
```

You will need to build gitblit from source. (Requires ant, mvn (maven))

```
  cd plugins/gitblit
  git clone https://github.com/lucamilanesio/gitblit.git
  cd gitblit
  git checkout bump-to-lucene-5.5.2
  git submodule update --init --recursive
  ant clean buildAll buildMavenArtifacts
```

After building it we will need to copy some jars to our local maven repo.

```
  mkdir -p ~/.m2/repository/com/gitblit/gitblit/1.8.1-SNAPSHOT/
  cp build/target/gitblit-1.8.1-SNAPSHOT.jar ~/.m2/repository/com/gitblit/gitblit/1.8.1-SNAPSHOT/
  cp build/target/gitblit-1.8.1-SNAPSHOT-sources.jar ~/.m2/repository/com/gitblit/gitblit/1.8.1-SNAPSHOT/
  cd ../../../
```

Put the external dependency Bazel build file into the Gerrit /plugins
directory, replacing the existing empty one.

```
  cd plugins
  rm external_plugin_deps.bzl
  ln -s gitblit/external_plugin_deps.bzl .
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

How to build the Gerrit Plugin API is described in the [Gerrit
documentation](../../../Documentation/dev-bazel.html#_extension_and_plugin_api_jar_files).

[Back to @PLUGIN@ documentation index][index]

[index]: index.html
