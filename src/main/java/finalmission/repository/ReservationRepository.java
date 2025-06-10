package finalmission.repository;

import finalmission.domain.ConferenceRoom;
import finalmission.domain.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByMemberId(Long id);

    boolean existsByDateAndTimeAndConferenceRoom(LocalDate date, LocalTime time, ConferenceRoom conferenceRoom);
}
