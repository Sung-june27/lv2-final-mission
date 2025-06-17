package finalmission.controller;

import static finalmission.helper.TestFixture.DEFAULT_TIME;
import static finalmission.helper.TestFixture.TOMORROW;
import static finalmission.member.domain.Role.USER;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import finalmission.helper.TestHelper;
import finalmission.member.domain.Member;
import finalmission.member.repository.MemberRepository;
import finalmission.reservation.domain.Reservation;
import finalmission.reservation.dto.request.CreateReservationRequest;
import finalmission.reservation.dto.request.UpdateReservationRequest;
import finalmission.reservation.repository.ReservationRepository;
import finalmission.room.domain.ConferenceRoom;
import finalmission.room.repository.ConferenceRoomRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ConferenceRoomRepository conferenceRoomRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    private Member member;
    private ConferenceRoom conferenceRoom;

    @BeforeEach
    public void setup() {
        initData();
        RestAssured.port = port;
    }

    @DisplayName("예약을 생성한다.")
    @Test
    void createReservation() {
        // given
        Long conferenceRoomId = conferenceRoom.getId();

        String token = TestHelper.login(member.getEmail(), member.getPassword());

        CreateReservationRequest request = new CreateReservationRequest(TOMORROW, DEFAULT_TIME, conferenceRoomId);

        // when & then
        RestAssured.given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("date", contains(TOMORROW.getYear(), TOMORROW.getMonthValue(), TOMORROW.getDayOfMonth()))
                .body("time", contains(DEFAULT_TIME.getHour(), DEFAULT_TIME.getMinute()))
                .body("conferenceRoomName", equalTo(conferenceRoom.getName()))
                .body("memberName", equalTo(member.getName()));
    }

    @DisplayName("본인의 예약을 조회할 수 있다.")
    @Test
    void getAllReservationsByMember() {
        // given
        saveReservation();

        String token = TestHelper.login(member.getEmail(), member.getPassword());

        // when & then
        RestAssured.given()
                .cookie("token", token)
                .when()
                .get("/reservations/mine")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].memberName", equalTo(member.getName()));
    }

    @DisplayName("본인의 예약을 수정할 수 있다.")
    @Test
    void updateReservation() {
        // given
        Reservation reservation = saveReservation();

        String token = TestHelper.login(member.getEmail(), member.getPassword());

        LocalDate updateDate = TOMORROW.plusDays(1);
        UpdateReservationRequest request = new UpdateReservationRequest(
                updateDate, DEFAULT_TIME, conferenceRoom.getId());

        // when & then
        RestAssured.given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/reservations/" + reservation.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("date", contains(updateDate.getYear(), updateDate.getMonthValue(), updateDate.getDayOfMonth()));
    }

    @DisplayName("타인의 예약을 수정할 수 없다.")
    @Test
    void updateReservation_WhenNotMine() {
        // given
        Reservation reservation = saveReservationByOtherMember();

        String token = TestHelper.login(member.getEmail(), member.getPassword());

        LocalDate updateDate = TOMORROW.plusDays(1);
        UpdateReservationRequest request = new UpdateReservationRequest(
                updateDate, DEFAULT_TIME, conferenceRoom.getId());

        // when & then
        RestAssured.given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .patch("/reservations/" + reservation.getId())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("본인의 예약을 삭제할 수 있다.")
    @Test
    void deleteReservation() {
        // given
        Reservation reservation = saveReservation();

        String token = TestHelper.login(member.getEmail(), member.getPassword());

        // when & then
        RestAssured.given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("/reservations/" + reservation.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("타인의 예약을 삭제할 수 없다.")
    @Test
    void deleteReservation_WhenNotMine() {
        // given
        Reservation reservation = saveReservationByOtherMember();

        String token = TestHelper.login(member.getEmail(), member.getPassword());

        // when & then
        RestAssured.given()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("/reservations/" + reservation.getId())
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private void initData() {
        reservationRepository.deleteAll();
        memberRepository.deleteAll();
        conferenceRoomRepository.deleteAll();

        Member beforeSaveMember = new Member("사용자", "user@email.com", "password", USER);
        member = memberRepository.save(beforeSaveMember);
        ConferenceRoom beforeSaveRoom = new ConferenceRoom("회의실");
        conferenceRoom = conferenceRoomRepository.save(beforeSaveRoom);
    }

    private Reservation saveReservation() {
        Reservation reservation = new Reservation(TOMORROW, DEFAULT_TIME, conferenceRoom, member);
        return reservationRepository.save(reservation);
    }

    private Reservation saveReservationByOtherMember() {
        Member otherMember = new Member("타인", "other@email.com", "password", USER);
        Member savedOtherMember = memberRepository.save(otherMember);

        Reservation reservation = new Reservation(TOMORROW, DEFAULT_TIME, conferenceRoom, savedOtherMember);
        return reservationRepository.save(reservation);
    }
}
