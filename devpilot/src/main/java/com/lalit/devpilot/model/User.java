package com.lalit.devpilot.model;

public class User {

    private int id;
    private String name;
    private String email;
    private Role role;
    private EmploymentStatus employmentStatus;

    public User(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.employmentStatus = EmploymentStatus.CURRENTLY_WORKING;
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
