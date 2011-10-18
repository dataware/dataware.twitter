package dataware.twitter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CookieUtils {

	private CookieUtils(){}

	///////////////////////////////////
	
	public static Map<String, String> fetch( Cookie[] cookies, String name ) {
		
		if ( cookies == null ) 
			return null;
	    
		Cookie cookie = null;		    
	    for ( Cookie c : cookies ) 
	    	if ( c.getName().equals( name ) )
	    		cookie = c;
	    
	    if ( cookie == null ) 
	    	return null;
	    
		Map< String, String > results = new HashMap< String, String >(); 
	  	String[] params = cookie.getValue().split( "&" );
    	for( String p : params ) {
	   		String[] parts = p.split( "=" );
	   		results.put( parts[ 0 ], parts[ 1 ] );
    	}
	    
	    return results;
	}

	///////////////////////////////////
		
	public static void deleteAllCookies(HttpServletRequest request,
			HttpServletResponse response) {
		
		for ( Cookie c : request.getCookies() ) { 
			c.setMaxAge(0);
			c.setPath( "/" );
			c.setDomain(request.getHeader( "host" ));
			response.addCookie( c );
		}
		
	}
	  
}
