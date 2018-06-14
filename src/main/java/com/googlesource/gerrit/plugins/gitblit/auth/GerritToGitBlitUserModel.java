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

import com.gitblit.Constants.AccessPermission;
import com.gitblit.Constants.AccessRestrictionType;
import com.gitblit.models.RepositoryModel;
import com.gitblit.models.TeamModel;
import com.gitblit.models.UserModel;
import com.gitblit.utils.StringUtils;
import com.google.gerrit.reviewdb.client.Project.NameKey;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.permissions.PermissionBackend.ForProject;
import com.google.gerrit.server.permissions.ProjectPermission;
import com.google.gerrit.server.permissions.RefPermission;
import com.google.inject.Provider;
import java.util.HashSet;
import java.util.Set;

public class GerritToGitBlitUserModel extends UserModel {
  public static final String ANONYMOUS_USER = "$anonymous";
  public static final char[] ANONYMOUS_PASSWORD = ANONYMOUS_USER.toCharArray();

  private static final long serialVersionUID = 1L;

  // field names are reflectively mapped in EditUser page
  public String username;
  public String password;
  public String cookie;
  public String displayName;
  public String emailAddress;
  public boolean canAdmin;
  public boolean excludeFromFederation;
  public final Set<String> repositories = new HashSet<String>();
  public final Set<TeamModel> teams = new HashSet<TeamModel>();

  private final transient Provider<CurrentUser> userProvider;
  private final transient PermissionBackend permissionBackend;

  // non-persisted fields
  public boolean isAuthenticated;

  public GerritToGitBlitUserModel(String username) {
    this(username, null, null);
  }

  public GerritToGitBlitUserModel(
      String username, Provider<CurrentUser> userProvider, PermissionBackend persmissionBackend) {
    super(username);
    this.username = username;
    this.isAuthenticated = true;
    this.userProvider = userProvider;
    this.permissionBackend = persmissionBackend;
  }

  public GerritToGitBlitUserModel() {
    super(ANONYMOUS_USER);
    this.userProvider = null;
    this.permissionBackend = null;
  }

  @Override
  public boolean canView(RepositoryModel repository, String ref) {
    return permissionBackend
        .user(userProvider)
        .project(new NameKey(StringUtils.stripDotGit(repository.name)))
        .ref(ref)
        .testOrFalse(RefPermission.READ);
  }

  @Override
  protected boolean canAccess(
      RepositoryModel repository,
      AccessRestrictionType ifRestriction,
      AccessPermission requirePermission) {
    ForProject projectPermissions =
        permissionBackend
            .user(userProvider)
            .project(new NameKey(StringUtils.stripDotGit(repository.name)));
    switch (ifRestriction) {
      case VIEW:
        return projectPermissions.testOrFalse(ProjectPermission.ACCESS);
      case CLONE:
        return projectPermissions.testOrFalse(ProjectPermission.RUN_UPLOAD_PACK);
      case PUSH:
        return projectPermissions.testOrFalse(ProjectPermission.RUN_RECEIVE_PACK);
      default:
        return true;
    }
  }

  public String getRepositoryName(String name) {
    if (name.endsWith(".git")) {
      name = name.substring(0, name.length() - 4);
    }
    return name;
  }

  @Override
  public boolean hasRepositoryPermission(String name) {
    return permissionBackend
        .user(userProvider)
        .project(new NameKey(StringUtils.stripDotGit(name)))
        .testOrFalse(ProjectPermission.ACCESS);
  }

  public boolean hasTeamAccess(String repositoryName) {
    for (TeamModel team : teams) {
      if (team.hasRepositoryPermission(repositoryName)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasRepository(String name) {
    return repositories.contains(name.toLowerCase());
  }

  public void addRepository(String name) {
    repositories.add(name.toLowerCase());
  }

  public void removeRepository(String name) {
    repositories.remove(name.toLowerCase());
  }

  public boolean isTeamMember(String teamname) {
    for (TeamModel team : teams) {
      if (team.name.equalsIgnoreCase(teamname)) {
        return true;
      }
    }
    return false;
  }

  public TeamModel getTeam(String teamname) {
    if (teams == null) {
      return null;
    }
    for (TeamModel team : teams) {
      if (team.name.equalsIgnoreCase(teamname)) {
        return team;
      }
    }
    return null;
  }

  @Override
  public String getName() {
    return username;
  }

  public String getDisplayName() {
    if (StringUtils.isEmpty(displayName)) {
      return username;
    }
    return displayName;
  }

  @Override
  public String toString() {
    return username;
  }

  @Override
  public int compareTo(UserModel o) {
    return username.compareTo(o.username);
  }

  public static UserModel getAnonymous() {
    return new GerritToGitBlitUserModel(ANONYMOUS_USER, null, null);
  }
}
