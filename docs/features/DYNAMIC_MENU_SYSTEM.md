# Dynamic Menu System

## Overview
The application now features a fully database-driven dynamic menu system that replaces the previous static hardcoded navigation. Menu items are stored in the `erp_entities` table and loaded dynamically via REST API.

## Features

### 1. Database-Driven Menus
- All menu items stored in `erp_entities` table
- Menu configuration includes:
  - `sequence` - Display order (1, 2, 3, etc.)
  - `system_name` - Internal identifier (e.g., 'student', 'attendance')
  - `presence` - Visibility flag (true/false)
  - `icon` - FontAwesome icon class (e.g., 'fas fa-user-graduate')
  - `route` - Angular route path (e.g., '#!/students')

### 2. Automatic Overflow Handling
- Menus that don't fit in navbar automatically move to "More" dropdown
- Responsive calculation based on navbar width
- Recalculates on window resize
- Smooth Bootstrap dropdown integration

### 3. Role-Based Filtering (Infrastructure Ready)
- Backend endpoints support role-based menu filtering
- `findActiveMenuItemsByRoleIds()` repository method
- `POST /api/erp-entities/menu-items/by-roles` endpoint
- Can be activated by passing user role IDs to API

### 4. Menu Caching
- `MenuService` caches menu items after first load
- Reduces API calls
- `refresh()` method available to reload menus
- `clearCache()` for manual cache invalidation

## Technical Architecture

### Backend Components

#### 1. ErpEntity Model (`ErpEntity.java`)
```java
// New fields added
private Integer sequence;          // Display order
private String systemName;         // Internal identifier
private Boolean presence = true;   // Visibility flag
private String icon;              // FontAwesome icon class
private String route;             // Angular route
```

#### 2. Repository Methods (`ErpEntityRepository.java`)
```java
// Fetch all active menu items ordered by sequence
@Query("SELECT e FROM ErpEntity e WHERE e.isActive = true AND e.presence = true ORDER BY e.sequence ASC")
List<ErpEntity> findActiveMenuItems();

// Fetch menu items filtered by roles
@Query("SELECT DISTINCT e FROM ErpEntity e JOIN e.roles r WHERE e.isActive = true AND e.presence = true AND r.id IN :roleIds ORDER BY e.sequence ASC")
List<ErpEntity> findActiveMenuItemsByRoleIds(@Param("roleIds") List<Long> roleIds);
```

#### 3. REST API Endpoints (`ErpEntityController.java`)
```java
// GET /api/erp-entities/menu-items
// Returns all active menu items

// POST /api/erp-entities/menu-items/by-roles
// Accepts: List<Long> roleIds
// Returns: Menu items accessible to those roles
```

#### 4. Service Layer (`ErpEntityService.java`)
```java
public List<ErpEntity> getActiveMenuItems()
public List<ErpEntity> getActiveMenuItemsByRoles(List<Long> roleIds)
```

### Frontend Components

#### 1. MenuService (`menu.service.js`)
Angular service for menu data management:
- `getMenuItems()` - Fetch all menu items with caching
- `getMenuItemsByRoles(roleIds)` - Fetch role-filtered menus
- `clearCache()` - Clear cached data
- `refresh()` - Reload menu items

#### 2. MainController Updates (`main.controller.js`)
Added menu management logic:
- `loadMenuItems()` - Load menus on init
- `calculateMenuOverflow()` - Determine visible vs overflow items
- `isMenuItemActive(menuItem)` - Check if menu matches current route
- Window resize listener for responsive recalculation

#### 3. Dynamic Navbar (`index.html`)
Replaced static menu items with:
```html
<!-- Visible menu items -->
<li class="nav-item" ng-repeat="menuItem in visibleMenuItems">
    <a class="nav-link" ng-href="{{menuItem.route}}" 
       ng-class="{active: isMenuItemActive(menuItem)}">
        <i class="{{menuItem.icon}} me-1"></i>{{menuItem.pluralName}}
    </a>
</li>

<!-- Overflow dropdown -->
<li class="nav-item dropdown" ng-show="hasOverflow">
    <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
        <i class="fas fa-ellipsis-h me-1"></i>More
    </a>
    <ul class="dropdown-menu">
        <li ng-repeat="menuItem in overflowMenuItems">
            <a class="dropdown-item" ng-href="{{menuItem.route}}">
                <i class="{{menuItem.icon}} me-2"></i>{{menuItem.pluralName}}
            </a>
        </li>
    </ul>
</li>
```

## Database Migration

### V005 Migration Script
Location: `src/main/resources/db/migration/V005__Add_Menu_Columns_And_Seed_Data.sql`

**Schema Changes:**
```sql
ALTER TABLE erp_entities 
    ADD COLUMN sequence INT DEFAULT 0,
    ADD COLUMN system_name VARCHAR(100),
    ADD COLUMN presence BOOLEAN DEFAULT TRUE,
    ADD COLUMN icon VARCHAR(100),
    ADD COLUMN route VARCHAR(255);
```

