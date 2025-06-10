package finalmission.service;

import finalmission.domain.ConferenceRoom;
import finalmission.domain.LoginMember;
import finalmission.domain.Member;
import finalmission.domain.Reservation;
import finalmission.dto.request.CreateReservationRequest;
import finalmission.dto.request.UpdateReservationRequest;
import finalmission.dto.response.CreateReservationResponse;
import finalmission.dto.response.ReadReservationResponse;
import finalmission.dto.response.ReservationByMemberResponse;
import finalmission.dto.response.UpdateReservationResponse;
import finalmission.error.BadRequestException;
import finalmission.error.NotFoundException;
import finalmission.repository.ConferenceRoomRepository;
import finalmission.repository.MemberRepository;
import finalmission.repository.ReservationRepository;
import java.util.List;
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

        validateAlreadyReserved(request, conferenceRoom);

        Reservation reservation = request.toReservation(conferenceRoom, member);
        Reservation saved = reservationRepository.save(reservation);

        return CreateReservationResponse.from(saved);
    }

    public List<ReadReservationResponse> findALl() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(ReadReservationResponse::from)
                .toList();
    }

    public List<ReservationByMemberResponse> findAllByMember(LoginMember loginMember) {
        List<Reservation> reservations = reservationRepository.findAllByMemberId(loginMember.id());
        return reservations.stream()
                .map(ReservationByMemberResponse::from)
                .toList();
    }

    public UpdateReservationResponse updateByMember(UpdateReservationRequest request, LoginMember loginMember) {
        Reservation reservation = getReservationById(request.id());
        ConferenceRoom conferenceRoom = getConferenceRoomById(request.conferenceRoomId());
        Member member = getMemberById(loginMember.id());
        validateByMember(reservation, member);
        reservation.update(request.date(), request.time(), conferenceRoom);
        return UpdateReservationResponse.from(reservation);
    }

    private void validateByMember(Reservation reservation, Member member) {
        if (!reservation.isMine(member)) {
            throw new BadRequestException("본인 예약만 수정 / 삭제 가능합니다.");
        }
    }

    private void validateAlreadyReserved(CreateReservationRequest request, ConferenceRoom conferenceRoom) {
        if (reservationRepository.existsByDateAndTimeAndConferenceRoom(request.date(), request.time(), conferenceRoom)) {
            throw new BadRequestException("예약이 이미 존재합니다.");
        }
    }

    private Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다."));
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
