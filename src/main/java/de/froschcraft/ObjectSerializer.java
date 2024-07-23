package de.froschcraft;

import java.io.*;
import java.util.Vector;

class ObjectSerializer {
    public static void serializeMessages(Vector<Message> messages, String file) {
        try (
                FileOutputStream fileOut = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fileOut)
        ) {
            out.writeObject(messages);
            System.out.printf("Nachrichten wurden in %s gespeichert.%n", file);
        } catch (
                IOException i
        ) {
            System.out.printf("Auf %s konnte nicht zugegriffen werden.%n", file);
        }
    }

    @SuppressWarnings("unchecked")
    public static Vector<Message> deserializeMessages(String file) {
        Vector<Message> messages = new Vector<>();

        try (
                FileInputStream fileIn = new FileInputStream("users.ser");
                ObjectInputStream in = new ObjectInputStream(fileIn)
        ) {
            messages = (Vector<Message>) in.readObject();
        } catch (ClassNotFoundException c) {
            System.out.printf("Die Klasse Vector<Message> konnte nicht in %s gefunden werden.%n", file);
        } catch (FileNotFoundException f) {
            System.out.printf("Die Datei %s konnte nicht gefunden werden.%n", file);
        } catch (EOFException e){
            System.out.printf("Ende der Datei %s erreicht.%n", file);
        } catch (IOException e) {
            System.out.printf("Auf %s konnte nicht zugegriffen werden.%n", file);
        }

        return messages;
    }
}
