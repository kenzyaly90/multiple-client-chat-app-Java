/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kenzy
 */
public class client implements Runnable{
    
    private PrintWriter out;
    private BufferedReader in;
    private boolean done;
    private Socket client;

    @Override
    public void run() {
       
        try {
            client = new Socket("127.0.0.1",9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            inputHandler inHandler = new inputHandler();
            
            Thread t = new Thread(inHandler);
            t.start();
            
            String inMessage;
            while((inMessage = in.readLine()) != null){
                System.out.println(inMessage);
            }    
            } 
            catch (IOException ex) {
                // handle
                shutDown();
            }
        
    }
    
     public void shutDown(){
            try {
                done = true;
                in.close();
                out.close();
                if(!client.isClosed()){
                    client.close();
                }
            } catch (IOException ex) {
                // handle
            }
                    
        }
    
    class inputHandler implements Runnable{
        


        @Override
        public void run() {
            
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
 
            while(!done){
                try {
                    String message = inReader.readLine();
                    if(message.equals("quit")){
                        out.println(message);
                        inReader.close();
                        shutDown();
                    }else{
                        out.println(message);
                    }
                } catch (IOException ex) {
                    // handle
                    shutDown();
                }
            }
            
        }
        
        
    }
    
    public static void main(String arg[]){
        client client = new client();
        client.run();
    }
    
}
