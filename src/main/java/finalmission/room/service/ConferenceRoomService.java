package finalmission.room.service;

import finalmission.global.error.exception.NotFoundException;
import finalmission.room.domain.ConferenceRoom;
import finalmission.room.repository.ConferenceRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConferenceRoomService {

    private final ConferenceRoomRepository conferenceRoomRepository;

    @Transactional(readOnly = true)
    public ConferenceRoom getById(Long conferenceRoomId) {
        return conferenceRoomRepository.findById(conferenceRoomId)
                .orElseThrow(() -> new NotFoundException("회의실을 찾을 수 없습니다."));
    }
}
