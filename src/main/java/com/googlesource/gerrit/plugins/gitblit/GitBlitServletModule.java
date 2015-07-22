// Copyright (C) 2014 The Android Open Source Project
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

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitblit.AvatarGenerator;
import com.gitblit.GravatarGenerator;
import com.gitblit.IStoredSettings;
import com.gitblit.guice.IPublicKeyManagerProvider;
import com.gitblit.guice.ITicketServiceProvider;
import com.gitblit.guice.WorkQueueProvider;
import com.gitblit.manager.FederationManager;
import com.gitblit.manager.GitblitManager;
import com.gitblit.manager.IAuthenticationManager;
import com.gitblit.manager.IFederationManager;
import com.gitblit.manager.IGitblit;
import com.gitblit.manager.INotificationManager;
import com.gitblit.manager.IPluginManager;
import com.gitblit.manager.IProjectManager;
import com.gitblit.manager.IRepositoryManager;
import com.gitblit.manager.IRuntimeManager;
import com.gitblit.manager.IServicesManager;
import com.gitblit.manager.IUserManager;
import com.gitblit.manager.NotificationManager;
import com.gitblit.manager.ProjectManager;
import com.gitblit.manager.RepositoryManager;
import com.gitblit.manager.RuntimeManager;
import com.gitblit.manager.ServicesManager;
import com.gitblit.servlet.BranchGraphServlet;
import com.gitblit.servlet.DownloadZipServlet;
import com.gitblit.servlet.LogoServlet;
import com.gitblit.servlet.PagesServlet;
import com.gitblit.servlet.SyndicationServlet;
import com.gitblit.tickets.ITicketService;
import com.gitblit.transport.ssh.IPublicKeyManager;
import com.gitblit.utils.JSoupXssFilter;
import com.gitblit.utils.WorkQueue;
import com.gitblit.utils.XssFilter;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.servlet.ServletModule;
import com.googlesource.gerrit.plugins.gitblit.app.GitBlitSettings;
import com.googlesource.gerrit.plugins.gitblit.app.ReallyNullTicketService;
import com.googlesource.gerrit.plugins.gitblit.auth.GerritToGitBlitUserService;

public class GitBlitServletModule extends ServletModule {
  private static final Logger log = LoggerFactory
      .getLogger(GitBlitServletModule.class);

  @Inject
  public GitBlitServletModule(@PluginName final String name,
      @GerritServerConfig final Config gerritConfig, final SitePaths sitePaths) {
    log.info("Create GitBlitModule with name='" + name);
  }

  @Override
  protected void configureServlets() {
    log.info("Configuring Gitblit core services");
    bind(IStoredSettings.class).to(GitBlitSettings.class);
    bind(XssFilter.class).to(JSoupXssFilter.class);
    bind(AvatarGenerator.class).to(GravatarGenerator.class);

    // bind complex providers
    bind(IPublicKeyManager.class).toProvider(IPublicKeyManagerProvider.class);
    bind(ITicketService.class).to(ReallyNullTicketService.class);
    bind(WorkQueue.class).toProvider(WorkQueueProvider.class);

    // core managers
    bind(IRuntimeManager.class).to(RuntimeManager.class);
    bind(IPluginManager.class).to(NullPluginManager.class);
    bind(INotificationManager.class).to(NotificationManager.class);
    bind(IUserManager.class).to(GerritToGitBlitUserService.class);
    bind(IAuthenticationManager.class).to(GerritToGitBlitUserService.class);
    bind(IRepositoryManager.class).to(RepositoryManager.class);
    bind(IProjectManager.class).to(ProjectManager.class);
    bind(IFederationManager.class).to(FederationManager.class);

    // the monolithic manager
    bind(IGitblit.class).to(GitblitManager.class);

    // manager for long-running daemons and services
    bind(IServicesManager.class).to(ServicesManager.class);

    log.info("Configuring Gitblit servlets and filters");
    serve("/graph/*").with(BranchGraphServlet.class);
    serve("/pages/*").with(PagesServlet.class);
    serve("/feed/*").with(SyndicationServlet.class);
    serve("/zip/*").with(DownloadZipServlet.class);
    serve("/logo.png").with(LogoServlet.class);
    serve("/static/logo.png").with(LogoServlet.class);

    filter("/*").through(GerritWicketFilter.class);
    filter("/pages/*").through(WrappedPagesFilter.class);
    filter("/feed/*").through(WrappedSyndicationFilter.class);
  }
}
