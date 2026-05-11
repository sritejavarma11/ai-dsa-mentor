# AI-Powered DSA Mentor

An AI-driven full-stack platform built to help developers master Data Structures and Algorithms through intelligent, contextual guidance instead of direct solutions. The platform analyzes user approaches in real time and provides optimized hints, complexity improvements, and debugging insights using the Gemini API.

---

## 🚀 Features

### Intelligent Hint Engine
- Analyzes user-written code in real time
- Detects logical mistakes and inefficient approaches
- Suggests optimizations such as improving from `O(N^2)` to `O(N)`
- Provides hints without exposing full solutions

### Dynamic Problem Discovery
- Fetches and loads curated DSA problems dynamically
- Supports query-based challenge generation
- Enables seamless practice directly inside the platform

### Integrated IDE Experience
- Built-in Monaco Editor for a competitive programming experience
- Supports multiple languages:
  - Java
  - Python
  - C++
  - JavaScript

### Low-Latency Backend
- RESTful backend powered by Spring Boot
- Uses Java's native `HttpClient` for efficient API communication
- Optimized for fast AI response handling

---

## 🛠️ Tech Stack

### Frontend
- React
- Monaco Editor
- CSS3

### Backend
- Java 17+
- Spring Boot
- Maven

### AI Integration
- Google Gemini API

---

# 🔧 Getting Started

## Prerequisites

Make sure you have the following installed:

- JDK 17 or higher
- Node.js and npm
- Gemini API Key from Google AI Studio

---

## Backend Setup

1. Navigate to the backend folder:
   ```bash
   cd backend
   ```

2. Configure your Gemini API key:
   ```bash
   export GEMINI_API_KEY=your_api_key_here
   ```

3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## Frontend Setup

1. Navigate to the frontend folder:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

---

# 📈 Future Roadmap

### Persistent Storage
- Integrate MySQL for:
  - User accounts
  - Submission history
  - Progress tracking

### Live Test Cases
- Add sandboxed code execution
- Validate submissions against hidden test cases

### Leaderboards
- Compare performance and solution efficiency
- Track progress against other users

---

# 📌 Project Goal

The objective of this platform is to create an interview-focused DSA learning environment where developers receive intelligent guidance, improve problem-solving skills, and learn optimization techniques without relying on direct answers.