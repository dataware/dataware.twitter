package datasphere.dataware;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Email;


@PersistenceCapable
public class DatawareUser {
	
    @PrimaryKey
    @Persistent
    //-- the id of the dataware user (specific to the underlying service)
    private String uid;
    
    @Persistent
    //-- time that the user was registered by the dataware (never changes)
    private long ctime;
    
    @Persistent
    //-- whether the dataware is currently running or not for this user
    private String activity;
    
	@Persistent
	//-- the authorization token for the underlying service
    private String serviceAccessToken;
    
	@Persistent
	//-- the authorization secret for the underlying service
    private String serviceSecret;
	
    @Persistent
    //-- state of the connection between the dataware and its underlying service
    private String serviceConnectivity;

    @Persistent
    //-- the last time an adjustment was made to the services status 
    private long serviceMtime;
    
    @Persistent
    //-- the datasphere address of the user
    private Email catalogJid;
    
    @Persistent
    //-- state of the connection between the dataware and its underlying service
    private String catalogConnectivity;

    @Persistent
    //-- the last time an adjustment was made to the services status 
    private long catalogMtime;

    //////////////////////////////////////////
    
    /**
     * constructor requires a user and a valid authorization token 
     */
    public DatawareUser( String uid, String serviceAuthToken ) {
    	
        setUid( uid );
        setCtime( System.currentTimeMillis() );
        setActivity( ActivityStatus.INACTIVE );
        setServiceAccessToken( serviceAuthToken );
        setServiceConnectivity( ConnectivityStatus.NONE );
        setServiceMtime( System.currentTimeMillis() );
        setCatalogConnectivity( ConnectivityStatus.NONE );
        setCatalogMtime( System.currentTimeMillis() );
    }
    
    //////////////////////////////////////////
    
	public boolean jidIsNew( String newJid ) {
		
		if ( newJid == null ) 
			return false;
		else if ( this.catalogJid == null ) 
			return true;
		else 
			return 
				( catalogJid.getEmail().equalsIgnoreCase( newJid ) ) 
				? false : true;
	}

    //////////////////////////////////////////
	
	public long getCtime() {
		return ctime;
	}

    //////////////////////////////////////////

	public void setCtime( long ctime ) {
		this.ctime = ctime;
	}
	
    //////////////////////////////////////////

	public String getActivity() {
		return this.activity;
	}

    //////////////////////////////////////////
	
	public void setActivity( ActivityStatus activity ) {
		this.activity = activity.toString() ;
	}

    //////////////////////////////////////////

	public boolean isActive() {
		return ActivityStatus.match( activity ) == ActivityStatus.ACTIVE;
	}
	
    //////////////////////////////////////////

	public boolean isComplete() {
		return ConnectivityStatus.match( catalogConnectivity ) == ConnectivityStatus.COMPLETE;
	}
	
    //////////////////////////////////////////
	
	public String getServiceAccessToken() {
		return serviceAccessToken;
	}


    //////////////////////////////////////////
	
	public void setServiceSecret( String serviceSecret ) {
		this.serviceSecret = serviceSecret;
	}

    //////////////////////////////////////////
	
	public String getServiceSecret() {
		return serviceSecret;
	}
	
    //////////////////////////////////////////
	
	public void setServiceAccessToken( String serviceAuthToken ) {
		this.serviceAccessToken = serviceAuthToken;
	}

    //////////////////////////////////////////
	
	public ConnectivityStatus getServiceConnectivity() {
		return ConnectivityStatus.match( serviceConnectivity );
	}

    //////////////////////////////////////////
	
	public void setServiceConnectivity( ConnectivityStatus status ) {
		this.serviceConnectivity = status.toString() ;
	}

    //////////////////////////////////////////

	public long getServiceMtime() {
		return serviceMtime;
	}

    //////////////////////////////////////////
	
	public void setServiceMtime( long serviceMtime ) {
		this.serviceMtime = serviceMtime;
	}

    //////////////////////////////////////////
	
	public Email getCatalogJid() {
		return catalogJid;
	}

    //////////////////////////////////////////
;	
	public void setCatalogJid( String catalogJid ) 
	throws DSFormatException {
		if ( JIDValidator.validate( catalogJid ) ) {
			this.catalogJid = new Email( catalogJid ) ;	
		} else {
			throw new DSFormatException( "JID supplied is in an incorrect format." );
		}
	}

    //////////////////////////////////////////
	
	public ConnectivityStatus getCatalogConnectivity() {
		return ConnectivityStatus.match( catalogConnectivity );
	}

    //////////////////////////////////////////
	
	public void setCatalogConnectivity( ConnectivityStatus status ) {
		this.catalogConnectivity = status.toString();
	}

    //////////////////////////////////////////
	
	public long getCatalogMtime() {
		return catalogMtime;
	}

    //////////////////////////////////////////
	
	public void setCatalogMtime( long catalogMtime ) {
		this.catalogMtime = catalogMtime;
	}

    //////////////////////////////////////////
	
	private void setUid( String uid ) {
		this.uid = uid;
	}
	
    //////////////////////////////////////////
	
	public String getUid() {
		return uid;
	}
	
    //////////////////////////////////////////
	
	public static DatawareUser fetch( PersistenceManager pm, String targetUid ) {
		try {
			return pm.getObjectById( DatawareUser.class, targetUid );
		} catch ( JDOObjectNotFoundException e ) {
			return null;
		}
	}
	
    //////////////////////////////////////////
	
	public boolean hasNewJID( String jid ) {
		
		if ( jid == null ) 
			return false;
		else if ( this.catalogJid == null ) 
			return true;
		else 
			return 
				( this.catalogJid.getEmail().equalsIgnoreCase( jid ) ) 
				? false : true;
	}

}
