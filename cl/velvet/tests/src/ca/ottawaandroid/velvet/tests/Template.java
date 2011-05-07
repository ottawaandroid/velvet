package ca.ottawaandroid.velvet.test;

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
	
	private static class MaybeCursor {
		private Cursor mC;

		public MaybeCursor(Cursor c) {
			mC = c;
		}

		public void close() {
			if ( null != mC ) {
				mC.close();
			}
		}

		public void moveToFirst() {
			if ( null != mC ) {
				mC.moveToFirst();
			}
		}

		public String getString(int i) {
			return (null != mC) ? mC.getString(i) : "";
		}
		
		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			close();
		}

		public String getString(String col) {
			String rv = "";
			if( mC != null ) {
				int i = -1;
				i = mC.getColumnIndex(col);
				rv = (i >= 0) ? mC.getString(i) : ""; 
			}
			return rv;
		}

		public boolean isValid() {
			return (null != mC);
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
	
	public Template(String tbl, List<String> cols, Cursor c, Schema s) {
		mCols = cols;
		mSchema = s;
		mTbl = tbl;
		setCursor(c);
		mSchema.register(mTbl, this);
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		mSchema.unregister(mTbl, this);
	}

	public String get(String col){
		return updates.containsKey(col) ? updates.get(col) : getFromDb(col);
	}

	private String getFromDb(String col) {
		String rv = null;
		if ( mCols.contains(col) ) {
			rv = mCursor.getString(col);
		}
		return rv;
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

	private void setCursor(Cursor c) {
		mCursor = new MaybeCursor(c); 
		mCursor.moveToFirst();
	}

	protected void resetCursor() {
		setCursor(mSchema.getCursorById(mTbl, getId()));
	}

	public Iterator<Pair> iterator() {
		return new LocalIterator();
	}
}