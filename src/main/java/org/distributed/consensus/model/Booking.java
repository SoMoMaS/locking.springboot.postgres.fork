package org.distributed.consensus.model;

import java.sql.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Version
    private Long version;

    @Column(name = "name")
    private String name;

    @Column(name = "roomId")
    private long roomId;

    @Column(name = "start")
    private Date start;

    @Column(name = "finish")
    private Date finish;

    public Booking(String name, long roomId, Date from, Date end) {
        this.name = name;
        this.roomId = roomId;
        this.start = from;
        this.finish = end;
    }

    public Booking() {

    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public long getVersion() {
        return version;
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

    // Setter für die Version (für das optimistische Locking)
}
