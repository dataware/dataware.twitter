package dataware.twitter.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import dataware.twitter.TwitterUtils;

@SuppressWarnings("serial")
public class AuthorizeServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger( 
			AuthorizeServlet.class.getName() 
	);
	
	private final String REQUEST_TOKEN = "oauth_request_token";
	private final String REQUEST_SECRET = "oauth_request_token_secret";
	private HttpSession session = null;
	
	public final static String ACCESS_TOKEN = "access_token";
	public final static String ACCESS_SECRET = "access_secret";
	public final static String UID = "uid";
	public final static String USERNAME = "username";
	
	///////////////////////////////////////////
	
	
	/**
	 * 
	 */
	public void doGet(
		HttpServletRequest req, 
		HttpServletResponse response )
	throws IOException {

		//-- get ready to return some html
		response.setContentType( "text/html" );
		
		//-- setup required persistence globals
	    this.session = req.getSession( true );
        
		String oauth_verifier =  req.getParameter( "oauth_verifier" ) ;
		String oauth_token = req.getParameter( "oauth_token" ) ;
        
		if ( oauth_token == null || oauth_verifier == null ) {
			requestAuthorization( response );
		} else {
			requestAccess( response, oauth_verifier );    	   
		}
	}
    
	
	///////////////////////////////////////////
	
	/**
	 * 
	 */
    private void requestAccess( HttpServletResponse response, String oauth_verifier ) 
    throws IOException {
    
    	PrintWriter out = response.getWriter();
		
    	Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer( TwitterUtils.CONSUMER_KEY, TwitterUtils.CONSUMER_SECRET );
			
        try {
        	AccessToken accessToken = twitter.getOAuthAccessToken(
        		session.getAttribute( REQUEST_TOKEN ).toString(),
				session.getAttribute( REQUEST_SECRET ).toString(), 
				oauth_verifier
			);
				
			session.setAttribute( USERNAME, accessToken.getScreenName() );
			session.setAttribute( UID, accessToken.getUserId() );
			session.setAttribute( ACCESS_TOKEN, accessToken.getToken() );
			session.setAttribute( ACCESS_SECRET, accessToken.getTokenSecret() );
				
		} catch (TwitterException e) {
			log.warning( "AuthorizeServlet : Twitter Exception " + e );
		}
		
		String head = "<html><head><title>Connect</title></head>";
		String body = "<body><script>window.close();</script></body></html>";
		out.print( head + body );
	}

    
	///////////////////////////////////////////
	
    
	/**
	 * get a request token and redirect the user to the twitter login
	 */
	private void requestAuthorization( HttpServletResponse response ) 
	throws IOException {
		
		 try {
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer( TwitterUtils.CONSUMER_KEY, TwitterUtils.CONSUMER_SECRET );
            
			RequestToken requestToken = twitter.getOAuthRequestToken();			
			session.setAttribute( REQUEST_TOKEN, requestToken.getToken() );
			session.setAttribute( REQUEST_SECRET, requestToken.getTokenSecret() );
			
			String redirectURL = 
				requestToken.getAuthorizationURL() + 
				"&oauth_callback_url=" + TwitterUtils.CALLBACK_URL;
			
			response.sendRedirect( redirectURL );
	            
		} catch (TwitterException e) {
			log.warning( "AuthorizeServlet: twitter exception! " + e );
		}
	}

}	
