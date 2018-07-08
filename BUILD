load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "gitblit",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: gitblit",
        "Gerrit-Module: com.googlesource.gerrit.plugins.gitblit.GitBlitModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.gitblit.GitBlitServletModule",
        "Gerrit-InitStep: com.googlesource.gerrit.plugins.gitblit.GitBlitInitStep",
        "Gerrit-ReloadMode: restart",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "//gitblit:gitblit",
        "@commons-codec//jar:neverlink",
        "@pf4j//jar",
    ],
)