**Seeded Menu Items:**
1. Dashboard (sequence=1, icon='fas fa-tachometer-alt', route='#!/')
2. Students (sequence=2, icon='fas fa-user-graduate', route='#!/students')
3. Attendance (sequence=3, icon='fas fa-calendar-check', route='#!/attendance')
4. Parents (sequence=4, icon='fas fa-users', route='#!/parents')
5. Subjects (sequence=5, icon='fas fa-book', route='#!/subjects')
6. Timetables (sequence=6, icon='fas fa-calendar', route='#!/timetables')
7. Health Records (sequence=7, icon='fas fa-heartbeat', route='#!/health')
8. Staff (sequence=8, icon='fas fa-user-tie', route='#!/staff')
9. Settings (sequence=99, presence=false) - Shown separately in right navbar

## Usage

### Adding New Menu Item
1. Insert into `erp_entities` table:
```sql
INSERT INTO erp_entities (singular_name, plural_name, description, is_active, sequence, system_name, presence, icon, route) 
VALUES ('Class', 'Classes', 'Class management', TRUE, 5, 'class', TRUE, 'fas fa-chalkboard', '#!/classes');
```

2. Menu automatically appears on next page load (or refresh cache)

### Changing Menu Order
```sql
UPDATE erp_entities SET sequence = 3 WHERE system_name = 'attendance';
UPDATE erp_entities SET sequence = 4 WHERE system_name = 'parents';
```

### Hiding Menu Item
```sql
UPDATE erp_entities SET presence = FALSE WHERE system_name = 'health_record';
```

### Refreshing Menu Cache
In browser console:
```javascript
angular.element(document.body).injector().get('MenuService').refresh();
```

## Configuration

### Overflow Detection Settings
In `main.controller.js`, adjust these values:
```javascript
var availableWidth = navbarWidth - 150; // Reserve space for "More" button
var itemWidth = 120; // Approximate width per menu item
```

### Menu Item Width Calculation
Currently uses fixed 120px per item. For dynamic calculation:
1. Measure actual DOM element widths
2. Consider text length and icon width
3. Add padding/margin

## Testing

### Manual Testing Checklist
- [ ] Menus load on application start
- [ ] Menu items display in correct sequence
- [ ] Icons appear correctly
- [ ] Clicking menu navigates to correct route
- [ ] Active menu highlighted based on current route
- [ ] Overflow works when window resized to narrow width
- [ ] "More" dropdown shows overflow items
- [ ] Settings icon remains in right navbar
- [ ] Menu cache works (check network tab - only one API call)

### Browser Compatibility
- Chrome ✓
- Firefox ✓
- Safari ✓
- Edge ✓

## Future Enhancements

### Planned Features
1. **Role-Based Menu Filtering** - Activate role filtering in frontend
2. **User Preferences** - Save user's preferred menu order
3. **Menu Icons Management** - Admin UI to change icons
4. **Nested Menus** - Support for sub-menus/dropdowns
5. **Menu Analytics** - Track which menus are most used
6. **Dynamic Badge Counts** - Show notifications (e.g., "5 new students")
7. **Menu Search** - Quick search for menu items in "More" dropdown
8. **Keyboard Navigation** - Keyboard shortcuts for menus

### Performance Optimizations
1. Lazy load menu data with $routeProvider resolve
2. Server-side caching with ETag headers
3. WebSocket updates for real-time menu changes
4. LocalStorage caching for offline support

## Troubleshooting

### Menus Not Appearing
1. Check database migration ran successfully:
   ```sql
   SELECT * FROM flyway_schema_history WHERE script = 'V005__Add_Menu_Columns_And_Seed_Data.sql';
   ```
2. Verify data exists:
   ```sql
   SELECT * FROM erp_entities WHERE presence = TRUE ORDER BY sequence;
   ```
3. Check browser console for API errors
4. Verify MenuService is loaded (check index.html script tag)

### Overflow Not Working
1. Check navbar width calculation in browser DevTools
2. Verify `$window` and `$timeout` are injected in MainController
3. Test with different screen sizes
4. Check for CSS conflicts affecting navbar width

### Active Menu Not Highlighting
1. Verify route matches menu item's `route` field
2. Check `isMenuItemActive()` logic in MainController
3. Ensure CSS `.active` class is defined
4. Check AngularJS `ng-class` binding in DevTools

## Related Documentation
- [Entity Management System](./ENTITY_MANAGEMENT.md)
- [Custom View System](./CUSTOM_VIEW_SYSTEM.md)
- [Theme System](../development/THEME_SYSTEM.md)
- [Database Migrations](../development/DATABASE_MIGRATIONS.md)

## Commit History
- Initial implementation: commit `45c6fa0`
- Database migration V005
- Frontend dynamic rendering with overflow handling
