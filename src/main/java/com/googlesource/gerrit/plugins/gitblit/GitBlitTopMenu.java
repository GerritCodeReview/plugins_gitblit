package com.googlesource.gerrit.plugins.gitblit;

import java.util.Arrays;
import java.util.List;

import com.google.gerrit.extensions.annotations.Listen;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.extensions.webui.TopMenuExtension;
import com.google.inject.Inject;

@Listen
public class GitBlitTopMenu implements TopMenuExtension {
  private String pluginName;

  @Inject
  public GitBlitTopMenu(@PluginName String pluginName) {
    this.pluginName = pluginName;
  }

  @Override
  public List<MenuEntry> getEntrys() {
    return Arrays.asList(new MenuEntry("GitBlit", Arrays.asList(new MenuItem(
        "Home", "/plugins/" + pluginName + "/"))));
  }
}
