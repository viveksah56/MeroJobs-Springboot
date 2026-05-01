package com.backend.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Employee extends User {
    @Column(nullable = false, unique = true)
    private String companyName;
    @Column(nullable = false)
    private String jobTitle;
    private String department;
    private String workLocation;
    private Double salary;
}