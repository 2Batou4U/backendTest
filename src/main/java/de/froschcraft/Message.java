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

    public Message(String message) {
        this.message = message;
        this.dateTime = ZonedDateTime.now(ZoneId.systemDefault());
    }

    public String getMessage() {
        return this.message;
    }

    public ZonedDateTime getDate() {
        return this.dateTime;
    }

    public String toString(){
        return String.format("%s: %s", this.getDate(), this.getMessage());
    }
}
