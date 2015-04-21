/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author kevinyu
 */
public class Map {
    
    private int mWidth, mHeight;

    private String mName;
    private int[][]mItems;
    private StringBuilder mStringRepresentation;
    
    private static HashMap<String,Map> sInstances = new HashMap<>();
    
    
    public Map (String fileName) {
        
        try {
            readFromFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            JSONObject mapJSON = (JSONObject) JSONValue.parse(mStringRepresentation.toString());
            mName = (String)mapJSON.get("name");
            mWidth = (int)(long) mapJSON.get("width");
            mHeight = (int)(long) mapJSON.get("height");
            mItems = new int[mWidth][mHeight];
            JSONArray rowJSON = (JSONArray) mapJSON.get("map");
            for (int i=0;i<rowJSON.size();i++) {
                JSONArray colJSON = (JSONArray)rowJSON.get(i);
                for (int j=0;j<colJSON.size();j++) {
                    String stringCode = (String) colJSON.get(j);
                    mItems[i][j] = Item.getItemNumberCode(stringCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void readFromFile(String fileName) throws IOException{
        BufferedReader inputStream = null;
        mStringRepresentation = new StringBuilder("");
        try {
            inputStream = new BufferedReader(new FileReader("map.json"));

            String l;
            while ((l = inputStream.readLine()) != null) {
                mStringRepresentation.append(l);
            }
        }finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    public String toString() {
        return mStringRepresentation.toString();
    }
    
    public void print() {
        System.out.println("Name : "+mName);
        System.out.println("Width : "+mWidth);
        System.out.println("Hegiht : "+mHeight);
        for (int i=0;i<mWidth;i++) {
            for (int j=0;j<mHeight;j++) {
                System.out.printf("%d", mItems[i][j]);
            }
            System.out.printf("\n");
        }
    }
    
    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public String getName() {
        return mName;
    }
    
    public int getItem(int positionX, int positionY) {
        return mItems[positionX][positionY];
    }
    
    public static Map getInstance(String fileName) {
        if (!sInstances.containsKey(fileName)) {
            sInstances.put(fileName,new Map(fileName));
        }
        return sInstances.get(fileName);
    }
}
