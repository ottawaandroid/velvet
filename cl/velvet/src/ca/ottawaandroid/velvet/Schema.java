package ca.ottawaandroid.velvet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Schema {
	private SQLiteDatabase mDb;
	private HashMap<String, List<Template>> mTemplates = new HashMap<String, List<Template>>();

	public Schema(Context c, String dbn) {
 		SQLiteOpenHelper oh = new SQLiteOpenHelper(c, dbn, null, 1) {
			@Override
			public void onCreate(SQLiteDatabase db) {
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int ov, int nv) {
			}
 		};
 		this.mDb = oh.getWritableDatabase();		
	}

	Cursor getCursorById(String tbl, int id) {
		return mDb.query(tbl, null, "_id=?", new String[] { Integer.toString(id) }, null, null, null);
	}

	private Template makeTemplate(String tbl, Cursor dc) {
		ArrayList<String> cols = lookupTableColumns(tbl);

		return (null == cols) ? null : new Template(tbl, cols, dc, this);
	}

	private ArrayList<String> lookupTableColumns(String tbl) {
		ArrayList<String> rv = null;
		Cursor pc = mDb.rawQuery("PRAGMA table_info(" + tbl + ")", null);
		if ( pc.getCount() > 0 ) {
			rv = new ArrayList<String>();

			pc.moveToFirst();
			while ( !pc.isAfterLast() ) {
				rv.add(pc.getString(1));
				pc.moveToNext();
			}
		}
		pc.close();
		return rv;
	}

	private void resetTemplates(String tbl) {
		List<Template> templates = mTemplates.get(tbl);
		for ( Template t : templates ) {
			t.resetCursor();
		}
	}

	private ContentValues contentValuesFromUpdates(HashMap<String, String> updates) {
		ContentValues vals = new ContentValues();
		for ( String k : updates.keySet() ) {
			vals.put(k, updates.get(k));
		}
		return vals;
	}


	void save(String tbl, HashMap<String, String> updates, int id) {
		mDb.update(tbl, contentValuesFromUpdates(updates), "_id=?", new String[] { Integer.toString(id) });
		resetTemplates(tbl);
	}

	Cursor create(String tbl, HashMap<String, String> updates) {
		return getCursorById(tbl, (int) mDb.insert(tbl, null, contentValuesFromUpdates(updates)));
	}
	
	void register(String mTbl, Template template) {
		List<Template> templates = null;
		if ( !mTemplates .containsKey(mTbl) ) {
			templates = new ArrayList<Template>();
			mTemplates.put(mTbl, templates);
		} else {
			templates = mTemplates.get(mTbl);
		}
		if ( !templates.contains(template) ) {
			templates.add(template);
		}
	}

	void unregister(String mTbl, Template template) {
		List<Template> templates = mTemplates.get(mTbl);
		if ( null != templates ) {
			templates.remove(template);
		}
	}

	public void table(String tbl, String... ss) {
	}

	public Template get(String tbl) {
		return makeTemplate(tbl, null);
	}

	public Template get(String tbl, int id) {
		return makeTemplate(tbl, getCursorById(tbl, id));
	}
}
