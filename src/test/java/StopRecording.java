import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class StopRecording {

    private static String Base_Url = "https://api.100ms.live/v2/recordings/room/";
    private static String Room_Id = "66eb04dafbcdd40b91868f70";

    private static String Auth_Token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjY2Nzc5NjIsImV4cCI6MTcyNzI4Mjc2MiwianRpIjoiNWNhNmU2ZGUtZDg3Yy00NzJhLTgwNDgtNjg3YWNlODk1MDUzIiwidHlwZSI6Im1hbmFnZW1lbnQiLCJ2ZXJzaW9uIjoyLCJuYmYiOjE3MjY2Nzc5NjIsImFjY2Vzc19rZXkiOiI2NmViMDM5YTQ5NDRmMDY3MzEzYTc5MjgifQ.8jPdQOrnREeKjhEJ_0m0zwTSHO2BGOhde84UHHErTyU";

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
}
