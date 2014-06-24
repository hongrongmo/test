####################################################################
#
# This is the config file for the evweb_appserver instance for EV
# Notes:
#  *  The LOGGING_CONFIG variable is set to "" because it
#     is the first info to show when doing ps -eaf command.  Since
#     this display is limited, we can't tell which instance is 
#     running!  
#  *  Corresponding to the comment above, the -Djava.util.logging.config.file
#     setting overrides what SHOULD have been set in LOGGING_CONFIG
#
####################################################################
if [ "$1" = "start" ] ; then
    JRE_HOME=/export/home/u1engpub/jdk1.6.0_31

    LOGGING_CONFIG=""
    export LOGGING_CONFIG

    JAVA_OPTS="-Dvmid=cert1con1 $JAVA_OPTS \
        -Djava.util.logging.config.file=$CATALINA_BASE/conf/logging.properties \
        -server -Xmx2024m -Xms512m \
        -Djava.awt.headless=true -Dfile.encoding=UTF-8 \
        -XX:PermSize=512m -XX:MaxPermSize=512m \
        -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC"
    export JAVA_OPTS

    CATALINA_OPTS="$CATALINA_OPTS \
        -Duser.dir=/export/home/u1engpub/evweb_appserver/bin \
        -Dcom.sun.management.jmxremote \
        -Dcom.sun.management.jmxremote.port=9089 \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -javaagent:/export/home/u1engpub/wily/Agent.jar \
        -Dcom.wily.introscope.agentName=evweb_appserver \
        -Dcom.wily.introscope.agentProfile=/export/home/u1engpub/wily/IntroscopeAgent.profile"
    export CATALINA_OPTS

fi
