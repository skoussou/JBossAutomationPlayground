# How to Test RH-PAM & ISTIO (Ideas)

* Pre-reqs
  * ISTIO must be installed & available in OCP Cluster See [Documentation](https://access.redhat.com/documentation/en-us/openshift_container_platform/4.3/html-single/service_mesh/index#ossm-supported-configurations_preparing-ossm-installation)
  * Be able to set ServiceMeshMemberRoll to the namespace you will use for RHPAM Deployments accessible via ISTIO

* To Install ISTIO Read
  * OCP 4 Installation: https://gitlab.consulting.redhat.com/enterprise-integration-design-sprint/infra/ocp
  * ISTIO Installation: https://github.com/dsanchor/istio-tutorial
  * See also 'Labs - Openshift Service Mesh - Istio.odp' in this repository on adding ISTIO PRoxy side car

## Install a standard DEV Environment (BC + KIE Server)

* CICD Tools setup: https://github.com/erkerc/openshift-cd-demo.git
* See https://github.com/skoussou/ocp_pam_app_dev 
  * Use the tools above rather than this repo's tool setup
  * Configure the OCP CLuster from above URLs/DOmains to be used
  * eg. ./Infrastructure/scripts76/setup_DEV_managed.sh dev-pam cicd-rhpam gps apps.labs-aws-430a.sandbox452.opentlc.com nexu
* Test BC To KIE Server (via CICD Nexus <distributionManagement> from the KJAR project) installaton of Evaluations project
  * eg. 
```
 <distributionManagement>
   <repository>
     <id>Nexus</id>
     <url>http://nexus-cicd-rhpam.apps.labs-aws-430c.sandbox1287.opentlc.com/repository/maven-releases</url>
   </repository>
   <snapshotRepository>
     <id>Nexus</id>
     <url>http://nexus-cicd-rhpam.apps.labs-aws-430c.sandbox1287.opentlc.com/repository/maven-snapshots</url>
   </snapshotRepository>
 </distributionManagement>
```


## Setup Template Auth Environment for ISTIO

* Configure 'pam-dev' in ServiceMeshMemberRoll
* Remove route for BC & KIE Server
* Create a new HTTP Service for BC and another for KIE Server to be used in the following ISTIO Configs (RHPAM-and-ServiceMesh/OPTION-0-gateway-destrules-kie-server.yam)

```
# used by OPTION-0 Template based kieserver
apiVersion: v1
kind: Service
metadata:
  name: dev-kie-server
  labels:
    app: gps-pamdev
    application: dev-kie-server
    group : istio-pam-tests
spec:
  ports:
    - name: http
      protocol: TCP
      port: 8080
      targetPort: 8080
  selector:
    deploymentConfig: gps-kieserver

# used by OPTION-0 Template based kieserver
apiVersion: v1
kind: Service
metadata:
  name: dev-bc
  labels:
    app: gps-pamdev
    application: dev-bc  
    group : istio-pam-tests
spec:
  ports:
    - name: http
      protocol: TCP
      port: 8080
      targetPort: 8080
  selector:
    deploymentConfig: gps-rhpamcentr
```

* Apply ISTIO Configs OPTION-0-gateway-destrules-kie-server.yaml
  * eg. cat ./RHPAM-and-ServiceMesh/OPTION-0-gateway-destrules-kie-server.yaml | APP_SUBDOMAIN=$(echo $SUBDOMAIN) NAMESPACE=$(echo $APPS_NAMESPACE) envsubst | oc apply -f - 
* Now for BC Route http://template-rhpam-bc-dev-pam-istio-system.apps.labs-aws-430c.sandbox1287.opentlc.com should work
  * Test by calling: watch -n1 "curl -v template-rhpam-bc-dev-pam-istio-system.apps.labs-aws-430c.sandbox1287.opentlc.com/docs/"
* Now for KIE Server (from template) http://template-rhpam-service-dev-pam-istio-system.apps.labs-aws-430c.sandbox1287.opentlc.com/docs/ should be accessible 
  * Test by calling: watch -n1 "curl -v http://template-rhpam-service-dev-pam-istio-system.apps.labs-aws-430c.sandbox1287.opentlc.com/docs/"


## Setup KIE Server/KJARs with multiple versions

* Note: ImageStreams should come from 'openshift' namespace, secrets/settigs.xml should be available for KIE Server in namespace based on previous template config
* Create 2 new PVCs required by the KIE Servers in 'pam-dev' 
  * oc create -f ./RHPAM-and-ServiceMesh/PVC-V110-.yaml
  * oc create -f ./RHPAM-and-ServiceMesh/PVC-V150-.yaml
* Create the necessary services for the 2 new KIE Servers to be used by ISTIO Config (see: ./RHPAM-and-ServiceMesh/Service.yaml pointing to Option 3)
* Apply ISTIO Configs OPTION-3-ADVANCED-gateway-destrules-kie-server.yaml
  * eg. cat ./RHPAM-and-ServiceMesh/OPTION-3-ADVANCED-gateway-destrules-kie-server.yaml | APP_SUBDOMAIN=$(echo $SUBDOMAIN) NAMESPACE=$(echo $APPS_NAMESPACE) envsubst | oc apply -f - 
* Create KIE Servers
  * KIE Server kjar-a-1-1-0: APPLY DC-KIE-v110-2
  * KIE Server kjar-a-1-5-0: APPLY DC-KIE-v150-2
* Access KIE Servers for kjar-a-1-1-0 & kjar-a-1-5-0 at: http://rhpam-service-a-${APPS_NAMESPACE}-istio-system.${SUBDOMAIN}/docs"
  * Check KIALI UI: The requests (execute ./RHPAM-and-ServiceMesh/loop-pam-custom-kjar-a.sh) should be 80%-20% on each server based on VirtualService weights, play with the weights)

  
