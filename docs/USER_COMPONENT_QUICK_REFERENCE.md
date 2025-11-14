# User Component - Quick Reference Guide

## Accessing the User Management Page

**URL:** `http://localhost:4200/setup/users`

**Navigation:**
1. Login to the application
2. Go to Settings/Setup
3. Click on "Users" under General section

## User Management Tasks

### ➕ Add a New User

1. Click **"+ New User"** button in the left panel
2. Fill in the form:
   - **Username** (required) - Unique identifier
   - **Email** (required) - Valid email address
   - **Password** (required) - Min 6 characters
   - **Confirm Password** (required) - Must match password
   - **First Name** (optional)
   - **Last Name** (optional)
   - **Phone** (optional) - Contact number
   - **User Type** (optional) - Admin, Manager, Teacher, Student, Staff
   - **Enabled** - Check to activate user on creation
3. Click **"Create User"** button
4. Success message confirms creation

### ✏️ Edit a User

1. Click on a user in the list to select them
2. Review their details on the right panel
3. Click **"Edit"** button
4. Modify the form fields
5. **Password** field is optional (leave blank to keep current)
6. Click **"Update User"** button
7. Success message confirms update

### 🔒 Deactivate a User

1. Select the user from the list
2. Click **"Deactivate"** button (red icon)
3. Confirm the action in the dialog
4. User status changes to "Inactive"
5. User can no longer log in

### 🔓 Activate a User

1. Select the inactive user from the list
2. Click **"Activate"** button (green icon)
3. User status changes to "Active"
4. User can now log in

### 🗑️ Delete a User

1. Select the user from the list
2. Click **"Delete"** button
3. Confirm the deletion in the dialog
4. User is permanently removed
5. List automatically refreshes

### 🔍 Search for Users

1. Type in the search box at the top of the user list
2. Results filter in real-time
3. Search works by:
   - Username
   - Email address
   - First name
   - Last name
4. Clear the search box to see all users again

## User Information Display

### User Details Section Shows:

**Basic Information:**
- Full name (or username)
- Email address
- Phone number
- User type/role
- Current status (Active/Inactive)

**Account Status:**
- Account Expired: Yes/No
- Account Locked: Yes/No
- Credentials Expired: Yes/No
- Last Login Date
- Password Changed Date

**Roles:**
- List of assigned roles/permissions

## Form Validation Rules

| Field | Rules | Example |
|-------|-------|---------|
| Username | Required, unique | john_doe |
| Email | Required, valid format | john@school.edu |
| Password (Add) | Required, min 6 chars | SecurePass123! |
| Confirm Password (Add) | Must match password | SecurePass123! |
| Password (Edit) | Optional, min 6 chars | Leave blank to keep |
| First Name | Optional, any text | John |
| Last Name | Optional, any text | Doe |
| Phone | Optional, any format | +1-234-567-8900 |
| User Type | Optional dropdown | Admin, Manager, etc. |

## Status Indicators

| Badge | Color | Meaning |
|-------|-------|---------|
| Active | Green | User can login |
| Inactive | Red | User cannot login |
| [✓] | Checked | Account is not expired/locked |
| [✗] | Unchecked | Account is expired/locked |

## Common Actions

### Bulk Operations
*Coming Soon*
- Select multiple users
- Activate/Deactivate in bulk
- Delete multiple users

### User Groups
*Coming Soon*
- Assign users to groups
- Manage group permissions
- Group-based access control

### Role Management
*Coming Soon*
- Assign roles to users
- Create custom roles
- Role-based access control

## API Endpoints Reference

```
GET    /settings/users                      - List all users
POST   /settings/users                      - Create user
GET    /settings/users/{id}                 - Get user details
PUT    /settings/users/{id}                 - Update user
DELETE /settings/users/{id}                 - Delete user
PUT    /settings/users/{id}/activate        - Activate user
PUT    /settings/users/{id}/deactivate      - Deactivate user
PUT    /settings/users/{id}/reset-password  - Reset password
GET    /settings/users/search?search=query  - Search users
GET    /settings/users/role/{role}          - Get users by role
```

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| Ctrl/Cmd + K | Focus search box (coming soon) |
| Enter | Submit form |
| Escape | Close form / Cancel |
| Tab | Navigate form fields |

## Troubleshooting

### Issue: "Cannot find user" error
- **Solution:** Verify the user exists and is not deleted
- **Solution:** Check username spelling and case sensitivity

### Issue: "Email already exists" error
- **Solution:** Use a different email address
- **Solution:** Delete the old user first if no longer needed

### Issue: "Password mismatch" error
- **Solution:** Ensure password and confirm password match exactly
- **Solution:** Check for spaces or special characters

### Issue: "User cannot deactivate" error
- **Solution:** You may not have permission
- **Solution:** Contact your administrator

### Issue: Form won't submit
- **Solution:** Check for red validation messages
- **Solution:** Ensure all required fields are filled
- **Solution:** Verify email format is valid

### Issue: Changes not saving
- **Solution:** Check browser console for errors
- **Solution:** Verify backend API is running
- **Solution:** Check network connectivity

## Best Practices

✅ **DO:**
- Use strong, unique passwords
- Use descriptive usernames
- Assign appropriate user types
- Deactivate instead of delete when possible
- Review user access regularly
- Keep email addresses current

❌ **DON'T:**
- Reuse same password for multiple users
- Use generic usernames like "user1"
- Share user accounts
- Keep unused accounts active
- Store passwords in plain text
- Assign excessive permissions

## User Types

| Type | Purpose | Permissions |
|------|---------|-------------|
| Admin | System administrator | All permissions |
| Manager | Department manager | Management permissions |
| Teacher | Instructor/Educator | Teaching permissions |
| Student | Learner | Student permissions |
| Staff | Support staff | Limited permissions |

## Data Fields Explanation

| Field | Description |
|-------|-------------|
| Username | Unique login identifier |
| Email | Contact and recovery email |
| Phone | Contact telephone number |
| User Type | Role/category of user |
| First Name | Given name |
| Last Name | Family name |
| Enabled | Active/Inactive status |
| Account Non-Expired | Account has not expired |
| Credentials Non-Expired | Password has not expired |
| Account Non-Locked | Account is not locked |
| Last Login | Last login timestamp |
| Password Changed | Last password change timestamp |
| Roles | Assigned permissions/roles |

## Security Notes

🔒 **Password Security:**
- Passwords stored encrypted
- Never displayed in plaintext
- Minimum 6 characters enforced
- History tracked (password change date)

🔒 **Account Security:**
- Account expiration tracking
- Account lock status
- Credentials expiration
- Last login monitoring
- Deactivation available

🔒 **Data Protection:**
- Only admins can manage users
- Confirmation required for destructive actions
- All changes logged
- Session-based access

## Support & Help

**Component Documentation:**
- Full component README: `/docs/USER_COMPONENT_README.md`
- Implementation details: `/docs/USER_COMPONENT_IMPLEMENTATION_SUMMARY.md`

**Backend Endpoints:**
- Ensure backend is running on: `http://localhost:8081`
- All endpoints require authentication

**Contact:**
- For issues: Contact your system administrator
- For bugs: Report in GitHub issues
- For features: Submit enhancement requests

## Version Info

**Component Version:** 1.0.0  
**Angular Version:** 17.3.12  
**Bootstrap Version:** 5.3.3  
**Last Updated:** November 14, 2025  

---

**Happy Managing! 👥**
