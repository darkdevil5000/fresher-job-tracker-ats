package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import java.time.LocalDate;

@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String title;

    @Column(length = 512)
    private String company;

    @Column(length = 512)
    private String location;

    @Column(length = 20000)
    private String description;

    @Column(length = 2048)
    private String applyUrl;

    @Column(length = 512)
    private String salary;

    private LocalDate datePosted;

    @Column(length = 512)
    private String source;

    private String applicationStatus = "SAVED";

    private Long userId;

    public Job() {}

    public Job(String title, String company, String location, String description, String applyUrl, String salary, LocalDate datePosted, String source) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
        this.applyUrl = applyUrl;
        this.salary = salary;
        this.datePosted = datePosted;
        this.source = source;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApplyUrl() { return applyUrl; }
    public void setApplyUrl(String applyUrl) { this.applyUrl = applyUrl; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public LocalDate getDatePosted() { return datePosted; }
    public void setDatePosted(LocalDate datePosted) { this.datePosted = datePosted; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
