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

    public void testVersionMigrations(){
	MigrationSet migrations = new MigrationSet(){{
	    version(1);
	    add( new CreateTable("table1", "c", "d", "e"));
	    version(2);
	    add(new CreateTable("table2", "f", "g"));
	}};

	migrations.apply(mDb, 0, 1);
	assertEntryCount(0, "table1");
	Cursor c = mDb.rawQuery("SELECT tbl_name FROM sqlite_master WHERE type='table' and tbl_name='table2'", null);
	try {
	    assertEquals(0, c.getCount());
	} finally {
	    c.close();
	}

	// Migrations should automatically where to continue
	// from if there is an incomplete migration.
	migrations.apply(mDb);
	{
	    assertEntryCount(0, "table1");
	    assertEntryCount(0, "table2");
	}
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