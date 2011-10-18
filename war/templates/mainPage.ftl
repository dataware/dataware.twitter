<#include "header.ftl">

	<div style="width:382px; margin-bottom:18px; margin-top:8px;">
		<div style="float:left">
		<img class="rounded" src="${ twitterUserDetails.getProfileImageURL() }"/>
		</div>
		<div style="margin-left:72px; margin-bottom:8px;">
			<b>${ twitterUserDetails.getScreenName() }</b>, welcome to your twitter dataware! See your stats
			control your datasphere connection and organize your channels...
		</div>
	</div>
	 
	<div style="width:382px; margin-top:16px; margin-bottom:18px;">
		<div style="float:left">
			<img src="/images/speech.gif"/>
		</div>
		<div style="margin-left:72px; margin-bottom:2px;">
		 	Currently the personal datasphere at:
		</div>
		<div style="margin-left:70px; margin-bottom: 8px; font-weight:bold; ">
			${ user.getCatalogJid().getEmail() }
		</div>
		
		<#if !user.isComplete()>
			<div style="margin-left:72px; margin-bottom:8px;">
				<i>hasn't accepted our invitation - until it does
				this service will be <span style="color:#cc5555; font-weight:bold">inactive</span>. 
				Click retry to resend the invite.</i>
			</div>
			<div style="margin-left:72px; margin-bottom:2px;">
				<a href="javascript:window.location='app?action=retryJid';" 
					onmouseout="button_off('retry'); return true;" 
			    	onmouseover="button_on('retry'); return true;" >
			      	<img src="/images/retry_off.png" style="border: none;" id="retry" />
			    </a>
		<#elseif !user.isActive() >
			<div style="margin-left:72px; margin-bottom:8px;">
				<i>is <span style="color:#883388; font-weight:bold">successfully linked</span> 
				, but the service is <span style="color:#cc5555; font-weight:bold">inactive</span>. 
				You may restart it at any time below:</i>
			</div>
			<div style="margin-left:72px; margin-bottom:2px;">
				<a href="javascript:window.location='app?action=restart';" 
					onmouseout="button_off('restart'); return true;" 
			    	onmouseover="button_on('restart'); return true;" >
			      	<img src="/images/restart_off.png" style="border: none;" id="restart" />
			    </a>
		<#else>
			<div style="margin-left:72px; margin-bottom:8px;">
				<i>is <span style="color:#883388; font-weight:bold">successfully linked</span> 
				, and the service is <span style="color:#883388; font-weight:bold">active</span>. 
				You may pause the service at any time below:</i>
			</div>
			<div style="margin-left:72px; margin-bottom:2px;">
				<a href="javascript:window.location='app?action=pause';" 
					onmouseout="button_off('pause'); return true;" 
			    	onmouseover="button_on('pause'); return true;" >
		      	<img src="/images/pause_off.png" style="border: none;" id="pause" />
		    </a>
		</#if>
					

			<a href="javascript:window.location='app?action=connect';" 
				onmouseout="button_off('change_datasphere'); return true;" 
		    	onmouseover="button_on('change_datasphere'); return true;" >
		      	<img src="/images/change_datasphere_off.png" style="border: none;" id="change_datasphere" />
		    </a>
		</div>
	</div>
	
	<div style="width:382px; margin-top:16px; padding-bottom:11px;">
		<div style="float:left">
			<img src="/images/bars.png"/>
		</div>
		<div style="margin-left:72px; margin-bottom:14px;">
		 	We don't store a single piece of your data. However, since 
		 	<b><i>${ joined }</i></b>, service performance statistics have been logged...
		</div>
		<div style="margin-left:70px; margin-bottom: 7px;">
			<div style="margin-bottom:10px; font-size:11px;">
				<div style="float:left"><img src="/images/icon_blue_post.png" width="45px" /></div>
				<div style="margin-left:57px">&#187; total posts: &nbsp;<b>${ fBUserStats.getPostsTotal() }</b></div>
				<div style="margin-left:57px">&#187; last post: &nbsp;<b>${ postsLast }</b></div>
				<div style="margin-left:57px">&#187; last checked: &nbsp;<b>${ postsLastPolled }</b></div>
			</div>
		</div>
	</div>
	
<#include "footer.ftl">
	