package de.froschcraft;

import java.io.*;
import java.util.HashMap;
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

    public static void serializeUsers(HashMap<String, User> users, String file) {
        try (
                FileOutputStream fileOut = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fileOut)
        ) {
            out.writeObject(users);
            System.out.printf("Nutzer wurden in %s gespeichert.%n", file);
        } catch (
                IOException i
        ) {
            System.out.printf("Auf %s konnte nicht zugegriffen werden.%n", file);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T deserialize(String file){
        T object = null;

        try (
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn)
        ) {

            object = (T) in.readObject();

        } catch (ClassNotFoundException c) {
            System.out.printf("Die Klasse Vector<T> konnte nicht in %s gefunden werden.%n", file);
        } catch (FileNotFoundException f) {
            System.out.printf("Die Datei %s konnte nicht gefunden werden.%n", file);
        } catch (EOFException e){
            System.out.printf("Ende der Datei %s erreicht.%n", file);
        } catch (IOException e) {
            System.out.printf("Auf %s konnte nicht zugegriffen werden.%n", file);
        }

        return object;
    }

    public static HashMap<String, User> deserializeUsers(String file) {
        return deserialize(file);
    }

    public static Vector<Message> deserializeMessages(String file) {
        return deserialize(file);
    }
}
