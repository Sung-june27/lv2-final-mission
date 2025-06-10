package finalmission.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private ConferenceRoom conferenceRoom;

    protected Reservation() {}

    public Reservation(LocalDate date, LocalTime time, Member member, ConferenceRoom conferenceRoom) {
        this(null, date, time, member, conferenceRoom);
    }

    public Reservation(Long id, LocalDate date, LocalTime time, Member member, ConferenceRoom conferenceRoom) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.member = member;
        this.conferenceRoom = conferenceRoom;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public Member getMember() {
        return member;
    }

    public ConferenceRoom getConferenceRoom() {
        return conferenceRoom;
    }
}
