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

package com.googlesource.gerrit.plugins.gitblit.app;

import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;

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
import com.gitblit.tickets.ITicketService;
import com.gitblit.transport.ssh.IPublicKeyManager;
import com.gitblit.wicket.GitBlitWebApp;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class GerritToGitBlitWebApp extends GitBlitWebApp {

  @Inject
  public GerritToGitBlitWebApp(
      Provider<IPublicKeyManager> publicKeyManagerProvider,
      Provider<ITicketService> ticketServiceProvider,
      IRuntimeManager runtimeManager, IPluginManager pluginManager,
      INotificationManager notificationManager, IUserManager userManager,
      IAuthenticationManager authenticationManager,
      IRepositoryManager repositoryManager, IProjectManager projectManager,
      IFederationManager federationManager, IGitblit gitblit,
      IServicesManager services) {
    super(publicKeyManagerProvider, ticketServiceProvider, runtimeManager,
        pluginManager, notificationManager, userManager, authenticationManager,
        repositoryManager, projectManager, federationManager, gitblit, services);
  }

  @Override
  protected IRequestCycleProcessor newRequestCycleProcessor() {
    return new WebRequestCycleProcessor() {
      protected IRequestCodingStrategy newRequestCodingStrategy() {
        return new StaticCodingStrategy("summary/", "project/");
      }
    };
  }
}
