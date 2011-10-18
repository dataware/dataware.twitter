<html>
	<head>
       	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
    	<title>MyDataSphere</title>
		<link href="main.css" rel="stylesheet" type="text/css">
    	<script type="text/javascript" src="jquery-1.4.2.js"></script>   
    	<script type="text/javascript" src="roundCorners.js"></script>
		<script>
			
			//-- preload required images
			login_on = new Image();
			login_on.src = "/images/login_on.png";
			login_off = new Image();
			login_off.src = "/images/login_off.png";
		
			change_datasphere_on = new Image();
			change_datasphere_on.src = "/images/change_datasphere_on.png";
			change_datasphere_off = new Image();
			change_datasphere_off.src = "/images/change_datasphere_off.png";
			
			pause_on = new Image();
			pause_on.src = "/images/pause_on.png";
			pause_off = new Image();
			pause_off.src = "/images/pause_off.png";
			
			restart_on = new Image();
			restart_on.src = "/images/restart_on.png";
			restart_off = new Image();
			restart_off.src = "/images/restart_off.png";
			
			connect_on = new Image();
			connect_on.src = "/images/connect_on.png";
			connect_off = new Image();
			connect_off.src = "/images/connect_off.png";
			
			cancel_on = new Image();
			cancel_on.src = "/images/cancel_on.png";
			cancel_off = new Image();
			cancel_off.src = "/images/cancel_off.png";
					
			continue_on = new Image();
			continue_on.src = "/images/continue_on.png";
			continue_off = new Image();
			continue_off.src = "/images/continue_off.png";
			
			retry_on = new Image();
			retry_on.src = "/images/retry_on.png";
			retry_off = new Image();
			retry_off.src = "/images/retry_off.png";
				
			function button_on( imgId ) {
				butOn = "/images/" + imgId + "_on.png";
				document.getElementById( imgId ).src = butOn;
			}
	
			function button_off( imgId ) {
				butOff = "/images/" + imgId + "_off.png";
				document.getElementById( imgId ).src = butOff;
			}
		</script>
	</head>
	
	<body>
    <div id="bgbox" style="position:relative;">
    	<div id="top"></div>
		<div id="content">
