<#include "header.ftl">

	<div style="width:390px; margin-top:16px; margin-bottom:11px;">
		<div style="float:left">
			<img src="/images/speech.gif" align="left"/>
		</div>
		<div style="margin-left:72px; margin-bottom:8px;">
			<i>Dataware works by notifying your <b>datasphere</b> when
			your facebook data is updated. </i>
		</div>
		
		<#if cause == "missing">
		<div style="margin-left:70px; color: #928d65; font-family: Georgia; font-size: 17px;">
			Please supply a datasphere address:
		</div>
		<#elseif cause == "invalid">
		<div style="margin-left:70px; color: #cc5555; font-family: Georgia; font-size: 17px;">
			Invalid address - please try again:
		</div>
		</#if>
	</div>
	
	<div style="height:250px; ">
		<form name="getJID" action="app" method="get">
 			<div id="inputbox">
 				<input type="text" size="32" maxlength="128" name="jid" value="<#if jid??>${ user.getCatalogJid() }</#if>" />
 			</div>
 			<div>
				<a href="javascript:document.forms['getJID'].submit();" 
        			onmouseout="button_off('connect'); return true;" 
	    			onmouseover="button_on('connect'); return true;" >
    				<img src="/images/connect_off.png" id="connect" style="margin-left:4px; margin-top:-2px; margin-bottom:5px" />
    			</a>
			</div>
			
			<#if user.isComplete() >
			<div>
			<a href="app" 
        		onmouseout="button_off('cancel'); return true;" 
	    		onmouseover="button_on('cancel'); return true;" >
    			<img src="/images/cancel_off.png" id="cancel" style="margin-left:4px; margin-top:-2px" />
    		</a>
			</div>    		
			</#if>
			
    		<input type="hidden" value="newJid" name="action"/>
		</form> 
	</div>
			
	<div id="loginResponseImage" style="position:absolute; bottom:22; right:-10;">
		<img src="/images/loginResponse.png" style="width:350px;"/>
	</div>

<#include "footer.ftl">


	