<?php
 
/*
 * Following code will list all chats that this user
 * in the range of them.
 * Need post user's location in latitude and longitude
 * and tag.
 */

 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 

// function that calculate the distance between two lat long points'
// can return distance in different unit: M for miles, K for kilometers
function distance($lat1, $lon1, $lat2, $lon2, $unit) {
  $theta = $lon1 - $lon2;
  $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
  $dist = acos($dist);
  $dist = rad2deg($dist);
  $miles = $dist * 60 * 1.1515;
  $unit = strtoupper($unit);
 
  if ($unit == "K") {
    return ($miles * 1.609344);
  } else {
        return $miles;
      }
}
//echo distance(32.9697, -96.80322, 29.46786, -98.53506, "M") . " Miles<br>";
//echo distance(32.9697, -96.80322, 29.46786, -98.53506, "K") . " Kilometers<br>";



// check for post data
if (isset($_GET["latitude"]) && isset($_GET["longitude"]) && isset($_GET["tags"])) {
    $latitude = $_GET['latitude'];
    $longitude = $_GET['longitude'];
    $tags = $_GET['tags'];
 
    
    $arrayLength = count($tags);
    $tagString = "";
    for ($x = 0; $x < $arrayLength; $x++) {
        $tagString .= "\"";
        $tagString .= $tags[$x];
        $tagString .= "\"";
        if($x != $arrayLength - 1){
            $tagString .= ", ";
        }
    }
    
    if ($tags[0] == ""){
        // get chats from chatss table
        $result = mysql_query("SELECT userShowName, chPhoneId, chTimeId, chChatTitle, chLatitude, chLongitude, max(meMessageId), max(meMessageTime)FROM chats, messages, users WHERE userPhoneId = chPhoneId AND chPhoneId = mePhoneId AND chTimeId = meTimeId GROUP BY mePhoneId, meTimeId;");
    }else{
        // get chats from chatss table
        $result = mysql_query("SELECT userShowName, chPhoneId, chTimeId, chChatTitle, chLatitude, chLongitude, max(meMessageId), max(meMessageTime)FROM chats, messages, users, chatTags WHERE userPhoneId = chPhoneId AND chPhoneId = mePhoneId AND chTimeId = meTimeId AND ctPhoneId = chPhoneId AND ctTimeId = chTimeId AND ctTagName IN ($tagString) GROUP BY mePhoneId, meTimeId;");
    }
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
            // looping through all results
            // products node
            $response["chats"] = array();
 
            while ($row = mysql_fetch_array($result)) {
               
                $chat = array();
                $chat["creatorUserName"] = $row["userShowName"];
                $chat["creatorId"] = $row["chPhoneId"];
                $chat["timeId"] = $row["chTimeId"];
                $chat["title"] = $row["chChatTitle"];
                $chat["latitude"] = $row["chLatitude"];
                $chat["longitude"] = $row["chLongitude"];
                $chat["numMessages"] = $row["max(meMessageId)"] + 1;
                $chat["lastMessageTime"] = $row["max(meMessageTime)"];
                
                
                $phoneId = $chat["creatorId"];
                $timeId = $chat["timeId"];
                
                
                $chat["tags"] = array();
                // get tags for each chat
                $tagsResult = mysql_query("SELECT ctTagName FROM chatTags WHERE ctPhoneId = '$phoneId' AND ctTimeId = '$timeId';");
                if (!empty($tagsResult)) {
                // check for empty result
                    if (mysql_num_rows($tagsResult) > 0) {
                    // looping through all results
                    // products node
                        while ($tagsRow = mysql_fetch_array($tagsResult)) {
                            array_push($chat["tags"], $tagsRow["ctTagName"]);
                        }
                    }
                }
                
                
                settype($chat["latitude"], "float");
                settype($chat["longitude"], "float");
               
                if (distance($latitude, $longitude, $chat["latitude"], $chat["longitude"], "K") <= 1){
        
                    // push single product into final response array
                    array_push($response["chats"], $chat);
                }
            }
            // success
            $response["success"] = 1;
 
            // echoing JSON response
        echo json_encode($response);
        } else {
            // no products found
            $response["success"] = 1;
            $response["message"] = "No chat found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 1;
        $response["message"] = "No chat found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing during fetching chats. ";
 
    // echoing JSON response
    echo json_encode($response);
}
?>