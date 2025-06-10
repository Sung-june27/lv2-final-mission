package finalmission.service;

import static finalmission.TestFixture.CONFERENCE_ROOM;
import static finalmission.TestFixture.DEFAULT_TIME;
import static finalmission.TestFixture.LOGIN_MEMBER;
import static finalmission.TestFixture.MEMBER;
import static finalmission.TestFixture.RESERVATION;
import static finalmission.TestFixture.TOMORROW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import finalmission.domain.Reservation;
import finalmission.dto.request.CreateReservationRequest;
import finalmission.dto.response.CreateReservationResponse;
import finalmission.dto.response.ReservationByMemberResponse;
import finalmission.error.BadRequestException;
import finalmission.repository.ConferenceRoomRepository;
import finalmission.repository.MemberRepository;
import finalmission.repository.ReservationRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ConferenceRoomRepository conferenceRoomRepository;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("예약을 추가한다.")
    @Test
    void create() {
        // given
        CreateReservationRequest request = new CreateReservationRequest(
                TOMORROW,
                DEFAULT_TIME,
                1L
        );
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(MEMBER));
        when(conferenceRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(CONFERENCE_ROOM));
        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(RESERVATION);

        // when
        CreateReservationResponse response = reservationService.create(request, LOGIN_MEMBER);

        // then
        assertAll(
                () -> assertThat(response.date()).isEqualTo(TOMORROW),
                () -> assertThat(response.time()).isEqualTo(DEFAULT_TIME),
                () -> assertThat(response.conferenceRoomName()).isEqualTo(CONFERENCE_ROOM.getName()),
                () -> assertThat(response.memberName()).isEqualTo(MEMBER.getName())
        );
    }

    @DisplayName("예약이 이미 존재한다면, 예외를 발생한다")
    @Test
    void create_WhenExistsReservation() {
        // given
        CreateReservationRequest request = new CreateReservationRequest(
                TOMORROW,
                DEFAULT_TIME,
                1L
        );
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.of(MEMBER));
        when(conferenceRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(CONFERENCE_ROOM));
        when(reservationRepository.existsByDateAndTimeAndConferenceRoom(any(), any(), any()))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> reservationService.create(request, LOGIN_MEMBER))
                .isInstanceOf(BadRequestException.class);
    }

    @DisplayName("모든 예약 정보를 가져온다.")
    @Test
    void findAll() {
        // given
        List<Reservation> reservations = createReservations();
        when(reservationRepository.findAll())
                .thenReturn(reservations);

        // when & then
        assertThatCode(() -> reservationService.findALl())
                .doesNotThrowAnyException();
    }

    @DisplayName("사용자는 본인의 예약 목록만 조회할 수 있다.")
    @Test
    void findAllByMember() {
        // given
        List<Reservation> reservations = createReservations();
        when(reservationRepository.findAllByMemberId(anyLong()))
                .thenReturn(reservations);

        // when
        List<ReservationByMemberResponse> responses = reservationService.findAllByMember(LOGIN_MEMBER);

        // then
        boolean allMatchByMember = responses.stream()
                .allMatch(response -> Objects.equals(response.memberName(), MEMBER.getName()));

        assertThat(allMatchByMember).isEqualTo(true);
    }

    private List<Reservation> createReservations() {
        List<Reservation> reservations = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            Reservation reservation = new Reservation((long) i, date, DEFAULT_TIME, CONFERENCE_ROOM, MEMBER);
            reservations.add(reservation);
        }
        return reservations;
    }
}
