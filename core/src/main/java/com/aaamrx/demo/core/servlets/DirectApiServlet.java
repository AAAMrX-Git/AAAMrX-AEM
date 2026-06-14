package com.aaamrx.demo.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component(
    service = { Servlet.class },
    property = {
        // We bind directly to an absolute path endpoint
        "sling.servlet.paths=/bin/myapiservlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET
    }
)
public class DirectApiServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String targetUrl = "https://jsonplaceholder.typicode.com/users/1"; 
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                }
            } else {
                result.append("{\"title\": \"API returned status code: ").append(conn.getResponseCode()).append("\"}");
            }
            conn.disconnect();
        } catch (Exception e) {
            result.append("{\"title\": \"Servlet Backend Exception: ").append(e.getMessage()).append("\"}");
        }

        response.getWriter().write(result.toString());
    }
}