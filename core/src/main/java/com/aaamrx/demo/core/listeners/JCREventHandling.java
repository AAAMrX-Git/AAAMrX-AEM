package com.aaamrx.demo.core.listeners;


import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = EventListener.class,
    immediate = true
)
public class JCREventHandling implements EventListener{

    private static final Logger log = LoggerFactory.getLogger(JCREventHandling.class);

    private Session session;

    @Reference
    private SlingRepository slingRepository;

    @Activate
    protected void activate() throws Exception{

        try {
            session = slingRepository.loginService("dataReaderService", null);
            session.getWorkspace().getObservationManager().addEventListener(
            this, 
            Event.NODE_ADDED | Event.PROPERTY_ADDED, 
            "/content/aaamrxdemo/ge/en", 
            true, 
            null, 
            null, 
            false);
            
        } catch (RepositoryException e) {
            // TODO: handle exception
            log.info("Error while adding event listner {}", e.getMessage());
        }
    }


    public void onEvent(EventIterator eventIterator){
        try {
            while(eventIterator.hasNext()){
                log.info("Event {}, Path{}",eventIterator.nextEvent().getType(), eventIterator.nextEvent().getPath());
            }
        } catch (Exception e) {
            // TODO: handle exception
            log.info("Error while handling JCR Event {}",e.getMessage());
        }
    }
    
}
