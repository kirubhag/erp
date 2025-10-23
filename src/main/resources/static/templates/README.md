# Template Organization Structure

This directory contains all HTML templates organized by entity/functionality for better maintainability and easier navigation.

## Directory Structure

```
templates/
├── README.md                    # This documentation file
├── dashboard.html               # Main dashboard template
├── attendance/                  # Attendance management templates
│   └── attendance.html
├── email/                       # Email system templates
│   ├── email-logs.html         # Email logs view
│   ├── email-template-form.html # Create/edit email templates
│   └── email-templates.html    # List email templates
├── health/                      # Health records templates
│   └── health.html
├── organization/                # Organization management templates
│   ├── organization.html        # Organization form
│   ├── settings-organization-edit.html
│   └── settings-organization-view.html
├── parent/                      # Parent management templates
│   └── parents.html
├── settings/                    # Settings and configuration templates
│   └── settings.html           # Modern settings page with card-based layout
├── student/                     # Student management templates
│   └── students.html
└── user/                        # User management templates
    ├── settings-user-edit.html
    └── settings-user-view.html
```

## Template Usage

### Route Configuration
Templates are referenced in `js/app.js` with their full path:
```javascript
.when('/students', {
    templateUrl: '/templates/student/students.html',
    controller: 'MainController'
})
```

### ng-include References
For dynamic template inclusion, use the full path:
```html
<div ng-include="'/templates/email/email-templates.html'"></div>
```

## Benefits of This Organization

1. **Entity-Based Grouping**: Related templates are grouped together by functionality
2. **Easy Navigation**: Developers can quickly find relevant templates
3. **Scalability**: New templates can be easily added to appropriate entity folders
4. **Maintainability**: Easier to maintain and update related templates
5. **Clear Structure**: Logical organization makes the codebase more professional

## Template Naming Conventions

- **Entity Lists**: `{entity}.html` (e.g., `students.html`, `parents.html`)
- **Settings Views**: `settings-{entity}-{action}.html` (e.g., `settings-user-view.html`)
- **Forms**: `{entity}-form.html` or descriptive names like `email-template-form.html`
- **Dashboard**: Entity-specific dashboards or main `dashboard.html`

## Future Additions

When adding new templates:
1. Determine the appropriate entity folder
2. Follow the naming conventions
3. Update route configurations in `js/app.js`
4. Update any ng-include references if needed
5. Update this README if adding new entity folders