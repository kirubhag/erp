package krs.erp.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
// no MockFilterChain needed
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;

public class AccessLoggingFilterTest {

    @Test
    public void filter_runs_without_exceptions_and_copies_response() throws ServletException, IOException {
        AccessLoggingFilter filter = new AccessLoggingFilter();

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setRequestURI("/test-path");

        MockHttpServletResponse resp = new MockHttpServletResponse();

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                // simulate a controller writing a small response
                response.setContentType("text/plain");
                response.getWriter().write("hello");
                response.getWriter().flush();
                ((HttpServletResponse) response).setStatus(200);
            }
        };

        filter.doFilter(req, resp, chain);

        // After filter execution, response should have been populated and status 200
        assertThat(resp.getContentAsString()).isEqualTo("hello");
        assertThat(resp.getStatus()).isEqualTo(200);
    }
}
