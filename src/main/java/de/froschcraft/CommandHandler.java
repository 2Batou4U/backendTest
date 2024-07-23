package de.froschcraft;

import java.util.Vector;

public class CommandHandler {
    private final ServerState serverState;

    private String currentCommand;
    private final Vector<String> commandHistory;
    private Integer currentHistoryIndex;

    public CommandHandler(ServerState serverState){
        this.currentCommand = "";
        this.commandHistory = new Vector<>();
        this.currentHistoryIndex = 0;

        this.serverState = serverState;
    }

    public void handleInput(String input){
        assert !input.isEmpty();

        // First of, save the user given command.
        this.currentCommand = input.strip();
        this.commandHistory.addElement(currentCommand);

        String[] commandArray = currentCommand.split(" ");

        switch (commandArray[0]){
            case "help":
                break;
            case "exit":
                this.serverState.setStopping();
                break;
            default:
                System.out.printf("Unknown command: '%s'%n", currentCommand);
                break;
        }
    }

    public String getLatestCommand(){
        return this.commandHistory.getLast();
    }

    public String getFirstCommand(){
        return this.commandHistory.getFirst();
    }

    public String getNextCommand(){
        String nextCommand = this.commandHistory.get(this.currentHistoryIndex);

        this.currentHistoryIndex = this.currentHistoryIndex < this.commandHistory.capacity() ? this.currentHistoryIndex + 1 : this.commandHistory.capacity() - 1;

        return nextCommand;
    }

    public String getPreviousCommand(){
        this.currentHistoryIndex = this.currentHistoryIndex > 0 ? this.currentHistoryIndex - 1 : 0;

        return this.commandHistory.get(this.currentHistoryIndex);
    }

    public void setCurrentIndex(int index){
        this.currentHistoryIndex = index;
    }
}
