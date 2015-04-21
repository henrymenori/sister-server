/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import java.math.BigInteger;
import java.security.MessageDigest;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author kevinyu
 */
public class User {
    private String mUsername;
    private String mPassword;
    private String mToken;
    private boolean mIsLogin;
    private Inventory mInventory;
    private int mPositionX,mPositionY;
    private ObjectId mId;
    
    public static final String ATTR_ID = "_id";
    public static final String ATTR_USERNAME = "username";
    public static final String ATTR_PASSWORD = "password";
    public static final String ATTR_TOKEN = "token";
    public static final String ATTR_ISLOGIN = "isLogin";
    public static final String ATTR_INVENTORY = "inventory";
    public static final String ATTR_POSITIONX = "positionX";
    public static final String ATTR_POSITIONY = "positionY";
    public static final String ATTR_TRADEBOX = "tradeBox";
    
    public User(Document mongoUser) {
        mId = mongoUser.getObjectId(ATTR_ID);
        loadFromJSONString(mongoUser.toJson());
    }
    
    private void loadFromJSONString(String userJSONString) {
        JSONObject userJSON = (JSONObject) JSONValue.parse(userJSONString);
        
        mUsername = (String) userJSON.get(ATTR_USERNAME);
        mPassword = (String) userJSON.get(ATTR_PASSWORD);
        mToken = (String) userJSON.get(ATTR_TOKEN);
        mIsLogin = (Boolean) userJSON.get(ATTR_ISLOGIN);
        mPositionX = (int) (long) userJSON.get(ATTR_POSITIONX);
        mPositionY = (int) (long) userJSON.get(ATTR_POSITIONY);
        
        JSONObject inventoryJSON = (JSONObject) userJSON.get(ATTR_INVENTORY);
        mInventory = new Inventory(inventoryJSON.toJSONString()); 
        
    }
    
    public User(String username,String password) {
        mUsername = username;
        mPassword = password;
        mToken = null;
        mIsLogin = false;
        mInventory = new Inventory();
        mPositionX = 0;
        mPositionY = 0;
    }
    
    public void login() {
        if (mIsLogin) return;
        mIsLogin = true;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(mUsername.getBytes());
            mToken = String.format("%032X", new BigInteger(1, messageDigest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void logout() {
        mIsLogin = false;
        mToken = null;
    }
    
    public ObjectId getId() {
        return mId;
    }
    
    public String getUsername() {
        return mUsername;
    }
    
    public String getPassword() {
        return mPassword;
    }
    
    public String getToken() {
        return mToken;
    }
    
    public int getPositionX() {
        return mPositionX;
    }
    
    public int getPositionY() {
        return mPositionY;
    }
    
    public Inventory getInventory() {
        return mInventory;
    }
    
    public void addItem(int itemCode) {
        mInventory.addItem(itemCode);
    }
    
    public synchronized void moveTo(int positionX, int positionY) {
        mPositionX = positionX;
        mPositionY = positionY;
    }
    
    public void insertTo(MongoCollection<Document> userCollection) {
        System.out.println(this.toDocument().toJson());
        userCollection.insertOne(this.toDocument());
        System.out.println("Finsih insert");
    }
    
    public void saveTo(MongoCollection<Document> userCollection) {
        userCollection.updateOne(eq(ATTR_USERNAME,mUsername),
                new Document("$set",this.toDocument()));
    }
    
    public Document toDocument() {
        Document userDocument = new Document();
        userDocument.append(ATTR_USERNAME, mUsername)
                .append(ATTR_PASSWORD, mPassword)
                .append(ATTR_TOKEN,mToken)
                .append(ATTR_ISLOGIN,mIsLogin)
                .append(ATTR_POSITIONX, mPositionX)
                .append(ATTR_POSITIONY,mPositionY)
                .append(ATTR_INVENTORY,mInventory.toDocument());
        return userDocument;
    }
    
}
