package com.example.demo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioScraperService {

    public static class PortfolioData {
        public String name = "Ankur Sutradhar";
        public String email = "ankursutradhar@example.com";
        public String phone = "+91 98765 43210";
        public String github = "https://github.com/ankursutradhar";
        public String linkedin = "https://linkedin.com/in/ankursutradhar";
        public String summary = "Motivated Software Engineer and Java Developer. BCA graduate (2025) and MCA candidate with experience in full stack web development and database management.";
        public String skills = "Java, React, Node.js, JavaScript, Python, Django, MySQL, Machine Learning, HTML, CSS, Git";
        public String education = "Master of Computer Applications (MCA) | PCU Pune (In Progress)\nBachelor of Computer Applications (BCA) | Graduated (2025)";
        public String experience = "Wipro Letter of Intent (LOI) Holder | Java Developer Intern / Trainee\nDeloitte Data Analytics Simulation Participant";
        public String projects = "IT Support Automation & Helpdesk: Automated ticket classification system built in Java.\nPortfolio Hub: Responsive custom portfolio showcasing full stack projects.";
    }

    public PortfolioData fetchPortfolioData(String targetUrl) {
        PortfolioData data = new PortfolioData();
        String url = (targetUrl == null || targetUrl.trim().isEmpty()) ? "https://ankursportfolio.engineer" : targetUrl;
        
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(8000)
                    .get();

            // 1. Name
            Element nameEl = doc.selectFirst(".profile-name");
            if (nameEl != null && !nameEl.text().isEmpty()) {
                data.name = nameEl.text().trim();
            }

            // 2. Summary
            Element metaEl = doc.selectFirst(".profile-meta");
            if (metaEl != null) {
                data.summary = metaEl.text().trim() + ". Passionate Java developer focused on building scalable backend solutions and intelligent automation.";
            }

            // 3. Skills
            Elements skillBadges = doc.select(".skill-badge");
            if (!skillBadges.isEmpty()) {
                List<String> skillList = new ArrayList<>();
                for (Element badge : skillBadges) {
                    if (!badge.text().trim().isEmpty()) {
                        skillList.add(badge.text().trim());
                    }
                }
                if (!skillList.isEmpty()) {
                    data.skills = String.join(", ", skillList);
                }
            }

            // 4. Education
            Element eduSection = doc.getElementById("education");
            if (eduSection != null) {
                Elements items = eduSection.select("li, .education-item, p");
                String eduText = items.stream()
                        .map(Element::text)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining("\n"));
                if (!eduText.isEmpty()) {
                    data.education = eduText;
                }
            }

            // 5. Experience
            Element expSection = doc.getElementById("experience");
            if (expSection != null) {
                Elements items = expSection.select("li, .experience-item, p");
                String expText = items.stream()
                        .map(Element::text)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining("\n"));
                if (!expText.isEmpty()) {
                    data.experience = expText;
                }
            }

            // 6. Projects
            Element projSection = doc.getElementById("projects");
            if (projSection != null) {
                Elements items = projSection.select(".project-card, li, p");
                String projText = items.stream()
                        .map(Element::text)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining("\n"));
                if (!projText.isEmpty()) {
                    data.projects = projText;
                }
            }

            // 7. Contact Details (Email/Phone/Socials)
            Element contactSection = doc.getElementById("contact");
            if (contactSection != null) {
                Elements links = contactSection.select("a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.startsWith("mailto:")) {
                        data.email = href.replace("mailto:", "").trim();
                    } else if (href.startsWith("tel:")) {
                        data.phone = href.replace("tel:", "").trim();
                    } else if (href.contains("github.com")) {
                        data.github = href;
                    } else if (href.contains("linkedin.com")) {
                        data.linkedin = href;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Could not dynamically scrape " + url + ": " + e.getMessage() + ". Using pre-configured portfolio profile values.");
        }
        return data;
    }
}
