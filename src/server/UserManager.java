/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author kevinyu
 */
public class UserManager {
    private ArrayList<User> mUsers;
    private HashMap<String,User> mLoginUsers;
    private MongoCollection<Document> mUserCollection;
    
    public UserManager(MongoDatabase database) {
        mUserCollection = database.getCollection("user");
    }
    
    public User findUserByUsername(String username) {
        Document userDocument = mUserCollection.find(eq(User.ATTR_USERNAME,username)).first();
        if (userDocument == null) {
            return null;
        }
        else {
            return new User(userDocument);
        }
    }
    
    public User findUserByToken(String token) {
        Document userDocument = mUserCollection.find(eq(User.ATTR_TOKEN,token)).first();
        if (userDocument == null) {
            return null;
        } else {
            return new User(userDocument);
        }
        
    }
    
    public synchronized void signup(String username,String password) throws Exception {
        User user = this.findUserByUsername(username);
        if (user != null) {
            throw new Exception("username already exist");
        } else {
            user = new User(username,password);
            user.insertTo(mUserCollection);
        }
    }
    
    public synchronized User login(String username,String password) throws Exception {
        User user = this.findUserByUsername(username);
        
        if (user == null) throw new Exception("username not found");
        
        if (!user.getPassword().equals(password)) throw new Exception("password is invalid");
        
        user.login();
        user.saveTo(mUserCollection);
        return user;

    }
    
    public void moveUser(String token, int positionX,int positionY) throws Exception {
        User user = this.findUserByToken(token);
        
        if (user == null) throw new Exception("token not found");
        
        if (positionX < 0 || positionY <0 || 
                positionX >= Map.getInstance("map.json").getWidth() || 
                positionY >= Map.getInstance("map.json").getHeight()) {
            throw new Exception("Location is outside map boundary");
        } 
        
        user.moveTo(positionX,positionY);
        user.saveTo(mUserCollection);
    }
    
    public List<Integer> getUserInventory(String token) throws Exception {
        User user = this.findUserByToken(token);
        
        if (user == null) throw new Exception("token not found");
        
        return user.getInventory().getListRepresentation();
    }
    
    public int userTakeItem(String token) throws Exception {
        User user = this.findUserByToken(token);
        
        if (user == null) throw new Exception("token not found");
        
        int itemCode = Map.getInstance("map.json").getItem(user.getPositionX(),user.getPositionY());
        
        user.addItem(itemCode);
        user.saveTo(mUserCollection);
        
        return itemCode;
    }
    
    public void userMixItem(String token, int itemCode1, int itemCode2) throws Exception {
        User user = this.findUserByToken(token);
        
        if (user == null) throw new Exception("token not found");
        
        user.getInventory().mixItem(itemCode1,itemCode2);
        
        user.saveTo(mUserCollection);
    }
    
    public void removeOfferedItem(String token ,int offeredItemCode
            , int nOfferedItem) throws Exception {
        
        User user = this.findUserByToken(token);
        
        if (user == null) throw new Exception("token not found");
        
        if (!user.getInventory().isItemEnough(offeredItemCode, nOfferedItem))
            throw new Exception("Number of Offered Item in Inventory is not enough");
        
        user.getInventory().removeOfferedItem(offeredItemCode, nOfferedItem);
        
        user.saveTo(mUserCollection);
    }
    
    public void addCancelledOfferItem(String userToken,Offer offer) throws Exception {
        User user = this.findUserByToken(userToken);
        
        if (user == null) throw new Exception("user's token not found");
        
        if (offer.getUserId() == user.getId()) throw new Exception("user have no this offer");    
        
        user.getInventory().addItem(offer.getOfferedItemCode(),
                offer.getNOfferedItem());
        
    }
    
}
