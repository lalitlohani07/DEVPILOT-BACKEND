package com.lalit.devpilot.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
@Entity
@Table(name = "users")
public class User {
@Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 20)
    private EmploymentStatus employmentStatus;

    // private int id;
    // private String name;
    // private String email;
    // private Role role;
    // private EmploymentStatus employmentStatus;
protected User() {
}
    public User(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.employmentStatus = EmploymentStatus.ACTIVE;
    }

    public User(int id, String name, String email, Role role, EmploymentStatus employmentStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.employmentStatus = employmentStatus;
    }

    // getter for id coz id cant be edited once given
    public int getId() {
        return id;
    }
public void setId(int id) {
        this.id = id;
    }
    // getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // getter and setters for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // getters and setters for role
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // getters and setters for employmentstatus
    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", employmentStatus=" + employmentStatus +
                '}';
    }
    @Override
public boolean equals(Object obj) {

    
    if (this == obj) {
        return true;
    }

    if (obj == null) {
        return false;
    }

    if (getClass() != obj.getClass()) {
        return false;
    }

    User other = (User) obj;

    if (this.id == 0 || other.id == 0) {
        return false;
    }
    
    return this.id == other.id;
}
@Override
public int hashCode() {
    return Integer.hashCode(id);
}
}
