package test;

import main.App;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    private App app;
    private static final int x = 10;

    @BeforeEach
    void setUp() {
        this.app = new App(x);
    }

    @org.junit.jupiter.api.Test
    void getX() {
        assertEquals(app.getX(), x);
    }

    @org.junit.jupiter.api.Test
    void square() {
        assertEquals(app.square(), x*x);
    }
}