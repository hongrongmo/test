####################################################################
#
# This is the config file for each evweb_appserver instance for EV
#
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

    JAVA_OPTS="-Dvmid=prod1con2 $JAVA_OPTS \
        -Djava.util.logging.config.file=$CATALINA_BASE/conf/logging.properties \
        -server -Xmx2048m -Xms512m \
        -Djava.awt.headless=true -Dfile.encoding=UTF-8 \
        -XX:PermSize=512m -XX:MaxPermSize=784m \
        -XX:+CMSClassUnloadingEnabled -XX:+UseConcMarkSweepGC"
    export JAVA_OPTS

    CATALINA_OPTS="$CATALINA_OPTS \
        -Duser.dir=/export/home/u1engpub/evweb_appserver2/bin \
        -Duser.home=/export/home/u1engpub/evweb_appserver2/bin"
    export CATALINA_OPTS

fi
