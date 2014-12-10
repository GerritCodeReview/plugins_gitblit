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

### Wicket
You need to clone and build a modified version of Wicket that
is currently published on GitHub under the GerritCodeReview
organisation: https://github.com/GerritCodeReview/wicket.git

    $ git clone https://github.com/GerritCodeReview/wicket.git
    $ git checkout wicket-1.4.23-gerrit
    $ mvn clean install -DskipTests

### Gitblit
You need to clone Gitblit from GitHub and build it locally
using the installMaven ANT target.

    $ git clone https://github.com/gitblit/gitblit.git
    $ git checkout develop
    $ ant -DresourceFolderPrefix=static installMaven

### Gitblit plugin
You are ready now to clone and build the Gitblit plugin: the
Wicket and Giblit dependencies will be taken from your local
Maven repository.

    $ mvn package

Configuration
-------------
In order to use GitBlit as GitWeb replacement, please apply
the following configuration to your Gerrit config.

    [gitweb]
        type = custom
        linkname = Gitblit
        url = plugins/
        revision = gitblit/commit/?r=${project}&h=${commit}
        project = gitblit/summary/?r=${project}
        branch = gitblit/log/?r=${project}&h=${branch}
        filehistory = gitblit/history/?f=${file}&r=${project}&h=${branch}
        file = gitblit/blob/?r=${project}&h=${commit}&f=${file}
        roottree = gitblit/tree/?r=${project}&h=${commit}