### Setup KIE Server/KJARs with multiple versions to use HTTP Header parameter

* After the previous setup is in place apply new ISTIO Configs OPTION-3a-ADVANCED-gateway-destrules-kie-server-HEADER-BASED-ROUTING.yaml
  * eg. cat ./RHPAM-and-ServiceMesh/OPTION-3a-ADVANCED-gateway-destrules-kie-server-HEADER-BASED-ROUTING.yaml | APP_SUBDOMAIN=$(echo $SUBDOMAIN) NAMESPACE=$(echo $APPS_NAMESPACE) envsubst | oc apply -f - 
* Now rather than 80/20 the requests will go to service 'custom-kieserver-kjar-a-v110' when the request to Route http://rhpam-service-a-${APPS_NAMESPACE}-istio-system.${SUBDOMAIN}/docs contains also header 'bizversion: version-kjar-a-110'
  * watch -n1 "curl -v -H 'bizversion: version-kjar-a-110'  http://rhpam-service-a-${APPS_NAMESPACE}-istio-system.${SUBDOMAIN}/docs
* without the header param requests will go to service 'custom-kieserver-kjar-a-v150'
  * watch -n1 "curl -v http://rhpam-service-a-${APPS_NAMESPACE}-istio-system.${SUBDOMAIN}/docs
* This is the result of _Virtual Service_ *rhpam-virtual-service* checking for the header _bizversion_ and value 'version-kjar-a-110'


### Possible Smart Routing with ISTIO

* We could create a *Smart Router* (Routing) effect with ISTIO
  * ALIAS BASED Route for all versions of a specific KJAR service eg. http://rhpam-service-a-${APPS_NAMESPACE}-istio-system.${SUBDOMAIN} (where -a- is the KJAR/ALIAS name)
  * Virtual Service which separates requests based on LABEL to a "subset" of KIE Servers (TBD: Test if host in VirtualService .... which is K8s service name could be wildcard so that we have more than one service used)
  * Loadbalancing against KIE Servers with the same Label Version -whilst keeping an ALIAS based ROUTE- would result in transparrent KJAR usage (so long as client knows/can select based on previous request the LABEL Version used in that request)
  * New requests, or requests which would not require version specific activities would go to the LATEST version (ie. updaes of the VirtualService and new DestinationRule would be require based on current config)

* *Problems* unresolved or not tested for now
  * agreggation of queries like Smart Router would do against ALL KIE Servers.
  * containerId/Alias on URL to be combined with Label when doing specific wotk on Process/Task Instance




