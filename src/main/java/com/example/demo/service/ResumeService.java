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
            String jobDescription, String templateStyle
    ) {
        OptimizationResult result = new OptimizationResult();

        // 1. Extract Keywords from Job Description
        Set<String> jdKeywords = new HashSet<>();
        String lowerJD = jobDescription.toLowerCase();
        for (String keyword : IT_KEYWORDS) {
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
            result.atsScore = 85;
        } else {
            result.atsScore = (int) (((double) matched.size() / jdKeywords.size()) * 100);
            if (result.atsScore < 35) result.atsScore = 38;
        }

        // Auto-optimize: Add missing skills to the generated list for LaTeX to boost score
        List<String> userSkills = Arrays.stream(skillsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<String> optimizedSkills = new ArrayList<>(userSkills);
        for (String mSkill : missing) {
            if (optimizedSkills.size() < 25) {
                optimizedSkills.add(mSkill);
            }
        }

        // Contextual AI Rewriter: dynamically append customized action clause to Projects using missing keywords
        String optimizedProjects = projectsStr;
        if (!missing.isEmpty()) {
            List<String> keywordsToInject = new ArrayList<>(missing);
            int injectCount = Math.min(3, keywordsToInject.size());
            List<String> injectList = keywordsToInject.subList(0, injectCount);
            
            String rewriteSentence = "\n* Applied industry best practices in " + String.join(", ", injectList) + 
                                     " to enhance scalable development, containerize dependencies, and optimize microservice pipeline integrations.";
            optimizedProjects = projectsStr + rewriteSentence;
        }

        // 4. Generate LaTeX Code based on Selected Template Style
        String style = (templateStyle == null) ? "classic" : templateStyle.toLowerCase();
        if (style.equals("modern")) {
            result.latexCode = generateModernLaTeX(name, email, phone, github, linkedin, summary, optimizedSkills, educationStr, experienceStr, optimizedProjects);
        } else if (style.equals("executive")) {
            result.latexCode = generateExecutiveLaTeX(name, email, phone, github, linkedin, summary, optimizedSkills, educationStr, experienceStr, optimizedProjects);
        } else {
            result.latexCode = generateClassicLaTeX(name, email, phone, github, linkedin, summary, optimizedSkills, educationStr, experienceStr, optimizedProjects);
        }

        return result;
    }

    private String generateClassicLaTeX(
            String name, String email, String phone, String github, String linkedin,
            String summary, List<String> skills, String education, String experience, String projects
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("%------------------------- (Classic Tech Template) -------------------------\n");
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

        sb.append("\\addtolength{\\oddsidemargin}{-0.5in}\n");
        sb.append("\\addtolength{\\evensidemargin}{-0.5in}\n");
        sb.append("\\addtolength{\\textwidth}{1in}\n");
        sb.append("\\addtolength{\\topmargin}{-.5in}\n");
        sb.append("\\addtolength{\\textheight}{1.0in}\n\n");

        sb.append("\\urlstyle{same}\n\n");
        sb.append("\\raggedbottom\n");
        sb.append("\\raggedright\n");
        sb.append("\\setlength{\\tabcolsep}{0in}\n\n");

        sb.append("\\titleformat{\\section}{\n");
        sb.append("  \\vspace{-4pt}\\scshape\\raggedright\\large\n");
        sb.append("}{}{0em}{}[\\color{black}\\titlerule \\vspace{-5pt}]\n\n");

        sb.append("\\begin{document}\n\n");

        // Header
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

        // Summary
        if (summary != null && !summary.isEmpty()) {
            sb.append("\\section{Professional Summary}\n");
            sb.append(escapeLaTeX(summary)).append("\n\n");
        }

        // Skills
        sb.append("\\section{Technical Skills}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={}]\n");
        sb.append("    \\small{\\item{\n");
        sb.append("     \\textbf{Skills & Frameworks: }{");
        sb.append(skills.stream().map(this::escapeLaTeX).collect(Collectors.joining(", ")));
        sb.append("}\n");
        sb.append("    }}\n");
        sb.append("\\end{itemize}\n\n");

        // Education
        sb.append("\\section{Education}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={}]\n");
        for (String eduLine : education.split("\n")) {
            if (!eduLine.trim().isEmpty()) {
                sb.append("    \\item \\small ").append(escapeLaTeX(eduLine.trim())).append(" \\\\\n");
            }
        }
        sb.append("\\end{itemize}\n\n");

        // Experience
        sb.append("\\section{Experience}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={}]\n");
        for (String expLine : experience.split("\n")) {
            if (!expLine.trim().isEmpty()) {
                sb.append("    \\item \\small ").append(escapeLaTeX(expLine.trim())).append(" \\\\\n");
            }
        }
        sb.append("\\end{itemize}\n\n");

        // Projects
        sb.append("\\section{Projects}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={}]\n");
        for (String projLine : projects.split("\n")) {
            if (!projLine.trim().isEmpty()) {
                sb.append("    \\item \\small ").append(escapeLaTeX(projLine.trim())).append(" \\\\\n");
            }
        }
        sb.append("\\end{itemize}\n\n");

        sb.append("\\end{document}\n");
        return sb.toString();
    }

    private String generateModernLaTeX(
            String name, String email, String phone, String github, String linkedin,
            String summary, List<String> skills, String education, String experience, String projects
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("%------------------------- (Modern Minimal Template) -------------------------\n");
        sb.append("\\documentclass[letterpaper,10pt]{article}\n\n");
        sb.append("\\usepackage[empty]{fullpage}\n");
        sb.append("\\usepackage{titlesec}\n");
        sb.append("\\usepackage{enumitem}\n");
        sb.append("\\usepackage[hidelinks]{hyperref}\n");
        sb.append("\\usepackage{charter}\n\n"); // Elegant font override

        sb.append("\\addtolength{\\oddsidemargin}{-0.5in}\n");
        sb.append("\\addtolength{\\evensidemargin}{-0.5in}\n");
        sb.append("\\addtolength{\\textwidth}{1in}\n");
        sb.append("\\addtolength{\\topmargin}{-.5in}\n");
        sb.append("\\addtolength{\\textheight}{1.0in}\n\n");

        sb.append("\\titleformat{\\section}{\n");
        sb.append("  \\vspace{3pt}\\bfseries\\large\\raggedright\n");
        sb.append("}{}{0em}{}[\\color{gray}\\titlerule \\vspace{-2pt}]\n\n");

        sb.append("\\begin{document}\n\n");

        // Centered Small Header
        sb.append("\\begin{center}\n");
        sb.append("    {\\Huge \\textbf{").append(escapeLaTeX(name)).append("}} \\\\\n");
        sb.append("    \\vspace{4pt}\n");
        sb.append("    \\small ").append(escapeLaTeX(phone)).append(" $\\cdot$ ");
        sb.append(escapeLaTeX(email));
        if (linkedin != null && !linkedin.isEmpty()) {
            sb.append(" $\\cdot$ \\href{").append(linkedin).append("}{LinkedIn}");
        }
        if (github != null && !github.isEmpty()) {
            sb.append(" $\\cdot$ \\href{").append(github).append("}{GitHub}");
        }
        sb.append("\n\\end{center}\n\\vspace{-10pt}\n\n");

        // Summary
        if (summary != null && !summary.isEmpty()) {
            sb.append("\\section{Summary}\n");
            sb.append(escapeLaTeX(summary)).append("\n\n");
        }

        // Skills
        sb.append("\\section{Skills}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={$\\circ$}]\n");
        sb.append("    \\item ").append(skills.stream().map(this::escapeLaTeX).collect(Collectors.joining(", "))).append("\n");
        sb.append("\\end{itemize}\n\n");

        // Education
        sb.append("\\section{Education}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={$\\bullet$}]\n");
        for (String eduLine : education.split("\n")) {
            if (!eduLine.trim().isEmpty()) {
                sb.append("    \\item ").append(escapeLaTeX(eduLine.trim())).append("\n");
            }
        }
        sb.append("\\end{itemize}\n\n");

        // Experience
        sb.append("\\section{Work Experience}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={$\\bullet$}]\n");
        for (String expLine : experience.split("\n")) {
            if (!expLine.trim().isEmpty()) {
                sb.append("    \\item ").append(escapeLaTeX(expLine.trim())).append("\n");
            }
        }
        sb.append("\\end{itemize}\n\n");

        // Projects
        sb.append("\\section{Projects}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={$\\bullet$}]\n");
        for (String projLine : projects.split("\n")) {
            if (!projLine.trim().isEmpty()) {
                sb.append("    \\item ").append(escapeLaTeX(projLine.trim())).append("\n");
            }
        }
        sb.append("\\end{itemize}\n\n");

        sb.append("\\end{document}\n");
        return sb.toString();
    }

    private String generateExecutiveLaTeX(
            String name, String email, String phone, String github, String linkedin,
            String summary, List<String> skills, String education, String experience, String projects
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("%------------------------- (Bold Executive Template) -------------------------\n");
        sb.append("\\documentclass[letterpaper,11pt]{article}\n\n");
        sb.append("\\usepackage[empty]{fullpage}\n");
        sb.append("\\usepackage{titlesec}\n");
        sb.append("\\usepackage{enumitem}\n");
        sb.append("\\usepackage[hidelinks]{hyperref}\n");
        sb.append("\\usepackage[usenames,dvipsnames]{color}\n");
        sb.append("\\usepackage{palatino}\n\n");

        sb.append("\\addtolength{\\oddsidemargin}{-0.4in}\n");
        sb.append("\\addtolength{\\evensidemargin}{-0.4in}\n");
        sb.append("\\addtolength{\\textwidth}{0.8in}\n");
        sb.append("\\addtolength{\\topmargin}{-.4in}\n");
        sb.append("\\addtolength{\\textheight}{0.8in}\n\n");

        sb.append("\\titleformat{\\section}{\n");
        sb.append("  \\vspace{5pt}\\bfseries\\scshape\\large\\color{MidnightBlue}\n");
        sb.append("}{}{0em}{}[\\color{MidnightBlue}\\titlerule \\vspace{-2pt}]\n\n");

        sb.append("\\begin{document}\n\n");

        // Left Aligned Elegant Executive Header
        sb.append("\\noindent\n");
        sb.append("\\begin{tabular*}{\\textwidth}{l@{\\extracolsep{\\fill}}r}\n");
        sb.append("  \\textbf{\\Huge ").append(escapeLaTeX(name)).append("} & ").append(escapeLaTeX(phone)).append(" \\\\\n");
        sb.append("  \\href{mailto:").append(email).append("}{").append(escapeLaTeX(email)).append("}");
        if (linkedin != null && !linkedin.isEmpty()) {
            sb.append(" $|$ \\href{").append(linkedin).append("}{LinkedIn}");
        }
        if (github != null && !github.isEmpty()) {
            sb.append(" & \\href{").append(github).append("}{GitHub}");
        }
        sb.append(" \\\\\n");
        sb.append("\\end{tabular*}\n\\vspace{10pt}\n\n");

        // Summary
        if (summary != null && !summary.isEmpty()) {
            sb.append("\\section{Executive Profile}\n");
            sb.append(escapeLaTeX(summary)).append("\n\n");
        }

        // Skills
        sb.append("\\section{Core Competencies}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={$-$}]\n");
        sb.append("    \\item \\textbf{Technical Stack: }{");
        sb.append(skills.stream().map(this::escapeLaTeX).collect(Collectors.joining(", ")));
        sb.append("}\n");
        sb.append("\\end{itemize}\n\n");

        // Experience
        sb.append("\\section{Professional Track}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={$\\star$}]\n");
        for (String expLine : experience.split("\n")) {
            if (!expLine.trim().isEmpty()) {
                sb.append("    \\item ").append(escapeLaTeX(expLine.trim())).append("\n");
            }
        }
        sb.append("\\end{itemize}\n\n");

        // Projects
        sb.append("\\section{Selected Projects}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={$\\star$}]\n");
        for (String projLine : projects.split("\n")) {
            if (!projLine.trim().isEmpty()) {
                sb.append("    \\item ").append(escapeLaTeX(projLine.trim())).append("\n");
            }
        }
        sb.append("\\end{itemize}\n\n");

        // Education
        sb.append("\\section{Academic background}\n");
        sb.append("\\begin{itemize}[leftmargin=0.15in, label={$-$}]\n");
        for (String eduLine : education.split("\n")) {
            if (!eduLine.trim().isEmpty()) {
                sb.append("    \\item ").append(escapeLaTeX(eduLine.trim())).append("\n");
            }
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
