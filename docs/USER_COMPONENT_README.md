# User Management Component

## Overview

The User Management Component is a comprehensive Angular component for managing system users. It provides a complete CRUD interface with features for activating/deactivating users and managing user roles.

## Features

### 1. **User List Display**
- View all active and inactive users
- Search functionality to filter users by name or email
- User status indicators (Active/Inactive badges)
- Profile avatars with user initials
- Responsive grid layout

### 2. **User Selection & Details**
- Click on any user to view their detailed information
- Display comprehensive user information including:
  - Basic info (username, email, phone)
  - Account status (expired, locked, credentials)
  - Last login date and password change date
  - Assigned roles
  - User type classification

### 3. **Add New User**
- "New User" button in the list panel
- Form validation for required fields
- Password confirmation field
- User type dropdown selection
- Enable/disable user on creation

### 4. **Edit User**
- Edit button in the details panel
- Pre-populated form with user data
- Optional password change (leave blank to keep current)
- Validation on all form fields

### 5. **Activate/Deactivate User**
- Toggle user status with dedicated buttons
- Confirmation dialog on deactivation
- Real-time status updates
- Success/error notifications

### 6. **Delete User**
- Delete button with confirmation dialog
- Permanent user removal
- Automatic list refresh after deletion

## Component Structure

### Files

```
src/main/resources/static/angular/src/app/
├── components/
│   └── user/
│       ├── user.component.ts          # Component logic
│       ├── user.component.html        # Template
│       └── user.component.css         # Styles
├── services/
│   └── user.service.ts               # API service
└── app.routes.ts                     # Routes
```

### Component Class (user.component.ts)

**Key Properties:**
- `users: User[]` - Array of all users
- `selectedUser: User | null` - Currently selected user
- `userForm: FormGroup` - Reactive form for add/edit
- `showUserForm: boolean` - Toggle form visibility
- `isEditMode: boolean` - Track add vs edit mode

**Key Methods:**

| Method | Purpose |
|--------|---------|
| `loadUsers()` | Fetch all users from backend |
| `selectUser(user)` | Select a user to display details |
| `openAddUserForm()` | Show form for creating new user |
| `openEditUserForm()` | Show form for editing selected user |
| `saveUser()` | Save user (create or update) |
| `activateUser()` | Enable selected user |
| `deactivateUser()` | Disable selected user |
| `deleteUser()` | Delete selected user |
| `searchUsers()` | Search users by query |
| `getFullName(user)` | Get user's full name or username |

### User Service (user.service.ts)

**API Endpoints:**

| Method | Endpoint | Purpose |
|--------|----------|---------|
| `getAllUsers()` | `GET /settings/users` | Fetch all users |
| `getUserById(id)` | `GET /settings/users/{id}` | Get user by ID |
| `getUserByUsername(username)` | `GET /settings/users/username/{username}` | Search user by username |
| `searchUsers(query)` | `GET /settings/users/search?search={query}` | Search users |
| `createUser(user)` | `POST /settings/users` | Create new user |
| `updateUser(id, user)` | `PUT /settings/users/{id}` | Update user |
| `deleteUser(id)` | `DELETE /settings/users/{id}` | Delete user |
| `activateUser(id)` | `PUT /settings/users/{id}/activate` | Activate user |
| `deactivateUser(id)` | `PUT /settings/users/{id}/deactivate` | Deactivate user |
| `resetPassword(id, password)` | `PUT /settings/users/{id}/reset-password` | Reset user password |
| `getUsersByRole(role)` | `GET /settings/users/role/{role}` | Get users by role |

## User Interface

### Left Panel - User List
```
+----------------------------+
| Active Users (10)   [+New] |
| [Search box]               |
|                            |
| [x] User1 Admin      [×]  |
| [x] User2 Manager     [×] |
| [ ] User3 Inactive    [×] |
| ...                        |
+----------------------------+
```

### Right Panel - User Details
```
+----------------------------+
| [Avatar] Full Name [Status] |
| User Type                  |
| email@school.edu           |
| 9876543210                 |
|                            |
| User Information           |
| Username: ...              |
| Email: ...                 |
| Phone: ...                 |
|                            |
| Account Status             |
| Expired: No                |
| Locked: No                 |
| Credentials: No            |
|                            |
| Roles                      |
| [Admin] [Manager]          |
|                            |
| [Edit] [Deactivate] [Delt] |
+----------------------------+
```

