package de.froschcraft;

public class ServerState {
    public enum State {
        STARTING,
        RUNNING,
        RELOADING,
        STOPPING
    }

    private State state;

    public ServerState() {
        this.state = State.STARTING;
    }

    public ServerState(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setRunning(){
        this.state = State.RUNNING;
    }

    public void setReloading(){
        this.state = State.RELOADING;
    }

    public void setStopping(){
        this.state = State.STOPPING;
    }

    public State setIfCheck(boolean condition, State state) {
        this.state = condition ? state : this.state;

        return this.state;
    }

    public void stopIfCheck(boolean condition) {
        this.state = condition ? State.STOPPING : this.state;
    }

    public boolean isRunning() {
        return this.state != State.STOPPING;
    }
}