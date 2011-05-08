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

	assertEquals(1, getDatabaseVersion());
    }

    public int getDatabaseVersion(){
	Cursor c = mDb.query("DATABASE_VERSION", null, null, null, null, null, null, null);
	c.moveToFirst();
	int version = c.getInt(c.getColumnIndex("CURRENT_VERSION"));
	c.close();
	return version;
    }
}