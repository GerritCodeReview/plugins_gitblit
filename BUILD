load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "gitblit",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: gitblit",
        "Gerrit-Module: com.googlesource.gerrit.plugins.gitblit.GitBlitModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.gitblit.GitBlitServletModule",
        "Gerrit-InitStep: com.googlesource.gerrit.plugins.gitblit.GitBlitInitStep",
        "Gerrit-ReloadMode: restart',
    ],
    deps = [
        ":gitblit-properties-jar",
        "//lib/httpcomponents:httpcore",
        "//plugins/gitblit/lib:confluence-core",
        "//plugins/gitblit/lib:force-partner-api",
        "//plugins/gitblit/lib:freemarker",
        "//plugins/gitblit/lib:gitblit-jar",
        "//plugins/gitblit/lib:groovy",
        "//plugins/gitblit/lib:ivy",
        "//plugins/gitblit/lib:javax-mail",
        "//plugins/gitblit/lib:jdom",
        "//plugins/gitblit/lib:jsoup",
        "//plugins/gitblit/lib:libpam4j",
        "//plugins/gitblit/lib:lucene-highlighter",
        "//plugins/gitblit/lib:lucene-memory",
        "//plugins/gitblit/lib:markdownpapers",
        "//plugins/gitblit/lib:mediawiki-core",
        "//plugins/gitblit/lib:pf4j",
        "//plugins/gitblit/lib:rome",
        "//plugins/gitblit/lib:textile-core",
        "//plugins/gitblit/lib:tika",
        "//plugins/gitblit/lib:tracwiki-core",
        "//plugins/gitblit/lib:twiki-core",
        "//plugins/gitblit/lib:unboundid",
        "//plugins/gitblit/lib:waffle-jna",
        "//plugins/gitblit/lib:wicket",
        "//plugins/gitblit/lib:wicket-extensions",
        "//plugins/gitblit/lib:wikitext-core",
    ],
    provided_deps = [
        "//lib/commons:codec",
        "//lib/commons:net",
        "//plugins/gitblit/lib:lucene-core",
    ]
)

prebuilt_jar(
    name = 'gitblit-properties-jar',
    binary_jar = ':gitblit-properties',
)
genrule(
    name = "gitblit-properties",
    cmd = "cp $SRCDIR/src/main/resources/gitblit.properties $TMP"
      + ";cd $TMP"
      + ";zip -q $OUT *",
    srcs = ["src/main/resources/gitblit.properties"],
    out = "gitblit-properties.zip",
)
