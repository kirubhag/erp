#!/usr/bin/env node

/**
 * Post-build script to organize and deploy Angular build artifacts
 * 1. Renames chunk files from chunk-HASH.js to erp-app-HASH.js
 * 2. Copies all build artifacts to the static folder
 * 3. Updates index.html references
 */

const fs = require('fs');
const path = require('path');

// Directories
const distDir = path.join(__dirname, 'dist', 'erp-frontend', 'browser');
const staticDir = path.join(__dirname, '..', '..', 'static');

console.log('🚀 Starting build organization and deployment...\n');

// Step 1: Verify build directory exists
if (!fs.existsSync(distDir)) {
    console.log('✗ Build directory not found:', distDir);
    console.log('  Run "npm run build:original" first');
    process.exit(1);
}

console.log('✓ Build directory found:', distDir);

// Step 2: Rename chunk files
const files = fs.readdirSync(distDir);
const renamedFiles = [];

console.log('\n📝 Renaming chunk files...');
files.forEach(file => {
    if (file.startsWith('chunk-') && (file.endsWith('.js') || file.endsWith('.js.map'))) {
        const newName = file.replace('chunk-', 'erp-app-');
        const oldPath = path.join(distDir, file);
        const newPath = path.join(distDir, newName);

        fs.renameSync(oldPath, newPath);
        renamedFiles.push({ old: file, new: newName });
        console.log(`  ✓ ${file} → ${newName}`);
    }
});

console.log(`\n✓ Renamed ${renamedFiles.length} chunk files`);

// Step 3: Update index.html references
const indexPath = path.join(distDir, 'index.html');
if (fs.existsSync(indexPath)) {
    let indexContent = fs.readFileSync(indexPath, 'utf8');
    let updated = false;

    renamedFiles.forEach(({ old, new: newName }) => {
        if (indexContent.includes(old)) {
            indexContent = indexContent.replace(new RegExp(old, 'g'), newName);
            updated = true;
        }
    });

    if (updated) {
        fs.writeFileSync(indexPath, indexContent);
        console.log('✓ Updated index.html with new chunk names');
    }
}

// Step 4: Copy all files to static directory
console.log('\n📦 Copying files to static directory...');

const filesToCopy = fs.readdirSync(distDir);
let copiedCount = 0;

filesToCopy.forEach(file => {
    const srcPath = path.join(distDir, file);
    const destPath = path.join(staticDir, file);

    // Skip directories (like assets, vendor, etc. - they need special handling)
    const stat = fs.statSync(srcPath);
    if (stat.isDirectory()) {
        return;
    }

    fs.copyFileSync(srcPath, destPath);
    copiedCount++;

    // Show progress for important files
    if (file === 'index.html' || file.startsWith('main-') || file.startsWith('polyfills-') ||
        file.startsWith('styles-') || file.startsWith('erp-app-')) {
        console.log(`  ✓ ${file}`);
    }
});

console.log(`\n✓ Copied ${copiedCount} files to static directory`);

// Step 5: Copy asset directories
console.log('\n📁 Copying asset directories...');

const dirsToCopy = ['assets', 'vendor'];
dirsToCopy.forEach(dir => {
    const srcDir = path.join(distDir, dir);
    const destDir = path.join(staticDir, dir);

    if (fs.existsSync(srcDir)) {
        copyDirectoryRecursive(srcDir, destDir);
        console.log(`  ✓ ${dir}/`);
    }
});

// Step 6: Generate file manifest
console.log('\n📋 Generating file manifest...');

// Re-read the dist directory to get the renamed files
const manifestFiles = fs.readdirSync(distDir);

const manifest = {
    buildDate: new Date().toISOString(),
    files: {
        main: manifestFiles.filter(f => f.startsWith('main-') && f.endsWith('.js')),
        polyfills: manifestFiles.filter(f => f.startsWith('polyfills-') && f.endsWith('.js')),
        styles: manifestFiles.filter(f => f.startsWith('styles-') && f.endsWith('.css')),
        chunks: manifestFiles.filter(f => f.startsWith('erp-app-') && f.endsWith('.js')).sort()
    },
    totalChunks: manifestFiles.filter(f => f.startsWith('erp-app-') && f.endsWith('.js')).length
};

const manifestPath = path.join(staticDir, 'build-manifest.json');
fs.writeFileSync(manifestPath, JSON.stringify(manifest, null, 2));
console.log(`  ✓ Created build-manifest.json`);

// Summary
console.log('\n' + '='.repeat(60));
console.log('✅ BUILD ORGANIZATION AND DEPLOYMENT COMPLETE');
console.log('='.repeat(60));
console.log(`\n📊 Summary:`);
console.log(`  • Renamed chunks: ${renamedFiles.length}`);
console.log(`  • Total chunks: ${manifest.totalChunks}`);
console.log(`  • Files copied: ${copiedCount}`);
console.log(`  • Main bundles: ${manifest.files.main.length}`);
console.log(`  • Polyfills: ${manifest.files.polyfills.length}`);
console.log(`  • Stylesheets: ${manifest.files.styles.length}`);
console.log(`\n📍 Deployment location: ${staticDir}`);
console.log(`\n✨ Your ERP application is ready to serve!\n`);

// Helper function to copy directories recursively
function copyDirectoryRecursive(src, dest) {
    if (!fs.existsSync(dest)) {
        fs.mkdirSync(dest, { recursive: true });
    }

    const entries = fs.readdirSync(src, { withFileTypes: true });

    for (const entry of entries) {
        const srcPath = path.join(src, entry.name);
        const destPath = path.join(dest, entry.name);

        if (entry.isDirectory()) {
            copyDirectoryRecursive(srcPath, destPath);
        } else {
            fs.copyFileSync(srcPath, destPath);
        }
    }
}
