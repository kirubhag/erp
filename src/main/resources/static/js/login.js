// Login Page JavaScript

// Get CSRF token from cookie
function getCookie(name) {
    const value = "; " + document.cookie;
    const parts = value.split("; " + name + "=");
    if (parts.length === 2) {
        return parts.pop().split(";").shift();
    }
    return null;
}

// Set CSRF token in form on page load
window.addEventListener('DOMContentLoaded', function() {
    // Fetch CSRF token from the server
    fetch('/csrf', {
        method: 'GET',
        credentials: 'include'
    })
    .then(response => response.json())
    .then(data => {
        if (data && data.token) {
            document.getElementById('csrf-token').value = data.token;
        }
    })
    .catch(error => {
        console.error('Error fetching CSRF token:', error);
        // Fallback to cookie
        const csrfToken = getCookie('XSRF-TOKEN');
        if (csrfToken) {
            document.getElementById('csrf-token').value = csrfToken;
        }
    });
});

// Show error message if login failed
const urlParams = new URLSearchParams(window.location.search);
if (urlParams.get('error') !== null) {
    document.getElementById('errorAlert').style.display = 'block';
}

// Show logout message if logged out
if (urlParams.get('logout') !== null) {
    document.getElementById('logoutAlert').style.display = 'block';
}
