package main;

public class App {
    private final int x;

    public App(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public int square() {
        return x * x;
    }
}
