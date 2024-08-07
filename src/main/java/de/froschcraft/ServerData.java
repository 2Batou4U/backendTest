package de.froschcraft;

import java.util.HashMap;
import java.util.Vector;

/**
 * Saves object retaining memory, like messages or users.
 */
public class ServerData {
    private Vector<Message> messages;
    private HashMap<String, User> users;

    /**
     * Standard constructor. Usually takes parameters straight from the deserializer.
     * @param messages Deserialized Messages.
     * @param users Deserialized Users.
     */
    public ServerData(
            Vector<Message> messages,
            HashMap<String, User> users
    ) {
        this.messages = messages;
        this.users = users;
    }

    /**
     * Get all the messages
     * @return this.messages
     */
    public Vector<Message> getMessages() {
        return this.messages;
    }

    /**
     * Get all the users.
     * @return this.users
     */
    public HashMap<String, User> getUsers() {
        return this.users;
    }

    /**
     * Add message to the messages vector.
     * @param message input message to be saved.
     */
    public void addMessage(Message message) {
        this.messages.add(message);
    }

    /**
     * Add new user to the users Hashmap.
     * @param user input user to be saved.
     */
    public void addUser(User user) {
        if(!this.userExists(user.getUsername())) {
            this.users.put(user.getUsername(), user);
        } else {
            throw new IllegalArgumentException("Nutzer existiert bereits!");
        }
    }

    /**
     * Internal call for forcing users to be set to a specific object.
     * @param users HashMap of users.
     */
    private void setUsers(HashMap<String, User> users) {
        this.users = users;
    }

    /**
     * Internal call for forcing messages to be set to a specific object.
     * @param messages Vector of messages.
     */
    private void setMessages(Vector<Message> messages) {
        this.messages = messages;
    }

    /**
     * Returns User object based on username.
     * @param username String to be matched.
     * @return User object.
     */
    public User getUser(String username) {
        return this.users.get(username);
    }

    /**
     * Boolean check whether a username exists.
     * @param username to be checked.
     * @return exist status.
     */
    public Boolean userExists(String username) {
        return this.users.containsKey(username);
    }

    /**
     * Dummy data. Overrides messages und users *IF* they are empty.
     */
    public void dummyData() {
        // Dummy data.
        if (this.getUsers() == null) {
            this.setUsers(new HashMap<>());

            User user = new User("adrian", "123");

            this.addUser(user);
        }

        if (this.getMessages() == null) {
            this.setMessages(new Vector<>());

            Message message = new Message("Test", this.getUser("adrian"));

            this.addMessage(message);
        }
    }

    /**
     * Dummy data. Overrides messages und users. This leads to potential data loss!
     */
    public void forceDummyData(){
        this.setUsers(new HashMap<>());
        User user = new User("adrian", "123");
        this.addUser(user);

        this.setMessages(new Vector<>());
        Message message = new Message("Test", this.getUser("adrian"));
        this.addMessage(message);
    }
}
