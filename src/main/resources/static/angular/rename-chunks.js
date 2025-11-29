#!/usr/bin/env node

/**
 * Post-build script to rename Angular chunk files
 * Changes: chunk-HASH.js -> erp-app-HASH.js
 */

const fs = require('fs');
const path = require('path');

const distDir = path.join(__dirname, 'dist', 'erp-frontend', 'browser');

if (!fs.existsSync(distDir)) {
    console.log('✗ Build directory not found:', distDir);
    process.exit(1);
}

// Get all files in the build directory
const files = fs.readdirSync(distDir);
const renamedFiles = [];

// Rename chunk-*.js and chunk-*.js.map files
files.forEach(file => {
    if (file.startsWith('chunk-') && (file.endsWith('.js') || file.endsWith('.js.map'))) {
        const newName = file.replace('chunk-', 'erp-app-');
        const oldPath = path.join(distDir, file);
        const newPath = path.join(distDir, newName);
        
        fs.renameSync(oldPath, newPath);
        renamedFiles.push({ old: file, new: newName });
        console.log(`✓ Renamed: ${file} -> ${newName}`);
    }
});

// Update references in index.html
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

console.log(`\n✓ Renamed ${renamedFiles.length} chunk files to erp-app-* format`);
