# User Component - Visual Architecture Guide

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Browser / Angular App                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                    UserComponent                         │  │
│  │  ┌──────────────────────────────────────────────────┐   │  │
│  │  │ Properties:                                      │   │  │
│  │  │  - users: User[]                               │   │  │
│  │  │  - selectedUser: User | null                   │   │  │
│  │  │  - userForm: FormGroup                         │   │  │
│  │  │  - searchQuery: string                         │   │  │
│  │  │  - loading: boolean                            │   │  │
│  │  └──────────────────────────────────────────────────┘   │  │
│  │                                                          │  │
│  │  ┌──────────────────────────────────────────────────┐   │  │
│  │  │ Methods:                                         │   │  │
│  │  │  - loadUsers()                                 │   │  │
│  │  │  - selectUser(user)                           │   │  │
│  │  │  - openAddUserForm()                          │   │  │
│  │  │  - openEditUserForm()                         │   │  │
│  │  │  - saveUser()                                 │   │  │
│  │  │  - activateUser()                            │   │  │
│  │  │  - deactivateUser()                          │   │  │
│  │  │  - deleteUser()                              │   │  │
│  │  │  - searchUsers()                             │   │  │
│  │  └──────────────────────────────────────────────────┘   │  │
│  │                                                          │  │
│  │  Injects: UserService, FormBuilder, Router            │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
         │
         │ HTTP Calls
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                        UserService                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  Base URL: http://localhost:8081/settings/users                 │
│                                                                   │
│  GET    /                    → getAllUsers()                    │
│  GET    /{id}                → getUserById(id)                  │
│  GET    /username/{username} → getUserByUsername(username)      │
│  GET    /search?search={q}   → searchUsers(query)               │
│  POST   /                    → createUser(user)                 │
│  PUT    /{id}                → updateUser(id, user)             │
│  DELETE /{id}                → deleteUser(id)                   │
│  PUT    /{id}/activate       → activateUser(id)                 │
│  PUT    /{id}/deactivate     → deactivateUser(id)               │
│  PUT    /{id}/reset-password → resetPassword(id, password)      │
│  GET    /role/{role}         → getUsersByRole(role)             │
│  GET    /count/active        → getActiveUsersCount()            │
│  GET    /count/inactive      → getInactiveUsersCount()          │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
         │
         │ REST API
         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  Spring Boot Backend / Database                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  UserController → UserService → UserRepository → Database        │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Component Hierarchy

```
App
├── Setup Component
│   └── Navigation → Users Link → /setup/users
│       └── UserComponent
│           ├── Left Panel: UserListComponent (logical)
│           │   ├── Header Section
│           │   │   ├── Title + Count
│           │   │   └── "New User" Button
│           │   ├── Search Section
│           │   │   └── Search Input
│           │   └── List Section
│           │       ├── User Item 1
│           │       ├── User Item 2
│           │       └── User Item N
│           │
│           └── Right Panel: UserDetailComponent (logical)
│               ├── User Details View
│               │   ├── Header Section (Avatar + Name + Status)
│               │   ├── User Information
│               │   ├── Account Status
│               │   ├── Roles
│               │   └── Action Buttons
│               │
│               └── User Form View
│                   ├── Form Title
│                   ├── Form Fields (Grid Layout)
│                   ├── Validation Messages
│                   └── Form Actions
```

## Data Flow Diagram

### User List Loading Flow

```
Component Init
    │
    ▼
ngOnInit()
    │
    ▼
loadUsers()
    │
    ▼
UserService.getAllUsers()
    │
    ▼
HTTP GET /settings/users
    │
    ▼
Backend Response: User[]
    │
    ▼
Set this.users = users
    │
    ▼
Select first user
    │
    ▼
Display on screen
```

### User Addition Flow

```
Click "New User" Button
    │
    ▼
openAddUserForm()
    │
    ├─ isEditMode = false
    ├─ Reset form
    └─ Show form view
    │
    ▼
User fills form
    │
    ▼
Submit form
    │
    ▼
saveUser()
    │
    ├─ Validate form
    └─ If invalid → Show errors
    │
    ▼
Create payload
    │
    ▼
UserService.createUser(payload)
    │
    ▼
HTTP POST /settings/users
    │
    ▼
Backend validates & saves
    │
    ▼
Backend response: New User
    │
    ▼
Show success message
    │
    ▼
closeUserForm()
    │
    ▼
loadUsers() [Refresh list]
    │
    ▼
Display updated list
```

### User Edition Flow

```
Select user from list
    │
    ▼
selectUser(user)
    │
    ├─ Set selectedUser
    └─ Show details
    │
    ▼
Click Edit button
    │
    ▼
openEditUserForm()
    │
    ├─ isEditMode = true
    ├─ Populate form with user data
    └─ Show form view
    │
    ▼
User modifies form
    │
    ▼
Submit form
    │
    ▼
saveUser()
    │
    ├─ Validate form
    └─ If invalid → Show errors
    │
    ▼
Create payload (without password if empty)
    │
    ▼
UserService.updateUser(id, payload)
    │
    ▼
HTTP PUT /settings/users/{id}
    │
    ▼
Backend validates & updates
    │
    ▼
Backend response: Updated User
    │
    ▼
Show success message
    │
    ▼
closeUserForm()
    │
    ▼
loadUsers() [Refresh list]
    │
    ▼
Display updated list
```

