
DIRNAME=`dirname $0`
# OS specific support (must be 'true' or 'false').
cygwin=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;
esac

# Setup JSDT_HOME
if [ "x$JSDT_HOME" = "x" ]; then
    # get the full path (without any relative bits)
    JSDT_HOME=`cd $DIRNAME/..; pwd`
fi
export JSDT_HOME

export JSDT_VERSION=0.5

JSDT_CLASSPATH=$JSDT_HOME/lib/jsdt-ui$JSDT_VERSION.jar


JSDT_CLASSPATH=$JSDT_CLASSPATH:$JSDT_HOME/lib/jsdt-engine$JSDT_VERSION.jar
JSDT_CLASSPATH=$JSDT_CLASSPATH:$JSDT_HOME/lib/jsdt-core$JSDT_VERSION.jar
JSDT_CLASSPATH=$JSDT_CLASSPATH:$JSDT_HOME/lib/chardet.jar
JSDT_CLASSPATH=$JSDT_CLASSPATH:$JSDT_HOME/lib/commons-io-1.3.2.jar
JSDT_CLASSPATH=$JSDT_CLASSPATH:$JSDT_HOME/lib/js.jar
JSDT_CLASSPATH=$JSDT_CLASSPATH:$JSDT_HOME/lib/json-1.0.jar
JSDT_CLASSPATH=$JSDT_CLASSPATH:$JSDT_HOME/lib/rsyntaxtextarea.jar

echo $JSDT_CLASSPATH

export JAVA_OPTS="-Xms128m -Xmx384m -Djsdt.home=$JSDT_HOME"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    JSDT_HOME=`cygpath --path --dos "$JSDT_HOME"`
    JSDT_CLASSPATH=`cygpath --path --dos "$JSDT_CLASSPATH"`
fi


java $JAVA_OPTS -cp $JSDT_CLASSPATH org.ayound.js.debug.ui.Main $*
