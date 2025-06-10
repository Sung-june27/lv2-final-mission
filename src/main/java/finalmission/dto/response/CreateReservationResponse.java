package finalmission.dto.response;

import finalmission.domain.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateReservationResponse(
        Long id,
        LocalDate date,
        LocalTime time,
        String conferenceRoomName,
        String memberName
) {

    public static CreateReservationResponse from(Reservation reservation) {
        return new CreateReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getConferenceRoom().getName(),
                reservation.getMember().getName()
        );
    }
}
