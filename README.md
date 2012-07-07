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
    git clone git://github.com/jsuwo/facebook-applet-server.git

3. Rename the directory to `facebook`:
    mv facebook-applet-server facebook

4. Ensure permissions are set properly:
    chown -R root:root facebook
    find -type f | xargs chmod 644
    find -type d | xargs chmod 755

5. Change to the `facebook` directory and make the `photos` directory writeable
   to all:
    cd facebook
    chmod 777 photos

6. Edit the file `includes/config.php.inc` and fill in your Facebook app details

7. Edit the file `java/launch.jnlp` and change the codebase attribute to reflect
   the hostname of your server.  Be sure to use `HTTPS`
   
8. Edit your application configuration on Facebook and change the following 
   settings:

   * Canvas URL: http://YOUR_HOSTNAME/facebook/
   * Secure Canvas URL: https://YOUR_HOSTNAME/facebook/

9. Save your changes and visit your app's page.  The applet should run and all
   should work.

Troubleshooting
===============

If you're using a self-signed SSL certificate, you need to make sure that either
the server's certificate or the certificate of the root authority is imported
into your browser's / operating system's list of trusted certificates, 
