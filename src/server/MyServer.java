/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author kevinyu
 */
public class MyServer {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        boolean listening = true;
        //TrackerConnector trackerConnector = new TrackerConnector();
        try {
            //trackerConnector.join();
            Map map = Map.getInstance("map.json");
            map.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        MongoDatabase database = mongoClient.getDatabase("philosopher");
        UserManager userManager = new UserManager(database);
        OfferManager offerManager = new OfferManager(database);
        
        try (ServerSocket serverSocket = new ServerSocket(2000)){
            ExecutorService executor = Executors.newFixedThreadPool(20);
            while(listening) {
                executor.submit(new InputProcessor(serverSocket.accept(),userManager,offerManager));
            }
            executor.shutdown();
        } catch (IOException e) {
            
        }
    }
    
}
