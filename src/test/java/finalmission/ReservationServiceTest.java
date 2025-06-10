package finalmission;

import static finalmission.TestFixture.CONFERENCE_ROOM;
import static finalmission.TestFixture.DEFAULT_TIME;
import static finalmission.TestFixture.LOGIN_MEMBER;
import static finalmission.TestFixture.MEMBER;
import static finalmission.TestFixture.RESERVATION;
import static finalmission.TestFixture.TOMORROW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import finalmission.domain.Reservation;
import finalmission.dto.request.CreateReservationRequest;
import finalmission.error.BadRequestException;
import finalmission.repository.ConferenceRoomRepository;
import finalmission.repository.MemberRepository;
import finalmission.repository.ReservationRepository;
import finalmission.service.ReservationService;
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
        final var response = reservationService.create(request, LOGIN_MEMBER);

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
}
