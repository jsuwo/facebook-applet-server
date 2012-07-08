package ca.uwo.csd.cs2212.facebook.events;

import javax.swing.ImageIcon;

/*******************************************************************************
 * FacebookPhotoEvent.java 
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 *
 * Object passed to a FacebookEventListener when a profile photo has been 
 * successfully downloaded.
 ******************************************************************************/
public class FacebookPhotoEvent extends java.util.EventObject {
    
    private final ImageIcon photo;
   
    public FacebookPhotoEvent(Object source, ImageIcon photo) {
        super(source);
        this.photo = photo;
    }
    
    public ImageIcon getPhoto() {
        return this.photo;
    }
}
