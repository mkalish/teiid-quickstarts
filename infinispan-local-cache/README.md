Infinispan Local-Cache Quickstart
================================

This quickstart demonstrates how Teiid can access a cache of Java Objects.

The example can be deployed using Maven from the command line or from Eclipse using
JBoss Tools.

Assumptions:
-  Teiid has been deployed to your jboss as server.

-------------------
System requirements
-------------------

If you have not done so, please review the System Requirements (../README.md)


####################
#   Setup
####################

Setup can be done either manually (see Manual Setup) or using maven (see Setup using the JBoss AS Maven plugin) 


#########################################
### Manual setup
#########################################

1) shutdown jbossas server

2) run:  mvn clean install

3) setup the module that contains the infinispan-quickstart-pojos.jar.
	-	under  src/module,  copy 'com' directory to <jbossas-dir>/modules/
	-   under  target, copy  infinispan-quickstart-pojos.jar to <jbossas-dir>/modules/com/client/quickstart/pojos/main

4) setup Infinispan cache

* Edit `standalone/configuration/standalone-teiid.xml` and add a cache

            <cache-container name="teiid-infinispan-quickstart" default-cache="local-quickstart-cache" start="EAGER">
                <local-cache name="local-quickstart-cache" start="EAGER"/>
            </cache-container>
            
5) setup Infinispan as a datasource

* Edit `standalone/configuration/standalone-teiid.xml` and add resource-adapter

             <resource-adapters>
                <resource-adapter>
                    <archive>
                        teiid-connector-infinispan.rar
                    </archive>
                    <transaction-support>NoTransaction</transaction-support>
                    <connection-definitions>
                        <connection-definition class-name="org.teiid.resource.adapter.infinispan.InfinispanManagedConnectionFactory" jndi-name="java:/infinispanTest" enabled="true" use-java-context="true" pool-name="java:/infinispanTest">
                            <config-property name="CacheTypeMap">
                                local-quickstart-cache:com.client.quickstart.pojo.Order
                            </config-property>
                            <config-property name="CacheJndiName">
                                java:jboss/infinispan/container/teiid-infinispan-quickstart
                            </config-property>
                            <config-property name="module">
                                com.client.quickstart.pojos
                            </config-property>
                        </connection-definition>
                    </connection-definitions>
                </resource-adapter>
            </resource-adapters>

            
5) Start the server

	*  run:  ./standalone.sh -c standalone-teiid.xml

6) deploy the sample application using the management console at http://localhost:9990

	* use the management console at http://localhost:9990 to deploy infinispan-quickstart.war from the target directory
	
7) deploy the VDB: infinispan-vdb.xml

	* copy infinispancache-vdb.xml and infinispancache-vdb.xml.dodeploy to {jbossas.server.dir}/standalone/deployments	

8) security:

-  to add the administrative user and password run: bin/adduser.sh   
-  [optional] setup Teiid user:  edit standalone/configuration/teiid-security-users.properties and add your user and password

	the default is username=user   password=user

9) Open a browser to:  http://localhost:8080/infinispan-quickstart/home.jsf
This should present a list of 10 Orders that were loaded into the cache.

10) Use a sql tool, like SQuirreL, to connect and issue following example query:

-  connect:  jdbc:teiid:orders@mm://localhost:31000
-  query: select * from OrdersView



#########################################
### Setup using the JBoss AS Maven plugin
#########################################

1) shutdown jbossas server

2) run:  mvn clean install

3) install the pojo.jar as a module

	*  `mvn install -Pinstall-module`


4) Start the server

	*  run:  ./standalone.sh -c standalone-teiid.xml
	
5) security:

-  to add the administrative user and password run: bin/add-user.sh   
-  [optional] setup Teiid user:  edit standalone/configuration/teiid-security-users.properties and add your user and password

	the default is username=user   password=user
	

6) setup the Infinispan Cache

    * `mvn -Psetup-cache jboss-as:add-resource` 
    
7) setup Infinispan as a datasource
    
    * `mvn -Psetup-datasource jboss-as:add-resource`  
    
8) deploy the sample application infinispan-quickstart.war and the infinispan-vdb.xml artifacts

	* `mvn install -Pdeploy-artifacts`
	
9) RESTART the jboss as server.  Without using CLI to configure the resources, the resource isn't activated.  
		Therefore, jboss-as requires a restart.
	
10) Open a browser to:  http://localhost:8080/infinispan-quickstart/home.jsf
   This should present a list of 10 Orders that were loaded into the cache.

11) Use a sql tool, like SQuirreL, to connect and issue following example query:

-  connect:  jdbc:teiid:orders@mm://localhost:31000
-  query: select * from OrdersView
