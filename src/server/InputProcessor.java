/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author kevinyu
 */
public class InputProcessor implements Runnable{
    private Socket mClientSocket = null;
    private final int BUFFER_SIZE = 1024;
    private RollingJSONParser mRollingJSONParser;
    private PrintWriter mSender;
    private UserManager mUserManager;
    private OfferManager mOfferManager;
    
    public InputProcessor (Socket clientSocket,
            UserManager userManager,OfferManager offerManager) {
        
        mClientSocket = clientSocket;
        mUserManager = userManager;
        mOfferManager = offerManager;
        mRollingJSONParser = new RollingJSONParser();
        
        try {
            mSender = new PrintWriter(new OutputStreamWriter(mClientSocket.getOutputStream(), 
                    StandardCharsets.UTF_8), true);
        } catch (IOException ex) {
            Logger.getLogger(InputProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() {
        
         try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    mClientSocket.getInputStream()));
        ) {
            String inputLine, outputLine;
            char[] buf = new char[1024];
            while (in.read(buf)>0) {
                mRollingJSONParser.receive(new String(buf));
                while(mRollingJSONParser.isJSONObjectExist()) {
                    JSONObject jsonObj =  mRollingJSONParser.getNextJSONObject();
                    handle(jsonObj);
                }
            }
            System.out.println("Client disconnected");
            mClientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void handle(JSONObject request) {
        String method = (String)request.get("method");
        JSONObject response = new JSONObject();
        
        if (method.equals("signup")) {
            response = handleSignUpRequest(request);
        } else if (method.equals("login")) {
            response = handleLoginRequest(request);
        } else if (method.equals("inventory")) {
            response = handleInventoryRequest(request);
        } else if (method.equals("map")) {
            response = handleMapRequest();
        } else if (method.equals("move")) {
            response = handleMoveRequest(request);
        } else if (method.equals("field")) {
            response = handleFieldRequest(request);
        } else if (method.equals("mixitem")) {
            response = handleMixItemRequest(request);
        } else if (method.equals("offer")) {
            response = handleOfferRequest(request);
        } else if (method.equals("tradebox")) {
            response = handleTradeBoxRequest(request);
        } else if (method.equals("findoffer")) {
            response = handleFindOfferRequest(request);
        } else if (method.equals("accept")) {
            response = handleAcceptRequest(request);
        } else if (method.equals("canceloffer")) {
            response = handleCancelOfferRequest(request);
        }
        
        System.out.println("Handle "+response.toString());
        mSender.print(response.toString());
        mSender.flush();
    }
    
    private JSONObject handleSignUpRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        try {
            mUserManager.signup(username,password);
            response.put("status","ok");
        }
        catch(Exception e) {
            response.put("status","fail");
            response.put("description",e.getMessage());
        }
        
        return response;
    }
    
    private JSONObject handleLoginRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        
        String username = (String) request.get("username");
        String password = (String) request.get("password");

        try {
            User user = mUserManager.login(username,password);
            
            response.put("status", "ok");
            response.put("token",user.getToken());
            response.put("x",user.getPositionX());
            response.put("y",user.getPositionY());
            response.put("time",System.currentTimeMillis()/1000);
            
        } catch (Exception e) {
            
            response.put("status", "fail");
            response.put("description",e.getMessage());
            
        }
        
        return response;
    }
    
    private JSONObject handleInventoryRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        String token = (String) request.get("token");
        
        try{
            response.put("status","ok");
            response.put("inventory",mUserManager.getUserInventory(token));
        } catch (Exception e) {
            response.put("status", "fail");
            response.put("description", e.getMessage());
        }
        
