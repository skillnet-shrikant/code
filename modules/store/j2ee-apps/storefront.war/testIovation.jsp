<html>
	<head>
		<script>
			console.log('in iovation test');

			// basic configuration
			var io_install_stm = false; // do not install Active X
			var io_exclude_stm = 12; // do not run Active X
			var io_install_flash = false; // do not install Flash
			var io_enable_rip = true; // collect Real IP information
			var sent = false;
			var isDeviceIdSet=false;

			function send_bb(bb) { // function to process the blackbox
				if (sent){
					document.getElementById('captureRedShieldDeviceId').value = bb;
					console.log('[test] ReDDeviceId: ', bb);
					console.log('[test] Capture deviced id: ', document.getElementById('captureRedShieldDeviceId').value);
					return;
				}
				sent = true;
			}
			// use callback to process blackbox as soon as it is done
			var io_bb_callback = function (bb, isComplete){
				// when done call the send function
				if (isComplete) {
					send_bb(bb);
				}
			};

			// set up timer to give us a maximum wait time before giving up on waiting
			// for all of the data to be collected. At that point, just send what is
			// available
			setTimeout(function() {
				try {
					var bb_info = ioGetBlackbox();
					var keys = Object.keys(bb_info);
					console.log('keys: ', keys);
					console.log('bb_info.finished ', bb_info.finished);
					send_bb(bb_info.blackbox);
					return;
				} catch (e) {
					send_bb(''); // if we are done but got an error,send an empty blackbox
				}
				send_bb(''); // if we are done but got an error,send an empty blackbox
			}, 5000);	// set a maximum wait time of 5 seconds
		</script>
		<script type="text/javascript" src="https://mpsnare.iesnare.com/snare.js"></script>
	</head>
	<body>
		<input value="" type="hidden" id="captureRedShieldDeviceId" name="captureRedShieldDeviceId"/>
	</body>
</html>
