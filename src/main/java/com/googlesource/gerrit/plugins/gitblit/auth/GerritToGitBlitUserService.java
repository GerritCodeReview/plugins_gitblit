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

package com.googlesource.gerrit.plugins.gitblit.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitblit.Constants;
import com.gitblit.Constants.AuthenticationType;
import com.gitblit.Constants.Role;
import com.gitblit.manager.IAuthenticationManager;
import com.gitblit.manager.IManager;
import com.gitblit.manager.IRuntimeManager;
import com.gitblit.manager.IUserManager;
import com.gitblit.models.TeamModel;
import com.gitblit.models.UserModel;
import com.gitblit.transport.ssh.SshKey;
import com.google.common.base.Strings;
import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.httpd.WebSession;
import com.google.gerrit.server.account.AccountException;
import com.google.gerrit.server.account.AccountManager;
import com.google.gerrit.server.account.AuthRequest;
import com.google.gerrit.server.account.AuthResult;
import com.google.gerrit.server.project.ProjectControl;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GerritToGitBlitUserService implements IAuthenticationManager,
    IUserManager {
  private static final Logger log = LoggerFactory
      .getLogger(GerritToGitBlitUserService.class);

  private final ProjectControl.Factory projectControl;
  private final AccountManager accountManager;
  private final DynamicItem<WebSession> webSession;

  public static final String SESSIONAUTH = "sessionid:";

  @Inject
  public GerritToGitBlitUserService(
      final ProjectControl.Factory projectControl,
      AccountManager accountManager, final DynamicItem<WebSession> webSession) {
    this.projectControl = projectControl;
    this.accountManager = accountManager;
    this.webSession = webSession;
  }

  @Override
  public UserModel authenticate(String username, char[] password) {
    String passwordString = new String(password);

    if (username.equals(GerritToGitBlitUserModel.ANONYMOUS_USER)) {
      return GerritToGitBlitUserModel.getAnonymous(projectControl);
    } else if (passwordString
        .startsWith(GerritToGitBlitUserService.SESSIONAUTH)) {
      return authenticateSSO(username,
          passwordString.substring(GerritToGitBlitUserService.SESSIONAUTH
              .length()));
    } else {
      return authenticateBasicAuth(username, passwordString);
    }
  }

  public UserModel authenticateSSO(String username, String sessionToken) {
    WebSession session = webSession.get();

    if (session.getSessionId() == null
        || !session.getSessionId().equals(sessionToken)) {
      log.warn("Invalid Gerrit session token for user '" + username + "'");
      return null;
    }

    if (!session.isSignedIn()) {
      log.warn("Gerrit session " + session.getSessionId() + " is not signed-in");
      return null;
    }

    if (!session.getCurrentUser().getUserName().equals(username)) {
      log.warn("Gerrit session " + session.getSessionId()
          + " is not assigned to user " + username);
      return null;
    }

    return new GerritToGitBlitUserModel(username, projectControl);
  }

  public UserModel authenticateBasicAuth(String username, String password) {
    if (Strings.isNullOrEmpty(username) || password == null
        || password.length() <= 0) {
      log.warn("Authentication failed: no username or password specified");
      return null;
    }

    AuthRequest who = AuthRequest.forUser(username);
    who.setPassword(new String(password));

    try {
      AuthResult authResp = accountManager.authenticate(who);
      webSession.get().login(authResp, false);
    } catch (AccountException e) {
      log.warn("Authentication failed for '" + username + "'", e);
      return null;
    }

    return new GerritToGitBlitUserModel(username, projectControl);
  }


  @Override
  public IManager start() {
    return null;
  }

  @Override
  public IManager stop() {
    return null;
  }

  @Override
  public UserModel authenticate(HttpServletRequest httpRequest) {
    String gerritUsername =
        (String) httpRequest.getAttribute("gerrit-username");
    String gerritToken = (String) httpRequest.getAttribute("gerrit-token");
    httpRequest.getSession().setAttribute(Constants.AUTHENTICATION_TYPE,
        AuthenticationType.CONTAINER);
    
    if (Strings.isNullOrEmpty(gerritUsername)
        || Strings.isNullOrEmpty(gerritToken)) {
      return GerritToGitBlitUserModel.getAnonymous(projectControl);
    } else {      
      return authenticateSSO(gerritUsername, gerritToken);
    }
  }

  @Override
  public UserModel authenticate(String username, SshKey key) {
    return null;
  }

  @Override
  public UserModel authenticate(HttpServletRequest httpRequest,
      boolean requiresCertificate) {
    return null;
  }

  @Override
  public String getCookie(HttpServletRequest request) {
    return null;
  }

  @Override
  public void setCookie(HttpServletResponse response, UserModel user) {
  }

  @Override
  public void setCookie(HttpServletRequest request,
      HttpServletResponse response, UserModel user) {
  }

  @Override
  public void logout(HttpServletResponse response, UserModel user) {
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      UserModel user) {
  }

  @Override
  public boolean supportsCredentialChanges(UserModel user) {
    return false;
  }

  @Override
  public boolean supportsDisplayNameChanges(UserModel user) {
    return false;
  }

  @Override
  public boolean supportsEmailAddressChanges(UserModel user) {
    return false;
  }

  @Override
  public boolean supportsTeamMembershipChanges(UserModel user) {
    return false;
  }

  @Override
  public boolean supportsTeamMembershipChanges(TeamModel team) {
    return false;
  }

  @Override
  public void setup(IRuntimeManager runtimeManager) {
  }

  @Override
  public String getCookie(UserModel model) {
    return null;
  }

  @Override
  public UserModel getUserModel(char[] cookie) {
    return null;
  }

  @Override
  public UserModel getUserModel(String username) {
    return new GerritToGitBlitUserModel(username, projectControl);
  }

  @Override
  public boolean updateUserModel(UserModel model) {
    return false;
  }

  @Override
  public boolean updateUserModels(Collection<UserModel> models) {
    return false;
  }

  @Override
  public boolean updateUserModel(String username, UserModel model) {
    return false;
  }

  @Override
  public boolean deleteUserModel(UserModel model) {
    return false;
  }

  @Override
  public boolean deleteUser(String username) {
    return false;
  }

  @Override
  public List<String> getAllUsernames() {
    return Collections.emptyList();
  }

  @Override
  public List<UserModel> getAllUsers() {
    return Collections.emptyList();
  }

  @Override
  public List<String> getAllTeamNames() {
    return Collections.emptyList();
  }

  @Override
  public List<TeamModel> getAllTeams() {
    return null;
  }

  @Override
  public List<String> getTeamNamesForRepositoryRole(String role) {
    return null;
  }

  @Override
  public TeamModel getTeamModel(String teamname) {
    return null;
  }

  @Override
  public boolean updateTeamModel(TeamModel model) {
    return false;
  }

  @Override
  public boolean updateTeamModels(Collection<TeamModel> models) {
    return false;
  }

  @Override
  public boolean updateTeamModel(String teamname, TeamModel model) {
    return false;
  }

  @Override
  public boolean deleteTeamModel(TeamModel model) {
    return false;
  }

  @Override
  public boolean deleteTeam(String teamname) {
    return false;
  }

  @Override
  public List<String> getUsernamesForRepositoryRole(String role) {
    return null;
  }

  @Override
  public boolean renameRepositoryRole(String oldRole, String newRole) {
    return false;
  }

  @Override
  public boolean deleteRepositoryRole(String role) {
    return false;
  }

  @Override
  public boolean isInternalAccount(String username) {
    return false;
  }

  @Override
  public boolean supportsRoleChanges(UserModel user, Role role) {
    return false;
  }

  @Override
  public boolean supportsRoleChanges(TeamModel team, Role role) {
    return false;
  }
}
