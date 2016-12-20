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
(with provided patch) using the installMaven ANT target.

    $ git clone https://github.com/gitblit/gitblit.git
    $ git checkout v1.8.0
    $ git apply --stat fix_gerrit_plugin.patch
    $ ant -DresourceFolderPrefix=static installMaven

### Gitblit plugin

How to build look into gerrit guide. 

Configuration
-------------
In order to use GitBlit as GitWeb replacement, please apply
the following configuration to your Gerrit config.

    [gitweb]
        type = custom
        linkname = Gitblit
        url = plugins/gitblit/
        revision = commit/?r=${project}&h=${commit}
        project = summary/?r=${project}
        branch = log/?r=${project}&h=${branch}
        filehistory = history/?f=${file}&r=${project}&h=${branch}
        file = blob/?r=${project}&h=${commit}&f=${file}
        roottree = tree/?r=${project}&h=${commit}

