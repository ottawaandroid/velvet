package ca.ottawaandroid.velvet.test;

import ca.ottawaandroid.velvet.migrations.MigrationSet;
import ca.ottawaandroid.velvet.migrations.CreateTable;
import android.database.Cursor;

public class MigrationsTest extends DbTestCase {
    public void setUp() throws Exception {
	super.setUp();
    }

    public void testApplyVersion() {
	MigrationSet migrations = new MigrationSet(){{
	    version(1);
	}};
	migrations.apply(mDb);

	assertEquals(1, getDatabaseVersion());
    }

    public void testCreateTable() {
	MigrationSet migrations = new MigrationSet(){{
	    version(1);
	    add( new CreateTable("table1", "c", "d", "e"));
	}};
	migrations.apply(mDb);

	assertEquals(1, getDatabaseVersion());
	assertEntryCount(0, "table1");
    }

    public void testAddVersionWithMigration(){
	MigrationSet migrations = new MigrationSet(){{
	    add(1, new CreateTable("table1", "c", "d", "e"));
	}};
	migrations.apply(mDb);

	assertEquals(1, getDatabaseVersion());
	assertEntryCount(0, "table1");
    }

    public int getDatabaseVersion(){
	Cursor c = mDb.query("DATABASE_VERSION", null, null, null, null, null, null, null);
	c.moveToFirst();
	int version = c.getInt(c.getColumnIndex("CURRENT_VERSION"));
	c.close();
	return version;
    }

    public Cursor getAllForTable(String tbl){
	return mDb.query(tbl, null, null, null, null, null, null, null);
    }

    public void assertEntryCount(int count, String tbl){
	Cursor c = getAllForTable(tbl);
	try {
	    assertEquals(count, c.getCount());
	} finally {
	    c.close();
	}
    }
}