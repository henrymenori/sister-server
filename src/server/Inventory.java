/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author kevinyu
 */
public class Inventory {
    
    private int[] mInventory;
    
    public Inventory(Document document) {
        loadFromJSON(document.toString());
        
    }
    
    public Inventory(String jsonString) {
        loadFromJSON(jsonString);
    }
    
    public Inventory() {
        mInventory = new int[Item.ITEM_KIND_NUMBER];
        
        for (int i=0;i<Item.ITEM_KIND_NUMBER;i++) {
            mInventory[i] = 0;
        }
    }
    
    
    public void loadFromJSON(String jsonString) {
        JSONObject inventoryJSON = (JSONObject) JSONValue.parse(jsonString);
        List<Long> listItem = (List<Long>) inventoryJSON.get("items");
        
        mInventory = new int[listItem.size()];
        for (int i=0;i<listItem.size();i++) {
            mInventory[i] = (int)(long) listItem.get(i);
        }
    }
    
    public void mixItem(int itemCode1,int itemCode2) throws Exception {
        if (mInventory[itemCode1] < 3 || mInventory[itemCode2] < 3) 
            throw new Exception("Material is not enough");
        
        
        int combinationResult = Item.getCombineResult(itemCode1, itemCode2);
        
        mInventory[itemCode1] -= 3;
        mInventory[itemCode2] -= 3;
        mInventory[combinationResult] += 1;
        
    }
    
    public void addItem(int item) {
        mInventory[item] ++;
    }
    
    public List<Integer> getListRepresentation() {
        ArrayList<Integer> items = new ArrayList<>();
        for (int i=0;i<Item.ITEM_KIND_NUMBER;i++) {
            items.add(mInventory[i]);
        }
        return items;
    }
    
    public String toString() {
        ArrayList<Integer> items = new ArrayList<>();
        for (int i=0;i<Item.ITEM_KIND_NUMBER;i++) {
            items.add(mInventory[i]);
        }
        return items.toString();
    }
    
    public Document toDocument() {
        Document document = new Document();
        List<Integer> listItem = new ArrayList<>();
        for (int i=0;i<mInventory.length;i++) {
           listItem.add(mInventory[i]); 
        }
        document.put("items", listItem);
        
        return document;
    }
    
    public boolean isItemEnough(int offeredItemCode,int nOfferedItem) {
        if (mInventory[offeredItemCode] < nOfferedItem)
            return false;
        else
            return true;
    }
    
    public void removeOfferedItem(int offeredItemCode,int nOfferedItem) {
        mInventory[offeredItemCode] -= nOfferedItem;
    }
    
    public void addItem(int item, int nItem) {
        mInventory[item] += nItem;
    }
}
