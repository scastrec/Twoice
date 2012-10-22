package stephane.castrec.Twoice.adapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import stephane.castrec.Twoice.R;
import stephane.castrec.Twoice.objects.SessionObject;
import twitter4j.Status;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetAdapter extends BaseAdapter {

	private List<Status> mTweets;
	private Context mContext;
	
	private ImageView mAvatar = null;
	private TextView mUser = null;
	private TextView mTweet = null;
	private TextView mDate = null;
	
	private Bitmap bmp = null;

	public TweetAdapter(Context pContext, List<Status> pTweets){
		mContext = pContext;
		if(pTweets != null)
			mTweets = pTweets;
	}
	
	/**
	 * updating list when receiving new item from server
	 * @param pList
	 */
	public void updateList(List<Status> pList){
		if(pList != null){
			if(mTweets != null) {
				mTweets.clear();
				mTweets.addAll(pList);
			} else {
				mTweets = pList;
			}
		}
	}

	@Override
	public int getCount() {
		if(mTweets != null)
			return mTweets.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int index) {
		if(mTweets != null)
			return mTweets.get(index);
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int index, View pView, ViewGroup arg2) {
		try {
			if (pView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				pView = inflater.inflate(R.layout.tweetitem, null);
			}
			mAvatar=(ImageView)pView.findViewById(R.id.item_avatar);
			mAvatar.setImageResource(R.drawable.ic_launcher);
			mAvatar.setTag(mTweets.get(index).getId());
			mDate = (TextView) pView.findViewById(R.id.item_date);
			mDate.setText(mTweets.get(index).getCreatedAt().getMonth()+"/"+mTweets.get(index).getCreatedAt().getDay());
			
			bmp = SessionObject.getAvatars(mTweets.get(index).getUser().getId());
			if( bmp == null) {
				DownLoadImage task1 = new DownLoadImage(mAvatar, mTweets.get(index));
				task1.execute(mTweets.get(index).getUser().getProfileImageURL());
			} else {
				mAvatar.setImageBitmap(bmp);
			}
			
			mUser=(TextView)pView.findViewById(R.id.item_pseudo);
			mUser.setText(mTweets.get(index).getUser().getName());
			mTweet=(TextView)pView.findViewById(R.id.item_tweet);
			mTweet.setText(mTweets.get(index).getText().toString());	
		} catch (Exception e) {
			Log.e("", "TweetAdapter getView exception",e);
		}
		
		return pView;
	}
	
	/**
	 * DownloadWebPageTask: Download thumbnails
	 * @author Stephane
	 *
	 */
	public class DownLoadImage extends AsyncTask<URL, Integer, Bitmap> {

		private ImageView mimage;
		private twitter4j.Status mTweet ;

		@Override
		protected Bitmap doInBackground(URL... url) {
			try {
				/* Open a new URL and get the InputStream to load data from it. */
				URLConnection conn = url[0].openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				/* Buffered is always good for a performance plus. */
				BufferedInputStream bis = new BufferedInputStream(is);
				/* Decode url-data to a bitmap. */
				Options lOption = new BitmapFactory.Options();
				//lOption.inSampleSize = 32;
				Bitmap bm = BitmapFactory.decodeStream(bis, null, lOption);
				bis.close();
				is.close();
				return bm;
			} catch (IOException e) {
				Log.e("twoice", "Remote Image Exception", e);
				return null;
			}
		}

		public  DownLoadImage(ImageView pimageView, twitter4j.Status pTweet) {
			mimage = pimageView;
			mTweet = pTweet;
		}

		//@Override
		protected void onProgressUpdate(Integer i)  {
			super.onProgressUpdate(i);
		}

		@Override
		protected void onPostExecute(Bitmap result)  {
			try	{
				if(result != null){
					if(mimage.getTag().equals(mTweet.getId())){
						mimage.setImageBitmap(result);
						mimage.setVisibility(View.VISIBLE);
						SessionObject.addAvatars(mTweet.getUser().getId(), result);
					}
				}
			}
			catch (Exception e) {
				Log.e("twoice", "ProductItemAdapter onPostExecute exception",e);
			}
		}
	}

}
