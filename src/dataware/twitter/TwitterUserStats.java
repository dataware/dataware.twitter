package dataware.twitter;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class TwitterUserStats {
	
    @PrimaryKey
    @Persistent
    private String uid;
       
    @Persistent
    private boolean validPermissions;
    
    @Persistent
    private long postsLast;
 
    @Persistent
    private long postsTotal;

    @Persistent
    private long postsLastPolled;

    @Persistent
    private long postsNextPoll;
    
    @Persistent
    private long postsTotalPolls;
    
	//////////////////////////////
 
    public TwitterUserStats( String uid ) {
        this.uid = uid;
        this.validPermissions = true;
    }
	
	//////////////////////////////
    
    public String getUid() { 
    	return uid; 
    }
	
	//////////////////////////////
    
	public boolean hasValidPermissions() {
		return validPermissions;
	}
	
	//////////////////////////////

	public void setValidPermissions( boolean b ) {
		this.validPermissions = b;
	}

	//////////////////////////////
	
	public void setPostsLastPolled( long time ) { 
		this.postsLastPolled = time;
	}	
	
	//////////////////////////////
	
	public void setPostsNextPoll( long time ) { 
		this.postsNextPoll = time; 
	}	
	
	//////////////////////////////
	
	public void setPostsLast( long time ) { 
		this.postsLast = time;	
	}
	
	//////////////////////////////
	
	public long getPostsLastPolled() { 
		return this.postsLastPolled; 
	}
	
	//////////////////////////////
	
	public long getPostsNextPoll
	() { 
		return this.postsNextPoll; 
	}
	
	//////////////////////////////
	
	public long getPostsLast() { 
		return this.postsLast; 
	}
	
	//////////////////////////////
	
	public long getPostsTotalPolls() {
		return this.postsTotalPolls; 
	}
	
	//////////////////////////////
	
	public long getPostsTotal()	{ 
		return this.postsTotal; 
	}	
	
	//////////////////////////////
	
	public void incrementPosts() { 
		this.postsTotal++;   
	}
	
	//////////////////////////////
	
	public void incrementPostPolls() { 
		this.postsTotalPolls++; 
	}
	
	//////////////////////////////
	
	public void resetStats() {
		this.postsLast = 0;
		this.postsTotal = 0;
		this.postsLastPolled = 0;
		this.postsTotalPolls = 0;
	}
	
	//////////////////////////////

	public static TwitterUserStats fetch( PersistenceManager pm, String targetUid ) {
	    return pm.getObjectById( TwitterUserStats.class, targetUid );
	}

}
