/*******************************************************************************
 * config.php.inc
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 *
 * Sets up the Facebook object.  Replace the values for appId and secret with
 * the values appropriate for your application.
 *
 ******************************************************************************/
<?php

// Load the SDK
require_once 'sdk/facebook.php';

// Put your application config here
$config = array();
$config['appId'] = 'CHANGE ME';
$config['secret'] = 'CHANGE ME';
$config['fileUpload'] = false;

// Create the Facebook object
$facebook = new Facebook($config);
?>
