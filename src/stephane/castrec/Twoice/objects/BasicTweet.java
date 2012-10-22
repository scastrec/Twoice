package stephane.castrec.Twoice.objects;

import java.util.Date;

import android.text.format.DateFormat;


import twitter4j.Annotations;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class BasicTweet implements Status{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String text;
	private String date;
	private User user;
	
	public BasicTweet(int id, String text, String date, String userName, int userId, String urlPhoto){
		
		this.id = id;
		this.text = text;
		this.date = date;
		user = new BasicUser(userId, userName, urlPhoto);
	}

	@Override
	public int compareTo(Status another) {
		return 0;
	}

	@Override
	public int getAccessLevel() {
		return 0;
	}

	@Override
	public RateLimitStatus getRateLimitStatus() {
		return null;
	}

	@Override
	public HashtagEntity[] getHashtagEntities() {
		return null;
	}

	@Override
	public MediaEntity[] getMediaEntities() {
		return null;
	}

	@Override
	public URLEntity[] getURLEntities() {
		return null;
	}

	@Override
	public UserMentionEntity[] getUserMentionEntities() {
		return null;
	}

	@Override
	@SuppressWarnings(value = {"deprecation" })
	public Annotations getAnnotations() {
		return null;
	}

	@Override
	public long[] getContributors() {
		return null;
	}

	@Override
	public Date getCreatedAt() {
		return new Date();//FIXME
	}

	@Override
	public GeoLocation getGeoLocation() {
		return null;
	}

	@Override
	public String getInReplyToScreenName() {
		return null;
	}

	@Override
	public long getInReplyToStatusId() {
		return 0;
	}

	@Override
	public long getInReplyToUserId() {
		return 0;
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	public long getRetweetCount() {
		return 0;
	}

	@Override
	public Status getRetweetedStatus() {
		return null;
	}

	@Override
	public String getSource() {
		return null;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public boolean isFavorited() {
		return false;
	}

	@Override
	public boolean isRetweet() {
		return false;
	}

	@Override
	public boolean isRetweetedByMe() {
		return false;
	}

	@Override
	public boolean isTruncated() {
		return false;
	}

	@Override
	public long getId() {
		return id;
	}
}