## Form Validation

### Add User Form
- **Username** - Required
- **Email** - Required, valid email format
- **Password** - Required, minimum 6 characters
- **Confirm Password** - Required, must match password
- **First Name** - Optional
- **Last Name** - Optional
- **Phone** - Optional
- **User Type** - Optional dropdown (Admin, Manager, Teacher, Student, Staff)

### Edit User Form
- **Username** - Required (read-only)
- **Email** - Required, valid email format
- **Password** - Optional (leave blank to keep current)
- **Confirm Password** - Optional (required if password is provided)
- All other fields same as add form

## Data Flow

1. **On Component Init:**
   ```
   ngOnInit() → loadUsers() → Subscribe to UserService.getAllUsers()
   → Set users array & select first user
   ```

2. **User Selection:**
   ```
   selectUser(user) → Set selectedUser → Hide form → Display details
   ```

3. **Adding User:**
   ```
   openAddUserForm() → Reset form → Show form → 
   saveUser() → POST to UserService.createUser() → 
   loadUsers() → Close form
   ```

4. **Editing User:**
   ```
   openEditUserForm() → Populate form → Show form → 
   saveUser() → PUT to UserService.updateUser() → 
   loadUsers() → Close form
   ```

5. **User Status Change:**
   ```
   activateUser/deactivateUser() → PUT to UserService → 
   loadUsers() → Update selectedUser
   ```

## API Response Format

### User Object
```typescript
interface User {
  id: number;
  username: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  userType?: string;
  enabled: boolean;
  accountNonExpired?: boolean;
  credentialsNonExpired?: boolean;
  accountNonLocked?: boolean;
  lastLoginDate?: string;
  passwordChangeDate?: string;
  roles?: any[];
}
```

## Styling

### Bootstrap Classes Used
- `card` - Card containers
- `list-group` - User list
- `form-control` / `form-select` - Form inputs
- `btn btn-primary/secondary/danger/success/warning` - Buttons
- `badge` - Status indicators
- `alert alert-success/danger` - Notifications
- `spinner-border` - Loading indicator

### Custom CSS Features
- Smooth transitions and animations
- Responsive layout for mobile devices
- Hover effects on list items
- Form validation styling
- Empty state designs
- Print-friendly styles

## Route Configuration

The component is registered in `app.routes.ts`:

```typescript
{ 
  path: 'setup/users', 
  component: UserComponent, 
  canActivate: [AuthGuard] 
}
```

## Integration with Setup Component

The User component is integrated into the Setup component's navigation. Users can access it via:
1. Navigate to `/setup` (Setup page)
2. Click on "Users" card in the "General" section
3. Or directly navigate to `/setup/users`

## Error Handling

- **Load Users Error** - Displays alert with error message
- **Save User Error** - Displays validation errors or server error
- **Delete/Status Change Error** - Displays confirmation and error details
- **Form Validation** - Real-time validation with error messages

## Success Notifications

- User created successfully
- User updated successfully
- User activated successfully
- User deactivated successfully
- User deleted successfully

## Browser Compatibility

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Performance Considerations

- Users list caches on component load
- Search uses debouncing on input
- Image placeholders use CDN for fast loading
- Responsive images for different screen sizes
- Lazy loading of user details panel

## Future Enhancements

- [ ] Bulk user operations (delete, activate, deactivate)
- [ ] User role assignment interface
- [ ] Password reset/expiry management
- [ ] User activity logging
- [ ] Import/export users functionality
- [ ] User groups management
- [ ] Custom field support
- [ ] User preferences synchronization

## Dependencies

- Angular Core
- Angular Forms (FormsModule, ReactiveFormsModule)
- Angular Common
- Bootstrap 5.3
- Bootstrap Icons
- HTTP Client

## Testing

### Unit Test Coverage
- Component initialization
- User list loading
- User selection and display
- Form validation
- CRUD operations
- Error handling
- Search functionality

### E2E Test Scenarios
- Add new user workflow
- Edit user workflow
- Activate/deactivate user
- Delete user
- Search users
- Form validation

## Security Considerations

- Protected route with AuthGuard
- Password confirmation on creation
- Confirmation dialogs on destructive actions
- No sensitive data in console logs
- HTTPS required for API calls
- CSRF protection via HTTP headers
