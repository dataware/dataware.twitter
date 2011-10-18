<#include "header.ftl">

	<div style="width:382px; margin-top:16px; margin-bottom:11px;">
		<div style="float:left">
			<img src="/images/bars.gif" align="left"/>
		</div>
		
		<div style="margin-left:72px;">
			An invitation has been sent to the datasphere at:
		</div>
		
		<div style="margin-left:70px; margin-bottom: 12px; color: #928d65; font-family: Georgia; font-size: 17px;">
			${ user.getCatalogJid().getEmail() }
		</div>
		
		<div>
			<i>Once the invitation is accepted (you may need to do this manually)
			your dataware will become <b>active</b>.</i>
		</div>
	</div>
		
	<div style="width:382px;margin-bottom:15px;">
		<div style="float:left;">
			<img src="/images/speech.gif" align="left"/>
		</div>
		<div style="margin-left:72px; margin-bottom:8px; padding-top:10px;">
			Don't forget: you can change your datasphere address, pause or 
			restart the service and check your stats at the <b><i>home page</i></b>...
		</div>
	</div>
	
	<div style="width:175px; height:180px;">
		<div>
			<a href='app' 
			onmouseout=\"button_off('continue'); return true;\" 
			onmouseover=\"button_on('continue'); return true;\"
			>
			<img id='continue' border='none' style='margin:10px 0px 0px -2px' src='/images/continue_off.png'/>
			</a>
		</div>
	</div>
		
	<div id="loginResponseImage" style="position:absolute; bottom:22; right:-10;">
		<img src="/images/loginResponse.png" style="width:350px;"/>
	</div>

<#include "footer.ftl">


	