package org.distributed.consensus.model;

import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "roomLockEntry")
public class RoomLockEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    @Column(name = "roomId", unique=true)
    private long roomId;

    public RoomLockEntry( long roomId) {
        this.roomId = roomId;
    }

    public RoomLockEntry() {

    }

    public long getId() {
        return id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }
}
