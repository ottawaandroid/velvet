package ca.ottawaandroid.velvet.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.InstrumentationTestCase;

import java.util.ArrayList;

public class DbTestCase extends InstrumentationTestCase {
	protected static final String TESTING_DATABASE = "velvet.db.testing";
	protected SQLiteDatabase mDb;

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Context ctxt = getInstrumentation().getContext();
		ctxt.deleteDatabase(TESTING_DATABASE);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		buildTestDatabase();
		insertTestData();
	}

	private void insertTestData() {
		String[] stuff = {
			"DROP TABLE IF EXISTS table0",
			"CREATE TABLE table0 (_id INTEGER PRIMARY KEY AUTOINCREMENT, a VARCHAR, b VARCHAR, c VARCHAR)",
			"INSERT INTO table0 (a, b, c) VALUES ('a0', 'b0', 'c0')",
			"INSERT INTO table0 (a, b, c) VALUES ('a1', 'b1', 'c1')",
			"INSERT INTO table0 (a, b, c) VALUES ('a2', 'b2', 'c2')",
		};
		for ( String sql : stuff) {
			mDb.execSQL(sql);
		}
	}

	private void buildTestDatabase() {
 		Context ctxt = getInstrumentation().getContext();
 		SQLiteOpenHelper oh = new SQLiteOpenHelper(ctxt, TESTING_DATABASE, null, 1) {
			@Override
			public void onCreate(SQLiteDatabase arg0) {
			}

			@Override
			public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			}
 		};
 		this.mDb = oh.getWritableDatabase();
	}

	protected boolean listEquals(ArrayList<String> ks, String[] strings) {
		boolean rv = false;
		if ( ks.size() == strings.length ) {
			int i = 0;
			rv = true;
			while ( rv && i < ks.size() ) {
				rv = (0 == ks.get(i).compareTo(strings[i]));
				i++;
			}
		}
		return rv;
	}
}