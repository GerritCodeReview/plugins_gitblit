# GitBlit plugin

This plugin integrates [GitBlit](https://github.com/gitblit/gitblit) _${GitBlit-Version}_ as a repository browser into [Gerrit](https://code.google.com/p/gerrit/),
with full SSO through Gerrit.

* License: [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
* [Home page](https://gerrit.googlesource.com/plugins/gitblit/+/refs/heads/master/README.md)
* Installed plugin version: _${pom.version}_

# Configuration

There are two different configurations: one for Gerrit so it knows how to generate links that will be processed by the plugin, and
an optional GitBlit configuration for the plugin itself.

## Gerrit configuration

In Gerrit's `gerrit.config`, define the `[gitweb]` section as follows:

	[gitweb]
	        type = custom
	        url = plugins/@PLUGIN@/
	        linkname = browse
	        project = summary/?r=${project}
	        revision = commit/?r=${project}&h=${commit}
	        branch = log/?r=${project}&h=${branch}
	        filehistory = history/?f=\${file}&r=${project}&h=${branch}
	        file = blob/?r=${project}&h=${commit}&f=\${file}
	        roottree = tree/?r=${project}&h=${commit}

This is normally done automatically if you add the plugin and run through `java -jar gerrit.war init -d site_path`, but you can also
add this manually to Gerrit's config file. The `linkname` can be adapted to your taste.

### Configuring the top menu

This plugin adds a "GitBlit" top menu to Gerrit, and also a new sub-menu item to the "Projects" top menu. Since v2.11 of this plugin, the link
texts for all sub-menu items can be configured to your taste in a `[plugin "@PLUGIN@"]` section in your `gerrit.config`. If the section is not present,
or some values in that section are not defined, the plugin uses built-in default texts. The default configuration would correspond to

	[plugin "@PLUGIN@"]
	        repositories = Repositories
	        activity = Activity
	        documentation = Documentation
	        search =
	        browse = Browse

The first four are sub-menu items of the "GitBlit" top menu, the last one is a new "browse" sub-menu item in Gerrit's "Projects" menu that is shown
for Gerrit's "current" project (since v2.11).

The "search" sub-menu item is by default not set and will thus not be shown. Setting it makes only sense if you enable GitBlit indexing on some of
your projects.

## GitBlit configuration

The plugin includes in the JAR a minimal default configuration to make GitBlit act only as a repository viewer. You can provide your own
customized [`gitblit.properties`](http://gitblit.com/properties.html) file located in Gerrit's `$GERRIT_SITE/etc` directory.
The internal JAR gitblit.properties takes precedence over the pre-defined configuration settings.

P.S. The following two GitBlit properties are not configurable as changing them would break the plugin functionality:

- git.repositoriesFolder is hardcoded to point to Gerrit repositories directory
- realm.userService is hardcoded to resolve GitBlit users using Gerrit authentication realm

The built-in configuration is archived in the GitBlit source repository. The latest version on master is
[`gitblit.properties`](https://gerrit.googlesource.com/plugins/gitblit/+/master/src/main/resources/gitblit.properties).

# Issue tracking

Report bugs or make feature requests at the [Gerrit issue tracker](https://code.google.com/p/gerrit/issues/list).

<hr style="color: #C0C0C0; background-color: #C0C0C0; border-color: #C0C0C0; height: 2px;" />
<div style="float:right;">
<a href="https://gerrit-review.googlesource.com/#/admin/projects/plugins/gitblit,dashboards" target="_blank">GitBlit plugin ${pom.version}</a>
</div>