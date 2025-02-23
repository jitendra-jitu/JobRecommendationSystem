package com.techstart.jobportal.service;

import com.techstart.jobportal.dto.JobDTO;
import com.techstart.jobportal.dto.CommentDTO;
import com.techstart.jobportal.dto.ExperienceRequiredDTO;
import com.techstart.jobportal.model.ExperienceRequired;
import com.techstart.jobportal.model.Job;
import com.techstart.jobportal.model.Comment;
import com.techstart.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    private static final List<Job> SAMPLE_JOBS = List.of(
            new Job(
                    "Junior Frontend Developer",
                    "TechStart Inc.",
                    "Remote",
                    "Full-time",
                    "fresher",
                    "Perfect opportunity for fresh graduates to start their career in frontend development with React and TypeScript.",
                    System.currentTimeMillis() - 24 * 60 * 60 * 1000, // 1 day ago
                    500000L, // Salary in long format
                    List.of("React", "HTML", "CSS", "JavaScript"),
                    new ExperienceRequired(0),
                    List.of(
                            new Comment("Great opportunity for freshers!", "John Doe", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("Perfect for those starting in web development.", "Jane Smith", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    120
            ),
            new Job(
                    "Junior Backend Developer",
                    "DevTech Solutions",
                    "Remote",
                    "Full-time",
                    "fresher",
                    "Exciting opportunity for fresh graduates to start a career in backend development using Java and Spring Boot.",
                    System.currentTimeMillis() - 48 * 60 * 60 * 1000, // 2 days ago
                    600000L, // Salary in long format
                    List.of("Java", "Spring Boot", "SQL"),
                    new ExperienceRequired(0),
                    List.of(
                            new Comment("A great chance for beginners in backend development!", "Alice Green", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("Perfect opportunity to learn and grow!", "Daniel White", System.currentTimeMillis() - 36 * 60 * 60 * 1000)
                    ),
                    85
            ),
            new Job(
                    "Senior Frontend Developer",
                    "TechSolutions Ltd.",
                    "Remote",
                    "Full-time",
                    "experienced",
                    "Exciting opportunity for experienced frontend developers to work on challenging projects using Angular and TypeScript.",
                    System.currentTimeMillis() - 72 * 60 * 60 * 1000, // 3 days ago
                    100000L, // Salary in long format
                    List.of("Angular", "TypeScript", "HTML", "CSS"),
                    new ExperienceRequired(2),
                    List.of(
                            new Comment("Great opportunity for experienced developers!", "David Brown", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("Perfect for those looking for a challenging role.", "Emily Davis", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    45
            ),
            new Job(
                    "React Developer",
                    "InnovateTech Pvt. Ltd.",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Seeking an experienced React Developer to build and optimize high-performance web applications.",
                    System.currentTimeMillis() - 96 * 60 * 60 * 1000, // 4 days ago
                    1200000L,
                    List.of("React", "JavaScript", "Redux", "CSS"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("React is the future! Great role!", "Amit Verma", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Would love to apply for this.", "Neha Sharma", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    50
            ),

            new Job(
                    "Vue.js Developer",
                    "NextGen Solutions",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Exciting opportunity for Vue.js developers to work on dynamic web projects.",
                    System.currentTimeMillis() - 120 * 60 * 60 * 1000, // 5 days ago
                    110000L,
                    List.of("Vue.js", "JavaScript", "Nuxt.js", "SCSS"),
                    new ExperienceRequired(2),
                    List.of(
                            new Comment("Vue.js developers are in high demand!", "Rahul Mehta", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("This role looks promising!", "Priya Patel", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    42
            ),

            new Job(
                    "Full Stack Developer",
                    "Digital Innovations",
                    "Pune, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Looking for a skilled full-stack developer with expertise in React and Node.js.",
                    System.currentTimeMillis() - 168 * 60 * 60 * 1000, // 7 days ago
                    1400000L,
                    List.of("React", "Node.js", "Express", "MongoDB"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Full-stack roles are always exciting!", "Siddharth Rao", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Love the tech stack for this job.", "Anjali Nair", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    60
            ),

            new Job(
                    "Frontend Engineer",
                    "SmartTech Labs",
                    "Remote",
                    "Full-time",
                    "experienced",
                    "Join our dynamic team and work on cutting-edge frontend projects with Next.js.",
                    System.currentTimeMillis() - 48 * 60 * 60 * 1000, // 2 days ago
                    1050000L,
                    List.of("Next.js", "React", "Tailwind CSS", "TypeScript"),
                    new ExperienceRequired(2),
                    List.of(
                            new Comment("Next.js is amazing for frontend!", "Mohit Kapoor", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("Excited to see more Next.js jobs!", "Sara Khan", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    38
            ),

            new Job(
                    "UI Developer",
                    "PixelPerfect Solutions",
                    "Chennai, Tamil Nadu",
                    "Full-time",
                    "experienced",
                    "Seeking a UI Developer with expertise in Figma and frontend technologies to create beautiful designs.",
                    System.currentTimeMillis() - 144 * 60 * 60 * 1000, // 6 days ago
                    950000L,
                    List.of("HTML", "CSS", "JavaScript", "Figma"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("UI design is a critical part of frontend!", "Vivek Sharma", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Great for those passionate about design.", "Kavita Joshi", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    33
            ),
            new Job(
                    "Site Engineer",
                    "Larsen & Toubro (L&T)",
                    "Mumbai, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Supervise and execute construction activities as per design specifications.",
                    System.currentTimeMillis() - 300 * 60 * 60 * 1000, // 12.5 days ago
                    700000L, // Salary in INR
                    List.of("AutoCAD", "Structural Engineering", "Project Management", "Construction Supervision"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("L&T is a great company for civil engineers!", "Rohan Sharma", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Exciting project opportunities.", "Pooja Desai", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    6
            ),

            new Job(
                    "Structural Engineer",
                    "Tata Consulting Engineers",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Analyze and design structural components for commercial and residential projects.",
                    System.currentTimeMillis() - 180 * 60 * 60 * 1000, // 7.5 days ago
                    900000L, // Salary in INR
                    List.of("STAAD Pro", "ETABS", "Revit", "Structural Analysis"),
                    new ExperienceRequired(5),
                    List.of(
                            new Comment("Tata is known for quality engineering.", "Arjun Nair", System.currentTimeMillis() - 36 * 60 * 60 * 1000),
                            new Comment("Structural engineers are always in demand.", "Ananya Singh", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    6
            ),

            new Job(
                    "Quality Control Engineer",
                    "Shapoorji Pallonji Group",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Ensure quality control and compliance with industry standards.",
                    System.currentTimeMillis() - 240 * 60 * 60 * 1000, // 10 days ago
                    750000L, // Salary in INR
                    List.of("Quality Inspection", "ISO 9001", "Material Testing", "Concrete Technology"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Good role for those into quality assurance.", "Vikas Rao", System.currentTimeMillis() - 60 * 60 * 60 * 1000),
                            new Comment("Shapoorji Pallonji has iconic projects!", "Deepika Sharma", System.currentTimeMillis() - 30 * 60 * 60 * 1000)
                    ),
                    6
            ),

            new Job(
                    "Highway Design Engineer",
                    "GMR Group",
                    "Delhi NCR",
                    "Full-time",
                    "experienced",
                    "Design and develop national and state highway projects.",
                    System.currentTimeMillis() - 120 * 60 * 60 * 1000, // 5 days ago
                    850000L, // Salary in INR
                    List.of("Civil 3D", "MX Road", "Highway Engineering", "Pavement Design"),
                    new ExperienceRequired(6),
                    List.of(
                            new Comment("Highway projects are booming in India!", "Rajiv Menon", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Amazing infrastructure development in Delhi NCR.", "Nisha Jain", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    6
            ),

            new Job(
                    "Construction Project Manager",
                    "NBCC (India) Limited",
                    "Kolkata, West Bengal",
                    "Full-time",
                    "experienced",
                    "Manage and oversee large-scale construction projects, ensuring timely execution.",
                    System.currentTimeMillis() - 360 * 60 * 60 * 1000, // 15 days ago
                    1000000L, // Salary in INR
                    List.of("Project Planning", "Primavera", "Cost Estimation", "Contract Management"),
                    new ExperienceRequired(8),
                    List.of(
                            new Comment("NBCC is handling some major government projects.", "Sandeep Yadav", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Great salary for experienced project managers!", "Madhuri Reddy", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    6
            ),
            new Job(
                    "Mechanical Design Engineer",
                    "Tata Motors",
                    "Pune, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Design and develop automotive components ensuring efficiency and safety.",
                    System.currentTimeMillis() - 300 * 60 * 60 * 1000, // 12.5 days ago
                    900000L, // Salary in INR
                    List.of("AutoCAD", "SolidWorks", "GD&T", "Sheet Metal Design"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Good opportunity at Tata Motors!", "Rahul Sharma", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("SolidWorks is a must-have skill.", "Priya Verma", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    6 // Number of comments * 3
            ),

            new Job(
                    "Production Engineer",
                    "Mahindra & Mahindra",
                    "Chennai, Tamil Nadu",
                    "Full-time",
                    "fresher",
                    "Monitor and optimize production processes in automobile manufacturing.",
                    System.currentTimeMillis() - 180 * 60 * 60 * 1000, // 7.5 days ago
                    600000L, // Salary in INR
                    List.of("Lean Manufacturing", "Six Sigma", "Process Improvement", "PLC"),
                    new ExperienceRequired(0),
                    List.of(
                            new Comment("Mahindra has a strong production setup.", "Amit Das", System.currentTimeMillis() - 36 * 60 * 60 * 1000),
                            new Comment("Freshers can apply, nice!", "Sneha Nair", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    6
            ),

            new Job(
                    "Quality Control Engineer",
                    "Larsen & Toubro",
                    "Mumbai, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Ensure compliance with quality standards in manufacturing.",
                    System.currentTimeMillis() - 240 * 60 * 60 * 1000, // 10 days ago
                    750000L, // Salary in INR
                    List.of("Quality Assurance", "ISO 9001", "Non-Destructive Testing", "GD&T"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("L&T is great for mechanical engineers.", "Vikram Rao", System.currentTimeMillis() - 60 * 60 * 60 * 1000),
                            new Comment("Quality control roles are in demand.", "Deepika Joshi", System.currentTimeMillis() - 30 * 60 * 60 * 1000)
                    ),
                    6
            ),

            new Job(
                    "HVAC Engineer",
                    "Voltas Limited",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Design and maintain HVAC systems for commercial and industrial buildings.",
                    System.currentTimeMillis() - 120 * 60 * 60 * 1000, // 5 days ago
                    850000L, // Salary in INR
                    List.of("HVAC Design", "Load Calculation", "Ducting", "Refrigeration"),
                    new ExperienceRequired(5),
                    List.of(
                            new Comment("Voltas is a leader in HVAC!", "Rajesh Patel", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Great pay for HVAC engineers.", "Neha Sinha", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    6
            ),

            new Job(
                    "Maintenance Engineer",
                    "BHEL",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Maintain and troubleshoot heavy machinery used in power plants.",
                    System.currentTimeMillis() - 360 * 60 * 60 * 1000, // 15 days ago
                    800000L, // Salary in INR
                    List.of("Maintenance Planning", "Mechanical Troubleshooting", "Thermal Power", "Vibration Analysis"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("BHEL is a dream company for many.", "Sandeep Reddy", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Good opportunity for experienced engineers.", "Manisha Kulkarni", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    6
            ),
            new Job(
                    "Angular Developer",
                    "TechMahindra",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Looking for an Angular Developer to build and maintain scalable enterprise applications.",
                    System.currentTimeMillis() - 48 * 60 * 60 * 1000, // 2 days ago
                    115000L,
                    List.of("Angular", "TypeScript", "RxJS", "SCSS"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Angular is a great choice for enterprise apps!", "Rajesh Kumar", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("Looks like an amazing opportunity!", "Sneha Verma", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    40
            ),

            new Job(
                    "Frontend Developer",
                    "Wipro",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Seeking a frontend developer with expertise in React and Redux for dynamic web applications.",
                    System.currentTimeMillis() - 96 * 60 * 60 * 1000, // 4 days ago
                    125000L,
                    List.of("React", "Redux", "JavaScript", "Bootstrap"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("React and Redux are the perfect combo!", "Arun Nair", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Exciting role for frontend devs.", "Meena Joshi", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    55
            ),

            new Job(
                    "JavaScript Engineer",
                    "TCS",
                    "Pune, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Hiring JavaScript engineers to work on high-performance, scalable applications.",
                    System.currentTimeMillis() - 120 * 60 * 60 * 1000, // 5 days ago
                    110000L,
                    List.of("JavaScript", "ES6", "Webpack", "CSS"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Love working with modern JavaScript!", "Gautam Sharma", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Great role for JS enthusiasts!", "Pooja Reddy", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    37
            ),

            new Job(
                    "UI/UX Developer",
                    "Cognizant",
                    "Chennai, Tamil Nadu",
                    "Full-time",
                    "experienced",
                    "Exciting opportunity for a UI/UX Developer with strong frontend design and implementation skills.",
                    System.currentTimeMillis() - 144 * 60 * 60 * 1000, // 6 days ago
                    102000L,
                    List.of("UI/UX", "HTML5", "CSS3", "Adobe XD", "Figma"),
                    new ExperienceRequired(2),
                    List.of(
                            new Comment("Great for those passionate about UI/UX!", "Karan Patel", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Love designing user-friendly interfaces!", "Nidhi Kapoor", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    30
            ),

            new Job(
                    "Next.js Developer",
                    "Infosys",
                    "Remote",
                    "Full-time",
                    "experienced",
                    "Looking for a Next.js Developer to build high-performance web applications.",
                    System.currentTimeMillis() - 168 * 60 * 60 * 1000, // 7 days ago
                    118000L,
                    List.of("Next.js", "React", "Tailwind CSS", "TypeScript"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Next.js is a game changer!", "Vishal Singh", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Perfect role for Next.js developers!", "Deepa Narayan", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    42
            ),
            new Job(
                    "Data Scientist",
                    "TCS",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Looking for a Data Scientist to analyze complex datasets and develop predictive models.",
                    System.currentTimeMillis() - 96 * 60 * 60 * 1000, // 4 days ago
                    140000L,
                    List.of("Python", "Machine Learning", "Pandas", "SQL", "TensorFlow"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("TCS offers great exposure in AI projects!", "Ravi Sharma", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Exciting opportunity for data enthusiasts.", "Neha Agarwal", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    60
            ),

            new Job(
                    "Machine Learning Engineer",
                    "Infosys",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Hiring an ML Engineer to build, optimize, and deploy ML models at scale.",
                    System.currentTimeMillis() - 120 * 60 * 60 * 1000, // 5 days ago
                    135000L,
                    List.of("Python", "Deep Learning", "PyTorch", "Big Data", "AWS"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Perfect for those skilled in deep learning!", "Amit Verma", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Great opportunity to work with large-scale ML models.", "Shruti Iyer", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    55
            ),

            new Job(
                    "AI Research Engineer",
                    "Wipro",
                    "Pune, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Join Wipro's AI team to research and develop cutting-edge AI solutions.",
                    System.currentTimeMillis() - 144 * 60 * 60 * 1000, // 6 days ago
                    150000L,
                    List.of("AI", "NLP", "Computer Vision", "Python", "Cloud Computing"),
                    new ExperienceRequired(5),
                    List.of(
                            new Comment("Amazing AI research projects at Wipro!", "Vikram Singh", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Looking forward to joining an AI-driven team.", "Megha Reddy", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    50
            ),

            new Job(
                    "Big Data Engineer",
                    "Cognizant",
                    "Chennai, Tamil Nadu",
                    "Full-time",
                    "experienced",
                    "Seeking a Big Data Engineer to work on high-volume data pipelines and analytics.",
                    System.currentTimeMillis() - 168 * 60 * 60 * 1000, // 7 days ago
                    130000L,
                    List.of("Big Data", "Hadoop", "Spark", "Kafka", "Scala"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Big Data roles are in huge demand!", "Arjun Patel", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Excited about the tech stack used here.", "Pooja Mehta", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    48
            ),

            new Job(
                    "AI Solutions Architect",
                    "HCL Technologies",
                    "Remote",
                    "Full-time",
                    "experienced",
                    "We are looking for an AI Solutions Architect to design and deploy AI-based business solutions.",
                    System.currentTimeMillis() - 192 * 60 * 60 * 1000, // 8 days ago
                    160000L,
                    List.of("AI", "Deep Learning", "Cloud AI Services", "Python", "MLOps"),
                    new ExperienceRequired(6),
                    List.of(
                            new Comment("AI architecture is the future of enterprise tech!", "Rahul Desai", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Amazing role for AI experts!", "Swetha Narayan", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    45
            ),
            new Job(
                    "Big Data Engineer",
                    "TCS",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Looking for a Big Data Engineer to manage large-scale data pipelines and real-time analytics.",
                    System.currentTimeMillis() - 96 * 60 * 60 * 1000, // 4 days ago
                    145000L,
                    List.of("Hadoop", "Spark", "Kafka", "Python", "AWS"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("TCS is a great place for Big Data engineers!", "Vikram Yadav", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Exciting opportunity to work with large-scale data.", "Ananya Rao", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    7800
            ),

            new Job(
                    "Software Test Engineer",
                    "Infosys",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Hiring a Software Test Engineer to work on automation frameworks and manual testing.",
                    System.currentTimeMillis() - 120 * 60 * 60 * 1000, // 5 days ago
                    120000L,
                    List.of("Selenium", "JUnit", "TestNG", "Java", "API Testing"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Infosys has a solid testing team!", "Rohan Singh", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Perfect role for automation testers!", "Kavita Iyer", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    9500
            ),

            new Job(
                    "Big Data Architect",
                    "Wipro",
                    "Chennai, Tamil Nadu",
                    "Full-time",
                    "experienced",
                    "Seeking a Big Data Architect to design and implement scalable data solutions for enterprise applications.",
                    System.currentTimeMillis() - 168 * 60 * 60 * 1000, // 7 days ago
                    175000L,
                    List.of("Big Data", "Cloud Computing", "Data Engineering", "ETL", "Hadoop"),
                    new ExperienceRequired(6),
                    List.of(
                            new Comment("Wipro has amazing projects in Big Data!", "Suresh Reddy", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("A dream job for Big Data professionals!", "Megha Patel", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    10200
            ),
            new Job(
                    "Quantum Computing Engineer",
                    "IBM Research",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Work on cutting-edge quantum computing algorithms and optimization techniques.",
                    System.currentTimeMillis() - 240 * 60 * 60 * 1000, // 10 days ago
                    180000L,
                    List.of("Quantum Computing", "Qiskit", "Python", "Quantum Algorithms"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Super niche field!", "Rajesh Verma", System.currentTimeMillis() - 72 * 60 * 60 * 1000)
                    ),
                    2
            ),

            new Job(
                    "Blockchain Security Analyst",
                    "CoinSecure",
                    "Mumbai, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Responsible for securing blockchain networks and developing smart contract audits.",
                    System.currentTimeMillis() - 168 * 60 * 60 * 1000, // 7 days ago
                    1600000L,
                    List.of("Blockchain", "Cybersecurity", "Smart Contracts", "Ethereum"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("This is the future of security!", "Anil Kumar", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    5
            ),

            new Job(
                    "RFID Systems Engineer",
                    "TagTech Solutions",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Design and deploy RFID tracking systems for supply chain automation.",
                    System.currentTimeMillis() - 120 * 60 * 60 * 1000, // 5 days ago
                    3350000L,
                    List.of("RFID", "Embedded Systems", "IoT", "Python"),
                    new ExperienceRequired(2),
                    List.of(
                            new Comment("RFID is a rare but growing field!", "Sunita Rao", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    3
            ),

            new Job(
                    "Bioinformatics Engineer",
                    "Genomics AI",
                    "Pune, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Develop AI-driven genomic data analysis models for medical research.",
                    System.currentTimeMillis() - 72 * 60 * 60 * 1000, // 3 days ago
                    3500000L,
                    List.of("Bioinformatics", "Machine Learning", "Python", "Genetic Algorithms"),
                    new ExperienceRequired(5),
                    List.of(
                            new Comment("Amazing fusion of AI and biology!", "Dr. Mehta", System.currentTimeMillis() - 36 * 60 * 60 * 1000)
                    ),
                    1
            ),

            new Job(
                    "Holographic Display Engineer",
                    "VisionTech Innovations",
                    "Delhi, India",
                    "Full-time",
                    "experienced",
                    "Research and develop next-gen holographic display systems for AR/VR applications.",
                    System.currentTimeMillis() - 96 * 60 * 60 * 1000, // 4 days ago
                    6650000L,
                    List.of("Holography", "3D Rendering", "Computer Vision", "C++"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("This is next-level AR tech!", "Vikas Gupta", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    4
            ),
            new Job(
                    "AI Research Scientist",
                    "Google DeepMind",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Join the team driving cutting-edge AI research to develop new algorithms and improve AI capabilities.",
                    System.currentTimeMillis() - 72 * 60 * 60 * 1000, // 3 days ago
                    9500000L,
                    List.of("Artificial Intelligence", "Deep Learning", "TensorFlow", "Python"),
                    new ExperienceRequired(5),
                    List.of(
                            new Comment("DeepMind is leading the AI revolution!", "Priya Sharma", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("A dream job for AI enthusiasts!", "Sandeep Reddy", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    35000
            ),

            new Job(
                    "Blockchain Developer",
                    "Ethereum Foundation",
                    "Remote",
                    "Full-time",
                    "experienced",
                    "Work on the core blockchain protocol and smart contracts for Ethereum.",
                    System.currentTimeMillis() - 48 * 60 * 60 * 1000, // 2 days ago
                    2100000L,
                    List.of("Blockchain", "Ethereum", "Solidity", "Smart Contracts"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Blockchain is the future of tech!", "Mohammed Ali", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("This is the perfect job for blockchain developers!", "Ritu Verma", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    50000
            ),

            new Job(
                    "Cloud Security Architect",
                    "Amazon Web Services (AWS)",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Design and implement security solutions for AWS Cloud services to safeguard customer data.",
                    System.currentTimeMillis() - 96 * 60 * 60 * 1000, // 4 days ago
                    2300000L,
                    List.of("Cloud Computing", "AWS", "Security Architecture", "Cybersecurity"),
                    new ExperienceRequired(6),
                    List.of(
                            new Comment("AWS is the leader in cloud tech!", "Suresh Patel", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Amazing job for cloud security experts!", "Neha Singh", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    45000
            ),

            new Job(
                    "Full Stack Web Developer (React & Node.js)",
                    "Microsoft",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Develop scalable, high-performance web applications using React, Node.js, and AWS.",
                    System.currentTimeMillis() - 120 * 60 * 60 * 1000, // 5 days ago
                    2200000L,
                    List.of("React", "Node.js", "AWS", "JavaScript", "Full Stack Development"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Working at Microsoft is a game-changer!", "Ankur Gupta", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("React and Node.js are the future of web development!", "Ayesha Khan", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    70000
            ),

            new Job(
                    "Data Scientist (AI & ML)",
                    "Tesla",
                    "Pune, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Use machine learning to analyze data and improve Tesla's AI-driven technologies and autonomous systems.",
                    System.currentTimeMillis() - 48 * 60 * 60 * 1000, // 2 days ago
                    2500000L,
                    List.of("Machine Learning", "AI", "Data Science", "Python", "Deep Learning"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Tesla is shaping the future of AI and automotive!", "Arjun Mehta", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("Amazing opportunity in AI at Tesla!", "Suman Reddy", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    85000
            ),
            new Job(
                    "AI Engineer",
                    "Meta",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Develop and optimize AI models to enhance user experience and content recommendations.",
                    System.currentTimeMillis() - 48 * 60 * 60 * 1000, // 2 days ago
                    250000L, // Salary adjusted for AI demand
                    List.of("Artificial Intelligence", "Machine Learning", "Python", "TensorFlow"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Amazing opportunity at Meta!", "Raj Kumar", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("AI engineers wanted here!", "Neha Sharma", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    25000
            ),

            new Job(
                    "Cloud Architect",
                    "IBM",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Design and implement scalable cloud architectures using IBM Cloud services.",
                    System.currentTimeMillis() - 72 * 60 * 60 * 1000, // 3 days ago
                    220000L, // Salary adjusted for Cloud computing demand
                    List.of("Cloud Computing", "IBM Cloud", "AWS", "Architecture Design"),
                    new ExperienceRequired(6),
                    List.of(
                            new Comment("Perfect for cloud architects!", "Madhuri Verma", System.currentTimeMillis() - 36 * 60 * 60 * 1000),
                            new Comment("IBM cloud is a top choice for tech professionals!", "Suman Reddy", System.currentTimeMillis() - 18 * 60 * 60 * 1000)
                    ),
                    30000
            ),

            new Job(
                    "Full Stack Developer (React, Node.js)",
                    "Accenture",
                    "Chennai, Tamil Nadu",
                    "Full-time",
                    "experienced",
                    "Build full-stack applications with React and Node.js for global clients.",
                    System.currentTimeMillis() - 120 * 60 * 60 * 1000, // 5 days ago
                    180000L, // Salary adjusted for full-stack demand
                    List.of("React", "Node.js", "JavaScript", "CSS", "HTML"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Exciting opportunities for full-stack developers!", "Amit Singh", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Great company for tech professionals!", "Sujata Rao", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    15000
            ),

            new Job(
                    "Blockchain Developer",
                    "Tata Consultancy Services (TCS)",
                    "Pune, Maharashtra",
                    "Full-time",
                    "experienced",
                    "Work on blockchain-based projects to create decentralized solutions.",
                    System.currentTimeMillis() - 96 * 60 * 60 * 1000, // 4 days ago
                    210000L, // Salary adjusted for Blockchain demand
                    List.of("Blockchain", "Ethereum", "Smart Contracts", "Solidity"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Blockchain is the future of tech!", "Arvind Patel", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("TCS is doing amazing work in blockchain!", "Aishwarya Mehta", System.currentTimeMillis() - 36 * 60 * 60 * 1000)
                    ),
                    20000
            ),

            new Job(
                    "DevOps Engineer",
                    "Cognizant",
                    "Kochi, Kerala",
                    "Full-time",
                    "experienced",
                    "Implement automation tools to improve efficiency in software development and deployment.",
                    System.currentTimeMillis() - 144 * 60 * 60 * 1000, // 6 days ago
                    190000L, // Salary adjusted for DevOps demand
                    List.of("DevOps", "Automation", "CI/CD", "Docker", "Kubernetes"),
                    new ExperienceRequired(5),
                    List.of(
                            new Comment("Great DevOps role at Cognizant!", "Sreeja Nair", System.currentTimeMillis() - 72 * 60 * 60 * 1000),
                            new Comment("Awesome opportunity for automation engineers!", "Madhur Gupta", System.currentTimeMillis() - 48 * 60 * 60 * 1000)
                    ),
                    18000
            ),

            new Job(
                    "Cybersecurity Analyst",
                    "Wipro",
                    "Noida, Uttar Pradesh",
                    "Full-time",
                    "experienced",
                    "Monitor and protect the organization's IT infrastructure against cyber threats.",
                    System.currentTimeMillis() - 60 * 60 * 60 * 1000, // 2.5 days ago
                    180000L, // Salary adjusted for Cybersecurity demand
                    List.of("Cybersecurity", "Network Security", "Firewalls", "Python"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Great job for cybersecurity professionals!", "Ravi Kumar", System.currentTimeMillis() - 36 * 60 * 60 * 1000),
                            new Comment("Wipro's cybersecurity team is top-notch!", "Kiran Patel", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    25000
            ),

            new Job(
                    "Data Scientist",
                    "Flipkart",
                    "Bangalore, Karnataka",
                    "Full-time",
                    "experienced",
                    "Leverage machine learning and analytics to optimize Flipkart's data-driven business strategies.",
                    System.currentTimeMillis() - 72 * 60 * 60 * 1000, // 3 days ago
                    230000L, // Salary adjusted for Data Science demand
                    List.of("Machine Learning", "Data Science", "Python", "SQL"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Great opportunity for data scientists!", "Vishal Agarwal", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Flipkart is investing heavily in AI and data science.", "Neelam Yadav", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    12000
            ),

            new Job(
                    "Software Engineer (AI & ML)",
                    "Zoho",
                    "Chennai, Tamil Nadu",
                    "Full-time",
                    "experienced",
                    "Develop software solutions using AI and machine learning techniques to improve business processes.",
                    System.currentTimeMillis() - 60 * 60 * 60 * 1000, // 2.5 days ago
                    200000L, // Salary adjusted for AI/ML demand
                    List.of("Artificial Intelligence", "Machine Learning", "Python", "TensorFlow"),
                    new ExperienceRequired(3),
                    List.of(
                            new Comment("Zoho offers a lot of growth in AI & ML fields!", "Harish Kumar", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("A dream job for AI/ML developers.", "Nidhi Rai", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    25000
            ),

            new Job(
                    "Product Manager (Tech)",
                    "Samsung Research India",
                    "Noida, Uttar Pradesh",
                    "Full-time",
                    "experienced",
                    "Lead the development of new tech products from concept to launch while working with cross-functional teams.",
                    System.currentTimeMillis() - 96 * 60 * 60 * 1000, // 4 days ago
                    250000L, // Salary adjusted for high demand in Product Management
                    List.of("Product Management", "Technology", "Team Leadership", "Agile"),
                    new ExperienceRequired(5),
                    List.of(
                            new Comment("Excellent leadership opportunity at Samsung!", "Sanjeev Kumar", System.currentTimeMillis() - 48 * 60 * 60 * 1000),
                            new Comment("Fantastic for experienced product managers!", "Amitabh Yadav", System.currentTimeMillis() - 24 * 60 * 60 * 1000)
                    ),
                    30000
            ),

            new Job(
                    "Machine Learning Engineer",
                    "Amazon",
                    "Hyderabad, Telangana",
                    "Full-time",
                    "experienced",
                    "Develop and implement machine learning models to optimize business processes and operations.",
                    System.currentTimeMillis() - 48 * 60 * 60 * 1000, // 2 days ago
                    240000L, // Salary adjusted for Machine Learning demand
                    List.of("Machine Learning", "Python", "Data Science", "TensorFlow"),
                    new ExperienceRequired(4),
                    List.of(
                            new Comment("Amazon is a great place to grow your ML career!", "Rajesh Chandra", System.currentTimeMillis() - 24 * 60 * 60 * 1000),
                            new Comment("Fantastic role for ML experts!", "Suhana Mehra", System.currentTimeMillis() - 12 * 60 * 60 * 1000)
                    ),
                    20000
            )




    );



    public List<JobDTO> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private JobDTO convertToDTO(Job job) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(job.getId());
        jobDTO.setTitle(job.getTitle());
        jobDTO.setCompany(job.getCompany());
        jobDTO.setLocation(job.getLocation());
        jobDTO.setType(job.getType());
        jobDTO.setCategory(job.getCategory());
        jobDTO.setDescription(job.getDescription());
        jobDTO.setPostedDate(job.getPostedDate());
        jobDTO.setRequiredSkills(job.getRequiredSkills());
        jobDTO.setExperienceRequired(new ExperienceRequiredDTO(job.getExperienceRequired().getYears()));
        jobDTO.setComments(job.getComments().stream().map(this::convertCommentToDTO).collect(Collectors.toList()));
        jobDTO.setLikeCount(job.getLikeCount());
        return jobDTO;
    }

    private CommentDTO convertCommentToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText(comment.getText());
        commentDTO.setAuthor(comment.getAuthor());
        commentDTO.setDate(comment.getDate());
        return commentDTO;
    }

    public List<Job> checkAndInsertDefaultJobs() {
        List<Job> jobs = jobRepository.findAll();  // Fetching all jobs

        if (jobs.size()<20) {  // Check if the job table is empty
            jobRepository.saveAll(SAMPLE_JOBS);  // Insert default jobs
            return SAMPLE_JOBS;  // Return the inserted default jobs
        }

        return jobs;
    }


    // Find job by ID
    public Job findById(Long jobId) {
        return jobRepository.findById(jobId).orElse(null);
    }

    // Save job with updated data (like count or comments)
    public Job save(Job job) {
        jobRepository.save(job);
        return job;
    }

}
