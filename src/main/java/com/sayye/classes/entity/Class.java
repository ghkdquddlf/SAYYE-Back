package com.sayye.classes.entity;

import com.sayye.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "classes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;
    private LocalDate startDate;
    private LocalDate endDate;

    public void update(String className, LocalDate startDate, LocalDate endDate) {
        this.className = className;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}