package com.aaamrx.demo.core.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
    methods = {HttpConstants.METHOD_GET, HttpConstants.METHOD_POST},
    resourceTypes = "aaamrxdemo/components/page",
    selectors =  "aaamrx",
    extensions = {"text","xml"}
)
public class AAAMrXResourceTypeServlet extends SlingAllMethodsServlet {
    
    private static final Logger log = LoggerFactory.getLogger(AAAMrXResourceTypeServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException{
        final Resource resource = request.getResource();

        response.setContentType("text");
        response.getWriter().write("Title : " + resource.getValueMap().get(JcrConstants.JCR_TITLE));
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
