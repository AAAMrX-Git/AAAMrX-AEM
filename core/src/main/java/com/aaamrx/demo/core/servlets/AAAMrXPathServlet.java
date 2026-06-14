package com.aaamrx.demo.core.servlets;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component(service = Servlet.class)
@SlingServletPaths(
    value = "/bin/servlet"
)
public class AAAMrXPathServlet extends SlingAllMethodsServlet{
    
    private static final Logger log = LoggerFactory.getLogger(AAAMrXPathServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse res) throws ServletException, IOException {
        
        log.info("\n================Started get method=====================\n");

        final ResourceResolver resolver = req.getResourceResolver();
        Page page = resolver.adaptTo(PageManager.class).getPage("/content/aaamrxdemo");
        JSONArray pagesArray = new JSONArray();

        try {
            Iterator<Page> childPages = page.listChildren();
            while(childPages.hasNext()){
                Page childPage = childPages.next();
                JSONObject pageObject = new JSONObject();
                pageObject.put(childPage.getTitle(), childPage.getPath());
                pagesArray.put(pageObject);
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.info("Error in Get Method :" + e.getMessage());
        }

        res.setContentType("application/json");
        res.getWriter().write(pagesArray.toString());
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        log.info("\n=============== Started POST ===================\n");
        try {
            List<RequestParameter> requestParameterList = request.getRequestParameterList();
            for(RequestParameter requestParameter : requestParameterList){
                log.info("\n========= Parameters ==== > {} : {}",requestParameter.getName(),requestParameter.getString());

            }
        } catch (Exception e) {
            // TODO: handle exception
            log.info("Error in Request {}",e.getMessage());
        }

        response.getWriter().write("============ Form Submitted ===========");
    }
    
}
