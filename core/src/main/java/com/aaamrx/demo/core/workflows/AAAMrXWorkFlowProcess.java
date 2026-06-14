package com.aaamrx.demo.core.workflows;

import javax.jcr.Node;
import javax.jcr.Session;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(service = WorkflowProcess.class,
    immediate = true,
    property = {
        "process.label" + " = AAAMrX Workflow Process"
    }
)
public class AAAMrXWorkFlowProcess implements WorkflowProcess {

    private static final Logger Log  = LoggerFactory.getLogger(AAAMrXWorkFlowProcess.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap arguments) throws WorkflowException {
        // TODO Auto-generated method stub
        Log.info("==========================Custom Workflow Process===========================");
        try {
            WorkflowData workflowData = workItem.getWorkflowData();
            if(workflowData.getPayloadType().equals("JCR_PATH")){
                Session session = workflowSession.adaptTo(Session.class);
                String path = workflowData.getPayload().toString() + "/jcr:content";
                Node node = (Node) session.getItem(path);

                String[] processArgs = arguments.get("PROCESS_ARGS", "string").toString().split(",");
                for(String wfArgs : processArgs){
                    String[] args = wfArgs.split(":");
                    String prop = args[0];
                    String val = args[1];
                    if(node != null){
                        node.setProperty(prop, val);
                    }
                }
                Log.info("==========================Custom Workflow Process Completed===========================");
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.info("Error in AAAMrXWorkFlowProcess: {}",e.getMessage());
        }

    }
    
}
