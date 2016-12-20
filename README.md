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
(with Lucene upgrade Pull-Request) using the installMaven ANT target.

    $ git clone https://github.com/gitblit/gitblit.git
    $ git checkout master && git fetch origin refs/pull/1168/head && git cherry-pick FETCH_HEAD
    $ ant -DresourceFolderPrefix=static installMaven

### Gitblit plugin

How to build look into gerrit guide. 

Configuration
-------------
In order to use GitBlit as GitWeb replacement, run the gerrit init after having
installed the gitblit.jar into the /plugins directory.

You will be prompted to enable Gitblit as code viewer and browser.

