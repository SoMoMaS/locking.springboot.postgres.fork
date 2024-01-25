package org.distributed.consensus.Dtos;

import lombok.Data;

import java.util.Date;

@Data
public class BookingDto {

    private String name;
    private long roomId;
    private Date start;
    private Date finish;
    private Integer version;
}
