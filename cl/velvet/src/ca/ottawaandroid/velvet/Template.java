package ca.ottawaandroid.velvet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.database.Cursor;

public class Template implements Iterable<Template.Pair> {
	private List<String> mCols;
	private MaybeCursor mCursor = new MaybeCursor(null);
	private Schema mSchema;
	private String mTbl;
	private HashMap<String, String> updates = new HashMap<String, String>();
	
	public class Pair extends android.util.Pair<String, String> {
		public Pair(String k, String v) {
			super(k, v);
		}
	}
	
	private class LocalIterator implements Iterator<Pair> {
		private int mI = 0;

		public boolean hasNext() {
			return mI < mCols.size();
		}

		public Pair next() {
			Pair rv = new Pair(mCols.get(mI), mCursor.getString(mI));
			mI++;
			return rv;
		}

		public void remove() {
		}
	}
	
	Template(String tbl, List<String> cols, Cursor c, Schema s) {
		mCols = cols;
		mSchema = s;
		mTbl = tbl;
		setCursor(c);
		mSchema.register(mTbl, this);
	}
	
	private String getFromDb(String col) {
		String rv = null;
		if ( mCols.contains(col) ) {
			rv = mCursor.getString(col);
		}
		return rv;
	}

	private void setCursor(Cursor c) {
		mCursor = new MaybeCursor(c);
		mCursor.moveToFirst();
	}

	protected void resetCursor() {
		setCursor(mSchema.getCursorById(mTbl, getId()));
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		mSchema.unregister(mTbl, this);
	}

	public String get(String col){
		return updates.containsKey(col) ? updates.get(col) : getFromDb(col);
	}

	public void set(String col, String val){
		if ( mCols.contains(col) ) {
			updates.put(col, val);
		}
	}
	
	public int getId() {
		return Integer.parseInt(get("_id"));
	}
	
	public void save(){
		if ( mCursor.isValid() ) {
			mSchema.save(mTbl, updates, getId());
		} else {
			setCursor(mSchema.create(mTbl, updates));
		}
		updates.clear();
	}

	public Iterator<Pair> iterator() {
		return new LocalIterator();
	}
}