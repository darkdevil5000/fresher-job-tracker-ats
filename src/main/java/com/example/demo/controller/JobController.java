package com.example.demo.controller;

import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.repository.JobRepository;
import com.example.demo.service.*;
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

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private InterviewPrepService interviewPrepService;

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

        List<Job> dashboardJobs = new ArrayList<>();
        for (Job j : jobs) {
            if (j.getUserId() == null) {
                dashboardJobs.add(j);
            }
        }

        if (dashboardJobs.isEmpty() && (search == null || search.trim().isEmpty())) {
            jobScraperService.scrapeJobs();
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
            @RequestParam("templateStyle") String templateStyle,
            HttpSession session,
            Model model
    ) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);

        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + jobId));

        ResumeService.OptimizationResult result = resumeService.optimizeAndGenerateLaTeX(
                name, email, phone, github, linkedin, summary, skills, education, experience, projects, job.getDescription(), templateStyle
        );

        model.addAttribute("job", job);
        model.addAttribute("candidateName", name);
        model.addAttribute("atsScore", result.atsScore);
        model.addAttribute("matchedSkills", result.matchedKeywords);
        model.addAttribute("missingSkills", result.missingKeywords);
        model.addAttribute("latexCode", result.latexCode);

        // Feed learning roadmaps for missing skill gaps to the view model
        model.addAttribute("roadmapList", roadmapService.getRoadmapsForSkills(result.missingKeywords));
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
            if (job.getUserId() == null && job.getApplicationStatus().equals("SAVED")) {
                userTrackerJobs.add(job);
            }
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
            if (status.equals("SAVED")) {
                jobRepository.delete(job);
            } else {
                job.setApplicationStatus(status);
                jobRepository.save(job);
            }
        }

        return "redirect:/tracker";
    }

    @GetMapping("/job/{id}/interview-prep")
    public String showInterviewPrep(@PathVariable("id") Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);

        Job job = jobRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid job Id:" + id));
        model.addAttribute("job", job);
        
        // Compile category questions tailored to job description
        model.addAttribute("questions", interviewPrepService.generateInterviewPrep(job.getTitle(), job.getDescription()));
        return "interview-prep";
    }
}
