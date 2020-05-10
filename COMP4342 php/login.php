<?php
    include("config.php");

    $con = mysqli_connect(DB_SERVER,DB_USERNAME,DB_PASSWORD);

    $sql = "CREATE DATABASE comp4342_db";
    $result = mysqli_query($con, $sql);

    mysqli_close($con);

    $db = mysqli_connect(DB_SERVER,DB_USERNAME,DB_PASSWORD, "comp4342_db");


    $sql = "CREATE TABLE IF NOT EXISTS User (UID INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, UserName VARCHAR(10) NOT NULL, Password VARCHAR(64) NOT NULL, Email VARCHAR(50) NOT NULL)";
    $result = mysqli_query($db, $sql);

    $sql = "CREATE TABLE IF NOT EXISTS Songlist (ID INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, UserName VARCHAR(10) NOT NULL, PlaylistName VARCHAR(50), SongName VARCHAR(50) NOT NULL)";
    $result = mysqli_query($db, $sql);

    $sql = "CREATE TABLE IF NOT EXISTS Playlist (PID INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, UserName VARCHAR(10) NOT NULL, PlaylistName VARCHAR(50) NOT NULL)";
    $result = mysqli_query($db, $sql);

    $sql = "CREATE TABLE IF NOT EXISTS Songs (SID INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, SongName VARCHAR(50) NOT NULL, mp3file MEDIUMBLOB NOT NULL)";
    $result = mysqli_query($db, $sql);

    // Authentication
    $UserName = $_POST["username"];
    $Password = $_POST["password"];
    $sql = "SELECT * FROM User WHERE UserName = '$UserName' AND Password = '$Password'";
    $result = mysqli_query($db, $sql);
    $row = mysqli_fetch_array($result);
    $count = mysqli_num_rows($result);
    if ($count == 1) $message = "login successfully";
    else $message = "Your user name or password is invalid";

    mysqli_close($db);

    // the response to android mobile
    echo($message);
?>
