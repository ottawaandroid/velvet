package ca.ottawaandroid.velvet;

import android.database.Cursor;

public class MaybeCursor {
    public static class EachCallbacks {
        public void before() { }
        public void next(MaybeCursor c) { }
        public void after() { }
    }

    private Cursor mC;

    public MaybeCursor(Cursor c) {
        mC = c;
    }

    private void eachInner(EachCallbacks ef) {
		if ( mC.getCount() > 0 ) {
            ef.before();

			mC.moveToFirst();
			while ( !mC.isAfterLast() ) {
                ef.next(this);
				mC.moveToNext();
			}
            ef.after();
            mC.moveToFirst();
		}
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
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

    public void each(EachCallbacks ef) {
        if ( null != mC ) {
            eachInner(ef);
        }
    }
}

