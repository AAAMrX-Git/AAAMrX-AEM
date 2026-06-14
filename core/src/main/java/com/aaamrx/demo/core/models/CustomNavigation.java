package com.aaamrx.demo.core.models;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaamrx.demo.core.models.items.LinkItem; // Import the LinkItem class

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CustomNavigation {

    private List<LinkItem> linksList = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomNavigation.class);

    // Injects the child resources created by the multifield (name must match dialog field name: ./links)
    @ChildResource
    private List<Resource> links;

    @PostConstruct
    protected void init() {
        LOGGER.error("Executing my model!");
        if (links != null) {
            for (Resource linkResource : links) {
                // Get the nested properties from the current link resource
                String label = linkResource.getValueMap().get("label", String.class);
                String url = linkResource.getValueMap().get("url", String.class);

                if (label != null && url != null) {
                    linksList.add(new LinkItem(label, url));
                }
            }
        }
    }

    public List<LinkItem> getLinks() {
        return linksList;
    }
}