        return response;
    }
    
    private JSONObject handleMixItemRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        String token = (String) request.get("token");
        
        int itemCode1 = (int) (long) request.get("item1");
        int itemCode2 = (int) (long) request.get("item2");
        try{
            mUserManager.userMixItem(token, itemCode1, itemCode2);
            response.put("status","ok");
            response.put("item",Item.getCombineResult(itemCode1, itemCode2));
        } catch (Exception e) {
            response.put("status", "fail");
            response.put("description", e.getMessage());
        }
        return response;
    }
    
    public JSONObject handleMapRequest() {
        JSONObject response = new JSONObject();
        response.put("status","ok");
        response.put("name",Map.getInstance("map.json").getName());
        response.put("width",Map.getInstance("map.json").getWidth());
        response.put("height",Map.getInstance("map.json").getHeight());
        return response;
    }
    
    public JSONObject handleMoveRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        
        int destinationX = (int) (long) request.get("x");
        int destinationY = (int) (long) request.get("y");
        String token = (String) request.get("token");
        
        try {
            mUserManager.moveUser(token,destinationX,destinationY);
            
            response.put("status","ok");
            response.put("time",  System.currentTimeMillis()/1000 + 4);
            
        } catch (Exception e) {
            response.put("status", "fail");
            response.put("description", e.getMessage());
        }
        
        return response;
    }
    
    public JSONObject handleFieldRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        
        String token = (String) request.get("token");
        
        try {
            int item = mUserManager.userTakeItem(token);
            response.put("status","ok");
            response.put("item",item);
        } catch (Exception e) {
            response.put("status", "fail");
            response.put("description", e.getMessage());
        }
        
        return response;
    }
    
    private JSONObject handleOfferRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        
        String token = (String) request.get("token");
        int offeredItemCode = (int)(long) request.get("offered_item");
        int nOfferedItem = (int)(long) request.get("n1");
        int demandedItemCode = (int)(long) request.get("demanded_item");
        int nDemandedItem = (int)(long) request.get("n2");
        
        User user = mUserManager.findUserByToken(token);
        if (user == null) {
            response.put("status", "fail");
            response.put("description","token not exist");
            return response;
        }
        
        try{
            mUserManager.removeOfferedItem(token, offeredItemCode, nOfferedItem);
            mOfferManager.addOffer(user.getId(), 
                    offeredItemCode,
                    nOfferedItem,
                    demandedItemCode,
                    nDemandedItem);
            response.put("status", "ok");
        } catch (Exception e) {
            response.put("status","fail");
            response.put("description",e.getMessage());
        }
        
        return response;
    }
    
    public JSONObject handleTradeBoxRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        
        String token = (String) request.get("token");
        User user = mUserManager.findUserByToken(token);
        if (user == null) {
            response.put("status", "fail");
            response.put("description","token not exist");
            return response;
        }
        
        try{
            List<Object> list = mOfferManager.getTradeBox(user.getId());
            response.put("status","ok");
            response.put("offers",list);
        }catch(Exception e) {
            response.put("status", "fail");
            response.put("description",e.getMessage());
        }
        
        return response;
    }
    
    public JSONObject handleFindOfferRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        
        int itemCode = (int)(long) request.get("item");
        
        try{
            
            List<Object> offers = mOfferManager.findOffer(itemCode);
            response.put("status","ok");
            response.put("offers",offers);
            
        }catch(Exception e) {
            response.put("status", "fail");
            response.put("description",e.getMessage());
        }
        
        return response;
    }
    
    public JSONObject handleAcceptRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        
        String offerToken = (String) request.get("offer_token");
        
        try{
            mOfferManager.acceptOffer(offerToken);
            response.put("status","ok");
        } catch (Exception e) {
            response.put("status","fail");
            response.put("description", e.getMessage());
        }
        
        return response;
    }
    
    public JSONObject handleCancelOfferRequest(JSONObject request) {
        JSONObject response = new JSONObject();
        
        String userToken = (String) request.get("token");
        String offerToken = (String) request.get("offer_token");
        
        try {
            Offer offer = mOfferManager.getOfferByToken(offerToken);
            mUserManager.addCancelledOfferItem(userToken, offer);
            mOfferManager.cancelOffer(offerToken);
        } catch (Exception e) {
            response.put("status","fail");
            response.put("description",e.getMessage());
        }
        
        return response;
    }
    
    
    
}
