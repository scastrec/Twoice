package stephane.castrec.Twoice.db;

import java.util.LinkedList;
import java.util.List;

import stephane.castrec.Twoice.objects.BasicTweet;
import twitter4j.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LastTweetsBDD {
	private static final String NOM_BDD = "twoice.db";


	private SQLiteDatabase _bdd;

	private  BaseSQLite _BaseSQLite;

	public LastTweetsBDD(Context context){
		//On créer la BDD et sa table
		_BaseSQLite = new BaseSQLite(context, NOM_BDD, null, 1);
		//_BaseSQLite.getWritableDatabase();
	}

	public void open(Boolean read){
		//on ouvre la BDD en écriture
		if(read)
			_bdd = _BaseSQLite.getReadableDatabase();
		else
			_bdd = _BaseSQLite.getWritableDatabase();
	}

	public void close(){
		//on ferme l'accès à la BDD
		_bdd.close();
	}

	public SQLiteDatabase getBDD(){
		return _bdd;
	}

	/**
	 * saveStatus
	 * @param pStatus
	 */
	public void saveStatus(List<Status> pStatus){
		try	{
			open(false);
			
			//first clear all previous statuses
			clearDB();
			
			//create new content
			//Create ContentValues (Works like HashMap)
			ContentValues values = new ContentValues();
			for(Status s : pStatus){
				values.clear();
				values.put(BaseSQLite.COL_ID, s.getId());
				values.put(BaseSQLite.COL_DATE, s.getCreatedAt().toString());
				values.put(BaseSQLite.COL_TWEET, s.getText());
				values.put(BaseSQLite.COL_URL_PHOTO, s.getUser().getProfileImageURL().toString());
				values.put(BaseSQLite.COL_USER_ID, s.getUser().getId());
				values.put(BaseSQLite.COL_USER_NAME, s.getUser().getName());
				getBDD().insert(BaseSQLite.TABLE_TWOICE, null, values);
			}
			close();
		}
		catch (Exception e) {
			Log.e("twoice", "saveStatus insertStatus exception",e);
		}
	}

	/**
	 * clearDB
	 * 		Remove all status fromDB 
	 */
	private void clearDB(){
		try {
			_bdd.delete(BaseSQLite.TABLE_TWOICE, null, null);
		} catch (Exception e) {
			Log.e("twoice", "clearDB insertStatus exception",e);
		}
	}

	/**
	 * remove tweet from is id
	 * @param id
	 * @return
	 */
	public int removeStatusWithID(String id){
		//Suppression d'un livre de la BDD grâce à l'ID
		return _bdd.delete(BaseSQLite.TABLE_TWOICE, BaseSQLite.COL_ID + " = " +id, null);
	}

	/**
	 * get all saved Tweets store in db
	 * @return
	 */
	public LinkedList<Status> getAllSavedStatus(){
		try	{
			open(true);
			Log.d("twoice", "LastTweetsBDD  getAllSavedStatus starting");
			//get values from db and put it in Cursor
			Cursor c = _bdd.query(BaseSQLite.TABLE_TWOICE, new String[] {BaseSQLite.COL_ID, BaseSQLite.COL_ID,BaseSQLite.COL_DATE, BaseSQLite.COL_TWEET, BaseSQLite.COL_URL_PHOTO, BaseSQLite.COL_USER_ID, BaseSQLite.COL_USER_NAME}, null, null, null, null, null);
			//if get something from db
			if(c != null && c.getCount()>0){
				LinkedList<Status> lStatuss = new LinkedList<Status>();
				for(int i = 0; i<c.getCount(); i++)	{				
					lStatuss.add(cursorToStatus(c, i));
				}
				//On ferme le cursor
				c.close();
				return lStatuss;
			} else {
				Log.i("twoice", "LastTweetsBDD : no tweets found in DB");
			}
			close();
		}
		catch (Exception e) {
			Log.e("twoice", "LastTweetsBDD  getAllSavedStatus  exception",e);
		}
		return null;
	}

	/**
	 * 
	 * @param c
	 * @param i
	 * @return
	 */
	private Status cursorToStatus(Cursor c, int i){
		try	{
			if (c.getCount() == 0)
				return null;
			c.moveToPosition(i);

			return new BasicTweet(c.getInt(c.getColumnIndex(BaseSQLite.COL_ID)), c.getString(c.getColumnIndex(BaseSQLite.COL_TWEET)), 
					c.getString(c.getColumnIndex(BaseSQLite.COL_DATE)), c.getString(c.getColumnIndex(BaseSQLite.COL_USER_NAME)), 
					c.getInt(c.getColumnIndex(BaseSQLite.COL_USER_ID)), c.getString(c.getColumnIndex(BaseSQLite.COL_URL_PHOTO)));

		}
		catch (Exception e) {
			Log.e("twoice", "ManageFavoritesBdd cursorToStatus exception",e);
		}
		return null;
	}
}
