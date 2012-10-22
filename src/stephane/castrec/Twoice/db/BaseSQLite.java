package stephane.castrec.Twoice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseSQLite extends SQLiteOpenHelper {

	public static final String TABLE_TWOICE = "twoice_tweets";
	public static final String COL_ID = "id";
	public static final String COL_TWEET = "tweet";
	public static final String COL_URL_PHOTO = "url_photo";
	public static final String COL_USER_ID = "userId";
	public static final String COL_USER_NAME = "userName";
	public static final String COL_DATE = "date";


	
	private static final String CREATE_BDD = "CREATE TABLE " + TABLE_TWOICE + " ("
	+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TWEET + " TEXT NOT NULL, "
	+ COL_URL_PHOTO + " TEXT NOT NULL, " + COL_USER_ID + " TEXT NOT NULL , " + COL_USER_NAME + " TEXT NOT NULL , " + COL_DATE + " TEXT NOT NULL);";
	
	public BaseSQLite(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("twoice", "BaseSQLite onCreate starting " + CREATE_BDD);
		db.execSQL(CREATE_BDD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		Log.d("twoice", "BaseSQLite onUpgrade starting");
		db.execSQL("DROP TABLE " + TABLE_TWOICE + ";");
		onCreate(db);
	}
}
