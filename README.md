# Freshers IT Job Portal & ATS Resume Builder

An automated, multi-user Spring Boot platform that crawls fresher IT jobs across India, calculates ATS keyword matching metrics, generates optimized LaTeX resumes, and provides an interactive CRM pipeline tracker and interview preparation hub.

## 🚀 Key Features
1. **Automated Daily Crawls:** Scans official career pages and top ATS host portals (`lever.co`, `greenhouse.io`, `myworkdayjobs.com`) across Pune, Bangalore, Mumbai, Chennai, and Hyderabad.
2. **Multi-User Profiles:** Sign up with your personal portfolio website (e.g. `ankursportfolio.engineer`). The system dynamically scrapes your portfolio elements to construct customized resumes.
3. **ATS Keyword Scoring & Contextual Rewriter:** Analyzes job descriptions, computes compatibility metrics, and dynamically auto-injects missing tech stack terminology to maximize ATS score matching.
4. **CRM Kanban Application Tracker:** Manage your search pipeline through four clean categories: *Saved*, *Applied*, *Resume Viewed*, and *Selected/Rejected*.
5. **Skill Gap Roadmaps:** Displays free high-quality learning resources and crash courses for detected technical skill gaps.
6. **Interview Prep Hub:** Access 10 role-specific, category-based developer questions and answers tailored to each job description.
7. **Email Digest Alerts:** Dispatches daily HTML newsletter updates of new job openings matching your profile to your phone via email.

---

## ⚙️ How to Run Locally
1. Clone the repository and navigate to the project directory:
   ```bash
   git clone https://github.com/darkdevil5000/fresher-job-tracker-ats.git
   cd fresher-job-tracker-ats
   ```
2. Build and launch the Spring Boot application:
   ```bash
   ./gradlew bootRun
   ```
3. Open your browser and navigate to: **[http://localhost:8080](http://localhost:8080)**

---

## ☁️ Deployment Instructions (Render & Railway)
This project is packaged with container blueprints (`Dockerfile` and `render.yaml`) for one-click cloud hosting.

### Deploy to Render
1. Push your repository modifications.
2. Link your GitHub account to [Render](https://render.com) and deploy using the Blueprint configuration.
3. Add the following environment variables under settings if you wish to activate active daily email dispatches:
   * `PORT = 8080`
   * `SPRING_MAIL_HOST = smtp.gmail.com`
   * `SPRING_MAIL_PORT = 587`
   * `SPRING_MAIL_USERNAME = your-email@gmail.com`
   * `SPRING_MAIL_PASSWORD = your-google-app-password`
