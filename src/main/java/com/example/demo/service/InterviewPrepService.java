package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class InterviewPrepService {

    public static class QuestionItem {
        private String category;
        private String question;
        private String answer;

        public QuestionItem(String category, String question, String answer) {
            this.category = category;
            this.question = question;
            this.answer = answer;
        }

        // Getters
        public String getCategory() { return category; }
        public String getQuestion() { return question; }
        public String getAnswer() { return answer; }
    }

    public List<QuestionItem> generateInterviewPrep(String jobTitle, String jobDescription) {
        List<QuestionItem> qas = new ArrayList<>();
        String title = (jobTitle == null) ? "" : jobTitle.toLowerCase();
        String desc = (jobDescription == null) ? "" : jobDescription.toLowerCase();

        // 1. Core Java (Standard for junior IT roles)
        qas.add(new QuestionItem(
                "Core Java",
                "What is the difference between an Abstract Class and an Interface in Java 8 and beyond?",
                "An Abstract Class can have state variables and instance constructors, whereas an Interface cannot hold instance state (only constants). Post Java 8, interfaces support 'default' and 'static' methods, and Java 9 added 'private' methods. You can implement multiple interfaces, but extend only one class."
        ));

        qas.add(new QuestionItem(
                "OOPs Concepts",
                "How does Runtime Polymorphism work in Java?",
                "Runtime polymorphism (Method Overriding) is resolved at runtime by JVM through dynamic binding. When a parent reference points to a child class object, and an overridden method is called, the JVM looks up the object's actual class type in the method table (vtable) and executes the child's implementation."
        ));

        // 2. Spring Boot / Backend (If description mentions spring/apis)
        if (desc.contains("spring") || desc.contains("api") || desc.contains("backend") || desc.contains("mvc") || desc.contains("rest")) {
            qas.add(new QuestionItem(
                    "Spring Framework",
                    "Explain the Spring Bean Lifecycle.",
                    "A Spring Bean's lifecycle is managed by the IoC Container: 1. Instantiation. 2. Populate Properties (Dependency Injection). 3. Name/Factory Aware interfaces. 4. Pre-initialization BeanPostProcessors. 5. Custom init method (or @PostConstruct). 6. Ready for use. 7. DisposableBean destroy method (or @PreDestroy) during context shutdown."
            ));
            qas.add(new QuestionItem(
                    "REST APIs",
                    "What are the main HTTP Status Codes, and when is PUT used vs PATCH?",
                    "HTTP Status codes: 200 OK (Success), 201 Created, 400 Bad Request, 401 Unauthorized, 404 Not Found, 500 Server Error. PUT is idempotent and replaces the entire resource representation, whereas PATCH applies a partial update to the resource."
            ));
        }

        // 3. Database / SQL (If SQL/DB mentioned)
        if (desc.contains("sql") || desc.contains("database") || desc.contains("mysql") || desc.contains("query") || desc.contains("hibernate") || desc.contains("jpa")) {
            qas.add(new QuestionItem(
                    "Databases & ORM",
                    "What is the N+1 select problem in Hibernate, and how do you resolve it?",
                    "The N+1 problem occurs when Hibernate executes 1 query to fetch parent records, and then executes N additional individual queries to fetch associated child records for each parent. It is resolved using: 1. 'join fetch' inside JPQL/HQL queries. 2. Defining an @EntityGraph. 3. Specifying a batch size override."
            ));
            qas.add(new QuestionItem(
                    "SQL",
                    "What are Database Indexes, and what is the trade-off of using them?",
                    "Indexes are data structures (like B-Trees) that speed up search query select speeds by avoiding full table scans. The trade-off is that they consume extra storage space and degrade write speeds (INSERT, UPDATE, DELETE) since the index must be recalculated."
            ));
        }

        // 4. Web Development / Javascript / React (If frontend mentioned)
        if (desc.contains("react") || desc.contains("javascript") || desc.contains("html") || desc.contains("css") || desc.contains("frontend") || desc.contains("ui")) {
            qas.add(new QuestionItem(
                    "Frontend (React)",
                    "What is the Virtual DOM in React, and how does Reconciliation work?",
                    "The Virtual DOM is a lightweight memory representation of the real DOM. When state changes, React builds a new virtual representation, compares it with the previous one (diffing algorithm), and batch-updates only the modified nodes in the real browser DOM (Reconciliation)."
            ));
            qas.add(new QuestionItem(
                    "JavaScript",
                    "Explain Event Delegation and why it is useful.",
                    "Event Delegation is a pattern where a single event listener is attached to a parent element rather than individual child elements. It leverages Event Bubbling: events trigger on children and bubble up to the parent. This saves memory and works automatically for dynamically added elements."
            ));
        }

        // 5. Data Structures & Algorithms / Problem Solving (Generic junior evaluation)
        qas.add(new QuestionItem(
                "Algorithms",
                "What is the time complexity of searching and inserting in a HashMap, and how are collisions resolved?",
                "HashMap offers O(1) average time complexity for both search and insertion. In Java 8+, collisions are resolved using a linked list inside bucket slots. If a bucket's elements exceed 8 (TREEIFY_THRESHOLD), it converts the linked list to a balanced Red-Black Tree, improving collision lookup speed to O(log N)."
        ));

        qas.add(new QuestionItem(
                "System Architecture",
                "What is the difference between Monolithic and Microservice Architectures?",
                "A Monolithic architecture houses all application components (UI, business logic, DB access) inside a single deployable unit, which is simple but hard to scale. A Microservices architecture breaks the application into small, independent, self-contained services communicating via lightweight APIs, allowing independent scaling but increasing network complexity."
        ));

        // Ensure we always have at least 6 standard questions if no keyword filters match
        while (qas.size() < 7) {
            qas.add(new QuestionItem(
                    "Software Engineering",
                    "What is Git Rebase vs Git Merge?",
                    "Git Merge merges branches by creating a new merge commit, preserving historical timeline structures. Git Rebase moves or reapplies the entire commits history onto another base branch, creating a completely flat and linear commit log."
            ));
        }

        return qas;
    }
}
