package dataware.twitter.servlets;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

import datasphere.dataware.ActivityStatus;
import datasphere.dataware.ConnectivityStatus;
import datasphere.dataware.DatawareUser;
import dataware.twitter.TwitterUserStats;
import dataware.twitter.PMF;

/**
 * Invitations can be sent - however what is received is merely the JID
 * Thus the datasphere (to automatically accept) needs to already know 
 * that the request is coming to send an immediate response. Hence a hack is
 * required that 
 * 
 * SendInvitation/SendMessage/GetPresence will throw an error for an invalid id,
 * but just say that the person is "offline" for a non-existent jid.
 * 
 * GAE accepts all invitations. It does not support IQ or presence stanzas.
 * However we can get at the raw XML of the incoming request.
 * There also seems no way in GAE to detect that an "accept" has arrived.
 *  
 * @author psxjog
 *
 */
@SuppressWarnings("serial")
public class ConnectServlet extends HttpServlet {
	
	@SuppressWarnings("unused")
	private static final String ACCEPT_MESSAGE = "subscribed";
	private static final String REJECT_MESSAGE = "unsubscribed";
	
	private static long POLLING_DELAY = 10 * 1000;
	private PersistenceManager pm = null;
	
	private static final Logger log = Logger.getLogger ( 
			ConnectServlet.class.getName() 
	);

	////////////////////////////
	
	@Override
	public void doGet(
		HttpServletRequest req,
		HttpServletResponse resp
	) throws IOException {
    
		doPost( req, resp );
	}
	
	////////////////////////////
	
	@SuppressWarnings("unchecked")
	@Override
	public void doPost(
		HttpServletRequest req,
		HttpServletResponse resp
	) throws IOException {
    
		//-- Parse incoming message
		XMPPService xmpp = XMPPServiceFactory.getXMPPService();
		Message msg = xmpp.parseMessage( req );
		String body = msg.getBody();
		
		//-- Determine the jid of the sender
		String jid = msg.getFromJid().getId();
		int pos = jid.lastIndexOf( '/' );
		jid = ( pos > 0 ) ? jid.substring( 0, pos ) : jid;

		log.warning( "XMPP messaging: [" + jid + "] : " + body );
		
		//-- determine if we know this person?
		pm = PMF.get().getPersistenceManager();
	    Query query = pm.newQuery( DatawareUser.class );
	    query.setFilter( "catalogJid == lastNameParam" );
	    query.declareParameters( "String lastNameParam" );

        try {
        	List< DatawareUser > results = ( List< DatawareUser > ) query.execute( jid );
            if ( !results.isEmpty() ) {
            	
            	//-- for each user either set their subscription as complete 
            	//-- (given that we know comms are possible now), or rejected
            	//-- if the user has sent an unsubscribed message. n.b. we have
            	//-- to do this via messages and not precense stanzas because
            	//-- google app engine does not support the latter.
                for ( DatawareUser user : results) {
                	if ( body.equalsIgnoreCase( REJECT_MESSAGE ) )
                		rejectSubscription( user ); 
                	else
                		acceptSubscription( user );
                }
            } else {
            	log.warning( "No user could be found corresponding to that datasphere address" );
            }
        } finally {
            query.closeAll();
        }
        
        pm.close();
	}
	
	////////////////////////////
	
	public void acceptSubscription( DatawareUser user ) 
	throws IOException {
		
		if ( ! user.isComplete() ) {
			log.warning( "subscription has been accepted for user: " + user.getUid() + ". Starting polling..." );
			try {
				user.setCatalogConnectivity( ConnectivityStatus.COMPLETE );
	
				TwitterUserStats fBUserStats = pm.getObjectById( TwitterUserStats.class, user.getUid() );
				user.setActivity( ActivityStatus.ACTIVE );
				
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(
					TaskOptions.Builder.url( "/retrieveUpdate" )
					.param( "uid", user.getUid() )
					.countdownMillis( POLLING_DELAY )
				);
				
				fBUserStats.setPostsNextPoll(
					System.currentTimeMillis() + POLLING_DELAY
				);
			}
			catch ( JDOObjectNotFoundException e ) {
				log.warning( "polling cancelled due to missing FBUserStats for " + user.getUid() );
			}
		}
		else {
			log.warning( "subscription has already been accepted. ignoring for user: " + user.getUid() );	
		}
	}
	
	////////////////////////////
	
	public void rejectSubscription( DatawareUser user ) 
	throws IOException {
		log.warning( "subscription has been rejected. Polling will end on next attempt..." );
		user.setCatalogConnectivity( ConnectivityStatus.REJECTED );
		user.setActivity( ActivityStatus.INACTIVE );
	}
}
