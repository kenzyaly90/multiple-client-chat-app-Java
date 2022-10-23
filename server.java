/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kenzy
 */
public class server implements Runnable{
    
    private ArrayList<connectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;
    
    public server(){
        connections = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            
            while(!done){
                Socket client = server.accept();
                connectionHandler handler = new connectionHandler(client);
                connections.add(handler);   
                pool.execute(handler);
            }
            
        } catch (IOException ex) {
            shutDown();
        }
    }
    
    public void broadcast(String message){
        for(connectionHandler ch : connections){
            if(ch != null){
                ch.sendMessage(message);
            }
        }
    }
    
    private void shutDown(){
        
        done = true;
        pool.shutdown();
        if(!server.isClosed()){
            try {
                server.close();
                for(connectionHandler ch : connections){
                    ch.shutDown();
                }
            } catch (IOException ex) {
                shutDown();
            }
        }
    }
    class connectionHandler implements Runnable{

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;
        private String message;

        private connectionHandler(Socket client) {
            this.client = client;
        }
        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("please enter your name: ");
                nickname = in.readLine();
                System.out.println(nickname + " connected!");
                
                broadcast(nickname + " joined the chat!");
                
                while((message = in.readLine())!=null){
                    
                    if(message.equals("quit")){
                        broadcast(nickname + " : left chat.");
                        shutDown();
                    }
                    else{
                        broadcast(nickname + " : " + message);
                    }
                    
                }
            } catch (IOException ex) {
                Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void sendMessage(String message){
            out.println(message);
        }
        
        public void shutDown(){
            try {
                in.close();
                out.close();
                if(!client.isClosed()){
                    client.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public static void main(String args[]){
        server server = new server();
        server.run();
    }
    
}
