/*******************************************************************************
 * fbproxy.php
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 *
 * Serves as a proxy between the Java applet and Facebook.
 * Note: I am not a PHP programmer, and this is simply a proof-of-concept.
 *
 * ============================================================================= 
 * Required parameters for all tasks
 * =============================================================================
 *
 *   do               Task to execute (currently supports 'refresh_profile', 
 *                                     'refresh_friends', and 'get_photo')
 *
 *   access_token     A valid Facebook access token for the user
 *
 * =============================================================================
 * Tasks
 * =============================================================================
 *
 * refresh_profile
 *
 *   Description
 *     Returns the user's profile details from Facebook in JSON format
 *
 *   Method
 *     GET
 *
 *   Additional Required Parameters
 *     None
 *
 *   Sample Request
 *     http://path/to/fbproxy.php?do=refresh_profile&access_token=AAAFSkncOSs...
 *
 *
 * refresh_friends
 *
 *   Description
 *     Returns the user's friend list from Facebook in JSON format
 *
 *   Method
 *     GET
 *
 *   Additional Required Parameters:
 *     None
 *
 *   Sample Request
 *     http://path/to/fbproxy.php?do=refresh_friends&access_token=AAAFSkncOS...
 *
 *
 * get_photo
 *
 *   Description
 *     Downloads the profile photo for the specified Facebook user
 *
 *   Method
 *     GET
 *
 *   Additional Required Parameters:
 *     uid             ID of the Facebook user for whom to download a photo
 *
 *   Sample Request
 *     http://path/to/fbproxy.php?do=get_photo&uid=55&access_token=AAAFSkncn...
 *     (Downloads the profile photo for user 55 on Facebook)
 *
 ******************************************************************************/
<?php

// Initialize our Facebook object
require_once 'includes/config.php.inc';

// If 'do' or 'access_token' were not passed, die with an error
if ((! isset($_GET['do'])) || (! isset($_GET['access_token'])))
{
  header('HTTP/1.1 403 Bad Request');
  die('Invalid request');
}

// Store the task to execute
$action = $_GET['do'];

// Store the token passed and set the current Facebook token accordingly
$token = $_GET['access_token'];
$facebook->setAccessToken($token);

switch ($action)
{
  // Refresh the user's profile details
  case "refresh_profile":

    // Try to get the details and print them out in JSON format
    try
    {
      $user_profile = $facebook->api('/me');
      echo json_encode($user_profile);
      exit;
    }
    catch (FacebookApiException $e)
    {
      header('HTTP/1.1 401 Forbidden');
      die($e->getMessage());
    }
    break;

  // Refresh the user's friend list
  case "refresh_friends":

    // Try to get the list and print it out in JSON format
    try
    {
      $friend_list = $facebook->api('/me/friends','GET');
      echo json_encode($friend_list);
      exit;
    }
    catch (FacebookApiException $e)
    {
      header('HTTP/1.1 401 Forbidden');
      die($e->getMessage());
    }
    break;

  // Get the photo of the specified user
  case "get_photo":

    // If a user ID wasn't passed, die with an error
    if (! isset($_GET['uid']))
    {
      header('HTTP/1.1 403 Bad Request');
      die('No user ID specified');
    }

    try
    {
      // Generate a unique filename for the image
      $filename = "photos/" . uniqid("img") . ".jpg";
      $uid = $_GET['uid'];

      // Download the image from Facebook and store it locally
      $ch = curl_init("https://graph.facebook.com/$uid/picture?type=large");
      $fp = fopen($filename, 'wb');

      curl_setopt($ch, CURLOPT_FILE, $fp);
      curl_setopt($ch, CURLOPT_HEADER, false);
      curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
      curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
      curl_setopt($ch, CURLOPT_MAXREDIRS, 10);

      curl_exec($ch);
      curl_close($ch);

      fclose($fp);

      // Send the file to the client.  We have to do it this way since the
      // applet cannot contact Facebook directly.

      header("Content-Type: image/jpeg");
      header("Content-Length: " . filesize($filename));
      readfile($filename);
    }
    catch (Exception $e)
    {
      header('HTTP/1.1 401 Forbidden');
      die($e->getMessage());
    }
    break;

  // Unknown task -- die with an error
  default:
    header('HTTP/1.1 403 Bad Request');
    die('Invalid action');
}

?>
