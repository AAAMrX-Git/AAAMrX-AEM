package com.aaamrx.demo.core.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class UserApiModel {

    private static final Logger LOG = LoggerFactory.getLogger(UserApiModel.class);

    private String name;
    private String email;

    @PostConstruct
    protected void init() {
        // We point directly to your working local servlet URL
        String servletUrl = "http://localhost:4502/bin/myapiservlet";
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(servletUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // We handle basic authentication since it's an internal local AEM request
            String userpass = "admin:admin"; // Adjust if your local login differs
            String basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString(userpass.getBytes());
            conn.setRequestProperty("Authorization", basicAuth);

            if (conn.getResponseCode() == 200) {
                try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                }
                
                // Parse fields cleanly using GSON
                JsonObject jsonObject = new Gson().fromJson(result.toString(), JsonObject.class);
                if (jsonObject != null) {
                    this.name = jsonObject.has("name") ? jsonObject.get("name").getAsString() : "No Name Found";
                    this.email = jsonObject.has("email") ? jsonObject.get("email").getAsString() : "No Email Found";
                }
            } else {
                LOG.error("Model loop failed. Status code: {}", conn.getResponseCode());
                this.name = "Unavailable";
                this.email = "Unavailable";
            }
            conn.disconnect();
        } catch (Exception e) {
            LOG.error("Exception in Sling Model execution chain", e);
            this.name = "Error loading name";
            this.email = "Error loading email";
        }
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}