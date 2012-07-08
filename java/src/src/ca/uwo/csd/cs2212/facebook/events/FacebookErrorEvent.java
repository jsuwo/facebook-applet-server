package ca.uwo.csd.cs2212.facebook.events;

/*******************************************************************************
 * FacebookErrorEvent.java 
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 *
 * Object passed to a FacebookEventListener when an error occurs while
 * communicating with the Facebook proxy script.
 ******************************************************************************/
public class FacebookErrorEvent extends java.util.EventObject {
    private final Exception exception;
    
    public FacebookErrorEvent(Object source, Exception ex) {
        super(source);
        this.exception = ex;
    }
    
    public Exception getException() {
        return this.exception;
    }
}