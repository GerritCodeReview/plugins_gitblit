package com.googlesource.gerrit.plugins.gitblit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.webui.TopMenuExtension;
import com.google.gerrit.server.AnonymousUser;
import com.google.gerrit.server.CurrentUser;
import com.google.inject.Inject;
import com.google.inject.Provider;

@Listen
public class GitBlitTopMenu implements TopMenuExtension {
  private final List<MenuEntry> fullMenuEntries;
  private final List<MenuEntry> restrictedMenuEntries;
  private final Provider<CurrentUser> userProvider;

  @Inject
  public GitBlitTopMenu(@PluginName String pluginName,
      Provider<CurrentUser> userProvider) {
    this.userProvider = userProvider;

    String gitBlitBaseUrl = "/plugins/" + pluginName + "/";
    List<MenuItem> restrictedItems =
        Arrays.asList(new MenuItem("Repositories", gitBlitBaseUrl));
    this.restrictedMenuEntries =
        Arrays.asList(new MenuEntry("GitBlit", restrictedItems));

    ArrayList<MenuItem> fullItems = new ArrayList<MenuItem>(restrictedItems);
    fullItems.addAll(Arrays.asList(new MenuItem("Activity", gitBlitBaseUrl
        + "activity/"), new MenuItem("Search", gitBlitBaseUrl + "lucene/")));
    this.fullMenuEntries = Arrays.asList(new MenuEntry("GitBlit", fullItems));
  }

  @Override
  public List<MenuEntry> getEntrys() {
    CurrentUser user = userProvider.get();
    if (user instanceof AnonymousUser) {
      return restrictedMenuEntries;
    } else {
      return fullMenuEntries;
    }
  }
}
