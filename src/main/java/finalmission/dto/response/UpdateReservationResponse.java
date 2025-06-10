package finalmission.dto.response;

import finalmission.domain.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateReservationResponse(
        Long id,
        LocalDate date,
        LocalTime time,
        String conferenceRoomName,
        String memberName
) {

    public static UpdateReservationResponse from(Reservation reservation) {
        return new UpdateReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getConferenceRoom().getName(),
                reservation.getMember().getName()
        );
    }
}
