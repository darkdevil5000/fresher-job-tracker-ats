package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoadmapService {

    public static class RoadmapItem {
        private String skillName;
        private String resourceTitle;
        private String resourceUrl;
        private String description;

        public RoadmapItem(String skillName, String resourceTitle, String resourceUrl, String description) {
            this.skillName = skillName;
            this.resourceTitle = resourceTitle;
            this.resourceUrl = resourceUrl;
            this.description = description;
        }

        // Getters
        public String getSkillName() { return skillName; }
        public String getResourceTitle() { return resourceTitle; }
        public String getResourceUrl() { return resourceUrl; }
        public String getDescription() { return description; }
    }

    private final Map<String, RoadmapItem> roadmapDb = new HashMap<>();

    public RoadmapService() {
        // Seed standard skills gap resources
        roadmapDb.put("java", new RoadmapItem("Java", "Java Programming Masterclass", "https://www.youtube.com/watch?v=A74TOX803D0", "Comprehensive course on core Java, OOPs, threads, and lambdas."));
        roadmapDb.put("spring boot", new RoadmapItem("Spring Boot", "Spring Boot Full Course", "https://www.youtube.com/watch?v=35EQXmHKZYs", "Learn Spring Boot, MVC, Dependency Injection, REST APIs, and JPA."));
        roadmapDb.put("hibernate", new RoadmapItem("Hibernate", "Hibernate & JPA Tutorial", "https://www.youtube.com/watch?v=JR7-ToDxSg0", "Master Object-Relational Mapping (ORM) and databases in Java."));
        roadmapDb.put("mysql", new RoadmapItem("MySQL", "SQL Database Course", "https://www.youtube.com/watch?v=HXV3zeQKqGY", "Learn SQL database designs, schemas, queries, joins, and indexing."));
        roadmapDb.put("react", new RoadmapItem("React", "ReactJS Tutorial for Beginners", "https://www.youtube.com/watch?v=Ke90Tje7VS0", "Understand React hooks, state management, components, and virtual DOM."));
        roadmapDb.put("docker", new RoadmapItem("Docker", "Docker & Containers Crash Course", "https://www.youtube.com/watch?v=3c-iKanjeVE", "Learn image building, dockerfiles, port mapping, and container deployment."));
        roadmapDb.put("git", new RoadmapItem("Git", "Git & GitHub Complete Course", "https://www.youtube.com/watch?v=apGV9Ad7XYY", "Master version control, branches, commits, merges, and pull requests."));
        roadmapDb.put("javascript", new RoadmapItem("JavaScript", "Modern JS Tutorial", "https://www.youtube.com/watch?v=hdI2bqOjy3c", "Learn ES6+ features, promises, async/await, and DOM APIs."));
        roadmapDb.put("python", new RoadmapItem("Python", "Python for Beginners", "https://www.youtube.com/watch?v=_uQrJ0TkZlc", "Complete programming concepts in Python from basics to scripts."));
    }

    public List<RoadmapItem> getRoadmapsForSkills(List<String> skills) {
        List<RoadmapItem> result = new ArrayList<>();
        if (skills == null) return result;

        for (String skill : skills) {
            String clean = skill.trim().toLowerCase();
            // Match substring (e.g. "jpa/hibernate" -> "hibernate")
            boolean matched = false;
            for (Map.Entry<String, RoadmapItem> entry : roadmapDb.entrySet()) {
                if (clean.contains(entry.getKey())) {
                    result.add(entry.getValue());
                    matched = true;
                    break;
                }
            }
            // Generic fallback if not found in database
            if (!matched) {
                result.add(new RoadmapItem(
                        skill,
                        "Learn " + skill + " on GeeksforGeeks",
                        "https://www.geeksforgeeks.org/" + skill.toLowerCase().replaceAll("[^a-z0-9]", "-") + "-tutorial/",
                        "Free comprehensive reference tutorials, guides, and example code on GeeksforGeeks."
                ));
            }
        }
        return result;
    }
}
