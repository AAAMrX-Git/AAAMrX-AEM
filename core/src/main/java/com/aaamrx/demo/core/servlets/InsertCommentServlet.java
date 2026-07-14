package com.aaamrx.demo.core.servlets;

import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Servlet.class,
    property = {
        "sling.servlet.methods=POST"
    }
)
@SlingServletPaths(value = "/bin/insertComment")
public class InsertCommentServlet extends SlingAllMethodsServlet {

    private static final String DATASOURCE_NAME = "AAAMRX_Ds";

    @Reference(target = "(datasource.name=" + DATASOURCE_NAME + ")")
    private DataSource dataSource;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        
        // 1. Extract the comment parameters from the request
        String author = request.getParameter("author");
        String comment = request.getParameter("comment");
        String articleId = request.getParameter("articleId");

        // Simple validation
        if (author == null || comment == null || articleId == null) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"Error\", \"message\":\"Missing parameters\"}");
            return;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            
            connection = dataSource.getConnection();

            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(false); 
            }

            // 3. Prepare and execute the SQL query
            String insertQuery = "INSERT INTO article_comments (article_id, author_name, comment_content) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, articleId);
            preparedStatement.setString(2, author);
            preparedStatement.setString(3, comment);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
                response.setStatus(SlingHttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\":\"Success\", \"message\":\"Comment saved to DB\"}");
            } else {
                throw new SQLException("Failed to insert record.");
            }

        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"Error\", \"message\":\"" + e.getMessage() + "\"}");
        } finally {
            // 4. Always safely close database resources
            try { if (preparedStatement != null) preparedStatement.close(); } catch (SQLException ignored) {}
            try { if (connection != null) connection.close(); } catch (SQLException ignored) {}
        }
    }
}