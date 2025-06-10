package finalmission.repository;

import finalmission.domain.ConferenceRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConferenceRoomRepository extends JpaRepository<ConferenceRoom, Long> {
}
