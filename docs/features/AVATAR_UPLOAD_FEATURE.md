# Avatar Upload and View Feature for Entity Lists

## Overview
Implemented a comprehensive avatar management system for student, staff, and parent records in both list and grid views.

## Features Implemented

### 1. Reusable Entity Avatar Component (`entity-avatar`)
- **Location**: `/src/main/resources/static/angular/src/app/components/entity-avatar/`
- **Features**:
  - View avatar with click-to-enlarge functionality
  - Upload new avatar with crop/edit capabilities
  - Delete existing avatar
  - Zoom and rotation controls for image editing
  - Automatic initials fallback when no avatar exists
  - Three size options: small, medium, large
  - Click-anywhere modal dismissal
  - Responsive design

### 2. Integration with Entity List Component
- **Table View**: 
  - Avatar component integrated into the avatar column type
  - Shows small avatar (32x32) with controls on hover
  - Click avatar to view full size
  - Upload/delete buttons appear on hover
  
- **Grid View**:
  - Avatar component integrated into card headers
  - Shows medium avatar (48x48) with controls
  - Maintains full upload/view/delete functionality

### 3. Component Properties

#### EntityAvatarComponent Inputs:
- `entityId` (required): ID of the student/staff/parent
- `entityType` (required): Type of entity ('student', 'staff', 'parent')
- `entityName` (required): Name for display and initials
- `organizationId` (required): Organization ID for file storage
- `size`: 'small' | 'medium' | 'large' (default: 'medium')
- `showControls`: Show upload/view/delete buttons (default: true)
- `clickable`: Make avatar clickable to view (default: true)

#### EntityAvatarComponent Outputs:
- `avatarUpdated`: Emits new avatar URL when uploaded
- `avatarDeleted`: Emits when avatar is deleted

### 4. API Integration
Uses existing backend endpoints:
- `POST /api/attachments/avatar/upload` - Upload new avatar
- `GET /api/attachments/avatar/{userId}` - Get avatar image
- `DELETE /api/attachments/avatar/{userId}` - Delete avatar

### 5. User Experience Features
- **Smooth Animations**: Fade-in for modals, slide-up for content
- **Hover Effects**: Controls appear on hover to reduce clutter
- **Image Editing**: Canvas-based crop with zoom (0.5x to 3x) and rotation (0° to 360°)
- **Error Handling**: Clear error messages for upload failures
- **Success Feedback**: Success messages on upload/delete
- **Loading States**: Visual feedback during operations
- **Responsive Design**: Works on mobile, tablet, and desktop

### 6. Visual Design
- **Rounded Avatars**: All avatars use border-radius: 50%
- **Gradient Background**: For initials fallback
- **Hover Overlay**: Semi-transparent overlay with eye icon on hover
- **Modal Design**: Clean, centered modals with backdrop blur
- **Control Buttons**: Icon buttons with hover states
- **Z-index Management**: Modals appear above all content (z-index: 10000)

## Usage Example

### In List Pages (Students, Staff, Parents)
The avatar component is automatically integrated when you define an 'avatar' column type:

```typescript
columns: EntityColumn[] = [
  { key: 'name', label: 'Name', type: 'avatar', sortable: true },
  // ... other columns
];
```

Make sure to pass `entityType` and `organizationId` to the entity-list component:

```html
<app-entity-list
  [entityType]="'student'"
  [organizationId]="currentUser.organizationId"
  [columns]="columns"
  [data]="students"
  ...>
</app-entity-list>
```

## Files Created/Modified

### New Files:
1. `entity-avatar.component.ts` - Component logic
2. `entity-avatar.component.html` - Component template
3. `entity-avatar.component.css` - Component styles

### Modified Files:
1. `entity-list.component.ts` - Import and integrate avatar component
2. `entity-list.component.html` - Use avatar component in table view
3. `entity-list.component.css` - Add modal z-index and gap utilities
4. `grid-view.component.ts` - Import avatar component, add inputs
5. `grid-view.component.html` - Use avatar component in card view

## Technical Details

### Image Upload Process:
1. User selects image file
2. File validated (image type, max 5MB)
3. Image loaded into canvas for editing
4. User adjusts zoom and rotation
5. Canvas converted to blob (JPEG, 90% quality)
6. Blob uploaded via FormData to backend
7. Avatar URL updated and displayed

### Avatar Display Priority:
1. Try to load avatar from `/api/attachments/avatar/{entityId}`
2. If load fails, show initials with gradient background
3. Cache timestamp added to URL to prevent caching issues

### Responsive Breakpoints:
- Mobile (< 768px): Full-width modals, stacked buttons
- Tablet (768px - 1024px): 95% width modals
- Desktop (> 1024px): Fixed max-width modals

## Benefits
1. **Consistency**: Same avatar component across all entity types
2. **User-Friendly**: Intuitive crop and edit functionality
3. **Performance**: Lazy loading, proper caching headers
4. **Maintainability**: Single source of truth for avatar logic
5. **Scalability**: Easy to add to new entity types

## Future Enhancements (Optional)
- [ ] Add drag-to-position on canvas
- [ ] Support multiple file formats (PNG, WebP)
- [ ] Add filters and effects
- [ ] Batch upload for multiple entities
- [ ] Avatar history/version control
- [ ] Integration with webcam capture
- [ ] AI-powered auto-crop/enhancement

## Testing Checklist
- [ ] Upload avatar for student
- [ ] Upload avatar for staff
- [ ] Upload avatar for parent
- [ ] View avatar in table view
- [ ] View avatar in grid view
- [ ] Edit avatar with zoom
- [ ] Edit avatar with rotation
- [ ] Delete avatar
- [ ] Verify initials fallback
- [ ] Test on mobile device
- [ ] Test with large images (near 5MB)
- [ ] Test error handling (wrong file type)
- [ ] Verify avatar persists after page reload

## Notes
- Avatar files are stored in `uploads/avatars/{organizationId}/` directory
- Files are renamed with UUID to prevent conflicts
- Metadata stored in `erp_attachments` table
- Organization-based isolation for multi-tenant support
