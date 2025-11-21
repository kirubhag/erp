# Import Wizard Route Configuration - Diagnostic Guide

## Issue
```
ERROR RuntimeError: NG04002: Cannot match any routes. URL Segment: 'import-wizard'
```

## Root Cause
The development server has cached the old routing configuration without the import-wizard route.

## Solution Applied
✅ Added import-wizard route to `src/main/resources/static/angular/src/app/app.routes.ts`

### Changes Made:

**File: app.routes.ts**

1. Added import:
```typescript
import { ImportWizardComponent } from './components/import-wizard/import-wizard.component';
```

2. Added route to routes array:
```typescript
{ path: 'import-wizard', component: ImportWizardComponent, canActivate: [AuthGuard] }
```

### Route Details:
- **Path**: `/import-wizard`
- **Component**: `ImportWizardComponent` (standalone)
- **Guard**: `AuthGuard` (requires authentication)
- **Location**: Before 'setup' routes for proper matching precedence

### Component Verification:
✅ Component file exists: `src/main/resources/static/angular/src/app/components/import-wizard/import-wizard.component.ts`
✅ Component is standalone: `standalone: true`
✅ Component properly imports all step components
✅ Component is properly exported

## To Fix the Error

### Option 1: Hard Refresh Browser (Recommended)
```
1. Press Ctrl+Shift+R (Windows/Linux) or Cmd+Shift+R (Mac)
   OR
2. Open Developer Tools (F12 → Network tab → Disable cache)
3. Refresh the browser (Ctrl+R or Cmd+R)
```

### Option 2: Restart Development Server
```bash
# Stop the current development server (Ctrl+C)
# Then restart:
ng serve
# or
npm start
```

### Option 3: Clear Application Cache
```
1. Open DevTools (F12)
2. Go to Application tab
3. Clear Storage → Clear site data
4. Refresh the page
```

## Verification After Fix

Once the route loads correctly, you should be able to:

1. ✅ Click the "Import" button in the student list
2. ✅ Navigate to `/import-wizard?entityType=students`
3. ✅ See the import wizard 4-step interface
4. ✅ Upload CSV file with student data
5. ✅ Map columns to entity fields
6. ✅ Review and confirm import
7. ✅ View import results

## Route Configuration

The route is now properly registered in the application:
- Imported in: `app.routes.ts` (line 19)
- Registered in: `routes` array (line 33)
- Protected by: `AuthGuard` (authentication required)
- Navigation works with: `router.navigate(['/import-wizard'], { queryParams: { entityType: 'students' } })`

## Component Files
```
src/main/resources/static/angular/src/app/components/import-wizard/
├── import-wizard.component.ts (main component)
├── import-step-1/ (file upload)
├── import-step-2/ (field mapping)
├── import-step-3/ (confirmation)
└── import-step-4/ (results)
```

## Git Commit
```
Commit: 4dd08dc
Message: fix: Add import-wizard route to application routing
```

---

**Status**: Route properly configured ✅
**Next Step**: Perform browser refresh to load new route configuration
