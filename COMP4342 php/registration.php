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

    $UserName = $Password = $Email = "";
    $UserNameError = $EmailError = "";

    // Validation
    if (empty($_POST["username"])) {
        $UserNameError = "User name is required";
    }
    else {
        $UserName = $_POST["username"];
        $result = mysqli_query($db, "SELECT * FROM User WHERE UserName = '$UserName'");
        $count = mysqli_num_rows($result);
        if (strlen($UserName) > 10) {
            $UserNameError = "User name length cannot exceed 10";
        }
        else if ($count == 1){
            $UserNameError = "This user name already exists";
        }
    }

    $Password = $_POST["password"];

    if (empty($_POST["email"])) {
        $EmailError = "Email is required";
    }
    else {
        $Email = $_POST["email"];
        $result = mysqli_query($db, "SELECT * FROM User WHERE Email = '$Email'");
        $count = mysqli_num_rows($result);
        if (strlen($Email) > 50){
            $EmailError = "Email cannot be exceed 50 characters";
        }
        else if (!filter_var($Email, FILTER_VALIDATE_EMAIL)) {
            $EmailError = "Invalid format and please re-enter valid email";
        }
        else if ($count == 1){
            $UserNameError = "This email already exists";
        }
    }

    // Insert records to database if the data is validated
    if ($UserNameError == "" && $EmailError == "") {

        // Insert a record to table Playlist as every user have a play list called 'Favourites' in the beginning
        $sql = "INSERT INTO Playlist (UserName, PlaylistName) VALUES ('$UserName', 'Favourites')";
        $result = mysqli_query($db, $sql);

        // Insert a record to table User
        $sql = "INSERT INTO User (UserName, Password, Email) VALUES ('$UserName', '$Password', '$Email')";
        if (mysqli_query($db, $sql) or die(mysqli_error($db))) {
            $message = "A new account created successfully";
        }
        else {
            $message = "Fail to create a new account";
        }
    }
    else {
        if ($UserNameError != "") $message = $UserNameError;
        else if ($EmailError != "") $message = $EmailError;
    }
    
    mysqli_close($db);

    // the response to android mobile
    echo($message);
?>

