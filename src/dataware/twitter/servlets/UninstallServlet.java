package dataware.twitter.servlets;


import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import datasphere.dataware.DatawareUser;
import dataware.twitter.TwitterUserStats;
import dataware.twitter.PMF;


@SuppressWarnings("serial")
public class UninstallServlet extends HttpServlet {
	
	private static final Logger log = Logger.getLogger( 
			UninstallServlet.class.getName() 
	);

	/**
	 *
	 */
	public void doPost(	HttpServletRequest req, HttpServletResponse resp )
	throws IOException {

		String uid = req.getParameter( "fb_sig_user" );
		if ( uid != null ) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			
			//-- check that we have received an uninstall user id
			log.warning( "UNINSTALL: attempt by user " + uid + ".");
	    
			try {
				DatawareUser user = pm.getObjectById( DatawareUser.class,  uid );
				pm.deletePersistent( user );
				log.warning( "UNINSTALL: User Object deleted from datastore." );
			}
			catch( JDOObjectNotFoundException j ) {
				log.warning( "UNINSTALL: user " + uid + " object could not be found so ignoring." );
			}
			
			try {
				TwitterUserStats fBUserStats = pm.getObjectById( TwitterUserStats.class,  uid );
				pm.deletePersistent( fBUserStats );
				log.warning( "UNINSTALL: User Stats deleted from datastore." );
			}
			catch( JDOObjectNotFoundException j ) {
				log.warning( "UNINSTALL: user " + uid + " stats could not be found so ignoring." );
			}
	
			pm.close();

		} else {
			log.warning( "UNINSTALL: no uid has been supplied, so nothing can be done..." );
		}
		
	}

}
