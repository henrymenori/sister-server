package server;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author kevinyu
 */
public class TrackerConnector {
    Socket mSocket;
    PrintWriter mSender;
    InputStreamReader mReceiver;
    RollingJSONParser mRollingJSONParser;
    
    public TrackerConnector() {
        try {
            mSocket = new Socket("167.205.32.46",8000);
            mSender =
                new PrintWriter(mSocket.getOutputStream(), true);
            mReceiver =
                new InputStreamReader(
                    new DataInputStream(mSocket.getInputStream()));
            mRollingJSONParser = new RollingJSONParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void join() throws IOException {
        
        JSONObject joinRequest = new JSONObject();
        joinRequest.put("method","join");
        joinRequest.put("ip","167.205.32.46");
        joinRequest.put("port",4000);
        mSender.println(joinRequest.toString());
        
        char buf[] = new char[4096];
        int nRead = 0;
        while((nRead = mReceiver.read(buf)) > 0) {
            
           mRollingJSONParser.receive(new String(buf).substring(0,nRead));
           while(mRollingJSONParser.isJSONObjectExist()) {
               JSONObject response = mRollingJSONParser.getNextJSONObject();
               saveServer(response);
           }
           
        }
        
        mSender.close();
        mReceiver.close();
        
    }
    
    public void saveServer(JSONObject response) {
        
        JSONArray servers =(JSONArray) response.get("value");
        for (int i=0;i<servers.size();i++) {
            
            JSONObject server = (JSONObject) servers.get(i);
            
            String ip = (String)server.get("ip");
            int port = (int) (long) server.get("port");
            
            ServerManager.getInstance().addServer(ip,port);
        }
        
    }
}
