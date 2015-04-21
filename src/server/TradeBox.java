/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author kevinyu
 */
public class TradeBox {
    
    private ArrayList<Offer> mOffers;
    
    public TradeBox() {
        mOffers = new ArrayList<>();
    }
    
    public TradeBox(Document document) {
        loadFromJSON(document.toJson());
    }
    
    public TradeBox(String jsonString) {
        loadFromJSON(jsonString);
    }
    
    public ArrayList<Offer> getOffers() {
        return mOffers;
    }
    
    public void loadFromJSON(String jsonString) {
        JSONObject tradeBoxJSON = (JSONObject)JSONValue.parse(jsonString);
        JSONArray offersJSON = (JSONArray)tradeBoxJSON.get("offers");
        
        mOffers = new ArrayList<>();
        for (int i=0;i<offersJSON.size();i++) {
            JSONObject offerJSON = (JSONObject)offersJSON.get(i);
            mOffers.add(new Offer(offerJSON.toJSONString()));
        }
    }
    
    public void addOffer(Offer offer) {
        mOffers.add(offer);
    }
    
    public List<Object> getListRepresentation() {
        List<Object> list = new ArrayList<Object>();
        
        for (Offer offer : mOffers) {
            list.add(offer.getListRepresentation());
        }
        
        return list;
    }
    
    public Document toDocument() {
        Document document = new Document();
        
        ArrayList<Document> offersDocument = new ArrayList<>();
        for (int i=0;i<mOffers.size();i++) {
            offersDocument.add(mOffers.get(i).toDocument());
        }
        
        document.append("offers",offersDocument);
        return document;
    }
}
