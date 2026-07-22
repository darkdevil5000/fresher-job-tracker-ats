package com.example.demo.controller;

import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.repository.JobRepository;
import com.example.demo.service.JobScraperService;
import com.example.demo.service.ResumeService;
import com.example.demo.service.PortfolioScraperService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobScraperService jobScraperService;

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private PortfolioScraperService portfolioScraperService;

    @GetMapping("/")
    public String dashboard(
            @RequestParam(value = "search", required = false) String search,
            HttpSession session,
            Model model
    ) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);

        List<Job> jobs;
        if (search != null && !search.trim().isEmpty()) {
            jobs = jobRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
            model.addAttribute("search", search);
        } else {
            jobs = jobRepository.findAllByOrderByDatePostedDesc();
        }

        // Filter out private tracker jobs so only global scraped jobs show on the home dashboard
        List<Job> dashboardJobs = new ArrayList<>();
        for (Job j : jobs) {
            if (j.getUserId() == null) {
                dashboardJobs.add(j);
            }
        }

        // If database is empty of global jobs, crawl them automatically
        if (dashboardJobs.isEmpty() && (search == null || search.trim().isEmpty())) {
            jobScraperService.scrapeJobs();
            // Refetch
            jobs = jobRepository.findAllByOrderByDatePostedDesc();
            dashboardJobs.clear();
            for (Job j : jobs) {
                if (j.getUserId() == null) {
                    dashboardJobs.add(j);
                }
            }
        }

        model.addAttribute("jobs", dashboardJobs);
        model.addAttribute("jobCount", dashboardJobs.size());
        return "index";
    }

    @GetMapping("/job/{id}")
    public String viewJob(@PathVariable("id") Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);

        Job job = jobRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + id));
        model.addAttribute("job", job);
        return "job-details";
    }

    @GetMapping("/sync")
    public String syncJobs(RedirectAttributes redirectAttributes) {
        List<Job> scraped = jobScraperService.scrapeJobs();
        redirectAttributes.addFlashAttribute("message", "Scrape completed! Synchronized active fresher jobs on official career pages.");
        return "redirect:/";
    }

    @GetMapping("/resume/build/{jobId}")
    public String showResumeBuilder(@PathVariable("jobId") Long jobId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);

        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + jobId));
        model.addAttribute("job", job);
        
        // Dynamic profile fetching based on the current logged-in user's portfolio link
        model.addAttribute("portfolio", portfolioScraperService.fetchPortfolioData(user.getPortfolioUrl()));
        return "resume-builder";
    }

    @PostMapping("/resume/generate")
    public String generateResume(
            @RequestParam("jobId") Long jobId,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam(value = "github", required = false) String github,
            @RequestParam(value = "linkedin", required = false) String linkedin,
            @RequestParam("summary") String summary,
            @RequestParam("skills") String skills,
            @RequestParam("education") String education,
            @RequestParam("experience") String experience,
            @RequestParam("projects") String projects,
            HttpSession session,
            Model model
    ) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);

        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + jobId));

        ResumeService.OptimizationResult result = resumeService.optimizeAndGenerateLaTeX(
                name, email, phone, github, linkedin, summary, skills, education, experience, projects, job.getDescription()
        );

        model.addAttribute("job", job);
        model.addAttribute("candidateName", name);
        model.addAttribute("atsScore", result.atsScore);
        model.addAttribute("matchedSkills", result.matchedKeywords);
        model.addAttribute("missingSkills", result.missingKeywords);
        model.addAttribute("latexCode", result.latexCode);

        // Fetch user portfolio details for the skill gap visualizer comparison
        model.addAttribute("portfolio", portfolioScraperService.fetchPortfolioData(user.getPortfolioUrl()));

        return "resume-view";
    }

    @GetMapping("/tracker")
    public String showTracker(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);

        List<Job> allJobs = jobRepository.findAllByOrderByDatePostedDesc();
        List<Job> userTrackerJobs = new ArrayList<>();

        for (Job job : allJobs) {
            // Keep the global saved jobs so they appear on their saved column
            if (job.getUserId() == null && job.getApplicationStatus().equals("SAVED")) {
                userTrackerJobs.add(job);
            }
            // Add user's private application status cards
            if (job.getUserId() != null && job.getUserId().equals(user.getId())) {
                userTrackerJobs.add(job);
            }
        }

        model.addAttribute("jobs", userTrackerJobs);
        return "tracker";
    }

    @PostMapping("/job/{id}/status")
    public String updateJobStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status,
            HttpSession session
    ) {
        User user = (User) session.getAttribute("user");
        Job job = jobRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + id));

        if (job.getUserId() == null) {
            // If they change the status of a global card, clone it to partition state
            if (!status.equals("SAVED")) {
                Job userJob = new Job(
                        job.getTitle(), job.getCompany(), job.getLocation(), job.getDescription(),
                        job.getApplyUrl(), job.getSalary(), job.getDatePosted(), job.getSource()
                );
                userJob.setUserId(user.getId());
                userJob.setApplicationStatus(status);
                jobRepository.save(userJob);
            }
        } else if (job.getUserId().equals(user.getId())) {
            // If it is already their own tracking card
            if (status.equals("SAVED")) {
                // If reset to saved, delete the clone so it falls back to global card
                jobRepository.delete(job);
            } else {
                job.setApplicationStatus(status);
                jobRepository.save(job);
            }
        }

        return "redirect:/tracker";
    }
}
