package dataware.twitter.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import twitter4j.Twitter;
import twitter4j.User;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

import datasphere.dataware.ActivityStatus;
import datasphere.dataware.ConnectivityStatus;
import datasphere.dataware.DatawareUser;
import dataware.twitter.CookieUtils;
import dataware.twitter.TwitterUserStats;
import dataware.twitter.PMF;
import dataware.twitter.TwitterUtils;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;

@SuppressWarnings("serial")
		
public class InterfaceServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger( 
			HttpServlet.class.getName() 
	);
	
	public final static String LOGIN_TEMPLATE = "login.ftl";
	public final static String CONNECT_TEMPLATE = "connect.ftl";
	public final static String LINKER_TEMPLATE = "linker.ftl";
	public final static String MAIN_PAGE_TEMPLATE = "mainPage.ftl";
	
	public final static String LOGOUT_ACTION   = "logout";
	public final static String CONNECT_ACTION  = "connect";
	public final static String NEWJID_ACTION   = "newJid";
	public final static String RETRYJID_ACTION = "retryJid";
	public final static String LOGIN_ACTION    = "login";
	public final static String PAUSE_ACTION    = "pause";
	public final static String RESTART_ACTION  = "restart";
	public final static String NULL_ACTION     = "";
	public final static String ACTION = "action";
	
	private PersistenceManager pm = null;
	private HttpSession session = null;
	private Configuration cfg = null;
	
	
	///////////////////////////////////////////
	
	
	/**
	 * 
	 */
	public void doGet(
		HttpServletRequest request, 
		HttpServletResponse response )
	throws IOException {

		//-- get ready to return some html
		response.setContentType( "text/html" );
		
		//-- setup the freemarker configuration files (n.b. classpath must have been set)
		cfg = new Configuration();
		cfg.setObjectWrapper( new DefaultObjectWrapper() );
		cfg.setServletContextForTemplateLoading( this.getServletContext(), "/templates/"); 
	            
		//-- setup required persistence globals
	    this.pm = PMF.get().getPersistenceManager();
	    this.session = request.getSession( true );
	    
	    //-- if we have been passed a new datasphere address store it in the session
	    if ( request.getParameter( TwitterUtils.JID ) != null )
	    	session.setAttribute( TwitterUtils.JID, request.getParameter( TwitterUtils.JID ) );
	
	    //-- and then determine the action that we're being expected to process
	    String action = 
	    	request.getParameter( ACTION ) != null ?
	    	request.getParameter( ACTION ) :
	    	NULL_ACTION;
	    
		//-- do we have authorization information (and hence service uid/access_token)?
	    boolean authorized = checkAuthorization( request );
	    if ( !authorized ) {
	    	log.warning( "user has not been authorized, so redirecting to login page..." );
	    	loginResponse( response );
	    	return;
	    }
	    
		//-- do we have this user in our records ( keyed by their service uid )?
	    DatawareUser user = DatawareUser.fetch( 
	    	pm, session.getAttribute( TwitterUtils.UID ).toString() 
	    );
	    
	    //-- do we have session information for this user?
	    if ( session.getAttribute( TwitterUtils.TWITTER_USER_DETAILS ) == null ) {
	    	try {
		    	log.warning( "user authorized and in datastore, but no service specific data..." );
	    		populateUserData( user );
	    	}
	    	catch ( Exception e ) {
		    	log.warning( "could not contact the service, so bailing... " + e );
	    		errorResponse( response );
	    		return;
	    	}
	    }
	    
	    //-- if we have got this far we are ready to process the action
	    processAction( user, action, request, response );
		
		//-- and finally tidy up
		pm.close();
	}


	///////////////////////////////////////////
	
	
	public boolean checkAuthorization( HttpServletRequest request ) {
		
		if ( session.getAttribute( TwitterUtils.UID ) != null && 
			 session.getAttribute( TwitterUtils.ACCESS_TOKEN ) != null ) {
			return true;
		} else {
			return false;
		}
	}
	
	
	///////////////////////////////////////////


	public DatawareUser createUser()
	throws IOException {
		
		DatawareUser user = null;
		TwitterUserStats fBUserStats = null;
		
    	user = new DatawareUser( 
    		session.getAttribute( TwitterUtils.UID ).toString(), 
    		session.getAttribute( TwitterUtils.ACCESS_TOKEN ).toString()
    	);
    	
    	user.setServiceSecret( 
    		session.getAttribute( TwitterUtils.ACCESS_SECRET ).toString() 
    	);
    	
		fBUserStats = new TwitterUserStats( 
			session.getAttribute( TwitterUtils.UID ).toString() 
		);
	        
		pm.makePersistent( user );
		pm.makePersistent( fBUserStats );
		return user;
	}
	
	
	///////////////////////////////////////////
	
	
	private void processAction(		
		DatawareUser user,
		String action,
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException 
	{
		log.warning( "processing action [" + action +"]" );
		
	    //-- if the user has just logged in, record that fact.
		if ( action.equals( LOGIN_ACTION ) ) {
			if ( user == null ) {
		    	log.warning( "user authorized, but not in datastore, so creating..." );
				user = createUser();
			}
    		log.warning( "successful login for " + user.getUid() );
    	}
		
		//-- invariance check
		if ( user == null ) {
			log.warning( "No user has been obtained or created. redirect to login..." );
	    	loginResponse( response );
	    	return;
		}
		
		//-- is the user logging out?
		if ( action.equals( LOGOUT_ACTION ) ) {
	    	log.warning( "user " + user.getUid() + " is logging out..." );
	    	CookieUtils.deleteAllCookies( request, response );
	    	loginResponse( response );
	    	return;	
		}
		
		//-- does the person need to provide a jid?
		else if ( action.equals( CONNECT_ACTION ) ) {	
			log.warning( "user has requested a new jid - redirecting to connect page" );
			connectResponse( response, user, null );
			return;
		}
		
		//-- has the user provided a new datasphere jid?
		else if ( action.equals( NEWJID_ACTION ) ) {

			String jid = session.getAttribute( "jid" ).toString();

			//-- attempt to invite the new datasphere to communicate
			try {
				if ( jid == null ) 
					throw new Exception();
				
				if ( user.hasNewJID( jid ) ) {
					XMPPService xmpp = XMPPServiceFactory.getXMPPService();
					xmpp.sendInvitation( new JID( jid ) );
					
					user.setCatalogJid( jid );
					user.setCatalogMtime( System.currentTimeMillis() );
					user.setCatalogConnectivity( ConnectivityStatus.PENDING );
					
					linkerResponse( response, user );
		        	log.warning( "new Jid (" + jid + ") created for uid " + 
		        		user.getUid() + ". Redirecting to linker page..." );
		    		return;
				}
				else {
					log.warning( "user is trying to change to the same datasphere address" );
				}
				
    		} catch ( Exception e ) {
    			log.warning( "new Jid (" + jid + ") failed for uid " + 
    				user.getUid() + ". Redirecting to connect page..." );
    			connectResponse( response, user, "invalid" );
        		return;
    		}
		}
		
		//-- has the user provided a new datasphere jid?
		else if ( action.equals( RETRYJID_ACTION ) ) {

			try {
				if ( user.getCatalogJid() == null ) 
					throw new Exception();
				
				XMPPService xmpp = XMPPServiceFactory.getXMPPService();
				xmpp.sendInvitation( new JID( user.getCatalogJid().getEmail() ) );
				user.setCatalogMtime( System.currentTimeMillis() );
					
				linkerResponse( response, user );
		        log.warning( " Jid (" + user.getCatalogJid() + ") reinvited for uid " 
		        	+ user.getUid() + ". Redirecting to linker page..." );
	    		return;

			} catch ( Exception e ) {
    			log.warning( "Jid reinvite (" + user.getCatalogJid() + ") failed for uid " 
    				+ user.getUid() + ". Redirecting to connect page..." );
    			connectResponse( response, user, "invalid" );
        		return;
    		}
		}
		
		//-- does the person want to pause the service
		else if ( action.equals( PAUSE_ACTION ) ) {	
			log.warning( "user has requested that the service be paused" );
			user.setActivity( ActivityStatus.INACTIVE );
		}
	
		//-- does the person want to restart the service
		else if ( action.equals( RESTART_ACTION ) ) {	
			log.warning( "user has requested that the service be restarted" );
			
			if ( user.isComplete() ) {
				user.setActivity( ActivityStatus.ACTIVE );
				log.warning( "service now active" );

				TwitterUserStats fBUserStats = TwitterUserStats.fetch( pm , user.getUid() );
				
				//-- if they have no record of a actve poll task create one
				if ( fBUserStats.getPostsNextPoll() < System.currentTimeMillis() ) {
					
					long nextPoll = System.currentTimeMillis() + TwitterUtils.POLLING_INTERVAL;
					fBUserStats.setPostsNextPoll( nextPoll );
					
					Queue queue = QueueFactory.getDefaultQueue();
					queue.add(
						TaskOptions.Builder.url( "/retrieveUpdate" )
						.param( "uid", user.getUid() )
						.countdownMillis( TwitterUtils.POLLING_INTERVAL )
					);
					
					log.warning( "polling restarted" );
				}
			}
			else {
				log.warning( "cannot start service because datasphere connection is incomplete" );
			}
		}
		
		//-- does the user have a jid? If not we need one.
		if ( user.getCatalogJid() == null ) {
			log.warning( "user " + user.getUid() + " has no jid - redirecting to connect page" );
			connectResponse( response, user, "missing" );
			return;
		}
	    
    	log.warning( "all is good with the world for uid " 
    		+ user.getUid() + " - redirecting to main page. ");
		mainPageResponse( response, user );
		return;
    }

	
	///////////////////////////////////////////
	
	
	private void mainPageResponse( 
		HttpServletResponse response, 
		DatawareUser user ) 
	throws IOException {	
		log.warning( "creating a main page response ");
		
		PrintWriter out = response.getWriter();
		Map< String, Object > data = new HashMap< String, Object >();
		data.put( "user", user );
		User twitterUserDetails = (User) session.getAttribute( TwitterUtils.TWITTER_USER_DETAILS );
		data.put( "twitterUserDetails", twitterUserDetails);
		
		try {
			TwitterUserStats fBUserStats = TwitterUserStats.fetch( pm , user.getUid() );
			
			data.put( "fBUserStats", fBUserStats );

			data.put( "postsLastPolled", 
				TwitterUtils.format( fBUserStats.getPostsLastPolled( ) ) );
			
			data.put( "postsNextPoll", 
					TwitterUtils.format( fBUserStats.getPostsNextPoll() ) );
			
			data.put( "postsLast", 
					TwitterUtils.format( fBUserStats.getPostsLast() ) );
		
			data.put( "joined", TwitterUtils.format( user.getCtime() ) );
			
			log.warning( "retrieved user stats." );
		}
		catch ( JDOObjectNotFoundException e ) {
			log.warning( "disturbingly cannot find the TwitterUserStats for " + user.getUid() );
		}
		
		
		try {
			cfg.getTemplate( MAIN_PAGE_TEMPLATE ).process( data, out );
		} catch (TemplateException e) {
			log.warning( "TEMPLATE ERROR: cannot find connect template - " + e );
		}
	}

	
	///////////////////////////////////////////
	
	
	private void linkerResponse( HttpServletResponse response, DatawareUser user ) 
	throws IOException {

		log.warning( "creating a linker response ");
		
		PrintWriter out = response.getWriter();
		Map< String, Object > data = new HashMap< String, Object >();
		data.put( "user", user );
		
		try {
			cfg.getTemplate( LINKER_TEMPLATE ).process( data, out );
		} catch (TemplateException e) {
			log.warning( "TEMPLATE ERROR: cannot find connect template - " + e );
		}
	}

	
	///////////////////////////////////////////
	
	
	private void connectResponse( 
		HttpServletResponse response, 
		DatawareUser user, 
		String cause ) 
	throws IOException {

		if ( cause == null ) cause = "missing";
		log.warning( "creating a connect response ");
		
		PrintWriter out = response.getWriter();
		Map< String, Object > data = new HashMap< String, Object >();
		data.put( "user", user );
		data.put( "cause", cause );
		
		try {
			cfg.getTemplate( CONNECT_TEMPLATE ).process( data, out );
		} catch (TemplateException e) {
			log.warning( "TEMPLATE ERROR: cannot find connect template - " + e );
		}
	}

	
	/////////////////////////////////////////
	
	
	private void loginResponse( HttpServletResponse response ) 
	throws IOException {
		
		log.warning( "creating a login response...");
		this.session.invalidate();
		
		PrintWriter out = response.getWriter();
		Map< String, Object > data = new HashMap< String, Object >();
		
		try {
			cfg.getTemplate( LOGIN_TEMPLATE ).process( data, out );
		} catch (TemplateException e) {
			log.warning( "TEMPLATE ERROR: cannot find login template - " + e );
		}
	}

	
	///////////////////////////////////////////
	
	private void errorResponse( HttpServletResponse response )
	throws IOException {
		log.warning( "creating an error response ");
		this.session.invalidate();
		
		PrintWriter out = response.getWriter();
		Map< String, Object > data = new HashMap< String, Object >();
		
		try {
			cfg.getTemplate( LOGIN_TEMPLATE ).process( data, out );
		} catch (TemplateException e) {
			log.warning( "TEMPLATE ERROR: cannot find error template - " + e );
		}
	}

	
	/////////////////////////////////////////
	

	/**
	 * @throws Exception 
	 * 
	 */
	private void populateUserData( DatawareUser user )
	throws Exception {
		
		if ( user == null )	return;
		Twitter twitter = TwitterUtils.getTwitterInstance( user );
		User twitterDetails = twitter.showUser( Integer.parseInt( user.getUid() ) );
		session.setAttribute( TwitterUtils.TWITTER_USER_DETAILS, twitterDetails );
	}

}
