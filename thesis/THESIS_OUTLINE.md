# Graduation Thesis — Working Outline

> **Author:** Berat Baran (9A210060)
> **Institution:** Istanbul Medipol University — Social Sciences Institute, MIS-4
> **Working Title:** *AI-Powered Smart Task Management Application: Development of a Multi-User Android Mobile Application*
> **Based on:** ToDoApp (this repository)

---

## Source Materials (in `thesis/`)

| File | Purpose | Notes |
|---|---|---|
| `THESIS WRITING GUIDE.docx` | Medipol formatting/structure rules | Read as docx. Dictates front-matter order, fonts, margins, section numbering. |
| `Berat Baran 9A210060 MIS-4 Literature Review.docx` | 10 academic sources already cited | Drives Chapter 2 structure. |
| `Berat Baran 9A210060 MIS-4 Annex-4.pdf` | 40-page scanned PDF, **no text layer** | Needs OCR (pdftoppm → image OCR) if contents are needed later. |

---

## Front Matter (Medipol-required order)

- Outer / Inner Cover
- Preface *(optional)*
- Table of Contents
- List of Abbreviations
- List of Figures, Pictures and Tables
- Turkish Abstract (Özet)
- English Abstract

---

## 1. Introduction
- 1.1. Background: Post-Pandemic Shift in Mobile Productivity
- 1.2. Problem Statement: Cognitive Load and the "Small Tasks Trap"
- 1.3. Aim and Objectives of the Study
- 1.4. Research Questions
- 1.5. Scope and Limitations
- 1.6. Significance of the Study
- 1.7. Thesis Structure

## 2. Literature Review
- 2.1. Mobile Application Development Trends *(Rathod & Agal, 2023)*
- 2.2. AI, Cross-Platform Technologies and 5G in User Engagement *(Shahwar et al., 2025)*
- 2.3. NLP and AI Chatbots for Intent-Aware Assistance *(Chintalapudi et al., 2025)*
- 2.4. Comparative Analysis of Existing Task-Management Platforms *(Shama et al., 2024)*
- 2.5. The Psychology of Task Management — "Small Tasks Trap" *(Rusou et al., 2020)*
- 2.6. Generative AI and Workplace Productivity *(Brynjolfsson et al., 2025)*
- 2.7. Backend Architectures for Multi-User Mobile Apps *(Milojković et al., 2024)*
- 2.8. Calendar-Based UI, Filtering and Overlay Components *(Torino et al., 2025)*
- 2.9. Biometric Authentication and Android Keystore Security *(Cho et al., 2025)*
- 2.10. Role-Based Access Control in Task-Management Systems *(Hirpara, 2024)*
- 2.11. Research Gap and Contribution of This Study

## 3. Methodology
- 3.1. Research Approach: Design & Development Methodology
- 3.2. Agile / SDLC Process Followed
- 3.3. Requirements Analysis
  - 3.3.1. Functional Requirements
  - 3.3.2. Non-Functional Requirements (Offline, Security, Localization, Performance)
- 3.4. Use-Case Diagrams and User Stories
- 3.5. Tools and Technologies Selection

## 4. System Design and Architecture
- 4.1. High-Level System Architecture
- 4.2. Clean Architecture Layers (Domain / Data / Presentation)
- 4.3. MVI Pattern (Contract / ViewModel / Screen)
- 4.4. Module Separation (`:app` and `:uikit` Design System)
- 4.5. Data Layer Design
  - 4.5.1. Encrypted Room Database Schema
  - 4.5.2. REST API Contract and DTO Layer
  - 4.5.3. Offline-First Synchronization Strategy *(ties to 2.1, 2.7)*
- 4.6. Authentication Flow
  - 4.6.1. Email, Google and Biometric Sign-In
  - 4.6.2. JWT Token Refresh and Secure Storage *(ties to 2.9)*
- 4.7. Push Notifications via Firebase Cloud Messaging
- 4.8. Background Work Scheduling with WorkManager
- 4.9. Navigation Graph and Screen Map
- 4.10. UI/UX Design System (TDTheme, Typography, Components)

## 5. Implementation
- 5.1. Development Environment and Toolchain
- 5.2. Dependency Injection with Hilt
- 5.3. Authentication Module *(ties to 2.9)*
- 5.4. Task Management Module
  - 5.4.1. Create / Edit / Filter / Search
  - 5.4.2. Calendar View and Overlay Reminder Cards *(ties to 2.8)*
  - 5.4.3. "Plan Your Day" Feature
- 5.5. Group / Multi-User Collaboration Module *(ties to 2.4, 2.10)*
  - 5.5.1. Group Creation, Invites and Membership
  - 5.5.2. Role-Based Access Control (Admin / Member)
  - 5.5.3. Shared Task Assignment and Activity Feed
- 5.6. Pomodoro Focus Engine
  - 5.6.1. Session Lifecycle and Summary Analytics
  - 5.6.2. Mitigating the Small-Tasks Trap via AI-Assisted Prioritization *(ties to 2.5, 2.6)*
- 5.7. AI / NLP-Assisted Task Assistance *(planned or partial — ties to 2.2, 2.3)*
- 5.8. Profile, Avatar Upload and Settings
- 5.9. Localization Strategy (English / Turkish)
- 5.10. Security Hardening (Encrypted DataStore, Keystore, TEE) *(ties to 2.9)*

## 6. Testing and Quality Assurance
- 6.1. Unit Testing of ViewModels and Repositories
- 6.2. Compose UI Testing
- 6.3. Static Analysis: KtLint and Detekt
- 6.4. Manual and Usability Test Scenarios *(ties to 2.8 methodology)*

## 7. Results and Discussion
- 7.1. Feature Coverage and Screenshots
- 7.2. Performance Observations (Startup, Memory, Sync Latency)
- 7.3. Comparison Against Related Applications *(ties to 2.4)*
- 7.4. Evaluation of Research Questions
- 7.5. Limitations

## 8. Conclusion and Future Work
- 8.1. Summary of Contributions
- 8.2. Academic and Practical Implications
- 8.3. Future Work
  - 8.3.1. Dynamic AI-Driven Role Assignment *(ties to 2.10)*
  - 8.3.2. Multi-Modal NLP Input (Voice / Image) *(ties to 2.3)*
  - 8.3.3. Scalability to Millions of Concurrent Users *(ties to 2.7)*
  - 8.3.4. Long-Term Study of AI-Assisted User Autonomy *(ties to 2.2, 2.6)*

---

## Back Matter

- Bibliography (10 sources from the Literature Review; add more as chapters grow)
- Appendices
  - Appendix A: Application Screenshots
  - Appendix B: Database Schema (ERD)
  - Appendix C: REST API Endpoint Reference
  - Appendix D: Source Code Repository Link
  - Appendix E: Ethics / Consent Form *(if user testing is performed)*

---

## Status / Next Steps

- [x] Outline drafted (titles + subtitles, aligned with 10 lit sources + Medipol guide)
- [ ] Write Turkish abstract (Özet) and English abstract
- [ ] Draft Chapter 1 (Introduction)
- [ ] Flesh out Chapter 2 paragraphs per source
- [ ] Produce figures: architecture diagram, use-case diagram, ERD, navigation graph
- [ ] Collect final screenshots for Appendix A
- [ ] Decide whether to OCR the Annex-4 PDF (scanned, 40 pages)
