package finalmission.dto.request;

import finalmission.domain.ConferenceRoom;
import finalmission.domain.Member;
import finalmission.domain.Reservation;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateReservationRequest(
        @NotNull LocalDate date,
        @NotNull LocalTime time,
        @NotNull Long conferenceRoomId
) {
    public Reservation toReservation(ConferenceRoom conferenceRoom, Member member) {
        return new Reservation(date, time, conferenceRoom, member);
    }
}
