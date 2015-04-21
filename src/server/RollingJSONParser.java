/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author kevinyu
 */
public class RollingJSONParser {
    private StringBuilder currentString;
    private int mNCurlyBrace;
    private Queue<JSONObject> mJSONQueue;
    private JSONParser mJSONParser;
    
    public RollingJSONParser() {
        currentString = new StringBuilder("");
        mJSONQueue = new LinkedBlockingQueue<JSONObject>();
        mJSONParser = new JSONParser();
        mNCurlyBrace = 0;
    }
    
    public void receive(String input) {
        for (int i=0;i<input.length();i++) {
            if ((mNCurlyBrace==0) && (input.charAt(i)!='{')) continue;
            currentString.append(input.charAt(i)); 
            if (input.charAt(i) == '{') {
                mNCurlyBrace++;
            } else if (input.charAt(i) == '}') {
                mNCurlyBrace--;
            }
            if (mNCurlyBrace == 0){
                try {
                    System.out.println(currentString);
                    mJSONQueue.add((JSONObject) mJSONParser.parse(currentString.toString()));
                } catch(Exception e) {
                    e.printStackTrace();
                }
                currentString.delete(0, currentString.length());
            }
        }
    }
    
    public boolean isJSONObjectExist() {
        return !mJSONQueue.isEmpty();
    }
    
    public JSONObject getNextJSONObject() {
        return mJSONQueue.remove();
    }
}
