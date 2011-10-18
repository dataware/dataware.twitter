
	   	</div>
	   	<div id="bottom"></div>
    </div>
	<div id='twitter-root'></div>
	<script src='http://connect.facebook.net/en_US/all.js'></script>
	<script src="http://zuzara.org/pub/oauth_popup/jquery.oauthpopup.js"></script>
	<script>
		    
	<#if user??>
    	$( "#top" ).append( "<a href=\"http://dataware-twitter.appspot.com/app?action=logout\" >logout</a>" );		
	<#else>
    	function login() {
	        $.oauthpopup({
	            path: 'http://dataware-twitter.appspot.com/authorize',
	            callback: function(){
	            	$( "#loginRequest" ).hide();
    				$( "#loginResponse" ).show();
    				window.location = "app?action=login";
	            }
	        });
	    }
	</#if>	
	    
	</script>
  </body>
</html>