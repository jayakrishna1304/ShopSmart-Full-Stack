# 🛒 ShopSmart — AI-Powered Full-Stack E-Commerce Platform

### Connecting Local Retailers and Customers Through Microservices and Generative AI

A full-stack e-commerce platform built with **React, TypeScript, Java, Spring Boot, and microservices**, featuring unified customer and retailer authentication, role-based navigation, and an LLM-powered AI chatbot with persistent conversational memory.

<p align="center">

  **Java · Spring Boot · React · TypeScript · Microservices · Generative AI**

</p>

---

## 📌 Overview

ShopSmart is a full-stack e-commerce platform designed to connect local retailers with customers through a unified digital shopping experience.

The application uses a modular architecture consisting of **11 backend microservices and a dedicated React frontend**. Customers and retailers access the platform through a common login and registration interface, with role-based navigation directing them to their respective experiences.

ShopSmart also integrates an AI service that connects with a Large Language Model (LLM) to support conversational interactions. By persisting and retrieving conversation history, the AI service provides a foundation for memory-based conversations.

The long-term vision is to evolve ShopSmart into an AI-assisted retail platform that helps customers discover relevant products and live offers while providing retailers with actionable business insights and data-driven recommendations.

## ✨ Key Features

### 🔐 Unified Authentication and Role-Based Navigation

- Common login and registration interface for customers and retailers.
- Role-based navigation to the appropriate customer or retailer interface.
- Separate experiences for shopping and retailer operations.
- Authentication and authorization workflows supported by the backend.

### 🛍️ Customer Shopping Experience

- Browse and explore available products.
- Shopping cart and checkout workflows.
- Discover shops and available products.
- Access loyalty and voucher-related functionality where implemented.

### 🏪 Retailer Management

- Dedicated retailer interface.
- Shop and product management capabilities.
- Voucher and promotional offer management.
- A foundation for future AI-assisted retail operations.

### 🤖 AI Chatbot with LLM Integration

- Integration of a Large Language Model through a dedicated AI microservice.
- Conversational interaction between users and the AI assistant.
- Persistent conversation history to support memory-based conversations.
- Retrieval of previous conversation context to support continuity across interactions.
- Separation of AI functionality from core commerce services.

### 🧩 Microservices Architecture

- 11 independently organized Spring Boot backend services.
- Separation of business responsibilities across services.
- REST API-based application integration.
- Modular structure supporting independent development and maintenance.

---

## 🏗️ System Architecture

ShopSmart separates the frontend, commerce functionality, authentication, and AI capabilities into distinct components.

```text
                  ┌─────────────────────────────┐
                  │       React Frontend        │
                  │      TypeScript + Vite      │
                  └──────────────┬──────────────┘
                                 │
                       Login / Registration
                                 │
                  ┌──────────────▼──────────────┐
                  │   Authentication Service    │
                  │    Identity and Access      │
                  └──────────────┬──────────────┘
                                 │
                       Role-Based Navigation
                       ┌─────────┴─────────┐
                       │                   │
              ┌────────▼────────┐ ┌────────▼────────┐
              │    Customer     │ │    Retailer     │
              │    Interface    │ │    Interface    │
              └────────┬────────┘ └────────┬────────┘
                       │                   │
                       └─────────┬─────────┘
                                 │
                       Backend REST APIs
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
   ┌──────▼────────┐    ┌────────▼────────┐    ┌────────▼────────┐
   │ Commerce      │    │ Loyalty and     │    │ AI Service      │
   │ Microservices │    │ Voucher Services│    │                 │
   └──────┬────────┘    └────────┬────────┘    └────────┬────────┘
          │                      │                      │
          │                      │               ┌──────▼───────┐
          │                      │               │ LLM Provider  │
          │                      │               └──────────────┘
          │                      │
   ┌──────▼──────────────────────▼──────────────────────┐
   │       Configured Databases and Persistence         │
   │   Business data and conversation history           │
   └────────────────────────────────────────────────────┘
```

*This is a conceptual architecture. Update it to reflect the actual API calls, service communication, database ownership, and authentication flow in your implementation. It does not imply that every service communicates directly with every other service.*

## 🧩 Backend Microservices

The backend is organized into 11 services, each responsible for a distinct business capability.

| # | Microservice | Responsibility |
|---:|---|---|
| 1 | Authentication Service | User authentication and identity workflows |
| 2 | Product Service | Product-related operations |
| 3 | Cart Service | Shopping cart management |
| 4 | Order Service | Order and checkout workflows |
| 5 | Retailer Service | Retailer management |
| 6 | Shop Service | Shop-related operations |
| 7 | Customer Service | Customer-related operations |
| 8 | Loyalty Service | Loyalty program management |
| 9 | Loyalty Transaction Service | Loyalty transaction workflows |
| 10 | Voucher Service | Voucher and promotion management |
| 11 | AI Service | LLM integration and conversational memory |

*Service responsibilities should match the actual implementation and API contracts.*

## 🤖 AI Service: Conversational Intelligence

The AI service integrates LLM capabilities into ShopSmart and provides a foundation for context-aware conversations.

### Current AI Capabilities

- LLM-powered conversational responses.
- Conversation history persistence.
- Retrieval of stored conversation context.
- Separation of AI functionality into a dedicated backend service.

### Conversation Workflow

1. A user sends a message through the frontend.
2. The frontend sends the request to the AI service.
3. The AI service identifies the relevant conversation and retrieves stored history.
4. The service prepares the current message and relevant context for the LLM.
5. The LLM generates a response.
6. The response and conversation messages are persisted according to the application's storage logic.
7. The response is returned to the frontend.

*Adapt this workflow to the exact order of operations in your implementation.*

