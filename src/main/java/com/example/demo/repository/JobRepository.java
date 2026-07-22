package com.example.demo.repository;

import com.example.demo.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findAllByOrderByDatePostedDesc();
    List<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titleKeyword, String descKeyword);
}
