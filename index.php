<?php
/*******************************************************************************
 * index.php
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 *
 * Main page of the Facebook app.  Checks if the user is logged into Facebook.
 * If so, the applet is displayed.  Otherwise, the user receives a message
 * indicating that he/she should login.
 *
 * The user's Facebook access token is passed as a parameter to the applet.
 * This allows the applet to call fbproxy.php and pass the access token in 
 * order to authorize subsequent calls to Facebook.  See 
 * includes/applet.html.inc.
 *
 * Note: I am not a PHP programmer, and this is simply a proof-of-concept.
 *
 ******************************************************************************/

// Initialize our Facebook object
require_once 'includes/config.php.inc';

include 'includes/header.html.inc';

// If the user was loaded, display the applet
if ($facebook->getUser()): 
  include 'includes/applet.html.inc';
else:

// Otherwise, redirect the user to get authorization for the app
$loginUrl = $facebook->getLoginUrl(
    array(
      'canvas' => true,
      'fbconnect' => false,
      'redirect_uri' => $app_url,
      //'scope' => 'read_stream, friends_likes'     // Additional permissions can be requested here, see https://developers.facebook.com/docs/authentication/permissions/
      )
    );
?>

<!-- Redirect the user to the authorization URL via JavaScript -->
<script type="text/javascript">
  top.location="<?= $loginUrl ?>";
</script>

<!-- If they don't have JavaScript enabled, show a link -->
Click <a href="<?= $loginUrl ?>">here</a> to access the app.

<?php 
endif;

include 'includes/footer.html.inc';
?>
