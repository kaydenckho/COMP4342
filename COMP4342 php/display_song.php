<?php
    define("DB_SERVER", "localhost");
    define("DB_USERNAME", "root");
    define("DB_PASSWORD", "");
    define("DB_DBNAME", "comp4342_db");

    $db = mysqli_connect(DB_SERVER,DB_USERNAME,DB_PASSWORD,DB_DBNAME);

    $UserName = $Type = $PlaylistName = $SongName = $Mode = $message = $sql = "";
    
    $UserName = $_POST["username"];
    $Type = $_POST["type"];
    $PlaylistName = $_POST["playlistname"];
    $SongName = $_POST["songname"];
    $Mode = $_POST["mode"];

    // Retrieve song names for displaying songs in a play list
    if (strcmp($Mode, "Show") == 0) {
        if (strcmp($Type, "all_song") == 0) $sql = "SELECT DISTINCT SongName FROM Songlist WHERE UserName = '$UserName'";
        else $sql = "SELECT DISTINCT SongName FROM Songlist WHERE UserName = '$UserName' AND PlaylistName = '$PlaylistName'";
        $result = mysqli_query($db, $sql);
        while ($row = mysqli_fetch_array($result)) {
            $message .= $row['SongName'] . "\n";
        }
    }

    // Add a song to a play list
    else if (strcmp($Mode, "Add") == 0) {

        // Validation for whether the specified song is already in the target playlist
        $result = mysqli_query($db, "SELECT * FROM Songlist WHERE UserName = '$UserName' AND PlaylistName = '$PlaylistName' AND SongName = '$SongName'");
        $count = mysqli_num_rows($result);
        if ($count == 1) {
            $message = $SongName . " already exists in " . $PlaylistName;
        }
        else {
            $sql = "INSERT INTO Songlist (UserName, PlaylistName, SongName) VALUES ('$UserName', '$PlaylistName', '$SongName')";
            if (mysqli_query($db, $sql) or die(mysqli_error($db))) {
                $message = $SongName . " is added to " . $PlaylistName . " successfully";
            }
            else {
                $message = "Fail to add " . $SongName . " to " . $PlaylistName;
            }
        }
    }

    // Remove a song from a play list
    else if (strcmp($Mode, "Remove") == 0) {
        $sql = "DELETE FROM Songlist WHERE UserName = '$UserName' AND PlaylistName = '$PlaylistName' AND SongName = '$SongName'";
        if (mysqli_query($db, $sql) or die(mysqli_error($db))) {
            $message = $SongName . " is removed from " . $PlaylistName . " successfully";
        }
        else {
            $message = "Fail to remove " . $SongName . " from " . $PlaylistName;
        }
    }

    

    
    mysqli_close($db);

    // the response to android mobile
    echo($message);
?>

