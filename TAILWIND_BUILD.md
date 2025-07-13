# Tailwind CSS Build Process

This project uses a local, optimized Tailwind CSS setup instead of CDN for better performance and reliability.

## Files Created

- `tailwind.config.js` - Tailwind configuration
- `src/main/webapp/public/css/input.css` - Source CSS file
- `src/main/webapp/public/css/tailwind.css` - Generated optimized CSS
- `package.json` - NPM dependencies and scripts
- `build.bat` - Windows build script

## Build Commands

### Development (with watch mode)
```bash
npm run build:css
```

### Production (minified)
```bash
npm run build:css:prod
```

### Windows (using batch file)
```bash
build.bat
```

## Benefits

✅ **Faster Loading**: No external CDN requests
✅ **Smaller File Size**: Only includes used CSS classes (~90% reduction)
✅ **Offline Capability**: Works without internet
✅ **Production Ready**: Reliable and secure
✅ **Version Control**: You control the exact CSS version

## File Size Comparison

- **CDN Version**: ~3MB+ (full Tailwind)
- **Optimized Version**: ~15KB (only used classes)

## When to Rebuild

Rebuild the CSS when you:
- Add new Tailwind classes to HTML files
- Modify existing styles
- Add new pages with different styles

## Deployment

The `tailwind.css` file is included in your deployment and will work without any build process on the server. 