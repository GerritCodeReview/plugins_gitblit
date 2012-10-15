package com.googlesource.gerrit.plugins.gitblit.auth;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitblit.IStoredSettings;
import com.gitblit.IUserService;
import com.gitblit.models.TeamModel;
import com.gitblit.models.UserModel;
import com.google.common.base.Strings;
import com.google.gerrit.httpd.WebSession;
import com.google.gerrit.server.account.AccountException;
import com.google.gerrit.server.account.AccountManager;
import com.google.gerrit.server.account.AuthMethod;
import com.google.gerrit.server.account.AuthRequest;
import com.google.gerrit.server.account.AuthResult;
import com.google.gerrit.server.project.ProjectControl;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class GerritToGitBlitUserService implements IUserService {
  private static final Logger log = LoggerFactory
      .getLogger(GerritToGitBlitUserService.class);

  private final ProjectControl.Factory projectControl;
  private AccountManager accountManager;

  private Provider<WebSession> webSession;

  public static final String SESSIONAUTH = "sessionid:";

  @Inject
  public GerritToGitBlitUserService(
      final ProjectControl.Factory projectControl,
      AccountManager accountManager, final Provider<WebSession> webSession) {
    this.projectControl = projectControl;
    this.accountManager = accountManager;
    this.webSession = webSession;
  }

  @Override
  public UserModel authenticate(String username, char[] password) {
    String passwordString = new String(password);

    if (passwordString.startsWith(GerritToGitBlitUserService.SESSIONAUTH)) {
      return authenticateSSO(username,
          passwordString.substring(GerritToGitBlitUserService.SESSIONAUTH
              .length()));
    } else {
      return authenticateBasicAuth(username, passwordString);
    }
  }

  public UserModel authenticateSSO(String username, String sessionToken) {
    WebSession session = webSession.get();

    if (session.getToken() == null || !session.getToken().equals(sessionToken)) {
      log.warn("Invalid Gerrit session token for user '" + username + "'");
      return null;
    }

    if (!session.isSignedIn()) {
      log.warn("Gerrit session " + session.getToken() + " is not signed-in");
      return null;
    }

    if (!session.getCurrentUser().getUserName().equals(username)) {
      log.warn("Gerrit session " + session.getToken()
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
      webSession.get().login(authResp, AuthMethod.PASSWORD, false);
    } catch (AccountException e) {
      log.warn("Authentication failed for '" + username + "'", e);
      return null;
    }

    return new GerritToGitBlitUserModel(username, projectControl);
  }

  @Override
  public UserModel getUserModel(String username) {

    return new GerritToGitBlitUserModel(username, projectControl);
  }

  @Override
  public boolean supportsCookies() {
    return false;
  }

  @Override
  public void setup(IStoredSettings settings) {

    ;
  }

  @Override
  public boolean supportsCredentialChanges() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean supportsDisplayNameChanges() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean supportsEmailAddressChanges() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean supportsTeamMembershipChanges() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String getCookie(UserModel model) {

    return model.cookie;
  }

  @Override
  public UserModel authenticate(char[] cookie) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void logout(UserModel user) {
    // TODO Auto-generated method stub
  }

  @Override
  public boolean updateUserModel(UserModel model) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean updateUserModel(String username, UserModel model) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean deleteUserModel(UserModel model) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean deleteUser(String username) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public List<String> getAllUsernames() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<UserModel> getAllUsers() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getAllTeamNames() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<TeamModel> getAllTeams() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<String> getTeamnamesForRepositoryRole(String role) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean setTeamnamesForRepositoryRole(String role,
      List<String> teamnames) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public TeamModel getTeamModel(String teamname) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean updateTeamModel(TeamModel model) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean updateTeamModel(String teamname, TeamModel model) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean deleteTeamModel(TeamModel model) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean deleteTeam(String teamname) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public List<String> getUsernamesForRepositoryRole(String role) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean setUsernamesForRepositoryRole(String role,
      List<String> usernames) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean renameRepositoryRole(String oldRole, String newRole) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean deleteRepositoryRole(String role) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean updateUserModels(List<UserModel> models) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean updateTeamModels(List<TeamModel> models) {
    // TODO Auto-generated method stub
    return false;
  }
}
