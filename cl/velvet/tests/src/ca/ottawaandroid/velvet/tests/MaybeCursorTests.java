package ca.ottawaandroid.velvet.test;

import ca.ottawaandroid.velvet.MaybeCursor;

public class MaybeCursorTests extends DbTestCase {
    private static class Detector extends MaybeCursor.EachCallbacks {
        public boolean sawBefore = false;
        public boolean sawAfter = false;
        public int     eachCount = 0;

        @Override
        public void before() {
            sawBefore = true;
        }

        @Override
        public void next(MaybeCursor c) {
            eachCount++;
        }

        @Override
        public void after() {
            sawAfter = true;
        }
    }

    private Detector mDetect;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
        mDetect = new Detector();
	}

    public void testEach() {
        new MaybeCursor(mDb.rawQuery("SELECT * FROM table0", null)).each(mDetect);

        assertTrue(mDetect.sawBefore);
        assertTrue(mDetect.sawAfter);
        assertEquals(3, mDetect.eachCount);
    }

    public void testEmptyEach() {
        new MaybeCursor(mDb.rawQuery("SELECT * FROM table0 WHERE _id=17", null)).each(mDetect);

        assertFalse(mDetect.sawBefore);
        assertFalse(mDetect.sawAfter);
        assertEquals(0, mDetect.eachCount);
    }
}

