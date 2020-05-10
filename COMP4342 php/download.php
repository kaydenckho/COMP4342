<?php
    include("config.php");
    define("DB_DBNAME", "comp4342_db");

    $db = mysqli_connect(DB_SERVER,DB_USERNAME,DB_PASSWORD,DB_DBNAME);

    $SongName = $message = $sql = "";

    $SongName = $_POST["SongName"];
    $UserName = $_POST["username"];

    $result = mysqli_query($db, "SELECT * FROM Songlist WHERE UserName = '$UserName' AND SongName = '$SongName'");
    $count = mysqli_num_rows($result);
    if ($count >= 1) {
        $message = $SongName . " is already downloaded";
    }
    else {
      $sql = "INSERT INTO Songlist (UserName, SongName) VALUES ('$UserName', '$SongName')";
      $result = mysqli_query($db, $sql);
      // Retrieve song for download
      $sql = "SELECT mp3file FROM Songs WHERE SongName = '$SongName'";
      $result = mysqli_query($db, $sql);
      $count = mysqli_num_rows($result);
      if ($count == 1) {
        $row = mysqli_fetch_array($result);
        $message = $row['mp3file'];
      }
    }

    mysqli_close($db);

    // the response to android mobile
    echo($message);

