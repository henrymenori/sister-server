/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 *
 * @author kevinyu
 */
public class Server {
    private String mIp;
    private int mPort;
    
    public Server(String ip,int port) {
        mIp = ip;
        mPort = port;
    }
    
    public String getIp() {
        return mIp;
    }
    
    public int getPort() {
        return mPort;
    }
}
