package finalmission.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateReservationRequest(
        Long id,
        LocalDate date,
        LocalTime time,
        Long conferenceRoomId
) {
}
