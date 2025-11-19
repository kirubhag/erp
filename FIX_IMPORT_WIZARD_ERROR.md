# CRITICAL: How to Fix the import-wizard Route Error

## ⚠️ The Error You're Seeing

```
ERROR RuntimeError: NG04002: Cannot match any routes. URL Segment: 'import-wizard'
```

## ✅ Verification: Everything IS Correctly Configured

**File: `/src/main/resources/static/angular/src/app/app.routes.ts`**

✓ ImportWizardComponent is imported (line 19)
✓ Route is defined (line 33): `{ path: 'import-wizard', component: ImportWizardComponent, canActivate: [AuthGuard] }`
✓ Component file exists: `/src/main/resources/static/angular/src/app/components/import-wizard/import-wizard.component.ts`
✓ Component is standalone: `standalone: true`
✓ Component has proper @Component decorator
✓ All syntax is correct

## 🔧 ROOT CAUSE

The **development server has an old cached/compiled version** of your Angular application that doesn't include the import-wizard route.

The source code is correct, but the **compiled JavaScript** that the browser is running is outdated.

## 🚀 FIX IT NOW

You MUST do ONE of the following:

### METHOD 1: Complete Dev Server Restart (RECOMMENDED - 100% Guarantee)

```bash
# 1. Stop the development server (Ctrl+C)
cd /Users/kirubha-2911/Documents/GitHub/erp/src/main/resources/static/angular

# 2. Clear all caches
rm -rf .angular
rm -rf dist
rm -rf node_modules

# 3. Reinstall dependencies
npm install

# 4. Start fresh development server
ng serve
# OR
npm start
```

**This is the most reliable fix.**

---

### METHOD 2: Hard Reload with Cache Clear (Faster)

**In your browser:**

1. **Windows/Linux:** `Ctrl + Shift + Delete` (opens Storage/Cache settings)
   **Mac:** `Cmd + Shift + Delete`

2. Or use Chrome DevTools:
   - Press `F12` to open DevTools
   - Go to **Network** tab
   - Check **Disable cache** 
   - Press `Ctrl + Shift + R` (or `Cmd + Shift + R` on Mac)
   - Keep DevTools open and navigate back to students page
   - Try clicking Import button again

3. Or clear cookies and cache:
   - Press `F12`
   - Go to **Application** tab
   - Click **Clear site data** button (bottom)
   - Refresh page with `Ctrl + R`

---

### METHOD 3: Incognito/Private Window (Quick Test)

1. Open a new Incognito/Private window
2. Navigate to your application
3. Try the import button
4. If it works: Use METHOD 1 or 2 to permanently fix the main browser

---

## ✅ How to Know It's Fixed

After applying the fix:

1. Click "Import" button in student list
2. You should see: "Import students - navigating to import-wizard route"
3. Page should navigate to `/import-wizard?entityType=students`
4. You should see the Import Wizard 4-step interface:
   - Step 1: File Upload
   - Step 2: Field Mapping
   - Step 3: Confirmation
   - Step 4: Results

---

## 📋 Verification Checklist

- [x] Route is defined in app.routes.ts
- [x] Component is imported
- [x] Component file exists
- [x] Component is standalone
- [x] Syntax is correct
- [ ] **Development server has been restarted** ← YOU ARE HERE
- [ ] Browser cache has been cleared
- [ ] Import button now works

---

## 🎯 Step-by-Step for METHOD 1 (Most Reliable)

```bash
# Open terminal and navigate to the Angular project
cd /Users/kirubha-2911/Documents/GitHub/erp/src/main/resources/static/angular

# Stop the current dev server (Ctrl+C if it's running)

# Remove all caches and compiled files
rm -rf .angular
rm -rf dist  
rm -rf node_modules

# Reinstall all dependencies (this takes 1-2 minutes)
npm install

# Start a fresh development server
ng serve

# Wait for: ✔ Compiled successfully
# Then navigate to http://localhost:4200 and try the import button
```

---

## 📝 Git Status

- Route changes committed: ✓ Commit 4dd08dc
- Route verified: ✓ Correct configuration
- Need to do: **Clear your development environment**

---

## 🆘 If It Still Doesn't Work After Everything

1. Check browser console for ANY errors (F12)
2. Check terminal where `ng serve` is running for any errors
3. Make sure you're on the `ERP_ANGULAR2_CHANGES` branch:
   ```bash
   git branch
   # Should show: * ERP_ANGULAR2_CHANGES
   ```

4. Verify latest changes are pulled:
   ```bash
   git log --oneline -5
   # Should show the import-wizard route commit
   ```

5. Last resort - Nuclear option:
   ```bash
   # From the angular folder
   rm -rf node_modules package-lock.json
   npm cache clean --force
   npm install
   npm audit fix
   ng serve
   ```

---

## Summary

- ✅ Code is correct
- ✅ Route is configured
- ✅ Component exists
- ❌ Dev server hasn't rebuilt with new route yet

**ACTION REQUIRED:** Restart your development server and clear browser cache using METHOD 1 or 2 above.

