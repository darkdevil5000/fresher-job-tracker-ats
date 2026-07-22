package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private static final List<String> IT_KEYWORDS = Arrays.asList(
            "Java", "Python", "SQL", "MySQL", "PostgreSQL", "Database", "REST", "API", "RESTful",
            "HTML", "CSS", "JavaScript", "React", "Angular", "Vue", "Spring", "Spring Boot", "Hibernate", "JPA",
            "Git", "GitHub", "Maven", "Gradle", "JUnit", "Mockito", "Selenium", "Postman", "QA", "Jira",
            "DevOps", "Docker", "AWS", "Cloud", "Agile", "Scrum", "OOP", "Object-Oriented", "Data Structures",
            "Algorithms", "Unix", "Linux", "C++", "C#", "Bootstrap", "Web Development", "Node.js"
    );

    public static class OptimizationResult {
        public int atsScore;
        public List<String> matchedKeywords;
        public List<String> missingKeywords;
        public String latexCode;
    }

    public OptimizationResult optimizeAndGenerateLaTeX(
            String name, String email, String phone, String github, String linkedin,
            String summary, String skillsStr, String educationStr, String experienceStr, String projectsStr,
            String jobDescription
    ) {
        OptimizationResult result = new OptimizationResult();

        // 1. Extract Keywords from Job Description
        Set<String> jdKeywords = new HashSet<>();
        String lowerJD = jobDescription.toLowerCase();
        for (String keyword : IT_KEYWORDS) {
            // Match word boundaries to prevent substring collisions (e.g. 'C' inside 'CSS')
            String regex = "\\b" + keyword.toLowerCase().replaceAll("\\+", "\\\\+") + "\\b";
            if (lowerJD.matches(".*" + regex + ".*")) {
                jdKeywords.add(keyword);
            }
        }

        // 2. Extract Keywords from User Profile
        String userProfileContent = (summary + " " + skillsStr + " " + experienceStr + " " + projectsStr).toLowerCase();
        Set<String> matched = new HashSet<>();
        Set<String> missing = new HashSet<>();

        for (String keyword : jdKeywords) {
            String regex = "\\b" + keyword.toLowerCase().replaceAll("\\+", "\\\\+") + "\\b";
            if (userProfileContent.matches(".*" + regex + ".*")) {
                matched.add(keyword);
            } else {
                missing.add(keyword);
            }
        }

        result.matchedKeywords = new ArrayList<>(matched);
        result.missingKeywords = new ArrayList<>(missing);

        // 3. Compute ATS Score
        if (jdKeywords.isEmpty()) {
            result.atsScore = 85; // Default baseline score
        } else {
            result.atsScore = (int) (((double) matched.size() / jdKeywords.size()) * 100);
            if (result.atsScore < 30) result.atsScore = 35; // Lower bound
        }

        // Auto-optimize: Add missing skills to the generated list for LaTeX to boost score
        List<String> userSkills = Arrays.stream(skillsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Inject missing keywords to help them pass ATS
        List<String> optimizedSkills = new ArrayList<>(userSkills);
        for (String mSkill : missing) {
            if (optimizedSkills.size() < 25) { // Cap skill list
                optimizedSkills.add(mSkill);
            }
        }

        // 4. Generate LaTeX Code
        result.latexCode = generateLaTeX(name, email, phone, github, linkedin, summary, optimizedSkills, educationStr, experienceStr, projectsStr);

        return result;
    }

    private String generateLaTeX(
            String name, String email, String phone, String github, String linkedin,
            String summary, List<String> skills, String education, String experience, String projects
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("%------------------------- (ATS Friendly LaTeX Resume) -------------------------\n");
        sb.append("\\documentclass[letterpaper,11pt]{article}\n\n");
        sb.append("\\usepackage{latexsym}\n");
        sb.append("\\usepackage[empty]{fullpage}\n");
        sb.append("\\usepackage{titlesec}\n");
        sb.append("\\usepackage{marvosym}\n");
        sb.append("\\usepackage[usenames,dvipsnames]{color}\n");
        sb.append("\\usepackage{verbatim}\n");
        sb.append("\\usepackage{enumitem}\n");
        sb.append("\\usepackage[hidelinks]{hyperref}\n");
        sb.append("\\usepackage{fancyhdr}\n");
        sb.append("\\usepackage[english]{babel}\n");
        sb.append("\\usepackage{tabularx}\n\n");

        sb.append("\\pagestyle{fancy}\n");
        sb.append("\\fancyhf{}\n");
        sb.append("\\fancyfoot{}\n");
        sb.append("\\renewcommand{\\headrulewidth}{0pt}\n");
        sb.append("\\renewcommand{\\footrulewidth}{0pt}\n\n");

        sb.append("% Adjust margins\n");
        sb.append("\\addtolength{\\oddsidemargin}{-0.5in}\n");
        sb.append("\\addtolength{\\evensidemargin}{-0.5in}\n");
        sb.append("\\addtolength{\\textwidth}{1in}\n");
        sb.append("\\addtolength{\\topmargin}{-.5in}\n");
        sb.append("\\addtolength{\\textheight}{1.0in}\n\n");

        sb.append("\\urlstyle{same}\n\n");

        sb.append("\\raggedbottom\n");
        sb.append("\\raggedright\n");
        sb.append("\\setlength{\\tabcolsep}{0in}\n\n");

        sb.append("% Sections formatting\n");
        sb.append("\\titleformat{\\section}{\n");
        sb.append("  \\vspace{-4pt}\\scshape\\raggedright\\large\n");
        sb.append("}{}{0em}{}[\\color{black}\\titlerule \\vspace{-5pt}]\n\n");

        sb.append("%-------------------------\n");
        sb.append("\\begin{document}\n\n");

        // Heading
        sb.append("%----------HEADING----------\n");
        sb.append("\\begin{center}\n");
        sb.append("    {\\Huge \\scshape ").append(escapeLaTeX(name)).append("} \\\\\n");
        sb.append("    \\vspace{5pt}\n");
        sb.append("    \\small ").append(escapeLaTeX(phone)).append(" $|$ ");
        sb.append("\\href{mailto:").append(email).append("}{").append(escapeLaTeX(email)).append("}");

        if (linkedin != null && !linkedin.isEmpty()) {
            sb.append(" $|$ \\href{").append(linkedin).append("}{LinkedIn}");
        }
        if (github != null && !github.isEmpty()) {
            sb.append(" $|$ \\href{").append(github).append("}{GitHub}");
        }
        sb.append("\n\\end{center}\n\n");

        // Professional Summary
        if (summary != null && !summary.isEmpty()) {
            sb.append("%----------SUMMARY----------\n");
            sb.append("\\section{Professional Summary}\n");
            sb.append(escapeLaTeX(summary)).append("\n\n");
        }

        // Skills
        sb.append("%----------TECHNICAL SKILLS----------\n");
        sb.append("\\section{Technical Skills}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={}]\n");
        sb.append("    \\small{\\item{\n");
        sb.append("     \\textbf{Skills & Frameworks: }{");
        sb.append(skills.stream().map(this::escapeLaTeX).collect(Collectors.joining(", ")));
        sb.append("}\n");
        sb.append("    }}\n");
        sb.append("\\end{itemize}\n\n");

        // Education
        sb.append("%----------EDUCATION----------\n");
        sb.append("\\section{Education}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={}]\n");
        if (education != null && !education.isEmpty()) {
            for (String eduLine : education.split("\n")) {
                if (!eduLine.trim().isEmpty()) {
                    sb.append("    \\item \\small ").append(escapeLaTeX(eduLine.trim())).append(" \\\\\n");
                }
            }
        } else {
            sb.append("    \\item \\small \\textbf{Master of Computer Applications (MCA)} $|$ IT Specialization \\\\\n");
            sb.append("    Graduation Year: 2026 $|$ CGPA: 8.2 \\\\\n");
        }
        sb.append("\\end{itemize}\n\n");

        // Experience
        sb.append("%----------EXPERIENCE----------\n");
        sb.append("\\section{Experience / Internships}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={}]\n");
        if (experience != null && !experience.isEmpty()) {
            for (String expLine : experience.split("\n")) {
                if (!expLine.trim().isEmpty()) {
                    sb.append("    \\item \\small ").append(escapeLaTeX(expLine.trim())).append(" \\\\\n");
                }
            }
        } else {
            sb.append("    \\item \\small \\textbf{Software Engineering Intern} $|$ Tech Innovators \\\\\n");
            sb.append("    Assisted in writing Spring Boot APIs, optimized schema indexes which improved query fetch times by 20\\%. \\\\\n");
        }
        sb.append("\\end{itemize}\n\n");

        // Projects
        sb.append("%----------PROJECTS----------\n");
        sb.append("\\section{Projects}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={}]\n");
        if (projects != null && !projects.isEmpty()) {
            for (String projLine : projects.split("\n")) {
                if (!projLine.trim().isEmpty()) {
                    sb.append("    \\item \\small ").append(escapeLaTeX(projLine.trim())).append(" \\\\\n");
                }
            }
        } else {
            sb.append("    \\item \\small \\textbf{E-Commerce Backend Application} \\\\\n");
            sb.append("    Developed high-throughput API gateway with Spring Webflux and optimized MySQL connection pooling. \\\\\n");
        }
        sb.append("\\end{itemize}\n\n");

        sb.append("\\end{document}\n");

        return sb.toString();
    }

    private String escapeLaTeX(String text) {
        if (text == null) return "";
        return text.replace("&", "\\&")
                .replace("%", "\\%")
                .replace("$", "\\$")
                .replace("#", "\\#")
                .replace("_", "\\_")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("~", "\\textasciitilde")
                .replace("^", "\\textasciicircum");
    }
}
