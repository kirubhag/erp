const PROXY_CONFIG = {
  "/api": {
    target: "http://localhost:8081",
    secure: false,
    logLevel: "debug",
    changeOrigin: true,
    cookieDomainRewrite: "localhost",
    cookiePathRewrite: "/",
    onProxyReq: (proxyReq, req, res) => {
      // Forward all cookies from the original request
      if (req.headers.cookie) {
        proxyReq.setHeader('cookie', req.headers.cookie);
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
    onProxyReq: (proxyReq, req, res) => {
      // Forward all cookies from the original request
      if (req.headers.cookie) {
        proxyReq.setHeader('cookie', req.headers.cookie);
      }
    }
  }
};

module.exports = PROXY_CONFIG;
