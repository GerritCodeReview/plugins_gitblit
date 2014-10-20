gerrit_plugin(
  name = 'gitblit',
  srcs = glob(['src/main/java/**/*.java']),
  manifest_entries = [
    'Gerrit-PluginName: gitblit',
    'Gerrit-Module: com.googlesource.gerrit.plugins.gitblit.GitBlitModule',
    'Gerrit-HttpModule: com.googlesource.gerrit.plugins.gitblit.GitBlitServletModule',
    'Gerrit-InitStep: com.googlesource.gerrit.plugins.gitblit.GitBlitInitStep',
    'Gerrit-ReloadMode: restart',
  ],
  deps = [
    '//lib/httpcomponents:httpcore',
    '//plugins/gitblit/lib:gitblit-jar',
    '//plugins/gitblit/lib:wicket',
    '//plugins/gitblit/lib:wicket-extensions',
    '//plugins/gitblit/lib:javax-mail',
    '//plugins/gitblit/lib:groovy',
    '//plugins/gitblit/lib:jdom',
    '//plugins/gitblit/lib:lucene-highlighter',
    '//plugins/gitblit/lib:lucene-memory',
    '//plugins/gitblit/lib:markdownpapers',
    '//plugins/gitblit/lib:rome',
    '//plugins/gitblit/lib:pf4j',
    '//plugins/gitblit/lib:unboundid',
    ':gitblit-properties-jar',
  ],
  provided_deps = [
    '//lib/commons:net',
    '//lib/commons:codec',
    '//plugins/gitblit/lib:lucene-core',
  ]
)

prebuilt_jar(
  name = 'gitblit-properties-jar',
  binary_jar = ':gitblit-properties',
)

genrule(
  name = 'gitblit-properties',
  cmd = 'cp $SRCDIR/src/main/resources/gitblit.properties $TMP'
    + ';cd $TMP'
    + ';zip -q $OUT *',
  srcs = ['src/main/resources/gitblit.properties'],
  out = 'gitblit-properties.zip',
)
