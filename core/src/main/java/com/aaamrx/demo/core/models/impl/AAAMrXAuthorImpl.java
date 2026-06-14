package com.aaamrx.demo.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.ResourcePath;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaamrx.demo.core.models.AAAMrXAuthor;
import com.aaamrx.demo.core.models.CustomNavigation;
import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = AAAMrXAuthor.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AAAMrXAuthorImpl implements AAAMrXAuthor{

    private static final Logger LOGGER = LoggerFactory.getLogger(AAAMrXAuthorImpl.class);

    @SlingObject
    Resource currentComponent;

    @ScriptVariable
    Page currentPage;

    @RequestAttribute(name="rAttribute")
    String reqAttribute;

    @ResourcePath(path = "/content/aaamrxdemo/language-masters/en/test")
    @Via("resource")
    Resource resource;

    @Inject
    @Via("resource")
    String authorname;

    @ValueMapValue
    List<String> books; 

    @Inject
    @Via("resource")
    String fname;

    @Inject
    @Via("resource")
    String lname;

   @Inject
    @Via("resource")
    String professor;

    @Inject
    @Via("resource")
    String code;

    @Override
    public String getFirstName() {
        return fname;
    }

    @Override
    public String getLastName() {
        return lname;
    }

    @Override
    public String getIsProfessor() {
        return professor;
    }

    @Override
    public String getCodeValue() {
        return code;
    }

    @Override
    public String getCurrentPageTitle() {
        return (currentPage != null) ? currentPage.getTitle() : "Default Title";
    }

    @Override
    public String getRequestAttribute() {
       return reqAttribute;
    }

    @Override
    public String getPageName() {
        return resource.getName();
    }

    @PostConstruct
    protected void aaamrx(){
        LOGGER.info("\n Inside aaamrx {} : {}",currentPage.getTitle(),reqAttribute,resource.getPath());
        LOGGER.error("\n Inside Error aaamrx :",currentPage.getTitle(),reqAttribute,resource.getPath());
    }

    @Override
    public String getAuthorName() {
        return authorname;
    }

    @Override
    public List<String> getBooks() {
        if(books!=null){
            return books;
        }
        else{
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, String>> getBooksWithMap() {
        List<Map<String, String>> bookDetailsMap=new ArrayList<>();
        try {
            Resource bookDetail=currentComponent.getChild("bookdetailswithmap");
            if(bookDetail!=null){
                for (Resource book : bookDetail.getChildren()) {
                    Map<String,String> bookMap=new HashMap<>();
                    bookMap.put("bookname",book.getValueMap().get("bookname",String.class));
                    bookMap.put("booksubject",book.getValueMap().get("booksubject",String.class));
                    bookMap.put("publishyear",book.getValueMap().get("publishyear",String.class));
                    bookDetailsMap.add(bookMap);
                }
            }
        }catch (Exception e){
            LOGGER.info("\n ERROR while getting Book Details {} ",e.getMessage());
        }
        LOGGER.info("\n SIZE {} ",bookDetailsMap.size());
        return bookDetailsMap;
    }
    
}
