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
