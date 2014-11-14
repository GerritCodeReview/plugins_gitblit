package com.googlesource.gerrit.plugins.gitblit;

import javax.servlet.ServletContext;

import com.gitblit.manager.IAuthenticationManager;
import com.gitblit.manager.IFederationManager;
import com.gitblit.manager.IGitblit;
import com.gitblit.manager.INotificationManager;
import com.gitblit.manager.IPluginManager;
import com.gitblit.manager.IProjectManager;
import com.gitblit.manager.IRepositoryManager;
import com.gitblit.manager.IRuntimeManager;
import com.gitblit.manager.IUserManager;
import com.gitblit.servlet.GitblitContext;
import com.gitblit.transport.ssh.IPublicKeyManager;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import dagger.ObjectGraph;

@Singleton
class GerritGitBlitContext extends GitblitContext {
  private final ObjectGraph dagger;
  private final SitePaths sitePaths;

  @Inject
  GerritGitBlitContext(final ObjectGraph dagger, final SitePaths sitePaths) {
    this.dagger = dagger;
    this.sitePaths = sitePaths;
  }

  void init(ServletContext context) {
    // Manually configure IRuntimeManager
    logManager(IRuntimeManager.class);
    IRuntimeManager runtime = dagger.get(IRuntimeManager.class);
    runtime.setBaseFolder(sitePaths.site_path);
    runtime.getStatus().servletContainer = context.getServerInfo();
    runtime.start();

    // create the plugin manager instance but do not start it
    loadManager(dagger, IPluginManager.class);

    // start all other managers
    startManager(dagger, INotificationManager.class);
    startManager(dagger, IUserManager.class);
    startManager(dagger, IAuthenticationManager.class);
    startManager(dagger, IPublicKeyManager.class);
    startManager(dagger, IRepositoryManager.class);
    startManager(dagger, IProjectManager.class);
    startManager(dagger, IFederationManager.class);
    startManager(dagger, IGitblit.class);

    logger.info("");
    logger.info("All managers started.");
    logger.info("");
  }
}
