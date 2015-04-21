/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;

/**
 *
 * @author kevinyu
 */
public class ServerManager {
    private static ServerManager instance = null;
    private ArrayList<Server> servers;
  
    private ServerManager() {
        servers = new ArrayList<>();
    }
    
    public static ServerManager getInstance() {
        if (instance == null ) instance = new ServerManager();
        instance = new ServerManager();
        return instance;
    }
    
    public void addServer(String ip, int port) {
        servers.add(new Server(ip,port));
    }
}
