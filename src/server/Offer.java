/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author kevinyu
 */
public class Offer {
    private int mOfferedItemCode;

   
    private int mNOfferedItem;
    private int mDemandedItemCode;
    private int mNDemandedItem;
    private boolean mAvailability;
    private UUID mToken;
    private ObjectId mUserId;
    
    public static String ATTR_OFFERED_ITEM = "offeredItem";
    public static String ATTR_NUMBER_OF_OFFERED_ITEM = "nOfferedItem";
    public static String ATTR_DEMANDED_ITEM = "demandedItem";
    public static String ATTR_NUMBER_OF_DEMANDED_ITEM = "nDemandedItem";
    public static String ATTR_AVAILABILITY = "availability";
    public static String ATTR_TOKEN = "token";
    public static String ATTR_USER_ID = "userId";
    
    public Offer(int offeredItemCode, int nOfferedItem,int demandedItemCode, int nDemandedItem,ObjectId userId) {
        mOfferedItemCode = offeredItemCode;
        mNOfferedItem = nOfferedItem;
        mDemandedItemCode = demandedItemCode;
        mNDemandedItem = nDemandedItem;
        mAvailability = true;
        mUserId = userId;
        
        mToken = UUID.randomUUID();
    }
    
    public Offer(Document document) {
        mUserId = document.getObjectId(ATTR_USER_ID);
        loadFromJSON(document.toJson());
    }
    
    public Offer(String jsonString) {
        loadFromJSON(jsonString);
    }
    
    public void loadFromJSON(String jsonString) {
        JSONObject offerJSON = (JSONObject)JSONValue.parse(jsonString);
        
        mOfferedItemCode = (int)(long)offerJSON.get(ATTR_OFFERED_ITEM);
        mNOfferedItem = (int)(long)offerJSON.get(ATTR_NUMBER_OF_OFFERED_ITEM);
        mDemandedItemCode = (int)(long)offerJSON.get(ATTR_DEMANDED_ITEM);
        mNDemandedItem = (int)(long)offerJSON.get(ATTR_NUMBER_OF_DEMANDED_ITEM);
        mAvailability = (boolean)offerJSON.get(ATTR_AVAILABILITY);
        mToken = UUID.fromString((String)(offerJSON.get(ATTR_TOKEN)));
        
    }
    
    public Document toDocument() {
        Document document = new Document();
        
        document.append(ATTR_OFFERED_ITEM,mOfferedItemCode)
                .append(ATTR_NUMBER_OF_OFFERED_ITEM,mNOfferedItem)
                .append(ATTR_DEMANDED_ITEM,mDemandedItemCode)
                .append(ATTR_NUMBER_OF_DEMANDED_ITEM,mNDemandedItem)
                .append(ATTR_AVAILABILITY,mAvailability)
                .append(ATTR_TOKEN, mToken.toString())
                .append(ATTR_USER_ID, mUserId);
        
        return document;
    }
    
    public ObjectId getUserId() {
        return mUserId;
    }
    
    public int getOfferedItemCode() {
        return mOfferedItemCode;
    }

    public int getNOfferedItem() {
        return mNOfferedItem;
    }

    public int getDemandedItemCode() {
        return mDemandedItemCode;
    }

    public int getNDemandedItem() {
        return mNDemandedItem;
    }

    public boolean isAvailable() {
        return mAvailability;
    }
    
    public void setAvailability(boolean availability) {
        mAvailability = availability;
    }
    
    public List<Object> getListRepresentation() {
        List<Object> list = new ArrayList<Object>();
        
        list.add(mOfferedItemCode);
        list.add(mNOfferedItem);
        list.add(mDemandedItemCode);
        list.add(mNDemandedItem);
        list.add(mAvailability);
        list.add(mToken.toString());
        
        return list;
    }
    
    public void insertTo(MongoCollection<Document> offerCollection) {
        offerCollection.insertOne(this.toDocument());
    }
    
    public void saveTo(MongoCollection<Document> offerCollection) {
        offerCollection.updateOne(eq(ATTR_TOKEN,mToken),
                new Document("$set",this.toDocument()));
    }
    
    public void removeFrom(MongoCollection<Document> offerCollection) {
        offerCollection.deleteOne(this.toDocument());
    }
    
}
