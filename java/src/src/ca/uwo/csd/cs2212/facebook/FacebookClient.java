package ca.uwo.csd.cs2212.facebook;

import ca.uwo.csd.cs2212.facebook.events.FacebookErrorEvent;
import ca.uwo.csd.cs2212.facebook.events.FacebookEventListener;
import ca.uwo.csd.cs2212.facebook.events.FacebookPhotoEvent;
import ca.uwo.csd.cs2212.facebook.events.FacebookProfileEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/*******************************************************************************
 * FacebookClient.java 
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 *
 * Helper class for accessing various Facebook functionality indirectly via the
 * proxy script.
 * 
 * Dependencies:
 * 
 *   The Apache HttpComponents library is used for all HTTP requests
 *   http://hc.apache.org/
 *   http://hc.apache.org/httpcomponents-client-ga/tutorial/html/
 * 
 *   The simple-json library for parsing JSON responses
 *   http://code.google.com/p/json-simple/
 *   http://code.google.com/p/json-simple/wiki/DecodingExamples
 *   
 ******************************************************************************/
public class FacebookClient {

    // The use of the simple-json parser raises some warnings
    @SuppressWarnings("unchecked")
    
    // The user's authentication token
    private final String authToken;
    
    // List of listeners subscribing to events from this class
    private final List<FacebookEventListener> listeners;
    
    // The hostname of the proxy script
    private final String hostname;
    
    // The path to the proxy script
    private final String script;

    /**
     * Initializes the client.
     *
     * @param hostname Hostname of the server hosting the proxy script
     * @param script Path to the script on the server
     * @param authToken The user's authentication token
     */
    public FacebookClient(String hostname, String script, String authToken) {
        this.hostname = hostname;
        this.script = script;
        this.listeners = new ArrayList<FacebookEventListener>();
        this.authToken = authToken;
    }

