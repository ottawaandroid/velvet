package ca.ottawaandroid.velvet.migrations;

import java.util.ArrayList;

import android.util.Log;
import android.database.sqlite.SQLiteDatabase;

public class CreateTable implements Migration {
    private static final String CREATE_TABLE_SQL = "CREATE TABLE %s ( %s )";
    private static final String COLUMN_STRING = "%s TEXT";

    private String mTbl;
    private ArrayList<String> mCols = new ArrayList<String>();

    public CreateTable(String tbl, String... cols){
	mTbl = tbl;
	for(String col : cols){
	    mCols.add(col);
	}
    }

    public void setUp(SQLiteDatabase db){
	String cols = "";
	for(int i = 0, len = mCols.size(); i < len; i++){
	    if(i > 0){ cols += ","; }
	    cols += String.format(COLUMN_STRING, mCols.get(i));
	}
	String sql = String.format(CREATE_TABLE_SQL, mTbl, cols);
	Log.d("Migration->CreateTable#setUp", sql);
	db.execSQL(sql);
    }

    public void tearDown(SQLiteDatabase db){
    }
}