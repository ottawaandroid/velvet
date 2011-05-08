package ca.ottawaandroid.velvet.migrations;

public interface Migration {
    public void setUp();

    public void tearDown();
}