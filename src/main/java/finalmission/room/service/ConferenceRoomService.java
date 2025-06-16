package finalmission.room.service;

import finalmission.global.error.exception.NotFoundException;
import finalmission.room.domain.ConferenceRoom;
import finalmission.room.dto.request.CreateRoomRequest;
import finalmission.room.dto.response.CreateRoomResponse;
import finalmission.room.repository.ConferenceRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConferenceRoomService {

    private final ConferenceRoomRepository conferenceRoomRepository;

    @Transactional(readOnly = true)
    public ConferenceRoom getById(Long conferenceRoomId) {
        return conferenceRoomRepository.findById(conferenceRoomId)
                .orElseThrow(() -> new NotFoundException("회의실을 찾을 수 없습니다."));
    }

    public CreateRoomResponse create(CreateRoomRequest request) {
        ConferenceRoom conferenceRoom = request.toConferenceRoom();
        ConferenceRoom saved = conferenceRoomRepository.save(conferenceRoom);

        return CreateRoomResponse.from(saved);
    }
}
