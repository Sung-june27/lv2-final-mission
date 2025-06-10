package finalmission.service;

import finalmission.domain.ConferenceRoom;
import finalmission.domain.LoginMember;
import finalmission.domain.Member;
import finalmission.domain.Reservation;
import finalmission.dto.request.CreateReservationRequest;
import finalmission.dto.response.CreateReservationResponse;
import finalmission.error.NotFoundException;
import finalmission.repository.ConferenceRoomRepository;
import finalmission.repository.MemberRepository;
import finalmission.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConferenceRoomRepository conferenceRoomRepository;
    private final MemberRepository memberRepository;

    public CreateReservationResponse create(CreateReservationRequest request, LoginMember loginMember) {
        ConferenceRoom conferenceRoom = getConferenceRoomById(request.conferenceRoomId());
        Member member = getMemberById(loginMember.id());
        Reservation reservation = request.toReservation(conferenceRoom, member);
        Reservation saved = reservationRepository.save(reservation);
        
        return CreateReservationResponse.from(saved);
    }

    private ConferenceRoom getConferenceRoomById(Long conferenceRoomId) {
        return conferenceRoomRepository.findById(conferenceRoomId)
                .orElseThrow(() -> new NotFoundException("회의실을 찾을 수 없습니다."));
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));
    }
}