    /**
     * Subscribes the specified listener to event notifications from this class
     *
     * @param listener Listener to accept event notifications from this class
     */
    public synchronized void addEventListener(FacebookEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Unsubscribes the specified listener from event notifications from this
     * class
     *
     * @param listener Listener to be removed from event notifications
     */
    public synchronized void removeEventListener(FacebookEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the current user's profile from Facebook via the server proxy.
     * This task is completed in a separate thread and any FacebookEventListeners
     * are notified when the operation is complete.
     */
    public void refreshUserProfile() {
        
        // Start a new thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    
                    // Get the user's profile
                    Map<String, String> profileData = getUserProfile();
                    Profile profile = new Profile(profileData);

                    // Get the user's friend list
                    List<Profile> friends = getFriendList();

                    // Sort it by name
                    Collections.sort(friends, new Comparator<Profile>() {

                        @Override
                        public int compare(Profile o1, Profile o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });

                    profile.addFriends(friends);

                    // Get the user's profile photo
                    ImageIcon photo = getUserPhoto(profile.getID());
                    profile.setPhoto(photo);

                    // Notify any listeners
                    fireProfileEvent(profile);
                } 
                catch (Exception ex) {
                    
                    // If an error occurred, notify any listeners
                    fireErrorEvent(ex);
                }
            }
        }).start();
    }
    
    /**
     * Gets the specified user's profile photo from Facebook via the server proxy.
     * This task is completed in a separate thread and any FacebookEventListeners
     * are notified when the operation is complete.
     * @param uid ID of the Facebook user whose profile photo should be downloaded
     */
    public void refreshUserPhoto(final String uid) {
        
        // Start a new thread
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    
                    // Get the photo and notify any listeners
                    ImageIcon photo = getUserPhoto(uid);
                    firePhotoEvent(photo);
                } 
                catch (Exception ex) {
                    
                    // If an error occurred, notify any listeners 
                    fireErrorEvent(ex);
                }
            }
        }).start();
    }

    /**
     * Notifies any FacebookEventListeners of an exception that occurred 
     * while performing a Facebook operation in another thread.
     * @param ex Exception that occurred
     */
    private synchronized void fireErrorEvent(final Exception ex) {

        // Run this on the event dispatching thread
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                FacebookErrorEvent evt = new FacebookErrorEvent(this, ex);
                Iterator<FacebookEventListener> it = listeners.iterator();

                while (it.hasNext()) {
                    it.next().errorReceived(evt);
                }
            }
        });
    }

    /**
     * Notifies any FacebookEventListeners when a profile is successfully
     * downloaded.
     * @param p The profile downloaded
     */
    private synchronized void fireProfileEvent(final Profile p) {

        // Run this on the event dispatching thread
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                FacebookProfileEvent evt = new FacebookProfileEvent(this, p);
                Iterator<FacebookEventListener> it = listeners.iterator();

                while (it.hasNext()) {
                    it.next().profileDataReceived(evt);
                }
            }
        });
    }

    /**
     * Notifies any FacebookEventListeners when a profile photo is successfully
     * downloaded.
     * @param photo The photo downloaded
     */    
    private synchronized void firePhotoEvent(final ImageIcon photo) {
        
        // Run this on the event dispatching thread
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                FacebookPhotoEvent evt = new FacebookPhotoEvent(this, photo);
                Iterator<FacebookEventListener> it = listeners.iterator();

                while (it.hasNext()) {
                    it.next().photoReceived(evt);
                }
            }
        });
    }

    /**
     * Contacts the proxy script to download the current user's profile data.
     * @return A map containing the user's profile data
     * @throws Exception If communication fails with the server
     */
    private Map<String, String> getUserProfile() throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        URIBuilder builder = new URIBuilder();
        
        StringBuilder response = new StringBuilder();
        String line;
        
        // http://hostname/script.php?do=refresh_profile&access_token=AAAWQER...
        builder.setScheme("http").
                setHost(hostname).
                setPath(script).
                setParameter("do", "refresh_profile").
                setParameter("access_token", this.authToken);

        // Perform the request via GET and expect to receive a JSON response
        HttpGet getRequest = new HttpGet(builder.build());
        getRequest.addHeader("accept", "application/json");

        // Execute the request and get the server response
        HttpResponse res = httpClient.execute(getRequest);
        
        BufferedReader br = new BufferedReader(
                new InputStreamReader((res.getEntity().getContent())));

        // Read the response
        while ((line = br.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }

        // Close the connection
        httpClient.getConnectionManager().shutdown();

        // If we didn't get a 200 status code, throw an exception
        if (res.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("HTTP " + res.getStatusLine().getStatusCode() + " error: " + response.toString());
        } 
        else {
            
            // Otherwise, parse the JSON response using the simple-json library
            // and return the resultant map
            return (Map<String, String>) JSONValue.parse(response.toString());
        }
    }

    /**
     * Contacts the proxy script to download the current user's friend list.
     * @return A list of profiles representing the users friends.  The profiles
     *         downloaded are not complete profiles -- they consist simply of
     *         each friend's name and ID.
     * @throws Exception If communication fails with the server
     */
    private List<Profile> getFriendList() throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        URIBuilder builder = new URIBuilder();

        StringBuilder response = new StringBuilder();
        String line;

        // http://hostname/script.php?do=refresh_friends&access_token=AAAAQWE...
        builder.setScheme("http").
                setHost(hostname).
                setPath(script).
                setParameter("do", "refresh_friends").
                setParameter("access_token", this.authToken);

        // Perform the request via GET and expect to receive a JSON response
        HttpGet getRequest = new HttpGet(builder.build());
        getRequest.addHeader("accept", "application/json");

        // Execute the request and get the server response
        HttpResponse res = httpClient.execute(getRequest);

        BufferedReader br = new BufferedReader(
                new InputStreamReader((res.getEntity().getContent())));

        // Read the response
        while ((line = br.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }

        // Close the connection
        httpClient.getConnectionManager().shutdown();

        // If we didn't get a 200 status code, throw an exception
        if (res.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("HTTP " + res.getStatusLine().getStatusCode() + " error: " + response.toString());
        } 
        else {
            
            // Otherwise, parse the JSON array and get the list of friends
            JSONObject obj = (JSONObject) JSONValue.parse(response.toString());
            JSONArray friendArray = (JSONArray) obj.get("data");
            List<Profile> friends = new ArrayList<Profile>();

            // For each friend in the response, create a Profile and add it to
            // the list to be returned
            for (Object o : friendArray) {
                friends.add(new Profile((Map<String, String>) o));
            }

            
            // Return the friend list
            return friends;
        }
    }

    /**
     * Contacts the proxy script to download the specified user's profile photo.
     * @param uid User whose profile photo is to be downloaded
     * @return The profile photo of the specified user
     * @throws Exception If communication fails with the server
     */
    private ImageIcon getUserPhoto(String uid) throws Exception {

        HttpClient httpClient = new DefaultHttpClient();
        URIBuilder builder = new URIBuilder();
        
        StringBuilder response = new StringBuilder();
        String line;

        // http://hostname/script.php?do=get_photo&uid=XXX&access_token=AAACCD..
        builder.setScheme("http").
                setHost(hostname).
                setPath(script).
                setParameter("do", "get_photo").
                setParameter("uid", uid).
                setParameter("access_token", this.authToken);

        // Perform the request via GET
        HttpGet getRequest = new HttpGet(builder.build());
        
        // Execute the request and get the server response
        HttpResponse res = httpClient.execute(getRequest);

        // If we didn't get a 200 status code, read the error message from the
        // server and throw an exception
        if (res.getStatusLine().getStatusCode() != 200) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((res.getEntity().getContent())));

            while ((line = br.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }

            throw new RuntimeException("HTTP " + res.getStatusLine().getStatusCode() + " error: " + response.toString());
        }

        // Otherwise, the data in the server response should be the binary photo
        // data.  Read it into a byte array.
        byte[] imageData = new byte[(int) res.getEntity().getContentLength()];
        DataInputStream stream = new DataInputStream(res.getEntity().getContent());
        stream.readFully(imageData);

        // Close the connection and create an ImageIcon from the bytes retrieved
        // from the server
        httpClient.getConnectionManager().shutdown();
        return new ImageIcon(imageData);
    }
}