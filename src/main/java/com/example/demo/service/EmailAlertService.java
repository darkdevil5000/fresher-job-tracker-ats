package com.example.demo.service;

import com.example.demo.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailAlertService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private PortfolioScraperService portfolioScraperService;

    public void sendDailyJobAlert(List<Job> newJobs, String recipientEmail) {
        if (newJobs == null || newJobs.isEmpty()) {
            System.out.println("No new jobs found. Skipping daily email alert.");
            return;
        }

        PortfolioScraperService.PortfolioData portfolio = portfolioScraperService.fetchPortfolioData(null);
        String htmlBody = buildHtmlAlert(newJobs, portfolio);

        // 1. Write mock alert HTML file locally to data/emails/ so the user can verify it immediately
        saveEmailMockLocally(htmlBody);

        // 2. Send via JavaMailSender if SMTP configuration exists and is active
        if (mailSender != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(recipientEmail);
                helper.setSubject("🔍 Freshers IT Job Digest - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                helper.setText(htmlBody, true);
                mailSender.send(message);
                System.out.println("Daily job alert email sent successfully to " + recipientEmail);
            } catch (Exception e) {
                System.err.println("SMTP not configured or failed to send mail. Fallback to local HTML preview. Error: " + e.getMessage());
            }
        } else {
            System.out.println("Spring MailSender is inactive. Local mockup created successfully.");
        }
    }

    private String buildHtmlAlert(List<Job> jobs, PortfolioScraperService.PortfolioData portfolio) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        sb.append("<style>");
        sb.append("body { font-family: 'Helvetica Neue', Arial, sans-serif; background-color: #0b0f19; color: #f3f4f6; margin: 0; padding: 20px; }");
        sb.append(".container { max-width: 600px; margin: 0 auto; background: #111827; border: 1px solid #1f2937; border-radius: 12px; padding: 30px; box-shadow: 0 4px 20px rgba(0,0,0,0.5); }");
        sb.append(".header { border-bottom: 2px solid #374151; padding-bottom: 15px; margin-bottom: 25px; }");
        sb.append("h1 { color: #f3f4f6; font-size: 24px; margin: 0; }");
        sb.append("h2 { color: #06b6d4; font-size: 18px; margin-top: 0; }");
        sb.append(".job-card { background: #1f2937; border: 1px solid #374151; border-radius: 8px; padding: 18px; margin-bottom: 18px; border-left: 4px solid #4f46e5; }");
        sb.append(".job-title { font-size: 18px; font-weight: bold; margin: 0 0 5px 0; color: #ffffff; }");
        sb.append(".job-meta { font-size: 13px; color: #9ca3af; margin-bottom: 10px; }");
        sb.append(".job-desc { font-size: 14px; color: #d1d5db; line-height: 1.5; margin-bottom: 15px; }");
        sb.append(".btn { display: inline-block; background: #4f46e5; color: #ffffff !important; text-decoration: none; padding: 8px 16px; border-radius: 6px; font-size: 13px; font-weight: bold; }");
        sb.append(".footer { font-size: 12px; color: #6b7280; text-align: center; border-top: 1px solid #374151; padding-top: 15px; margin-top: 25px; }");
        sb.append("</style></head><body>");

        sb.append("<div class='container'>");
        sb.append("<div class='header'>");
        sb.append("<h1>🔍 Daily Freshers IT Jobs Digest</h1>");
        sb.append("<p style='color: #9ca3af; margin: 5px 0 0 0;'>Prepared for: <strong>").append(portfolio.name).append("</strong></p>");
        sb.append("</div>");

        sb.append("<h2>Latest BCA/MCA Openings matching your profile:</h2>");

        for (Job job : jobs) {
            sb.append("<div class='job-card'>");
            sb.append("<div class='job-title'>").append(job.getTitle()).append("</div>");
            sb.append("<div class='job-meta'>🏢 ").append(job.getCompany()).append(" | 📍 ").append(job.getLocation()).append(" | 💰 ").append(job.getSalary()).append("</div>");
            sb.append("<div class='job-desc'>").append(job.getDescription().replace("\n", "<br>").substring(0, Math.min(250, job.getDescription().length()))).append("...</div>");
            sb.append("<a href='").append(job.getApplyUrl()).append("' class='btn' target='_blank'>🔗 Verify & Apply Directly</a>");
            sb.append("</div>");
        }

        sb.append("<div class='footer'>");
        sb.append("<p>You are receiving this daily scan digest based on your location criteria (Pune, Bangalore, Mumbai, Chennai, Hyderabad).</p>");
        sb.append("<p>&copy; 2026 IT-JobScrape. Built in Java & Spring Boot.</p>");
        sb.append("</div></div></body></html>");

        return sb.toString();
    }

    private void saveEmailMockLocally(String htmlContent) {
        try {
            File dir = new File("./data/emails");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = "job_alert_" + LocalDate.now().toString() + ".html";
            File file = new File(dir, fileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(htmlContent);
            }
            System.out.println("Mock alert email saved locally to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Could not write mock alert email file: " + e.getMessage());
        }
    }
}
