package com.pelatro.signup.entity;




import jakarta.persistence.*;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name="employees")
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false) // Custom column name for firstName
    private String firstName;

    @Column(name = "last_name", nullable = false) // Custom column name for lastName
    private String lastName;

    @Column(name = "email_id", unique = true, nullable = false) // Custom column name for emailId
    private String emailId;
    
    @Column(name = "profile_picture") // New column for profile picture
    private String profilePicture;
    
    public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    // Foreign key reference to the Department table
    private Department department;  // Many-to-one relationship with Department

    @Column(name = "role", nullable = false)  // Column for role
    private String role;

    @Column(name = "phone_number")  // Column for phone number (optional)
    private String phoneNumber;

    // Constructors
    public Employee() {}

    public Employee(String firstName, String lastName, String emailId, Department department, String role, String phoneNumber,String profilePicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailId = emailId;
        this.department = department;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.profilePicture=profilePicture;;
    }

    // Getters and Setters for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
