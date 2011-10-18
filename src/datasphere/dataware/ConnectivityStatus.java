package datasphere.dataware;

import java.util.EnumSet;

public enum ConnectivityStatus {
	NONE, PENDING, COMPLETE, REJECTED;
	
	public static ConnectivityStatus match( String action ) {
		for( ConnectivityStatus a : EnumSet.allOf( ConnectivityStatus.class ) ) 
			if ( action.equalsIgnoreCase( a.toString() ) )
				return a;
		return null;
	}
}
