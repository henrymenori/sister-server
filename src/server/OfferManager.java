package server;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author kevinyu
 */
public class OfferManager {
    
    MongoCollection<Document> mOfferCollection;
    
    public OfferManager(MongoDatabase database) {
        
        mOfferCollection = database.getCollection("offers");
        
    }
    
    public ArrayList<Offer> getOffersByUserId(ObjectId userId) {
        ArrayList<Offer> offers = new ArrayList<Offer>();
        
        MongoCursor<Document> cursor = mOfferCollection.find(eq(Offer.ATTR_USER_ID,userId)).iterator();
        try {
            while (cursor.hasNext()) {
                offers.add(new Offer(cursor.next()));
            }
        } finally {
            cursor.close();
        }
        
        return offers;
    }
    
    public ArrayList<Offer> getOffersByOfferedItem(int offeredItemCode) {
        ArrayList<Offer> offers = new ArrayList<Offer>();
        
        MongoCursor<Document> cursor = mOfferCollection.
                find(eq(Offer.ATTR_OFFERED_ITEM,offeredItemCode)).iterator();
        try {
            while (cursor.hasNext()) {
                offers.add(new Offer(cursor.next()));
            }
        } finally {
            cursor.close();
        }
        
        return offers;
    }
    
    public Offer getOfferByToken(String token) {
        Document offerDocument = mOfferCollection.
                find(eq(Offer.ATTR_TOKEN,token)).first();
        if (offerDocument == null) {
            return null;
        } else {
            return new Offer(offerDocument);
        }
    }
    
    public void addOffer(ObjectId userId,
            int offeredItemCode,int nOfferedItem,
            int demandedItemCode,int nDemandedItem) {
        Offer newOffer = new Offer(offeredItemCode, nOfferedItem, 
                demandedItemCode, nDemandedItem, userId);
        
        newOffer.insertTo(mOfferCollection);
    }
    
    public void acceptOffer(String offerToken) throws Exception {
        Offer offer = getOfferByToken(offerToken);
        if (offer.isAvailable())
            offer.setAvailability(false);
        else
            throw new Exception("offer is not available");
    }
    
    public List<Object> getTradeBox(ObjectId userId) {
        ArrayList<Offer> offers = getOffersByUserId(userId);
        List<Object> tradeBox = getOffersListRepresentation(offers);
        
        return tradeBox;
    }
    
    public List<Object> findOffer(int offeredItemCode) {
        List<Object> offersListRepresentation;
        
        ArrayList<Offer> offers = getOffersByOfferedItem(offeredItemCode);
        offersListRepresentation = getOffersListRepresentation(offers);
        
        return offersListRepresentation;
    }
    
    public void cancelOffer(String offerToken) throws Exception {
        Offer offer = getOfferByToken(offerToken);
        if (offer.isAvailable()) offer.removeFrom(mOfferCollection);
        else throw new Exception("Cannot cancel offer because it has been accepted");
    }
    
    
    private List<Object> getOffersListRepresentation(ArrayList<Offer> offers) {
        List<Object> offersListRepresentation = new ArrayList<>();
        
        for (Offer offer : offers) {
            offersListRepresentation.add(offer.getListRepresentation());
        }
        
        return offersListRepresentation;
    }
}
