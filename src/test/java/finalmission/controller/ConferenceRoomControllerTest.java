package finalmission.controller;

import static finalmission.TestFixture.ADMIN;
import static finalmission.TestFixture.MEMBER;
import static org.hamcrest.Matchers.equalTo;

import finalmission.TestHelper;
import finalmission.member.repository.MemberRepository;
import finalmission.room.dto.request.CreateRoomRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConferenceRoomControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
    }

    @DisplayName("회의실을 생성할 수 없다.")
    @Test
    void createConferenceRoom() {
        // given
        memberRepository.save(ADMIN);
        String token = TestHelper.login(ADMIN.getEmail(), ADMIN.getPassword());

        CreateRoomRequest request = new CreateRoomRequest("회의실");

        // when & then
        RestAssured.given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/room")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("회의실"));
    }

    @DisplayName("어드민이 아니라면, 회의실을 생성할 수 없다.")
    @Test
    void createConferenceRoom_WhenNotAdmin() {
        // given
        memberRepository.save(MEMBER);
        String token = TestHelper.login(MEMBER.getEmail(), MEMBER.getPassword());

        // when & then
        RestAssured.given()
                .cookie("token", token)
                .when()
                .post("/room")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
