<?php

require_once('functions.php');
require_once('session.php');
require_once('mysql.php');

if (isset($_POST['pid']) && isset($_POST['password'])) {
	//check if entry exists in database
	$pid = $_POST['pid'];
	$password = md5($_POST['password']);
	if ($stmt = $mysqli->prepare("select p_id, fname from person where pid = ? and password = ?")) {
		$stmt->bind_param("ss", $pid, $password);
		$stmt->execute();
		$stmt->bind_result($p_id, $fname);
	    //if there is a match set session variables and send user to homepage
	    if ($stmt->fetch()) {
			$_SESSION["pid"] = $pid;
			$_SESSION["fname"] = $fname;
			$_SESSION["REMOTE_ADDR"] = $_SERVER["REMOTE_ADDR"]; //store clients IP address to help prevent session hijack
			header('Location: index.php');
			die();
	    }
		//if no match then tell them to try again
		else {
			sleep(1); //pause a bit to help prevent brute force attacks
			echo "Your username or password is incorrect, click <a href=\"login.php\">here</a> to try again.";
			die();
		}
		$stmt->close();
		$mysqli->close();
	}
}
