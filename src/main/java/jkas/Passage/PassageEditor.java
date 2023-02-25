package jkas.Passage;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jkas.jwt.JwtUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ServerEndpoint("/passage/editor/util")
public class PassageEditor {

    private Session session;
    private String username;
    private String mode;
    private String passageID;
    private static ConcurrentMap<String, Session> clients = new ConcurrentHashMap<>();

    // Util methods
    private String getUsername(String token){
        if (JwtUtils.checkIsExpired(token) || !JwtUtils.checkIsValid(token)){
            return "Invalid token";
        }
        Map<String, Object> userInfo = JwtUtils.getClaims(token);
        return userInfo.get("username").toString();
    }

    private void sendSingleMessage(String username, String message){
        // check session validation
        Session session = clients.get(username);
        if (session == null || !session.isOpen()){
            System.out.println("This session is no longer valid.");
        }
        try{
            System.out.println("Send single message to user: " + username);
            session.getAsyncRemote().sendText(message);
        }
        catch (Exception e){
            System.out.println("An error occured while sending the message");
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userToken") String userToken){
        try{
            this.session = session;
            this.username = this.getUsername(userToken);
            clients.put(this.username, session);
            System.out.println("Passage Editor Utilities connected to a new client");
        }
        catch (Exception e){
            System.out.println("An error occur while connecting to the server.");
        }
    }

    @OnClose
    public void onClose(){
        try{
            clients.remove(this.username, this.session);
            System.out.println("Server has disconnected from a client.");
        }
        catch (Exception e){
            System.out.println("An error occured while disconnecting from the server");
        }
    }

    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("An error occured while processing this socket connection. Error messages is here:");
        error.printStackTrace();
    }

    // todo: 在完成html与md格式的转换后制作文章修改与保存操作

}
