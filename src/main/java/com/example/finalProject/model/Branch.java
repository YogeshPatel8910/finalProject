package com.example.finalProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String phone;


    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    @ToString.Exclude
    private List<Doctor> doctors;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    @ToString.Exclude
    private List<Appointment> appointments;

//    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="branch_department",
            joinColumns = @JoinColumn(name = "branch_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    @ToString.Exclude
    private Set<Department> department;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return Objects.equals(id, branch.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
