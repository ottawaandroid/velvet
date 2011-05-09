package ca.ottawaandroid.velvet.migrations;

import android.database.sqlite.SQLiteDatabase;

public interface Migration {
    public void setUp(SQLiteDatabase db);

    public void tearDown(SQLiteDatabase db);
}