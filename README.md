# Student Information System (ERP)

## Overview

A comprehensive web-based Student Information System built with **Spring Boot** backend and **AngularJS** frontend. This system manages student records, attendance tracking, health records, parent information, and administrative settings with a modern, responsive interface.

## Architecture

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.x
- **Database**: MySQL (Port: 3307)
- **Server Port**: 8081
- **Language**: Java 17+

### Frontend (AngularJS)
- **Framework**: AngularJS 1.8.3
- **UI Library**: Bootstrap 5
- **Architecture**: Single Page Application (SPA)
- **Module System**: Lazy loading with entity-based organization

## Features

### 📚 Student Management
- Student registration and profile management
- Academic record tracking
- Search and filtering capabilities
- Bulk operations

### 📅 Attendance Tracking
- Daily attendance recording
- Attendance reports and analytics
- Absence notifications
- Historical attendance data

### 👨‍👩‍👧‍👦 Parent Management
- Parent profiles and contact information
- Student-parent relationships
- Communication logs
- Parent portal access

### 🏥 Health Records
- Medical history tracking
- Vaccination records
- Health incident reports
- Emergency contact information

### ⚙️ Settings & Administration
- Organization settings
- User management
- System configuration
- Email templates and notifications

## Project Structure

### Backend Structure
```
src/
├── main/
│   ├── java/
│   │   └── krs/
│   │       └── erp/
│   │           ├── ErpApplication.java
│   │           ├── ServletInitializer.java
│   │           └── model/
│   │               └── BaseEntity.java
│   └── resources/
│       ├── application.properties
│       ├── static/           # Frontend files
│       └── templates/
└── test/
    └── java/
        └── krs/
            └── erp/
                └── ErpApplicationTests.java
```

### Frontend Structure (Entity-Based Organization)
```
static/
├── index.html              # Main application entry point
├── css/                    # Stylesheets
├── js/
│   ├── app.js             # Main Angular application and routing
│   ├── modules/           # Angular module definitions
│   ├── templates/         # HTML templates (entity-organized)
│   │   ├── student/       # Student-related templates
│   │   ├── parent/        # Parent-related templates
│   │   ├── attendance/    # Attendance-related templates
│   │   ├── health/        # Health-related templates
│   │   ├── settings/      # Settings-related templates
│   │   └── email/         # Email-related templates
│   ├── core/              # Core services and controllers
│   │   ├── api.js         # API service for backend communication
│   │   ├── script-loader.service.js  # Dynamic script loading
│   │   ├── performance-monitor.service.js  # Performance tracking
│   │   └── main.controller.js  # Main application controller
│   ├── student/           # Student module files
│   │   ├── student.controller.js
│   │   └── student.service.js
│   ├── parent/            # Parent module files
│   │   ├── parent.controller.js
│   │   └── parent.service.js
│   ├── attendance/        # Attendance module files
│   │   ├── attendance.controller.js
│   │   └── attendance.service.js
│   ├── health/            # Health module files
│   │   ├── health.controller.js
│   │   └── health.service.js
│   ├── settings/          # Settings module files
│   │   ├── settings.controller.js
│   │   ├── settings.service.js
│   │   ├── organization.controller.js
│   │   └── organization.service.js
│   └── email/             # Email module files
│       ├── email.service.js
│       ├── email-template.controller.js
│       ├── email-log.controller.js
│       └── entity-email.controller.js
└── vendor/                # Third-party libraries
    ├── bootstrap/
    ├── angularjs/
    └── fontawesome/
```

## Technical Features

### 🚀 Lazy Loading System
- **Dynamic Module Loading**: Scripts are loaded on-demand when navigating to different sections
- **Performance Optimization**: Reduces initial page load time
- **Script Loader Service**: Manages dynamic script injection and dependency resolution
- **Performance Monitoring**: Tracks loading times and performance metrics

### 📱 Responsive Design
- **Bootstrap 5**: Modern, mobile-first UI framework
- **Adaptive Layout**: Works seamlessly across desktop, tablet, and mobile devices
- **Toast Notifications**: User-friendly feedback system
- **Loading States**: Visual indicators for better user experience

### 🔧 Development Features
- **Entity-Based Organization**: Logical grouping of related functionality
- **Service-Oriented Architecture**: Reusable business logic components
- **RESTful API Integration**: Clean separation between frontend and backend
- **Error Handling**: Comprehensive error management and user feedback

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Node.js (for frontend dependencies)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd erp
   ```

2. **Database Setup**
   ```bash
   # Create MySQL database
   mysql -u root -p
   CREATE DATABASE erp_db;
   ```

3. **Configure Application**
   ```bash
   # Update src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3307/erp_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run the Application**
   ```bash
   # Build and run backend
   ./mvnw spring-boot:run
   
   # Or run with custom port
   ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
   ```

5. **Access the Application**
   - Open browser and navigate to: `http://localhost:8081`
   - The application will automatically load required frontend modules

### Development Setup

1. **Backend Development**
   ```bash
   # Run in development mode with hot reload
   ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"
   ```

2. **Frontend Development**
   - Frontend files are served directly by Spring Boot
   - Changes to HTML/CSS/JS files are reflected immediately
   - Use browser developer tools for debugging

## API Endpoints

### Students
- `GET /api/students` - Get all students
- `POST /api/students` - Create new student
- `GET /api/students/{id}` - Get student by ID
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

### Attendance
- `GET /api/attendance` - Get attendance records
- `POST /api/attendance` - Mark attendance
- `GET /api/attendance/student/{id}` - Get attendance for specific student

### Parents
- `GET /api/parents` - Get all parents
- `POST /api/parents` - Create new parent
- `GET /api/parents/{id}` - Get parent by ID

### Health
- `GET /api/health` - Get health records
- `POST /api/health` - Create health record
- `GET /api/health/student/{id}` - Get health records for student

## Performance Optimization

### Frontend Performance
- **Lazy Loading**: Modules loaded on-demand
- **Script Caching**: Prevents duplicate script loading
- **Performance Monitoring**: Built-in timing and metrics
- **Minimal Initial Bundle**: Only core scripts loaded initially

### Backend Performance
- **Connection Pooling**: Optimized database connections
- **Caching**: Strategic caching for frequently accessed data
- **RESTful Design**: Efficient API design patterns

## Browser Support

- ✅ Chrome 60+
- ✅ Firefox 55+
- ✅ Safari 12+
- ✅ Edge 79+
- ⚠️ Internet Explorer not supported

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style Guidelines

- **Java**: Follow Spring Boot best practices
- **JavaScript**: Use ES5 syntax (AngularJS compatibility)
- **HTML**: Semantic markup with Bootstrap classes
- **CSS**: Use Bootstrap utilities when possible

## Troubleshooting

### Common Issues

1. **Application not starting on port 8081**
   ```bash
   # Check if port is in use
   lsof -i :8081
   # Kill existing process if needed
   kill -9 <PID>
   ```

2. **Database connection errors**
   - Verify MySQL is running on port 3307
   - Check database credentials in application.properties
   - Ensure database exists

3. **JavaScript console errors**
   - Check browser console for specific errors
   - Verify all script paths are correct
   - Clear browser cache and reload

4. **Module loading issues**
   - Check network tab in developer tools
   - Verify script-loader.service.js paths
   - Ensure files exist in correct directories

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, email support@yourcompany.com or create an issue in the repository.

---

**Happy Coding! 🚀**