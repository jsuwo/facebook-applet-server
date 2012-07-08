<?php
/*******************************************************************************
 * config.php.inc
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 *
 * Sets up the Facebook object.  Replace the values for appId and secret with
 * the values appropriate for your application.
 *
 ******************************************************************************/

// Load the SDK
require_once 'sdk/facebook.php';

// Put your application config here
$config = array();
$config['appId'] = 'YOUR_FACEBOOK_APP_ID';
$config['secret'] = 'YOUR_FACEBOOK_APP_SECRET';
$config['fileUpload'] = false;

// Create the Facebook object
$facebook = new Facebook($config);
?>
