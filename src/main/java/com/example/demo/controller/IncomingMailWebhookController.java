package com.example.demo.controller;

import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mail")
public class IncomingMailWebhookController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    /**
     * Inbound Mail Webhook (SendGrid/Mailgun webhook layout).
     * POST body parameters:
     *   "from" -> "ankur.sutradhar999@gmail.com"
     *   "body" -> "Apply Wipro" or "Applied TCS" or "Resume Viewed Cognizant"
     */
    @PostMapping("/incoming")
    public ResponseEntity<String> receiveEmailUpdate(@RequestBody Map<String, String> payload) {
        String from = payload.get("from");
        String body = payload.get("body");

        if (from == null || body == null) {
            return ResponseEntity.badRequest().body("Missing from or body params");
        }

        // Find registered user by username or dynamic portfolio lookup matching their contact details
        User targetUser = null;
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            // Match user (simple mock: if username matches prefix, or match email directly)
            if (from.toLowerCase().contains(u.getUsername().toLowerCase())) {
                targetUser = u;
                break;
            }
        }
        // Fallback to primary default user if username not matching directly
        if (targetUser == null && !allUsers.isEmpty()) {
            targetUser = allUsers.get(0);
        }

        if (targetUser == null) {
            return ResponseEntity.status(404).body("No registered users found to pair incoming updates");
        }

        String lowerBody = body.toLowerCase();
        String status = "APPLIED";
        if (lowerBody.contains("resume viewed") || lowerBody.contains("viewed")) {
            status = "RESUME_VIEWED";
        } else if (lowerBody.contains("selected")) {
            status = "SELECTED";
        } else if (lowerBody.contains("rejected")) {
            status = "REJECTED";
        }

        // Identify company name mentioned in the email body
        List<Job> allJobs = jobRepository.findAll();
        Job matchJob = null;
        for (Job job : allJobs) {
            if (lowerBody.contains(job.getCompany().toLowerCase())) {
                matchJob = job;
                break;
            }
        }

        if (matchJob == null) {
            return ResponseEntity.ok("Received webhook but no matching company job listings found in database for instruction: " + body);
        }

        // Apply status transition logic
        if (matchJob.getUserId() == null) {
            // Clone to user-specific status card
            Job userJob = new Job(
                    matchJob.getTitle(), matchJob.getCompany(), matchJob.getLocation(), matchJob.getDescription(),
                    matchJob.getApplyUrl(), matchJob.getSalary(), matchJob.getDatePosted(), matchJob.getSource()
            );
            userJob.setUserId(targetUser.getId());
            userJob.setApplicationStatus(status);
            jobRepository.save(userJob);
        } else if (matchJob.getUserId().equals(targetUser.getId())) {
            matchJob.setApplicationStatus(status);
            jobRepository.save(matchJob);
        }

        return ResponseEntity.ok("Successfully parsed email instruction: Status updated to " + status + " for " + matchJob.getCompany());
    }
}
