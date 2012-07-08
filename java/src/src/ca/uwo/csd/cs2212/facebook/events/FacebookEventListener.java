package ca.uwo.csd.cs2212.facebook.events;

/*******************************************************************************
 * FacebookEventListener.java 
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 *
 * Interface for a class that wishes to receive notifications from the
 * FacebookClient.
 ******************************************************************************/
public interface FacebookEventListener {

    /**
     * Called when a user's profile has been successfully downloaded.
     * @param e Profile details
     */
    public void profileDataReceived(FacebookProfileEvent e);

    /**
     * Called when an error occurs while attempting to communicate with the
     * Facebook proxy script.
     * @param e Error details
     */
    public void errorReceived(FacebookErrorEvent e);

    /**
     * Called when a user's profile photo has been successfully downloaded.
     * @param e Photo details
     */
    public void photoReceived(FacebookPhotoEvent e);
}
