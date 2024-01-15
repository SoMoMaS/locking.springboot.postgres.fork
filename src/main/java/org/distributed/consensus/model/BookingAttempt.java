package org.distributed.consensus.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "booking_attempts")
public class BookingAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "name")
    private String name;

    @Column(name = "roomId")
    private long roomId;

    @Column(name = "start")
    private Date start;

    @Column(name = "finish")
    private Date finish;

    @Column(name = "status")
    private String status;

    public BookingAttempt(String name, long roomId, Date from, Date end, String status) {
        this.name = name;
        this.roomId = roomId;
        this.start = from;
        this.finish = end;
        this.status = status;
    }

    public BookingAttempt() {

    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public long getRoomId() {
        return roomId;
    }

    public Date getStart() {
        return start;
    }

    public Date getFinish() {
        return finish;
    }

    public String getStatus() {
        return status;
    }

    // Setter für den Namen
    public void setName(String name) {
        this.name = name;
    }

    // Setter für die Raum-ID
    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    // Setter für das Startdatum
    public void setStart(Date start) {
        this.start = start;
    }

    // Setter für das Enddatum
    public void setFinish(Date finish) {
        this.finish = finish;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}