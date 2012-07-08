package ca.uwo.csd.cs2212.facebook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

/*******************************************************************************
 * Profile.java
 * Author: Jeff Shantz <x@y, x = jshantz4, y = csd.uwo.ca>
 * 
 * Represents a user's Facebook profile
 ******************************************************************************/
public class Profile {
    
    // Parsed JSON data returned from the server
    private final Map<String, String> profileData;
    
    // The user's friends
    private List<Profile> friends;
    
    // Photo of the user
    private ImageIcon photo;
    
    /**
     * Builds the profile from the parsed JSON response from the server.
     * @param profileData The map resulting from parsing the JSON server response
     */
    public Profile(Map<String, String> profileData) {
        this.profileData = profileData;  
        this.friends = new ArrayList<Profile>();
    }
    
    public String getID()
    {
        return this.profileData.get("id");
    }
    
    public String getName()
    {
        return this.profileData.get("name");
    }
    
    public String getFirstName()
    {
        return this.profileData.get("first_name");
    }
    
    public String getLastName()
    {
       return this.profileData.get("last_name"); 
    }
    
    public String getLink()
    {
       return this.profileData.get("link"); 
    }
    
    public String getUsername()
    {
        return this.profileData.get("username");
    }
    
    public String getBirthday() 
    {
        return this.profileData.get("birthday");
    }
   
    public String getGender()
    {
        return this.profileData.get("gender");
    }
    
    public String getRelationshipStatus()
    {
        return this.profileData.get("relationship_status");
    }
    
    public String getPolitical()
    {
        return this.profileData.get("political");
    }
    
    public String getWebSite()
    {
        return this.profileData.get("website");
    }
    
    public String getQuote()
    {
        return this.profileData.get("quotes");
    }
    
    public void addFriend(Profile friend)
    {
        this.friends.add(friend);
    }
    
    public void addFriends(Iterable<Profile> friends)
    {
        for (Profile friend : friends)
            this.addFriend(friend);
    }    
    
    public List<Profile> getFriends()
    {
        return this.friends;
    }
    
    public void setPhoto(ImageIcon photo)
    {
        this.photo = photo;
    }
    
    public ImageIcon getPhoto()
    {
        return this.photo;
    }
    
    @Override
    public String toString()
    {
        return this.getName();
    }
}
