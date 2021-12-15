package com.dev.controllers;

import com.dev.Persist;
import com.dev.objects.PostObject;
import com.dev.objects.UserObject;
import com.dev.objects.MessageObject;
import com.dev.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;


@RestController
public class TestController {


    @Autowired
    private Persist persist;

    @PostConstruct
    private void init () {

    }

    @RequestMapping("sign-in")
    public String signIn (String username, String password) {
        String token = persist.getTokenByUsernameAndPassword(username, password);
        return token;
    }

    @RequestMapping("create-account")
    public boolean createAccount (String username, String password) {
        boolean success = false;
        boolean alreadyExists = persist.getTokenByUsernameAndPassword(username, password) != "This account not exist!";
        if (!alreadyExists) {
            UserObject userObject = new UserObject();
            userObject.setUsername(username);
            userObject.setPassword(password);
            String hash = Utils.createHash(username, password);
            userObject.setToken(hash);
            success = persist.addAccount(userObject);
        }
        System.out.println(success);
        return success;
    }


    @RequestMapping("get-posts")
    public List<PostObject> getPosts (String token) {
        return persist.getPostsByUser(token);
    }

    @RequestMapping("remove-post")
    public boolean removePost (String token, int postId) {
        return persist.removePost(token, postId);
    }

    @RequestMapping(value = "/get-username-by-token")
    public String getUsernameByToken(String token){
        return persist.getUsernameByToken(token);
    }

    @RequestMapping("get-messages")
    public List<MessageObject> getAllMessages(String token){
        return  persist.getAllMessages(token);
    }

    @RequestMapping(value = "/send-message")
    public boolean sendMessage (String sender , String receiver ,String title , String content, String token)
    {
        return persist.sendMessage(sender,receiver,title,content, token);
    }

    @RequestMapping(value = "/read-message")
    public void setMessageAsRead(int id){
        persist.setMessageAsRead(id);
    }
    @RequestMapping(value = "/delete-message-by-id")
    public void deleteMessageById(int id){
        persist.deleteMessageById(id);
    }
    @RequestMapping(value = "/user-exist")
    public boolean doseUserExist(String username) {
        return persist.doseUserExist(username);


    }

}
