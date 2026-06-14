package com.aaamrx.demo.core.listeners;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = ResourceChangeListener.class,
    immediate = true,
    property = {
        ResourceChangeListener.PATHS +"=/content/aaamrxdemo/in/en",
        ResourceChangeListener.CHANGES +"=ADDED",
        ResourceChangeListener.CHANGES +"=REMOVED",
        ResourceChangeListener.CHANGES +"=CHANGED"
    }
)
public class ResourceEventHandling implements ResourceChangeListener {

    private static final Logger log = LoggerFactory.getLogger(ResourceEventHandling.class);

    Session session;

    @Override
    public void onChange(List<ResourceChange> list) {
        for(ResourceChange RC: list){
            try {
                log.info("\n=====Type {} : Path {}=====\n",RC.getType(),RC.getPath());
                Node node = (Node) session.getNode(RC.getPath());
                node.setProperty("eventListenerAdd","Content created in "+RC.getPath()+" by "+session.getUserID());
                session.save();
                log.info("\n ====== Added the property ========\n");
                
            } catch (Exception e) {
                // TODO: handle exception
                log.info("Exception : {}",e.getMessage());
            }
            
        }
    }
    //Test to check if the changes from VS code is visible in testBranch to GIT.
    
}
