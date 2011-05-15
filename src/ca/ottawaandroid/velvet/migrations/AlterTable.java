package ca.ottawaandroid.velvet.migrations;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.util.Log;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class AlterTable implements Migration {
    private static final String ALTER_TABLE_SQL = "ALTER TABLE %s ";
    private static final String RENAME_TABLE = ALTER_TABLE_SQL + "RENAME TO %s";
    private static final String ADD_COLUMN = ALTER_TABLE_SQL + "ADD %s TEXT";

    private String mTbl;
    private ArrayList<String> alterations = new ArrayList<String>();

    public AlterTable(String tbl){
	mTbl = tbl;
    }

    public void addColumn(String colName){
	String addColSQL = String.format(ADD_COLUMN, mTbl, colName);
	alterations.add(addColSQL);
    }

    public void renameTable(String newTblName){
	String newTblSQL = String.format(RENAME_TABLE, mTbl, newTblName);
    }

    public void setUp(SQLiteDatabase db){
	for(String alteration : alterations){
	    Log.d("Migration->AlterTable#setUp", alteration);
	    db.execSQL(alteration);
	}
    }

    public void tearDown(SQLiteDatabase db){
    }
}