### User Deactivation Flow

```
Select user
    │
    ▼
Click Deactivate button
    │
    ▼
Show confirmation dialog
    │
    ▼
User confirms
    │
    ▼
deactivateUser()
    │
    ▼
Create payload: { ...user, enabled: false }
    │
    ▼
UserService.updateUser(id, payload)
    │
    ▼
HTTP PUT /settings/users/{id}
    │
    ▼
Backend disables user
    │
    ▼
Backend response: Updated User
    │
    ▼
Show success message
    │
    ▼
Update selectedUser
    │
    ▼
loadUsers() [Refresh list]
    │
    ▼
Display updated user with Inactive badge
```

## UI Layout Structure

```
┌────────────────────────────────────────────────────────────────┐
│ UserComponent                                                   │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│ ┌──────────────────────┐  ┌─────────────────────────────────┐ │
│ │   LEFT PANEL         │  │     RIGHT PANEL                 │ │
│ │                      │  │                                 │ │
│ │ ┌────────────────┐   │  │ ┌─────────────────────────────┐ │ │
│ │ │ Active Users   │   │  │ │ USER DETAILS VIEW           │ │ │
│ │ │ (10)      [+]  │   │  │ │ (default)                   │ │ │
│ │ └────────────────┘   │  │ │                             │ │ │
│ │                      │  │ │ [Avatar] Name [Status]      │ │ │
│ │ ┌────────────────┐   │  │ │ Type, Email, Phone          │ │ │
│ │ │ [🔍 Search]    │   │  │ │                             │ │ │
│ │ └────────────────┘   │  │ │ User Information            │ │ │
│ │                      │  │ │ ─────────────────────────    │ │ │
│ │ ┌────────────────┐   │  │ │ Username: ...               │ │ │
│ │ │ [✓] User1      │   │  │ │ Email: ...                  │ │ │
│ │ │ Type: Admin    │   │  │ │                             │ │ │
│ │ │                │   │  │ │ Account Status              │ │ │
│ │ │ [✓] User2      │   │  │ │ ─────────────────────────    │ │ │
│ │ │ Type: Manager  │   │  │ │ Expired: No                 │ │ │
│ │ │                │   │  │ │ Locked: No                  │ │ │
│ │ │ [ ] User3      │   │  │ │ Last Login: ...             │ │ │
│ │ │ Type: Student  │   │  │ │                             │ │ │
│ │ │ [Inactive]     │   │  │ │ Roles                       │ │ │
│ │ │                │   │  │ │ ─────────────────────────    │ │ │
│ │ │ ...            │   │  │ │ [Admin] [Manager]           │ │ │
│ │ └────────────────┘   │  │ │                             │ │ │
│ │                      │  │ │ [Edit] [Deactivate] [Delt]  │ │ │
│ │ ┌────────────────┐   │  │ │                             │ │ │
│ │ │ [Empty State]  │   │  │ └─────────────────────────────┘ │ │
│ │ │ No users found │   │  │                                 │ │
│ │ └────────────────┘   │  │ OR                              │ │
│ │                      │  │                                 │ │
│ │                      │  │ ┌─────────────────────────────┐ │ │
│ │                      │  │ │ USER FORM VIEW              │ │ │
│ │                      │  │ │ (when adding/editing)       │ │ │
│ │                      │  │ │                             │ │ │
│ │                      │  │ │ Add New User                │ │ │
│ │                      │  │ │ ─────────────────────────    │ │ │
│ │                      │  │ │                             │ │ │
│ │                      │  │ │ ┌─────┬─────────────────┐   │ │ │
│ │                      │  │ │ │User │[  username  ]   │   │ │ │
│ │                      │  │ │ ├─────┼─────────────────┤   │ │ │
│ │                      │  │ │ │Mail │[  email     ]   │   │ │ │
│ │                      │  │ │ ├─────┼─────────────────┤   │ │ │
│ │                      │  │ │ │Pass │[  password  ]   │   │ │ │
│ │                      │  │ │ ├─────┼─────────────────┤   │ │ │
│ │                      │  │ │ │Type │[Select Type ]   │   │ │ │
│ │                      │  │ │ └─────┴─────────────────┘   │ │ │
│ │                      │  │ │                             │ │ │
│ │                      │  │ │ [Create User] [Cancel]      │ │ │
│ │                      │  │ │                             │ │ │
│ │                      │  │ └─────────────────────────────┘ │ │
│ │                      │  │                                 │ │
│ └──────────────────────┘  └─────────────────────────────────┘ │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

## Form Validation Flow

```
Form Input Change
    │
    ▼
