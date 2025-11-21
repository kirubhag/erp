# Unused Files Analysis Report

## Summary
This report identifies unused files and dependencies in the ERP project that can be safely removed.

---

## 1. Unused Vendor Libraries (CONFIRMED SAFE TO REMOVE)

### jQuery and jQuery UI
**Total Size:** 372 KB (88 KB + 284 KB)

**Files to Remove:**
- `/src/main/resources/static/angular/src/vendor/jquery/` (88 KB)
- `/src/main/resources/static/angular/src/vendor/jquery-ui/` (284 KB)

**Verification:**
- ✅ No usage of `$()` or `jQuery` found in any TypeScript files
- ✅ No usage found in any HTML component templates
- ✅ Not listed in package.json dependencies
- ✅ Only referenced in index.html (3 lines to be removed)

**index.html References to Remove:**
```html
Line 13: <link rel="stylesheet" href="./vendor/jquery-ui/jquery-ui.min.css">
Line 19: <script src="/vendor/jquery/jquery.min.js"></script>
Line 22: <script src="/vendor/jquery-ui/jquery-ui.min.js"></script>
```

**Impact:** Reduces page load by 372 KB and 3 HTTP requests

---

## 2. Outdated Test Files (RECOMMEND UPDATE)

### app.component.spec.ts
**Status:** Contains outdated default Angular CLI tests

**Issues:**
- Tests expect title 'erp-frontend' (needs verification)
- Tests expect h1 with 'Hello, erp-frontend' (doesn't match actual app)
- Appears to be unchanged from Angular CLI scaffolding

**Recommendation:** Either update tests to match actual application or remove if not actively testing

### grid-view.component.spec.ts
**Status:** Needs analysis

**Recommendation:** Review and update or remove based on testing strategy

---

## 3. Component Usage Verification

### Verified as USED (Do NOT Remove):
- ✅ `student-list.component` - Route: `/students`
- ✅ `import-history.component` - Route: `/setup/import-history`

### Requires Verification (30 components total):
All 30 components in `/src/app/components/` need systematic route verification to identify any orphaned components.

---

## 4. Recommendations

### Immediate Actions (High Priority):

1. **Remove jQuery/jQuery UI**
   ```bash
   rm -rf src/main/resources/static/angular/src/vendor/jquery
   rm -rf src/main/resources/static/angular/src/vendor/jquery-ui
   ```
   Then remove the 3 references from index.html

   **Space Saved:** 372 KB
   **Performance Gain:** 3 fewer HTTP requests on page load

2. **Consider Bootstrap from CDN**
   - Current vendor size: 308 KB
   - Could use Bootstrap CDN instead for better caching
   - **Not removing yet** - need to verify no custom modifications

### Medium Priority:

3. **Update or Remove Test Files**
   - Review app.component.spec.ts and update tests
   - Review grid-view.component.spec.ts
   - Decide on testing strategy going forward

4. **Complete Component Audit**
   - Systematically check all 30 components for route registration
   - Identify any truly orphaned components
   - Remove unused components and their associated files

### Low Priority:

5. **Assets Directory Review**
   - Check for unused images
   - Check for unused icons/fonts
   - Remove any orphaned asset files

---

## 5. Before/After Impact

### Before:
- Total vendor size: 1.0 MB
- jQuery dependencies: 372 KB (unused)
- index.html: 3 unnecessary script/link tags
- Test files: 2 (1 confirmed outdated)

### After (if all recommendations implemented):
- Total vendor size: ~640 KB
- jQuery dependencies: Removed (372 KB saved)
- index.html: Cleaned up
- Test files: Updated or removed
- Component count: Potentially reduced

---

## 6. Risk Assessment

### Low Risk (Safe to Remove):
- ✅ jQuery and jQuery UI - **No code dependencies found**

### Medium Risk (Needs Review):
- ⚠️ Test files - May want to update rather than remove
- ⚠️ Bootstrap vendor files - Check if using CDN is better

### High Risk (Requires Thorough Verification):
- ⛔ Component files - Must verify routing before removal
- ⛔ Service files - Must check injection usage
- ⛔ Asset files - May be referenced dynamically

---

## Next Steps

1. **Execute jQuery removal** (confirmed safe)
2. **Continue component audit** (verify all 28 remaining components)
3. **Review test strategy** (update or remove spec files)
4. **Assets cleanup** (check for unused images/files)
5. **Document changes** (commit with detailed message)

---

Generated: $(date)
