package com.aaamrx.demo.core.servlets;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;


@Component(service = Servlet.class)
@SlingServletPaths( 
    value = {"/bin/executeWorkflow"}
)
public class TriggerWorkflowServlet extends SlingSafeMethodsServlet {

    private static final Logger Log = LoggerFactory.getLogger(TriggerWorkflowServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException{

        final ResourceResolver resourceResolver = request.getResourceResolver();

        String payload = request.getRequestParameter("page").getString();

        try {
            if(StringUtils.isNotBlank(payload)){

                WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);

                WorkflowModel workflowModel = workflowSession.getModel("/var/workflow/models/AAAMrX_Create_Version");

                WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", payload);

                workflowSession.startWorkflow(workflowModel, workflowData);

            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.info("\n Error in Workflow {}", e.getMessage());
        }

    }
    
}