FormControl.setValue()
    │
    ├─ Run Validators
    │   ├─ Required validator
    │   ├─ Email validator
    │   ├─ Min length validator
    │   └─ Custom validators
    │
    ▼
Update FormControl.errors
    │
    ▼
Template detects change
    │
    ▼
Display validation messages
    │
    ├─ If error: Show error message (red)
    └─ If valid: Remove error message
    │
    ▼
Update Submit button state
    │
    ├─ If form valid: Enable submit
    └─ If form invalid: Disable submit
```

## Search Flow

```
User types in search box
    │
    ▼
Input event triggered
    │
    ▼
Update searchQuery property
    │
    ▼
searchUsers() called
    │
    ├─ If empty: loadUsers() [show all]
    └─ If text: Make API call
    │
    ▼
UserService.searchUsers(query)
    │
    ▼
HTTP GET /settings/users/search?search={query}
    │
    ▼
Backend searches database
    │
    ▼
Backend returns filtered User[]
    │
    ▼
Set this.users = filteredUsers
    │
    ▼
Template updates list
    │
    ▼
Display search results
```

## State Management

```
Component State Variables:
├── users: User[] - All loaded users
├── selectedUser: User | null - Currently selected user
├── searchQuery: string - Search text
├── loading: boolean - Loading indicator
├── errorMessage: string - Error display
├── successMessage: string - Success display
├── showUserForm: boolean - Toggle form visibility
├── isEditMode: boolean - Edit vs Add mode
└── userForm: FormGroup - Reactive form

Form State:
├── FormGroup
│   ├── username: FormControl
│   ├── firstName: FormControl
│   ├── lastName: FormControl
│   ├── email: FormControl
│   ├── phone: FormControl
│   ├── password: FormControl
│   ├── confirmPassword: FormControl
│   ├── userType: FormControl
│   └── enabled: FormControl
└── Validators:
    ├── Required
    ├── Email
    ├── MinLength(6)
    └── PasswordMatch
```

## File Structure Map

```
/src/main/resources/static/angular/src/app/
│
├── components/
│   ├── user/
│   │   ├── user.component.ts       (287 lines)
│   │   │   └─ 3 classes
│   │   │      ├─ User (interface)
│   │   │      └─ UserComponent
│   │   ├── user.component.html     (245 lines)
│   │   │   └─ Template structure
│   │   └── user.component.css      (103 lines)
│   │       └─ Styling & animations
│   │
│   ├── setup/
│   │   └─ Includes Users link
│   │
│   └── other components...
│
├── services/
│   ├── user.service.ts            (127 lines)
│   │   ├─ User (interface)
│   │   └─ UserService (class)
│   │       └─ 13 API methods
│   │
│   └── other services...
│
└── app.routes.ts                  (Updated)
    └─ Added /setup/users route
```

## Component Lifecycle

```
┌─────────────────────────────────────────────────────┐
│            Angular Component Lifecycle              │
├─────────────────────────────────────────────────────┤
│                                                     │
│ constructor()                                       │
│    │ Create component instance                     │
│    ▼                                               │
│ ngOnInit()                                          │
│    │ Initialize component                          │
│    │ loadUsers() → Fetch data from API             │
│    ▼                                               │
│ ngAfterViewInit()                                   │
│    │ View initialization complete                  │
│    ▼                                               │
│ [Component active]                                  │
│    │ User interactions                             │
│    │ - Click buttons                               │
│    │ - Type in forms                               │
│    │ - Select users                                │
│    ▼                                               │
│ ngOnDestroy()                                       │
│    │ Cleanup subscriptions                         │
│    └─ Component destroyed                          │
│                                                     │
└─────────────────────────────────────────────────────┘
```

## Responsive Breakpoints

```
Mobile (< 768px)
├─ Single column layout
├─ User list on top
├─ Details/form below
└─ Stack vertically

Tablet (768px - 1024px)
├─ Two columns
├─ User list: 50%
├─ Details: 50%
└─ Side by side

Desktop (> 1024px)
├─ Two columns
├─ User list: 40%
├─ Details: 60%
└─ Optimal spacing
```

## Error Handling Tree

```
Operation (Load/Save/Delete)
    │
    ├─ Success Response
    │   ├─ Update UI
    │   ├─ Show success message
    │   └─ Refresh data
    │
    └─ Error Response
        ├─ Network Error
        │   └─ "Failed to connect. Please try again."
        │
        ├─ 400 Bad Request
        │   └─ "Invalid input. Check your data."
        │
        ├─ 401 Unauthorized
        │   └─ "You are not authorized to perform this action."
        │
        ├─ 404 Not Found
        │   └─ "User not found."
        │
        ├─ 409 Conflict
        │   └─ "Email already exists."
        │
        ├─ 500 Server Error
        │   └─ "Server error. Please try again later."
        │
        └─ Unknown Error
            └─ "An unexpected error occurred."
```

---

This visual guide helps understand the complete architecture, data flow, and component structure of the User Management system.
