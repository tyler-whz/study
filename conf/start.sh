CLASSPATH=$CLASSPATH
for x in lib/*.*
 do
  if [ "$x" != "lib/*.*" ] ; then
    if [ -z "$CLASSPATH" ] ; then
      CLASSPATH=$x
    else
      CLASSPATH="$x":"$CLASSPATH"
    fi
  fi
done
echo
echo "Using JAVA_HOME:       $JAVA_HOME"
echo "Using REPORT_HOME:     $REPORT_HOME"
echo "Using CLASSPATH:       $CLASSPATH"
echo
"$JAVA_HOME"/bin/java -Duser.home=. -classpath "$CLASSPATH" nio.MyServer -Xms900m -Xmx 900m   &