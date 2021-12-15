package com.dev;

import com.dev.objects.MessageObject;
import com.dev.objects.PostObject;
import com.dev.objects.UserObject;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.dev.utils.Utils.createHash;

@Component
public class Persist {
    private Connection connection;

    @PostConstruct
    public void createConnectionToDatabase() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ashcollage?useSSL=false&useUnicode=true&characterEncoding=utf8&serverTimezone=IST", "root", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addAccount(UserObject userObject) {
        boolean success = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO users (username, password, token) VALUE (?, ?, ?)");
            preparedStatement.setString(1, userObject.getUsername());
            preparedStatement.setString(2, userObject.getPassword());
            preparedStatement.setString(3, userObject.getToken());
            preparedStatement.executeUpdate();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public String getTokenByUsernameAndPassword(String username, String password) {
        String response = null;
        boolean usernameExist;
        try {
            usernameExist = doseUserExist(username);
            if (wrongLoginTry(username) < 5) {
                if (usernameExist) {
                    PreparedStatement preparedStatement = this.connection.prepareStatement(
                            "SELECT token FROM users WHERE username = ? AND password = ?");
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        response = resultSet.getString("token");
                        System.out.println(response);
                    } else {
                        response = "Wrong password!";
                        addWrongLogin(username);
                    }
                } else {
                    response = "This account not exist!";
                }
            } else {
                response = "Account blocked, please contact administrator";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }


    public boolean addPost(String token, String content) {
        boolean success = false;
        try {
            Integer userId = getUserIdByToken(token);
            if (userId != null) {
                PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO posts (content, creation_date, author_id) VALUE (?, NOW(), ?)");
                preparedStatement.setString(1, content);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }

    public Integer getUserIdByToken(String token) {
        Integer id = null;
        try {
            System.out.println(token);
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT id FROM users WHERE token = ?");
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;

    }

    public String getUsernameById(int id) {
        String name = "";
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT username FROM users WHERE id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                name = resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getUsernameByToken(String token) {
        String username = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT username FROM users WHERE token=?");
            preparedStatement.setString(1, token);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next())
                username = rs.getString("username");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    public List<MessageObject> getAllMessages(String token) {
        List<MessageObject> messages = new ArrayList<>();
        int id = getUserIdByToken(token);
        try {
            createConnectionToDatabase();
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM messages WHERE receiverId=? ORDER BY sendTime DESC");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                MessageObject message = new MessageObject();
                message.setSenderId(resultSet.getInt("senderId"));
                message.setSenderName(getUsernameById(resultSet.getInt("senderId")));
                message.setReceiverId(id);
                message.setContent(resultSet.getString("content"));
                message.setTitle(resultSet.getString("title"));
                message.setReadTime(resultSet.getString("readTime"));
                message.setSendTime(resultSet.getString("sendTime"));
                message.setId(resultSet.getInt("id"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(messages);
        return messages;
    }

    public boolean doseUserExist(String username) {
        boolean userExist = false;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userExist = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExist;

    }

    public boolean deleteMessageById(int id) {
        boolean isDeleted = false;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "DELETE FROM messages WHERE id = ?"
            );
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isDeleted = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isDeleted;
    }

    public boolean sendMessage(String username,String receiver, String title, String content, String token) {
        boolean success = false;
        try {
            int sender_id = this.getUserIdByToken(token);
            int receiver_id = this.getUserIdByUsername(receiver);
            createConnectionToDatabase();
            PreparedStatement preparedStatement1 = this.connection.prepareStatement(
                    "INSERT INTO messages (senderId, receiverId, title, content, sendTime) VALUES (?,?,?,?,now()) "
            );
            preparedStatement1.setInt(1, sender_id);
            preparedStatement1.setInt(2, receiver_id);
            preparedStatement1.setString(3, title);
            preparedStatement1.setString(4, content);
            int updates = preparedStatement1.executeUpdate();
            if (updates > 0) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(success);
        return success;
    }

    public Integer getUserIdByUsername(String username) {
        Integer id = null;
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT id FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public boolean setMessageAsRead(int id) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(
                    "UPDATE messages SET readTime = now() WHERE id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }


    public int wrongLoginTry(String username) {
        int wrongTry = 0;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT loginTries  FROM users WHERE username =?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                wrongTry = resultSet.getInt("loginTries");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wrongTry;

    }

    public void addWrongLogin(String username) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE users\n" +
                    "SET loginTries = loginTries + 1\n WHERE username = ?");
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<PostObject> getPostsByUser(String token) {
        List<PostObject> postObjects = new ArrayList<>();
        try {
            Integer userId = getUserIdByToken(token);
            if (userId != null) {
                PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM posts WHERE author_id = ? ORDER BY id DESC");
                preparedStatement.setInt(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    PostObject postObject = new PostObject();
                    String content = resultSet.getString("content");
                    String date = resultSet.getString("creation_date");
                    postObject.setId(resultSet.getInt("id"));
                    postObject.setContent(content);
                    postObject.setDate(date);
                    postObjects.add(postObject);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postObjects;
    }


    public boolean removePost(String token, int postId) {
        boolean success = false;
        try {
            Integer userId = getUserIdByToken(token);
            if (userId != null) {
                PreparedStatement preparedStatement = this.connection.prepareStatement("DELETE FROM posts WHERE id = ? AND author_id = ? ");
                preparedStatement.setInt(1, postId);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success;
    }


}