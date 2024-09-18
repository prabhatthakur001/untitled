import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class StopRecording {

    private static String Base_Url = "https://api.100ms.live/v2/recordings/room/";
    private static String Room_Id = System.getenv("Room_Id");
    private static String Auth_Token = System.getenv("Auth_Token");
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = Base_Url;
    }

    @Test
    public void stopRecording() {
        String stopRecordingEndpoint = Room_Id + "/stop";
        Response response = given()
                .header("Authorization", "Bearer " + Auth_Token)
                .contentType("application/json")
                .when()
                .post(stopRecordingEndpoint)
                .then()
                .statusCode(200)
                .body("status", equalTo("stopped"))
                .extract()
                .response();


        String meetingUrl = response.jsonPath().getString("meeting_url");
        Assert.assertTrue("Meeting url in response not similar to start api response",meetingUrl.equals(StartRecording.meetingUrl));

        String status = response.jsonPath().getString("status");
        Assert.assertTrue(status.equals("Stopped"));
    }

    //--------------------- edge case ------------------------------------------//

    @Test
    public void invalidRoomId() throws IOException {
        String stopRecordingEndpoint = "1" + "/stop";

        Response response = given()
                .header("Authorization", "Bearer " + Auth_Token)
                .contentType("application/json")
                .when()
                .post(stopRecordingEndpoint)
                .then()
                .statusCode(403)  // Expecting a 200 status code for successful start
                .extract()
                .response();

        String errorMsg = response.jsonPath().getString("message");
        Assertions.assertTrue(errorMsg.equals("user does not have required permission"));
    }
}
