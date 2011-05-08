package ca.ottawaandroid.velvet.migrations;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class MigrationSet {
    private static final String DATABASE_VERSION_SQL = "CREATE TABLE IF NOT EXISTS DATABASE_VERSION (ID INTEGER PRIMARY KEY, CURRENT_VERSION INTEGER)";
    private static final String DATABASE_VERSION = "DATABASE_VERSION", ID = "ID", VERSION = "CURRENT_VERSION";
    
    private int mVersion = 0;
    private LinkedHashMap<Integer, ArrayList<Migration>> mVerMigrations;

    public MigrationSet(){
	mVerMigrations = new LinkedHashMap<Integer, ArrayList<Migration>>();
    }

    public void version(int v){
	if( !mVerMigrations.containsKey(v) && v == (mVersion + 1)){
	    ++mVersion;
	    mVerMigrations.put(v, new ArrayList<Migration>());
	}
    }

    public void add(Migration migration){
    }

    public void add(int version, Migration migration){
    }

    public void apply(SQLiteDatabase db){
	int currentMigration = 0;
	for(Integer mVer : mVerMigrations.keySet()){
	    migrate(mVerMigrations.get(mVer));
	    updateVersionTable(db, mVer.intValue());
	}
    }

    void migrate(ArrayList<Migration> ms){
	for(Migration m : ms){
	    m.setUp();
	}
    }

    void rollback(ArrayList<Migration> ms){
    }

    private void updateVersionTable(SQLiteDatabase db, int nextVersion){
	db.execSQL(DATABASE_VERSION_SQL);
	ContentValues cv = new ContentValues();
	cv.put(ID, 1);
	cv.put(VERSION, nextVersion);
	db.replace(DATABASE_VERSION, null, cv);
    }
}