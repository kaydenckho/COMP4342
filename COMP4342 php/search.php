<?php
    include("config.php");
    define("DB_DBNAME", "comp4342_db");

    $db = mysqli_connect(DB_SERVER,DB_USERNAME,DB_PASSWORD,DB_DBNAME);

    $query = $message = $sql = "";

    $query = strtolower($_POST["query"]);

    // Retrieve song names for displaying search results
    $sql = "SELECT SongName FROM Songs WHERE lower(SongName) = '$query'";
    $result = mysqli_query($db, $sql);
    $count = mysqli_num_rows($result);
    if ($count == 1) {
      $row = mysqli_fetch_array($result);
      $message = $row['SongName'];
    }

    mysqli_close($db);

    // the response to android mobile
    echo($message);
?>
