Overview
========

This repository contains a proof-of-concept for indirect communication between a
Java applet and Facebook using PHP as a proxy.

The architecture is as follows:
  
  1. The user visits the application on Facebook, and `index.php` is displayed

  2. If the user is logged in to Facebook, `index.php` displays the applet
     * Otherwise the user is told to login to Facebook first

  3. `index.php` passes the current user's authentication token to the applet

  4. The applet loads and calls `fbproxy.php` on the server, passing it the
     Facebook authentication token that was passed to it, and requesting the
     user's profile details from Facebook

  5. The details are retrieved by `fbproxy.php` and returned to the applet
     in JSON format, where they are displayed

  6. Next, the applet calls `fbproxy.php` on the server, again passing it the
     Facebook authentication token passed to it, but this time requesting the
     user's list of friends on Facebook

  7. The list is retrieved by `fbproxy.php` and returned to the applet in 
     JSON format, where it is displayed

  8. Finally, the applet calls `fbproxy.php` on the server, requesting the
     user's profile photo.  Now, fbproxy.php could just return the URL of the
     user's profile photo on Facebook, but, of course, the applet cannot call
     Facebook due to the sandbox -- it can only call back the server.  To work
     around this, `fbproxy.php` downloads the image from Facebook and stores it
     on the server.  It then sends the image data to the applet where it is 
     displayed

  9. Each time the user selects a friend from his/her friends list in the applet,
     the applet calls `fbproxy.php` on the serer, requesting the friend's photo.
     Once again, the phoot is downloaded to the server and returned to the applet
     where it is displayed.

Installation
============

These instructions assume your web server root is at `/var/www/`.

1. SSH into your server and switch to root

2. Change to `/var/www` and clone the repository:
    ````
    git clone git://github.com/jsuwo/facebook-applet-server.git
    ````

3. Rename the directory to `facebook`:
    ````
    mv facebook-applet-server facebook
    ````

4. Ensure permissions are set properly:
    ````
    chown -R root:root facebook            # Make root the owner of all files/directories (should be anyway)
    chmod 755 facebook                     # Give the 'facebook' directory 755 permissions
    cd facebook                            # Change to the 'facebook' directory
    find -type f | xargs chmod 644         # Give all files 644 permissions
    find -type d | xargs chmod 755         # Give all directories 755 permissions
    ````

5. Make the `photos` directory writeable to all so that the PHP script can download profile photos from
   Facebook and save them there:
    ````
    chmod 777 photos
    ````

6. Edit the file `includes/config.php.inc` and fill in your Facebook app details.
   Specifically, you will need to specify the `appId` and `secret` for your app,
   which can be obtained from https://developers.facebook.com/apps
   ````php
  $facebook = new Facebook(array(
    'appId'  => 'YOUR APP ID HERE',
    'secret' => 'YOUR APP SECRET HERE',
    'cookie' => true
  ));
  ````
  
  Additionally, you will need to specify the URL for your app on Facebook in the `app_url`
  variable.  This is listed under the heading *Canvas Page* in your app summary at 
  https://developers.facebook.com/apps

  ````php
  $app_url = 'YOUR CANVAS URL HERE';
  ````
   
  For example, if your app is named `testqwe`, then you would enter

  ````php
  $app_url = 'https://apps.facebook.com/testqwe/';
  ````

  Notice that this is the URL of your app *on Facebook* -- not the URL of your server.

7. Edit the file `java/launch.jnlp` and change the codebase attribute to reflect
   the hostname of your server.  Be sure to use `HTTPS`.  Don't forget the `/facebook/java`
   at the end of the URL.  For example, if your hostname is `example.com`, then the entire
   line should read:

   ````xml
   <jnlp codebase="https://example.com/facebook/java" href="launch.jnlp" spec="1.0+">
   ````
 
    Notice that `https` is being used, and the path `/facebook/java` has been specified
    after the hostname.
   
8. Edit your application configuration on Facebook and change the following 
   settings:
   * _Canvas URL:_ `http://YOUR_HOSTNAME/facebook/`
   * _Secure Canvas URL:_ `https://YOUR_HOSTNAME/facebook/`

9. Save your changes and visit your app's page.  The applet should run and all
   should work.

Troubleshooting / Tips
======================

* If you're using a self-signed SSL certificate, you need to make sure that either
  the server's certificate or the certificate of the root authority is imported
  into your browser's / operating system's list of trusted certificates, 

* It is immensely useful to see what Facebook is returning to your PHP script.  
  You can print out the contents of a variable in PHP using the following:

  ````
  print_r($my_var);
  ````

* It is also extremely useful to test the PHP script to see what it is returning
  to your applet.  You can do so by simply visiting the `fbproxy.php` script in
  your browser.  For example, if I wanted to see what the `refresh_profile` task
  returns, I can just go to:

  ````
  http://hostname/facebook/fbproxy.php?do=refresh_profile&access_token=MY_ACCESS_TOKEN
  ````

  Similarly, to get my friend list, I can go to:

  ````
  http://hostname/facebook/fbproxy.php?do=refresh_friends&access_token=MY_ACCESS_TOKEN
  ````

  Of course, in order to properly authenticate, you will need to find out your
  Facebook access token.  You can do this by writing a quick PHP script to print out
  your token.  Create a script `print_token.php` and place it in the same directory
  as `fbtoken.php`:

  ````
  <?php
  require_once 'includes/config.php.inc';  
  echo $facebook->getAccessToken();
  ?>
  ````

* My system kept caching my old applet code (even though I disabled this in the 
  Java control panel) and wouldn't display new changes to the applet in my browser
  until I killed my Java process (i.e. simply closing the tab and reopening the page
  on a new tab was not sufficient).  YMMV here.

* I put an error console in the applet that should display any exceptions that occur
  in the applet.  Still, if nothing is showing up and your Java console doesn't show
  anything, then check out the web server's error log:

  ````
  cat /var/log/apache/error.log
  ````

  You can poll for changes in the error log by issuing the following command.  Then
  go and run your script and see if anything appears in the log.  Press `Ctrl+C` to
  stop polling.

  ````
  tail -f /var/log/apache/error.log
  ````

Applet Source Code
==================

The source code for the applet is located in the `java/src` directory.  If you 
are going to edit it, I *highly* recommend doing so in NetBeans since, otherwise,
you won't be able to use a GUI editor to design `FacebookApplet.java`.

The main classes are:

* `ca.uwo.csd.cs2212.FacebookApplet`
  * Main applet class

* `ca.uwo.csd.cs2212.facebook.FacebookClient`

  * Used to communicate with the Facebook proxy script (i.e. the PHP script on
    the server).  This script uses the following libraries:

    * Apache HttpComponents (http://hc.apache.org/) for all HTTP communication.
      See http://hc.apache.org/httpcomponents-client-ga/tutorial/html/ for a 
      tutorial.

    * simple-json (http://code.google.com/p/json-simple/) for parsing JSON
      server responses.  See http://code.google.com/p/json-simple/wiki/DecodingExamples
      for examples of decoding JSON responses using the library.

* The rest of the classes are fairly minor.  All have been commented at least to
  some extent, so you can see their purpose in the source code.

Resources
=========

* Check out the Graph API documentation at https://developers.facebook.com/docs/reference/api/.

* If you want to do more than just grabbing data from Facebook (e.g. posting a
  message to a wall), then you will need to request extended permissions.  See 
  the documentation at https://developers.facebook.com/docs/authentication/permissions/#user_friends_perms.
