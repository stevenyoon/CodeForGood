<?php

$logged_in = FALSE;
$p_id = NULL;

// start session, check session IP with client IP, if no match start a new session
session_start();
if(isset($SESSION["REMOTE_ADDR"]) && $SESSION["REMOTE_ADDR"] != $SERVER["REMOTE_ADDR"]) {
  session_destroy();
  session_start();
}

if (isset($_SESSION['pid'])) {
	$logged_in = TRUE;
	$p_id = $_SESSION['pid'];
}
