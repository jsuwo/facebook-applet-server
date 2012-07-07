Overview
======================

This repository contains a proof-of-concept for indirect communication between a
Java applet and Facebook using PHP as a proxy.

The architecture is as follows:
  
  * The user visits the application on Facebook, and `index.php` is displayed

  * If the user is logged in to Facebook, `index.php` displays the applet
    * Otherwise the user is told to login to Facebook first

  * `index.php` passes the current user's authentication token to the applet

  * The applet loads and calls `fbproxy.php` on the server, passing it the
    Facebook authentication token that was passed to it, and requesting the
    user's profile details from Facebook

  * The details are retrieved by `fbproxy.php` and returned to the applet
    in JSON format, where they are displayed

  * Next, the applet calls `fbproxy.php` on the server, again passing it the
    Facebook authentication token passed to it, but this time requesting the
    user's list of friends on Facebook

  * The list is retrieved by `fbproxy.php` and returned to the applet in 
    JSON format, where it is displayed

  * Finally, the applet calls `fbproxy.php` on the server, requesting the
    user's profile photo.  Now, fbproxy.php could just return the URL of the
    user's profile photo on Facebook, but, of course, the applet cannot call
    Facebook due to the sandbox -- it can only call back the server.  To work
    around this, `fbproxy.php` downloads the image from Facebook and stores it
    on the server.  It then sends the image data to the applet where it is 
    displayed

  * Each time the user selects a friend from his/her friends list in the applet,
    the applet calls `fbproxy.php` on the serer, requesting the friend's photo.
    Once again, the phoot is downloaded to the server and returned to the applet
    where it is displayed.

Installation
======================

