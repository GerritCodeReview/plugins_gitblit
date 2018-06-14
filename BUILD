load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "gitblit",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    manifest_entries = [
        "Gerrit-PluginName: gitblit",
        "Gerrit-Module: com.googlesource.gerrit.plugins.gitblit.GitBlitModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.gitblit.GitBlitServletModule",
        "Gerrit-InitStep: com.googlesource.gerrit.plugins.gitblit.GitBlitInitStep",
        "Gerrit-ReloadMode: restart",
    ],
    deps = [
        "@commons-codec//jar:neverlink",
        "@commons-io//jar",
        "@commons-net//jar:neverlink",
        "@confluence_core//jar",
        "@force_partner_api//jar",
        "@freemarker//jar",
        "@gitblit_jar//jar",
        "@groovy//jar",
        "@httpcore//jar:neverlink",
        "@ivy//jar",
        "@jdom//jar",
        "@libpam4j//jar",
        "@lucene-core//jar:neverlink",
        "@lucene-highlighter//jar:neverlink",
        "@lucene-memory//jar:neverlink",
        "@mail//jar",
        "@markdownpapers//jar",
        "@mediawiki_core//jar",
        "@pf4j//jar",
        "@rome//jar",
        "@textile_core//jar",
        "@tika//jar",
        "@tracwiki_core//jar",
        "@twiki_core//jar",
        "@unboundid//jar",
        "@waffle_jna//jar",
        "@wicket//jar",
        "@wicket_extensions//jar",
        "@wikitext_core//jar",
    ],
)
