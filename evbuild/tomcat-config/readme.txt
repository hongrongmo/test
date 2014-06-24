This folder contains 2 files used to setup a Tomcat environment for EV:

1) catalina.properties
2) context.xml

After you have created a new Tomcat server in Eclipse, you should be able to override the files created by 
Eclipse by these in order to pick up database connections.

NOTE that the catalina.properties file is currently using EV's "cert" database connections. 