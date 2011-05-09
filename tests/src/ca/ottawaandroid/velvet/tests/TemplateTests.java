package ca.ottawaandroid.velvet.test;

import java.util.ArrayList;

import ca.ottawaandroid.velvet.Schema;
import ca.ottawaandroid.velvet.Template;

public class TemplateTests extends DbTestCase {
    private Schema mSchema;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mSchema = new Schema(getInstrumentation().getContext(), TESTING_DATABASE);
	}

	public void testEmptyTemplate() {
// migrations
//		Schema s = new Schema(getInstrumentation().getContext(), TESTING_DATABASE) {{
//			table("table0", "a", "b", "c");
//		}};
		Template t = mSchema.get("table0");
		assertNotNull(t);
		assertEquals("", t.get("a"));
		assertEquals("", t.get("b"));
		assertEquals("", t.get("c"));
		assertNull(t.get("d"));
		assertNull(t.get("e"));
	}
	
	public void testPopulatedTemplate() {
		Template t0 = mSchema.get("table0", 1);
		{
			assertEquals("a0", t0.get("a"));
			assertEquals("b0", t0.get("b"));
			assertEquals("c0", t0.get("c"));
		}
		Template t1 = mSchema.get("table0", 2);
		{
			assertEquals("a1", t1.get("a"));
			assertEquals("b1", t1.get("b"));
			assertEquals("c1", t1.get("c"));
		}
		Template t2 = mSchema.get("table0", 3);
		{
			assertEquals("a2", t2.get("a"));
			assertEquals("b2", t2.get("b"));
			assertEquals("c2", t2.get("c"));
		}
		
	}
	
	public void testIterableTemplate() {
		ArrayList<String> ks = new ArrayList<String>();
		ArrayList<String> vs = new ArrayList<String>();

		Template t0 = mSchema.get("table0", 1);
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
		
		Template te = mSchema.get("table0");
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
		Template t = mSchema.get("invalidTable");
		assertNull(t);
	}
	
	public void testSaveTemplate() {
		Template t0 = mSchema.get("table0", 1);
		{
			t0.set("a", "aNew");
			t0.set("b", "bNew");
			
			assertEquals("aNew", t0.get("a"));
			assertEquals("bNew", t0.get("b"));
			assertEquals("c0", t0.get("c"));
		}
		Template oT0 = mSchema.get("table0", 1);
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
		Template nT0 = mSchema.get("table0", 1);
		{
			assertEquals("aNew", nT0.get("a"));
			assertEquals("bNew", nT0.get("b"));
			assertEquals("c0", nT0.get("c"));
		}
	}
	
	public void testSaveTemplateCheckOther() {
		Template t0 = mSchema.get("table0", 1);
		Template t1 = mSchema.get("table0", 1);
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
		Template tN = mSchema.get("table0");
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
		Template tN = mSchema.get("table0");
		tN.set("a", "aN");
		tN.set("b", "bN");
		tN.set("c", "cN");
		tN.set("d", "dN");
		tN.set("e", "eN");

		tN.save();
		
		Template tS = mSchema.get("table0", 4);
		assertNotNull(tS);
		assertFalse(0 == tS.get("_id").compareTo(""));
		assertEquals("aN", tS.get("a"));
		assertEquals("bN", tS.get("b"));
		assertEquals("cN", tS.get("c"));
		assertNull(tS.get("d"));
		assertNull(tS.get("e"));
	}
}
