  <script src="https://www.java.com/js/deployJava.js"></script>
  <script>
    var attributes = {
      code:       "ca.uwo.csd.cs2212.FacebookApplet",
      archive:    "CS2212_Facebook_Applet.jar, lib/commons-codec-1.6.jar, " +
                  "lib/commons-logging-1.1.1.jar, lib/fluent-hc-4.2.1.jar, " +
                  "lib/httpclient-4.2.1.jar, lib/httpclient-cache-4.2.1.jar, " + 
                  "lib/httpcore-4.2.1.jar, lib/httpmime-4.2.1.jar, " +
                  " lib/json-simple-1.1.1.jar",
      width:      600,
      height:     800
    };
  
    var parameters = {
      jnlp_href:"https://<?= $_SERVER["HTTP_HOST"] ?>/facebook/java/launch.jnlp",
      token: "<?= $facebook->getAccessToken() ?>",
      proxy_script: "/facebook/fbproxy.php"
    }; 
  
    var version = "1.6";
    deployJava.runApplet(attributes, parameters, version);
  </script>
