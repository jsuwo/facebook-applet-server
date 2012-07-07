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
<?php

// Initialize our Facebook object
require_once 'includes/config.php.inc';

// Get the current user
$user = $facebook->getUser();

if ($user)
{

  // Try to load the user's profile details
  try
  {
    $user_profile = $facebook->api('/me');
  } catch (FacebookApiException $e) {
    error_log($e);
    $user = null;
  }
}

include 'includes/header.html.inc';

// If the user was loaded, display the applet
if ($user): 
  include 'includes/applet.html.inc';
else:
?>
  Dude, you're not even logged in!
<?php 
endif;

include 'includes/footer.html.inc';
?>
