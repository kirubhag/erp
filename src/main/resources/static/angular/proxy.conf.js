module.exports = {
  "/api": {
    target: "http://localhost:8081",
    secure: false,
    logLevel: "debug",
    changeOrigin: true,
    cookieDomainRewrite: "localhost",
    cookiePathRewrite: "/",
    bypass: function(req, res, proxyOptions) {
      if (req.headers['cookie']) {
        return null;
      }
    }
  },
  "/settings": {
    target: "http://localhost:8081",
    secure: false,
    logLevel: "debug",
    changeOrigin: true,
    cookieDomainRewrite: "localhost",
    cookiePathRewrite: "/",
    bypass: function(req, res, proxyOptions) {
      // Bypass proxy for Angular routes - serve index.html instead
      if (req.url.startsWith('/settings/auth/')) {
        console.log('Bypassing proxy for Angular route:', req.url);
        return '/index.html';
      }
      if (req.headers['cookie']) {
        return null;
      }
    }
  }
};
