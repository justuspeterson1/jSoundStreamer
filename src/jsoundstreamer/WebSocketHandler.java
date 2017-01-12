/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsoundstreamer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.handler.StaticFileHandler;

public class WebSocketHandler extends BaseWebSocketHandler {
    
    private static Set<WebSocketConnection> connections = Collections.synchronizedSet(new HashSet<WebSocketConnection>());
    
    public void connect(int port) {
        WebServer webServer = WebServers.createWebServer(port)
                .add("/", new WebSocketHandler())
                .add(new StaticFileHandler("/web"));
        webServer.start();
        System.out.println("Server running at " + webServer.getUri());
    }
    
    @Override
    public void onOpen(WebSocketConnection connection) {
        connections.add(connection);
        System.out.println("Get new connection. Total: " + connections.size());
    }

    @Override
    public void onClose(WebSocketConnection connection) {
        connections.remove(connection);
        System.out.println("Lost connection. Total: " + connections.size());
    }

    @Override
    public void onMessage(WebSocketConnection connection, String message) {}
    
    public Set<WebSocketConnection> getPeers() {
        return connections;
    }

}
