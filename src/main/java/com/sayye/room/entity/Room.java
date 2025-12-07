package com.sayye.room.entity;

import com.sayye.reservation.entity.Reservation;
import com.sayye.room.dto.request.RoomReqDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private Integer location;

    @Column(nullable = false)
    @Min(1)
    private Integer capacity;

    private String description;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();


    @Builder
    public Room(String roomName, Integer location, Integer capacity, String description) {
        this.roomName = roomName;
        this.location = location;
        this.capacity = capacity;
        this.description = description;
    }

    public void update(RoomReqDto roomReqDto) {
        this.roomName = roomReqDto.getRoomName();
        this.location = roomReqDto.getLocation();
        this.capacity = roomReqDto.getCapacity();
        this.description = roomReqDto.getDescription();
    }


}
