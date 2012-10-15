// Copyright (C) 2012 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.googlesource.gerrit.plugins.gitblit;

import java.io.File;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitblit.Constants;
import com.gitblit.GitBlit;
import com.gitblit.IUserService;
import com.gitblit.gerrit.GerritUserService;
import com.google.gerrit.extensions.annotations.PluginData;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.servlet.ServletModule;

public class GitBlitModule extends ServletModule {

  private Logger logger = LoggerFactory.getLogger(GitBlitModule.class);

  private final File workDirectory;

  @Inject
  public GitBlitModule(@PluginName final String name,
      @PluginData final File workDirectory,
      @GerritServerConfig final Config gerritConfig, final SitePaths sitePaths) {
    logger.info("Create GitBlitModule with name='" + name + "' workDir='"
        + workDirectory.getName() + "'");
    this.workDirectory = workDirectory;
  }

  @Override
  protected void configureServlets() {

    logger.info("Configuring servlet and filtes");
    bind(IUserService.class).to(GerritUserService.class);
    bind(GitBlit.class);
    Constants.setGitBlitBase("/");
    Constants.setGitBlitWorkDirectory(workDirectory);
    serve("/pages/*").with(WrappedPagesServlet.class);
    serve("/feed/*").with(WrappedSyndicationServlet.class);
    filter("/*").through(GerritWicketFilter.class);
    filter("/pages/*").through(WrappedPagesFilter.class);
    filter("/feed/*").through(WrappedSyndicationFilter.class);
  }
}
