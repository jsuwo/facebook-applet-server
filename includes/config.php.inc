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

// App details -- CHANGE THESE
$facebook = new Facebook(array(
  'appId'  => 'YOUR APP ID HERE',
  'secret' => 'YOUR APP SECRET HERE',
  'cookie' => true
));

// URL of your Facebook app -- CHANGE THIS
$app_url = 'YOUR CANVAS URL HERE';
?>
