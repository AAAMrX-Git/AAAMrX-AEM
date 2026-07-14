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
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
    resourceTypes = "aaamrx/components/content/comments", // Path to your component under /apps
    methods = "POST",
    extensions = "json" // The selector/extension to trigger this servlet
)
public class InsertCommentResServlet extends SlingAllMethodsServlet {

    private static final String DATASOURCE_NAME = "AAAMRX_Ds";

    @Reference(target = "(datasource.name=" + DATASOURCE_NAME + ")")
    private DataSource dataSource;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        
        // Form parameters sent via standard POST
        String author = request.getParameter("author");
        String comment = request.getParameter("comment");
        
        // CRITICAL ADVANTAGE: The resource path *is* the unique article/component ID!
        String articleId = request.getResource().getPath(); 

        

        try (Connection connection = dataSource.getConnection()) {

            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(false); 
            }
            String insertQuery = "INSERT INTO article_comments (article_id, author_name, comment_content) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
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
            }
            
        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"Error\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }
}