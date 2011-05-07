package ca.ottawaandroid.velvet.test;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.InstrumentationTestCase;

public class SchemaTest extends InstrumentationTestCase {
	private static final String TESTING_DATABASE = "velvet.db.testing";
	private SQLiteDatabase mDb;

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

	private boolean listEquals(ArrayList<String> ks, String[] strings) {
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

	public void testEmptyTemplate() {
// migrations
//		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE) {{
//			table("table0", "a", "b", "c");
//		}};
		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE);
		
		Template t = s.get("table0");
		assertNotNull(t);
		assertEquals("", t.get("a"));
		assertEquals("", t.get("b"));
		assertEquals("", t.get("c"));
		assertNull(t.get("d"));
		assertNull(t.get("e"));
	}
	
	public void testPopulatedTemplate() {
		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE);

		Template t0 = s.get("table0", 1);
		{
			assertEquals("a0", t0.get("a"));
			assertEquals("b0", t0.get("b"));
			assertEquals("c0", t0.get("c"));
		}
		Template t1 = s.get("table0", 2);
		{
			assertEquals("a1", t1.get("a"));
			assertEquals("b1", t1.get("b"));
			assertEquals("c1", t1.get("c"));
		}
		Template t2 = s.get("table0", 3);
		{
			assertEquals("a2", t2.get("a"));
			assertEquals("b2", t2.get("b"));
			assertEquals("c2", t2.get("c"));
		}
		
	}
	
	public void testIterableTemplate() {
		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE);
		ArrayList<String> ks = new ArrayList<String>();
		ArrayList<String> vs = new ArrayList<String>();

		Template t0 = s.get("table0", 1);
		{
			for ( Template.Pair p : t0 ) {
				ks.add(p.first);
				vs.add(p.second);
			}
	
			assertTrue(listEquals(ks, new String[] { "_id", "a", "b", "c" }));
			assertTrue(listEquals(vs, new String[] { "1", "a0", "b0", "c0" }));
		}

		ks.clear();
		vs.clear();
		
		Template te = s.get("table0");
		{
			for ( Template.Pair p : te ) {
				ks.add(p.first);
				vs.add(p.second);
			}
	
			assertTrue(listEquals(ks, new String[] { "_id", "a", "b", "c" }));
			assertTrue(listEquals(vs, new String[] { "", "", "", "" }));
		}
	}
	
	public void testGetInvalidTemplate() {
		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE);
		
		Template t = s.get("invalidTable");
		assertNull(t);
	}
	
	public void testSaveTemplate() {
		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE);
		
		Template t0 = s.get("table0", 1);
		{
			t0.set("a", "aNew");
			t0.set("b", "bNew");
			
			assertEquals("aNew", t0.get("a"));
			assertEquals("bNew", t0.get("b"));
			assertEquals("c0", t0.get("c"));
		}
		Template oT0 = s.get("table0", 1);
		{
			assertEquals("a0", oT0.get("a"));
			assertEquals("b0", oT0.get("b"));
			assertEquals("c0", oT0.get("c"));
		}
		t0.save();
		{
			assertEquals("aNew", t0.get("a"));
			assertEquals("bNew", t0.get("b"));
			assertEquals("c0", t0.get("c"));
		}
		Template nT0 = s.get("table0", 1);
		{
			assertEquals("aNew", nT0.get("a"));
			assertEquals("bNew", nT0.get("b"));
			assertEquals("c0", nT0.get("c"));
		}
	}
	
	public void testSaveTemplateCheckOther() {
		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE);
		
		Template t0 = s.get("table0", 1);
		Template t1 = s.get("table0", 1);
		{
			t0.set("a", "aNew");
			t0.set("b", "bNew");
		}
		t0.save();
		
		{
			assertEquals("aNew", t1.get("a"));
			assertEquals("bNew", t1.get("b"));
			assertEquals("c0", t1.get("c"));
		}
	}
	
	public void testSetInvalidColumn() {
		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE);
		
		Template tN = s.get("table0");
		tN.set("a", "aN");
		tN.set("b", "bN");
		tN.set("c", "cN");
		tN.set("d", "dN");
		tN.set("e", "eN");

		assertEquals("aN", tN.get("a"));
		assertEquals("bN", tN.get("b"));
		assertEquals("cN", tN.get("c"));
		assertNull(tN.get("d"));
		assertNull(tN.get("e"));
	}
	
	public void testCreateTemplate() {
		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE);
		
		Template tN = s.get("table0");
		tN.set("a", "aN");
		tN.set("b", "bN");
		tN.set("c", "cN");
		tN.set("d", "dN");
		tN.set("e", "eN");

		tN.save();
		
		Template tS = s.get("table0", 4);
		assertNotNull(tS);
		assertFalse(0 == tS.get("_id").compareTo(""));
		assertEquals("aN", tS.get("a"));
		assertEquals("bN", tS.get("b"));
		assertEquals("cN", tS.get("c"));
		assertNull(tS.get("d"));
		assertNull(tS.get("e"));
	}
}
