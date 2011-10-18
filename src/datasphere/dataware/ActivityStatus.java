package datasphere.dataware;

import java.util.EnumSet;

public enum ActivityStatus {

	ACTIVE, INACTIVE;
	
	public static ActivityStatus match( String s ) {
		for( ActivityStatus a : EnumSet.allOf( ActivityStatus.class ) ) 
			if ( s.equalsIgnoreCase( a.toString() ) )
				return a;
		return null;
	}
}
