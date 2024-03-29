package stephane.castrec.Twoice.objects;

import java.net.URL;
import java.util.Date;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.User;

public class BasicUser implements User{
	
	private int id;
	private String name;
	private String urlPhoto;
	
	public BasicUser(int id, String name, String urlPhoto){
		this.id = id;
		this.name = name;
		this.urlPhoto = urlPhoto;
	}

	@Override
	public int compareTo(User another) {
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
	public Date getCreatedAt() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public int getFavouritesCount() {
		return 0;
	}

	@Override
	public int getFollowersCount() {
		return 0;
	}

	@Override
	public int getFriendsCount() {
		return 0;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getLang() {
		return null;
	}

	@Override
	public int getListedCount() {
		return 0;
	}

	@Override
	public String getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getProfileBackgroundColor() {
		return null;
	}

	@Override
	public String getProfileBackgroundImageUrl() {
		return null;
	}

	@Override
	public String getProfileBackgroundImageUrlHttps() {
		return null;
	}

	@Override
	public URL getProfileImageURL() {
		try {
			return new URL(urlPhoto);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public URL getProfileImageUrlHttps() {
		return null;
	}

	@Override
	public String getProfileLinkColor() {
		return null;
	}

	@Override
	public String getProfileSidebarBorderColor() {
		return null;
	}

	@Override
	public String getProfileSidebarFillColor() {
		return null;
	}

	@Override
	public String getProfileTextColor() {
		return null;
	}

	@Override
	public String getScreenName() {
		return null;
	}

	@Override
	public Status getStatus() {
		return null;
	}

	@Override
	public int getStatusesCount() {
		return 0;
	}

	@Override
	public String getTimeZone() {
		return null;
	}

	@Override
	public URL getURL() {
		return null;
	}

	@Override
	public int getUtcOffset() {
		return 0;
	}

	@Override
	public boolean isContributorsEnabled() {
		return false;
	}

	@Override
	public boolean isFollowRequestSent() {
		return false;
	}

	@Override
	public boolean isGeoEnabled() {
		return false;
	}

	@Override
	public boolean isProfileBackgroundTiled() {
		return false;
	}

	@Override
	public boolean isProfileUseBackgroundImage() {
		return false;
	}

	@Override
	public boolean isProtected() {
		return false;
	}

	@Override
	public boolean isShowAllInlineMedia() {
		return false;
	}

	@Override
	public boolean isTranslator() {
		return false;
	}

	@Override
	public boolean isVerified() {
		return false;
	}

}
