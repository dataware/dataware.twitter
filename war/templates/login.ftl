<#include "header.ftl">

	<div style="width:360px; margin-top:16px; margin-bottom:20px;">
		<img src="/images/speech.gif" align="left"/>
		This is a <b>free dataware service</b> connecting your Twitter account to your datasphere.<br/>
		The aim? To give you control over and insight into <i>your Twitter data</i>.
	</div>
	
	<div style="width:360px; margin-top:10px; margin-bottom:20px;">
		<img src="/images/bars.gif" align="left"/>
		<img src="/images/twitterlogo.jpg" width="63px" style="margin-right:2px"/>
		generates all sorts of data about you, but we're all losing track of just what's in there. 
		We send your <b>personal datasphere</b> the stats, so that <i>you</i> can keep tabs of what's going on. 
	</div>
	
	<div style="width:360px; margin-top:10px; margin-bottom:26px; ">
		<div style="float:left"><img src="/images/speech.gif"/></div>
		<div style="margin-left:72px; margin-bottom:5px;">
			To try this service, once you have a datasphere address, simply login below
			- and we guarantee: 
		</div>
		<div style="margin-left:72px">
				<b>none of your data</b> is stored by this service.<br/> 
				<i>Your privacy is paramount</i>.
		</div>
	</div>
	
	<div id="loginRequest">
	<a href="javascript:login();"
        	onmouseout="button_off('login'); return true;" 
        	onmouseover="button_on('login'); return true;" >
    <img src="/images/login_off.png" border="0" id="login" />
	</a>
	</div>
	
	<div id="loginResponse" style="display:none;">
		<img src="/images/login_off.png" align="left" border="0" style="margin-right:12px;" />
		<span class="medium"> Successful login! </span><br/>Redirecting - please wait... 
		<br/><br/>
	</div>
	
	<div id="loginResponseImage" style="position:absolute; bottom:22; right:-10; display:none">
		<img src="/images/loginResponse.png" style="width:350px;"/>
	</div>
	
	
<#include "footer.ftl">