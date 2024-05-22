package com.aptproject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.glassfish.tyrus.server.Server;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Starting server" );
        runServer();
    }

    public static void runServer(){
        Server server = new Server("localhost", 8088, "/websocket", WebSocketServer.class);

        try{
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            server.stop();
        }
    }
}
