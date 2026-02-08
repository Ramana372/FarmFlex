# 🌾 AgriBuy - Agricultural Equipment Marketplace 🚜

**A Modern Full-Stack Web Application for Buying and Renting Agricultural Machinery**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.java.com) [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot) [![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org) [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue.svg)](https://www.postgresql.org)

## 📋 Overview

AgriBuy is a comprehensive digital marketplace where farmers can list agricultural equipment (tractors, harvesters, ploughs, etc.) for **RENT** or **SALE**. Buyers can browse, filter, search, and securely purchase equipment with integrated Razorpay payments.

Built with **Spring Boot 3**, **React 18**, **PostgreSQL**, and modern web technologies, AgriBuy eliminates middlemen and connects farmers directly with equipment users.

### ✨ Key Highlights

- 🔐 **Secure Authentication** - JWT-based with email verification
- 📱 **Responsive Design** - Mobile-first with Tailwind CSS
- 💳 **Razorpay Integration** - Secure payment processing for RENT & SALE
- 👨‍🌾 **Role-Based Access** - FARMER and ADMIN roles
- 📊 **Admin Dashboard** - Approve/reject listings, manage users
- 🔍 **Advanced Search** - Filter by type, category, location, price
- 📦 **Docker Ready** - Single-command deployment

---

## 🚀 Key Features

### For Farmers (FARMER Role)
- ✅ List equipment with multiple images and detailed descriptions
- ✅ Choose between RENT (daily rate) or SALE (one-time price)
- ✅ Edit listings until they go live
- ✅ Track pending approvals, live listings, sales, and bookings
- ✅ Receive rejection feedback from admins

### For Buyers
- ✅ Browse all approved equipment with advanced filtering
- ✅ View complete listing details with image carousel
- ✅ Rent equipment with custom date ranges
- ✅ Purchase equipment with one-click checkout
- ✅ Track all orders and bookings in history

### For Admins (ADMIN Role)
- ✅ Review pending listings for approval
- ✅ Approve listings to make them visible to buyers
- ✅ Reject listings with detailed feedback to farmers
- ✅ View all farmers and their listings
- ✅ Dashboard with key statistics

---

## 💻 Technologies & Stack

### Backend
- **Java 17** - Modern language features
- **Spring Boot 3.5.3** - Enterprise framework
- **PostgreSQL 12+** - Relational database
- **JPA/Hibernate** - Object-relational mapping
- **Spring Security** - Authentication & authorization
- **Maven** - Build automation
- **Razorpay API** - Payment processing

### Frontend
- **React 18** - UI library with hooks
- **Vite** - Lightning-fast build tool
- **Tailwind CSS** - Utility-first CSS framework
- **Axios** - HTTP client with interceptors
- **React Router v6** - Client-side routing
- **Context API** - State management

### Deployment
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **PostgreSQL Docker** - Database container

---

## 📁 Project Structure

```
AgriMart/
├── Backend/                          # Spring Boot REST API
│   ├── src/main/java/com/example/
│   │   ├── Config/                  # Spring configurations
│   │   │   ├── FileUploadConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebConfig.java
│   │   ├── Controller/              # REST endpoints (8 controllers)
│   │   │   ├── AuthController.java
│   │   │   ├── ListingController.java
│   │   │   ├── OrderController.java
│   │   │   ├── PaymentController.java
│   │   │   ├── ProductController.java
│   │   │   ├── UserController.java
│   │   │   └── AdminController.java
│   │   ├── DTO/                     # Data Transfer Objects
│   │   │   ├── ListingCreateRequest.java
│   │   │   ├── ListingResponse.java
│   │   │   ├── OrderResponse.java
│   │   │   ├── UserResponse.java
│   │   │   ├── PaymentCreateOrderRequest.java
│   │   │   ├── PaymentConfirmRequest.java
│   │   │   └── RejectListingRequest.java
│   │   ├── Model/                   # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Listing.java
│   │   │   ├── Order.java
│   │   │   └── Product.java
│   │   ├── Repo/                    # Repository interfaces
│   │   │   ├── UserRepo.java
│   │   │   ├── ListingRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   └── ProductRepository.java
│   │   ├── Security/                # JWT authentication
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtTokenProvider.java
│   │   │   └── JwtUtil.java
│   │   ├── Service/                 # Business logic
│   │   │   ├── AuthService.java
│   │   │   ├── ListingService.java
│   │   │   ├── OrderService.java
│   │   │   ├── PaymentService.java
│   │   │   └── UserService.java
│   │   └── BackendApplication.java
│   ├── src/main/resources/
│   │   └── application.properties    # Configuration
│   ├── pom.xml                       # Maven dependencies
│   ├── Dockerfile                    # Docker image
│   └── mvnw                          # Maven wrapper
│
├── Frontend/                         # React + Vite application
│   ├── src/
│   │   ├── api/                     # API client
│   │   │   ├── apiClient.js         # Axios instance with JWT
│   │   │   └── agriAPI.js           # Organized API endpoints
│   │   ├── components/              # Reusable components
│   │   │   └── ProtectedRoute.jsx
│   │   ├── context/                 # React Context
│   │   │   └── AuthContext.jsx
│   │   ├── pages/                   # Page components (11 pages)
│   │   │   ├── HomePage.jsx
│   │   │   ├── BrowseProductsPage.jsx
│   │   │   ├── ListingDetailsPage.jsx
│   │   │   ├── LoginPage.jsx
│   │   │   ├── RegisterPage.jsx
│   │   │   ├── VerifyEmailPage.jsx
│   │   │   ├── DashboardPage.jsx
│   │   │   ├── FarmerProductsPage.jsx
│   │   │   ├── AdminDashboardPage.jsx
│   │   │   ├── AdminApprovalsPage.jsx
│   │   │   └── AddProductPage.jsx
│   │   ├── utils/                   # Helper functions
│   │   │   └── helpers.js           # 450+ lines, 9 utility modules
│   │   ├── App.jsx                  # Main component
│   │   ├── main.jsx                 # Entry point
│   │   └── index.css                # Global styles
│   ├── public/                       # Static assets
│   ├── package.json                  # npm dependencies
│   ├── vite.config.js               # Vite config
│   ├── tailwind.config.js           # Tailwind config
│   ├── postcss.config.cjs           # PostCSS config
│   ├── Dockerfile                    # Docker image
│   └── index.html                    # HTML template
│
├── Uploads/                          # Local storage directory
├── githubimages/                     # Documentation images
│
├── docker-compose.yml                # Multi-container orchestration
├── PROJECT_SETUP.md                  # Detailed setup guide (400+ lines)
├── API_DOCUMENTATION.md              # Complete API reference (600+ lines)
├── SETUP.sh                          # Linux/macOS setup script
├── SETUP.bat                         # Windows setup script
├── README.md                         # This file
└── .gitignore
```

---

## 🚀 Quick Start

### Prerequisites
- **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Node.js 16+** - [Download](https://nodejs.org/)
- **PostgreSQL 12+** - [Download](https://www.postgresql.org/download/)
- **Razorpay Account** - Test mode available at [razorpay.com](https://razorpay.com/)

### Option 1: Using Automation Scripts (Recommended)

**Windows:**
```bash
SETUP.bat
```

**macOS / Linux:**
```bash
chmod +x SETUP.sh
./SETUP.sh
```

### Option 2: Manual Setup

**Backend:**
```bash
# Navigate to backend
cd Backend

# Update database credentials in src/main/resources/application.properties
# Then run:
./mvnw clean install
./mvnw spring-boot:run
# Server runs at http://localhost:8090
```

**Frontend (in another terminal):**
```bash
# Navigate to frontend
cd Frontend

# Install dependencies
npm install

# Start development server
npm run dev
# App runs at http://localhost:3000
```

### Option 3: Using Docker (Fastest)

```bash
# Create environment file
cat > .env << EOF
RAZORPAY_KEY_ID=your-razorpay-key
RAZORPAY_KEY_SECRET=your-razorpay-secret
POSTGRES_PASSWORD=your-db-password
EOF

# Run the stack
docker-compose up -d

# Access:
# Frontend: http://localhost:3000
# Backend: http://localhost:8090
# Database: localhost:5432
```

---

## 📖 Documentation

- **[PROJECT_SETUP.md](./PROJECT_SETUP.md)** - Complete setup guide with architecture, features, and troubleshooting
- **[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)** - Full API reference with cURL examples and response formats

---

## 🔐 Security Features

- ✅ **Password Hashing** - BCrypt encryption (min 8 chars, uppercase, number, special char)
- ✅ **JWT Authentication** - 24-hour token expiration
- ✅ **Email Verification** - Required before login access
- ✅ **Role-Based Access Control** - FARMER and ADMIN roles with @PreAuthorize
- ✅ **CORS Configuration** - Restricted to frontend origin
- ✅ **Payment Security** - Razorpay SHA256 signature verification
- ✅ **SQL Injection Prevention** - JPA parameterized queries
- ✅ **Double-Booking Prevention** - Transaction-based order validation

---

## 💳 Payment Processing

### RENT Flow
1. Select equipment and rental dates
2. Calculate: `rentPricePerDay × numberOfDays`
3. Click "Rent Now" → Razorpay modal opens
4. On success → Order created, listing status → BOOKED
5. Track in "My Bookings"

### SALE Flow
1. View equipment and sale price
2. Click "Buy Now" → Razorpay modal opens
3. On success → Order created, listing status → SOLD
4. Track in "My Orders"

---

## 📊 Database Schema

### User (Entity)
- id (PK)
- name, email (unique), phone, location
- password (hashed), role (FARMER/ADMIN)
- emailVerified, emailVerificationToken
- createdAt, updatedAt (timestamps)

### Listing (Entity)
- id (PK)
- ownerId (FK → User)
- type (RENT/SALE), category (enum)
- title, description
- rentPricePerDay / salePrice
- location, images (Array of image URLs)
- status (PENDING/LIVE/REJECTED/SOLD/BOOKED)
- rejectionReason (if rejected)
- createdAt, updatedAt

### Order (Entity)
- id (PK)
- buyerId (FK → User)
- listingId (FK → Listing)
- amount, paymentId, paymentStatus
- rentStartDate, rentEndDate (null for SALE)
- createdAt, updatedAt

### Product (Entity)
- id (PK), name, description, etc.

---

## 🧪 API Endpoints

### Authentication
- `POST /api/auth/register` - Register farmer/buyer
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/verify-email` - Verify email with token
- `POST /api/auth/forgot-password` - Reset password

### Listings
- `GET /api/listings` - Browse all live listings (with filters)
- `POST /api/listings` - Create new listing (multipart form-data)
- `GET /api/listings/{id}` - Get listing details
- `PUT /api/listings/{id}` - Update PENDING/REJECTED listing
- `DELETE /api/listings/{id}` - Delete draft listing
- `GET /api/listings/my` - My listings (grouped by status)

### Orders
- `GET /api/orders/my` - My bookings and purchases
- `GET /api/orders/{id}` - Order details
- `GET /api/orders/listing/{id}/available` - Check if listing available

### Payments
- `POST /api/payments/create-order` - Create Razorpay order
- `POST /api/payments/confirm` - Confirm payment with signature

### Admin
- `GET /api/admin/dashboard` - Dashboard statistics
- `GET /api/admin/listings/pending` - Pending approvals
- `POST /api/admin/listings/{id}/approve` - Approve listing
- `POST /api/admin/listings/{id}/reject` - Reject listing
- `GET /api/admin/farmers` - All farmers
- `GET /api/admin/users` - All users

See [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) for complete details with examples.

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Database connection refused | Verify PostgreSQL running, check credentials in `application.properties` |
| Port 8090 already in use | Change port: `server.port=8091` in `application.properties` |
| Images not loading | Ensure image URLs are accessible, use placeholder images if needed |
| Payment verification failed | Verify Razorpay keyId/keySecret, test mode must be enabled |
| CORS errors | Check frontend URL in `SecurityConfig.java` @CrossOrigin |
| JWT token invalid | Token may be expired (24hrs), login again to get new token |

For more help, see [PROJECT_SETUP.md](./PROJECT_SETUP.md) troubleshooting section.

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -m 'Add new feature'`
4. Push to branch: `git push origin feature/new-feature`
5. Open a Pull Request

---

## 📄 License

MIT License - see [LICENSE](LICENSE) file

---

## 🙌 Support & Contact

- 📧 Email: support@agribuy.local
- 🐛 Report bugs: [Create an issue](../../issues)
- 💬 Discuss: [Start a discussion](../../discussions)

---

<div align="center">

### 🌾 Empowering Farmers Through Technology 🚜

**Built with React, Spring Boot, and PostgreSQL**

**[Setup Guide](./PROJECT_SETUP.md) • [API Docs](./API_DOCUMENTATION.md) • [Get Started](#-quick-start)**

</div>

7. **Version Control**  
   - Git repository initialized.  
   - Code pushed to GitHub repository `AgriMart`.

8. **Testing & Debugging**  
   - Unit and integration tests in `test/`.  
   - Debugging via IntelliJ IDEA.

9. **Running the Application**  
   - Run `BackendApplication` class in IntelliJ IDEA.  
   - Access at `http://localhost:<yourportnumer>`.

---

## Setup and Installation

```bash
# Clone the repository
git clone https://github.com/Ramana372/AgriMart.git
cd AgriMart/Backend

# Ensure PostgreSQL is installed and 'agrimart' database is created
# Update application.properties with DB credentials

# Open in IntelliJ IDEA, resolve Maven dependencies
# Run the application
