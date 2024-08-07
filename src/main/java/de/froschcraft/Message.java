package de.froschcraft;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String message;
    private final ZonedDateTime dateTime;
    private final User user;

    public Message(String message, User user) {
        this.message = message;
        this.user = user;
        this.dateTime = ZonedDateTime.now(ZoneId.systemDefault());
    }

    public String getMessage() {
        return this.message;
    }

    public ZonedDateTime getDate() {
        return this.dateTime;
    }

    public User getUser() {
        return this.user;
    }

    public String toString(){
        return String.format("%s schrieb am %s: %s", this.getUser(), this.getDate(), this.getMessage());
    }
}
