package jkas.Passage;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jkas.General.NewMessageClass;
import jkas.jwt.JwtUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ServerEndpoint("/passage/editor/util")
public class PassageEditor {

    private Session session;
    private String username;
    private String userID;
    private static ConcurrentMap<String, Session> clients = new ConcurrentHashMap<>();
    @Autowired
    private JdbcTemplate targetdb = new JdbcTemplate();

    // Util methods
    private String getUsername(String token){
        if (JwtUtils.checkIsExpired(token) || !JwtUtils.checkIsValid(token)){
            return "Invalid token";
        }
        Map<String, Object> userInfo = JwtUtils.getClaims(token);
        return userInfo.get("username").toString();
    }

    private String getUserID(String token){
        if (JwtUtils.checkIsExpired(token) || !JwtUtils.checkIsValid(token)){
            return "Invalid token";
        }
        Map<String, Object> userInfo = JwtUtils.getClaims(token);
        return userInfo.get("id").toString();
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

    public Boolean createPassage(String userid, String title, String content){
        String sql = String.format("insert into passagedb (title, authorid, date, visible, content) values (%s, %s, %s, %s, %s)", title, userid, LocalDateTime.now(), 1, content);
        int affectRowNum = targetdb.update(sql);
        if (affectRowNum > 0){
            return true;
        }
        else{
            return false;
        }
    }

    public Boolean modifyPassage(String title, String content, String passageID){
        String sql = String.format("update passagedb set content='%s' title='%s' where passageid='%s'", content, title, passageID);
        int affectRowNum = targetdb.update(sql);
        if (affectRowNum > 0){
            return true;
        }
        else{
            return false;
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userToken") String userToken){
        try{
            this.session = session;
            this.username = this.getUsername(userToken);
            this.userID = this.getUserID(userToken);
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

    @OnMessage
    public void onMessage(String originMessage){
        System.out.println("Receive a message from the client");
        JSONObject message = new JSONObject(originMessage);
        // identify the type of the process
        if (message.get("type") == "new"){
            // create new passage in the system
            Boolean result = createPassage(this.userID, message.get("title").toString(), message.get("content").toString());
            if (result){
                sendSingleMessage(this.username, "true");
            }
            else{
                sendSingleMessage(this.username, "false");
            }
        }
        else{
            // modify the passage, need passageID
            Boolean result = modifyPassage(message.get("title").toString(), message.get("content").toString(), message.get("id").toString());
            if (result){
                sendSingleMessage(this.username, "true");
            }
            else{
                sendSingleMessage(this.username, "false");
            }
        }
    }

}
