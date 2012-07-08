package ca.uwo.csd.cs2212.facebook.events;

import ca.uwo.csd.cs2212.facebook.Profile;

/*******************************************************************************
 * FacebookProfileEvent.java 
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 *
 * Object passed to a FacebookEventListener when a profile has been successfully
 * downloaded.
 ******************************************************************************/
public class FacebookProfileEvent extends java.util.EventObject {
    
    private final Profile profile;
   
    public FacebookProfileEvent(Object source, Profile profile) {
        super(source);
        this.profile = profile;
    }
    
    public Profile getProfile() {
        return this.profile;
    }
}
