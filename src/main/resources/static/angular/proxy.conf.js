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
  }
};
