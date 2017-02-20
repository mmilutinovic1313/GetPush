/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupgetpost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 *
 * @author Group5
 */
public class ClientHandler implements Runnable {

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("\nClientHandler Started for " + 
            this.socket);
        handleRequest(this.socket);
        System.out.println("ClientHandler Terminated for " 
            + this.socket + "\n");
    }
    
    public void handleRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));) {
            String headerLine = in.readLine();
            StringTokenizer tokenizer = 
                new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            
            File diary = new File("Diary.txt");
            diary.createNewFile();
            BufferedWriter writer = null;
            
            if (httpMethod.equals("GET")) {
                System.out.println("Get method processed");
                String httpQueryString = tokenizer.nextToken();
                StringBuilder responseBuffer = new StringBuilder();
                responseBuffer
                .append("<html><h1>WebServer Home Page.... </h1><br>")
                .append("<b>Welcome to my web server!</b><BR>")
                .append("</html>");
                sendResponse(socket, 200, responseBuffer.toString());}  
            
            else if (httpMethod.equals("POST")){
                System.out.println("POST method processed");
                String httpQueryString = tokenizer.nextToken();
                
                writer = new BufferedWriter(new FileWriter(diary, true));
                writer.write("h");
                
                StringBuilder responseBuffer = new StringBuilder();
                responseBuffer
                .append("<html><h1>WebServer Home Page.... </h1><br>")
                .append("<b>Welcome to my web server!</b><BR>")
                .append("</html>");
                sendResponse(socket, 200, responseBuffer.toString());}
            
            else {
            System.out.println("The HTTP method is not recognized");
            sendResponse(socket, 405, "Method Not Allowed");}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    public void sendResponse(Socket socket, 
            int statusCode, String responseString) {
        String statusLine;
        String serverHeader = "Server: WebServer\r\n";
        String contentTypeHeader = "Content-Type: text/html\r\n";

        try (DataOutputStream out = 
                new DataOutputStream(socket.getOutputStream());) {
        
        if (statusCode == 200) {
            statusLine = "HTTP/1.0 200 OK" + "\r\n";
            String contentLengthHeader = "Content-Length: " 
                + responseString.length() + "\r\n";

            out.writeBytes(statusLine);
            out.writeBytes(serverHeader);
            out.writeBytes(contentTypeHeader);
            out.writeBytes(contentLengthHeader);
            out.writeBytes("\r\n");
            out.writeBytes(responseString);} 
        else if (statusCode == 405) {
            statusLine = "HTTP/1.0 405 Method Not Allowed" + "\r\n";
            out.writeBytes(statusLine);
            out.writeBytes("\r\n");} 
        else {
            statusLine = "HTTP/1.0 404 Not Found" + "\r\n";
            out.writeBytes(statusLine);
            out.writeBytes("\r\n");}
        
            out.close();
        } catch (IOException ex) {
            // Handle exception
        }
    }

}