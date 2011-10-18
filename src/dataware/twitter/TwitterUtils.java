package dataware.twitter;

import java.text.SimpleDateFormat;
import java.util.Date;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import datasphere.dataware.DatawareUser;


public class TwitterUtils {
	
	//-- app account details
	public final static String CONSUMER_KEY = "BVhGYnT8GWihDPmcS6OlkQ";
	public final static String CONSUMER_SECRET = "9nW1ezhO5bAIWIo93eVsa7h0Rhqwn84phlwRPPQqpY";
	public final static String CALLBACK_URL = "http://dataware-twitter.appspot.com/authorize";
	
	//-- polling interval between asking twitter about any new updates
	public final static long POLLING_INTERVAL = 30 * 60 * 1000;
	
	//-- constants
	public final static String ACCESS_TOKEN = "access_token";
	public final static String ACCESS_SECRET = "access_secret";
	public final static String UID = "uid";
	public final static String USERNAME = "username";
	public final static String JID = "jid";
	public final static String TWITTER_USER_DETAILS = "twitter_details";
	
    public static SimpleDateFormat timefmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    
  //////////////////////////////
	
	public static String format( Date d ) {
		if ( d == null ) {
			return "never";
		} else {
			SimpleDateFormat fmt = new SimpleDateFormat( "dd MMM yyyy HH:mm" );
			return fmt.format( d );
		}
	}
	
	//////////////////////////////
	
	public static String format( long time ) {
		return ( time == 0 ) ? "never" : format ( new Date( time ) );
	}
	
	//////////////////////////////
	
	public static long getAsTime( Date d ) {
		return ( d == null ) ? 0 : d.getTime();
	}
	
	/////////////////////////////////////////
	
	public static Twitter getTwitterInstance( DatawareUser user )
	throws Exception {	
		AccessToken accessTtoken = new AccessToken( 
			user.getServiceAccessToken(), 
			user.getServiceSecret()
		);
		
		TwitterFactory factory = new TwitterFactory();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer( TwitterUtils.CONSUMER_KEY, TwitterUtils.CONSUMER_SECRET );
		twitter.setOAuthAccessToken( accessTtoken );
		
		return twitter;
	}
}
	
