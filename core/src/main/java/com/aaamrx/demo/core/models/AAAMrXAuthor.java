package com.aaamrx.demo.core.models;

import java.util.List;
import java.util.Map;

public interface AAAMrXAuthor {

    String getFirstName();
    String getLastName();
    String getIsProfessor();
    String getCodeValue();
    String getCurrentPageTitle();
    String getRequestAttribute();
    String getPageName();
    String getAuthorName();
    List<String> getBooks();
    List<Map<String, String>> getBooksWithMap();
}