### Future AI Roadmap

#### Phase 1 — Context-Aware Shopping Assistant

- Connect the AI service to authorized product and shop APIs.
- Answer questions using current product information.
- Help customers discover relevant products.
- Retrieve currently valid vouchers and offers from the relevant services.

#### Phase 2 — Live Offers and Personalized Recommendations

- Recommend relevant products based on permitted customer preferences and activity.
- Surface eligible promotions and vouchers using current offer data.
- Explain offer conditions, expiry dates, and eligibility.
- Improve recommendations using relevant conversation context.

#### Phase 3 — AI-Powered Retailer Intelligence

- Analyze product performance, order trends, and voucher usage where data is available.
- Identify potential demand patterns and underperforming product categories.
- Suggest promotional strategies and product assortment improvements.
- Generate inventory-planning suggestions based on historical sales and demand signals.

#### Phase 4 — Market Analysis and Business Insights

- Analyze market trends using reliable and current external data sources.
- Identify potential customer demand and emerging product opportunities.
- Compare available market signals with retailer-specific business performance.
- Provide explainable, data-supported recommendations to help retailers make decisions.

**Design principle:** The AI service should retrieve current commerce information through authorized APIs or appropriate data-access mechanisms rather than relying on the LLM's training knowledge for live prices, inventory, or offers.

*The roadmap describes planned enhancements, not existing functionality unless explicitly implemented.*

---

## 🛠️ Technology Stack

| Category | Technologies |
|---|---|
| Frontend | React, TypeScript, JavaScript |
| Frontend Tooling | Vite, npm |
| Backend | Java, Spring Boot |
| API Development | REST APIs |
| Service Communication | OpenFeign, where implemented |
| Persistence | Spring Data JPA / Hibernate, where used |
| Databases | MySQL, MongoDB, where configured |
| Security | Spring Security, JWT, where implemented |
| AI Integration | Large Language Model API |
| Build Tools | Maven, npm |
| Version Control | Git, GitHub |

Update this table to include your actual LLM provider, database technologies, and libraries.

## 📂 Repository Structure

```text
ShopSmart-Full-Stack/
│
├── Backend/
│   ├── auth-service/
│   ├── product-service/
│   ├── cart-service/
│   ├── order-service/
│   ├── retailer-service/
│   ├── shop-service/
│   ├── customer-service/
│   ├── loyalty-service/
│   ├── loyalty-transaction-service/
│   ├── voucher-service/
│   └── ai-service/
│
├── Frontend/
│   ├── public/
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
│
├── docs/
│   └── architecture.png
│
├── .gitignore
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Git
- A compatible JDK version
- Maven or the included Maven Wrapper
- Node.js and npm
- The database services configured by the application
- Access to the LLM provider if the AI service requires an external API

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/ShopSmart-Full-Stack.git
cd ShopSmart-Full-Stack
```

### 2. Configure Environment Variables

Configure the necessary database connections, backend service URLs, authentication secrets, and LLM provider credentials.

Use service-specific configuration files or `.env.example` templates where appropriate.

Never commit real credentials, private API keys, database passwords, or JWT signing secrets.

### 3. Start the Backend Services

Navigate to each service directory and follow its setup requirements.

For Maven-based Spring Boot services:

```bash
cd Backend/auth-service
mvn spring-boot:run
```

Repeat for the remaining services. Ensure their databases, environment variables, ports, and dependent services are configured correctly.

### 4. Start the Frontend

Open a separate terminal:

```bash
cd Frontend
npm install
npm run dev
```

Open the local development URL displayed by Vite.

### 5. Verify the Application

- Test customer registration and login.
- Test retailer registration and login.
- Verify that each role navigates to the correct interface.
- Test product, cart, order, and retailer workflows that are implemented.
- Test AI conversations and verify that conversation history is persisted and retrieved as expected.

*Startup commands and configuration requirements may vary by service. Add the actual ports, environment variables, and startup order once verified.*

## 🔒 Security and Data Handling

- Keep credentials outside version control.
- Enforce authorization on the backend.
- Restrict customer and retailer data access according to user roles.
- Avoid sending unnecessary personal or sensitive information to the LLM.
- Validate requests and handle errors appropriately.
- Verify live offer details against authoritative application data.

## 🧪 Engineering Practices

Areas to implement and document as applicable:

- REST API validation and error handling.
- Unit and integration testing.
- Database transaction management.
- Inter-service communication and failure handling.
- Centralized logging and service health monitoring.
- API documentation using OpenAPI or Swagger.
- Automated build and deployment pipelines.

## 🔮 Future Improvements

- Dockerize backend services and the frontend.
- Add Docker Compose for local development.
- Improve centralized logging, metrics, and distributed tracing.
- Expand automated test coverage.
- Implement reliable service health checks and failure recovery.
- Integrate live commerce data into the AI assistant.
- Introduce AI-powered retailer analytics and explainable recommendations.
- Evaluate AI response quality, privacy, and recommendation usefulness.

These improvements can be introduced incrementally as the project evolves.

## 👨‍💻 Author

**Musham Jayakrishna**

Java Full Stack Developer

**Areas of Interest:** Java, Spring Boot, React, Microservices, REST API Development, and Generative AI.

---

## ⭐ Project Highlights

- Full-stack application with a React and TypeScript frontend.
- 11 organized Spring Boot backend microservices.
- Unified customer and retailer login with role-based navigation.
- Dedicated AI service integrating an LLM and persistent conversational history.
- A roadmap toward context-aware shopping assistance and AI-powered retail intelligence.

**Explore the repository:** [ShopSmart-Full-Stack](https://github.com/jayakrishna1304/ShopSmart-Full-Stack)

