<?php
    include("config.php");
    define("DB_DBNAME", "comp4342_db");

    $db = mysqli_connect(DB_SERVER,DB_USERNAME,DB_PASSWORD,DB_DBNAME);


    $UserName = $PlaylistName = $OldPlaylistName = $Mode = $message= "";
    $PlaylistNameError = "";

    $UserName = $_POST["username"];
    $OldPlaylistName = $_POST["oldPlaylistname"];
    $Mode = $_POST["mode"];

    // Validation
    if (strcmp($Mode, "Add") == 0 || strcmp($Mode, "Rename") == 0) {
        if (empty($_POST["playlistname"])) {
            $PlaylistNameError = "Playlist name is required";
        }
        else {
            $PlaylistName = $_POST["playlistname"];
            $result = mysqli_query($db, "SELECT * FROM Playlist WHERE UserName = '$UserName' AND PlaylistName = '$PlaylistName'");
            $count = mysqli_num_rows($result);
            if (strlen($PlaylistName) > 50){
                $PlaylistNameError = "Playlist name cannot be exceed 50 characters";
            }
            else if ($count == 1){
                $PlaylistNameError = "This playlist name already exists";
            }
        }
    }

    if ($PlaylistNameError == "") {
        // Add a play list
        if (strcmp($Mode, "Add") == 0) {
            $sql = "INSERT INTO Playlist (UserName, PlaylistName) VALUES ('$UserName', '$PlaylistName')";
            if (mysqli_query($db, $sql) or die(mysqli_error($db))) {
                $message = "A new playlist is created successfully";
            }
            else {
                $message = "Fail to create a new playlist";
            }
        }

        // Rename a play list
        else if (strcmp($Mode, "Rename") == 0) {
            $result = mysqli_query($db, "UPDATE Songlist SET PlaylistName = '$PlaylistName' WHERE UserName = '$UserName' AND PlaylistName = '$OldPlaylistName'");
            $sql = "UPDATE Playlist SET PlaylistName = '$PlaylistName' WHERE UserName = '$UserName' AND PlaylistName = '$OldPlaylistName'" ;
            if (mysqli_query($db, $sql) or die(mysqli_error($db))) {
                $message = "The playlist is renamed successfully";
            }
            else {
                $message = "Fail to rename the playlist";
            }
        }

        // Remove a play list
        else if (strcmp($Mode, "Remove") == 0) {
            $result = mysqli_query($db, "DELETE FROM Songlist WHERE UserName = '$UserName' AND PlaylistName = '$OldPlaylistName'");
            $sql = "DELETE FROM Playlist WHERE UserName = '$UserName' AND PlaylistName = '$OldPlaylistName'";
            if (mysqli_query($db, $sql) or die(mysqli_error($db))) {
                $message = "The playlist is removed successfully";
            }
            else {
                $message = "Fail to remove the playlist";
            }
        }

        // Retrieve play list names for displaying play lists in the list of play lists
        else if (strcmp($Mode, "Show") == 0) {
            $sql = "SELECT PlaylistName FROM Playlist WHERE UserName = '$UserName'";
            $result = mysqli_query($db, $sql);
            while ($row = mysqli_fetch_array($result)) {
                $message .= $row['PlaylistName'] . "\n";
            }
        }
    }
    else {
        $message = $PlaylistNameError;
    }
    
    mysqli_close($db);

    // the response to android mobile
    echo($message);
?>

