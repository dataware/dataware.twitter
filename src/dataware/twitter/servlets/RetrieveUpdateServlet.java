package dataware.twitter.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

import datasphere.dataware.DSFormatException;
import datasphere.dataware.DSUpdate;
import datasphere.dataware.DatawareUser;
import dataware.twitter.TwitterUserStats;
import dataware.twitter.PMF;
import dataware.twitter.TwitterUtils;

@SuppressWarnings("serial")
public class RetrieveUpdateServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger( 
			RetrieveUpdateServlet.class.getName() 
	);
	
	/////////////////////////////////
	
	
	public void doGet(
		HttpServletRequest req, 
		HttpServletResponse resp )
	throws IOException {

		//-- obtain the uid of the user whose facebook account we are polling
		String uid = req.getParameter( TwitterUtils.UID );
		
		if ( uid == null ) {
			log.warning( "RetrieveUpdateServlet: no user supplied " );
		}
		else {
			try { 
				//-- obtain the user from the datastore, along with his stats
				PersistenceManager pm = PMF.get().getPersistenceManager();
				DatawareUser user = DatawareUser.fetch( pm, uid );
				TwitterUserStats fBUserStats = TwitterUserStats.fetch( pm, uid );
				log.warning( "RetrieveUpdateServlet: user = " + uid );
				
				//-- if the user active fetch there facebook posts...
				if ( user.isActive() ) {
					
					Twitter twitter = TwitterUtils.getTwitterInstance( user );
					long postsLast = (long) fBUserStats.getPostsLast();
					
					ResponseList< Status > userTimeline = null; 
					if ( postsLast > 0 ) {
						Paging paging = new Paging( postsLast );
						userTimeline = twitter.getUserTimeline( paging );
					}
					else { 
						userTimeline = twitter.getUserTimeline();
					}

					log.warning( "SINCE = " + postsLast );
					log.warning( "TIMELINE SIZE = " + userTimeline.size() );

					if ( userTimeline.size() > 0 ) {
						processPosts( user, fBUserStats, userTimeline );
					}
					else {
						log.warning( "RetrieveUpdateServlet: no new posts detected" );
					}
										
					//-- reschedule another polling request for some later point in time
					reschedule( uid, fBUserStats );
					
					//-- change the user's stats and store them
					fBUserStats.incrementPostPolls();
					fBUserStats.setPostsLastPolled( System.currentTimeMillis() );
					
					pm.close();
				}
				
				//-- if the user is inactive polling is not rescheduled
				else {
					log.warning( "service is inactive. no more polling will occur until this changes" );
					fBUserStats.setPostsNextPoll( 0 );
				}
			}
			
			//-- if the user cannot be found in our records there is not much we can do...
			catch ( JDOObjectNotFoundException e )  {
				log.severe( "RetrieveUpdateServlet: user or their details could not be found... " + e );
			} 
			
			catch (Exception e) {
				log.severe( "RetrieveUpdateServlet: we have hit an exception... " + e );
				e.printStackTrace();
			}
		}
	}
		

	/////////////////////////////////
	
	
	public void doPost( HttpServletRequest req, HttpServletResponse resp )
	 throws IOException {
		 doGet(req, resp);
	 }
		 
	
	/////////////////////////////////	
	

	private void processPosts( 
			DatawareUser user, 
			TwitterUserStats fBUserStats, 
			ResponseList< Status > posts ) {
		
		long latestPostId = fBUserStats.getPostsLast(); 
		String msg = "";
		
		for ( Status p : posts ) {
			
			//-- and is this the latest post we have seen?
			if ( p.getId() > latestPostId ) 
				latestPostId = p.getId();

			fBUserStats.incrementPosts();

			try {
				msg = "New Twitter update: \"" + p.getText() + "\""; 	
				
				//-- create the update message to the datasphere
				DSUpdate d = new DSUpdate( "ds:twitter", "ds:twitter:post", "create" )
					.setCtime( p.getCreatedAt().getTime() )
					.setFtime( System.currentTimeMillis() )
					.setType( "ds:twitter:tweet" )
					.setDescription( msg )
					.addTag( "ds:communication" )
					.addMetadata( "message",  p.getText() )
					.addMetadata( "user", p.getUser().getScreenName() );
				
				if ( p.getHashtagEntities() != null ) 
					d.addMetadata( "hashtagEntities", p.getHashtagEntities().toString() );

				//-- and finally notify the datasphere that an event has occurred
				send( user.getCatalogJid().getEmail(), d.toString() );
				
			} catch ( DSFormatException e ) {
				log.warning( "RetrieveUpdateServlet: encountered a format exception: " + e.toString() );
			}				
		}
			
		log.warning( "setting last post time as: " + latestPostId );
		fBUserStats.setPostsLast( latestPostId );
		log.warning( "set last post time as: " + fBUserStats.getPostsLast() );
	}


	/////////////////////////////////
	
	
	private void send( String recipient, String msgBody ) {
    
		log.warning( "SEND: " + msgBody );
		if ( recipient == null ) {
			log.warning( "XMPP ERROR: no recipient specified: " + msgBody );
		}
		else {
			
			JID jid = new JID( recipient + "/datasphere" );
			Message msg = new MessageBuilder()
	            .withRecipientJids(jid)
	            .withBody(msgBody)
	            .build();
			
	        boolean messageSent = false;
	        XMPPService xmpp = XMPPServiceFactory.getXMPPService();
	        SendResponse status = xmpp.sendMessage( msg );
	        messageSent = ( status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS );      
	        
	        if (!messageSent) 
	        	log.warning( "XMPP message not sent [" + recipient + "]: " + msgBody );
	        else 
	        	log.warning( "XMPP message sent [" + recipient + "]: " + msgBody );
		}
	}

	/////////////////////////////////
	
	
	private void reschedule( String uid, TwitterUserStats fBUserStats ) {
		
		long nextPoll = System.currentTimeMillis() + TwitterUtils.POLLING_INTERVAL;
		fBUserStats.setPostsNextPoll( nextPoll );
		
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(
			TaskOptions.Builder.url( "/retrieveUpdate" )
			.param( TwitterUtils.UID, uid )
			.countdownMillis( TwitterUtils.POLLING_INTERVAL )
		);
	}
}
