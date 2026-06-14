package com.aaamrx.demo.core.services.impl;

import com.aaamrx.demo.core.services.AdaptiveFormService;
import com.aaamrx.demo.core.services.CustomFormSubmissionService; // Assuming this package for the core logic service
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Adaptive Form Service Implementation that handles the incoming HttpServletRequest
 * on submission and delegates the core logic (DB and Email) to the CustomFormSubmissionService.
 */
@Component(service = AdaptiveFormService.class, immediate = true)
public class AdaptiveFormServiceImpl implements AdaptiveFormService {

    private static final Logger LOG = LoggerFactory.getLogger(AdaptiveFormServiceImpl.class);

    // 1. Inject the core submission logic service
    @Reference
    private CustomFormSubmissionService customSubmissionService;

    @Override
    public String afService(HttpServletRequest slingHttpServletRequest) {
        LOG.info("\n ===============Calling AF Handler (AdaptiveFormServiceImpl)=============");

        // 2. Extract form data from the request parameters
        Map<String, String> formDataMap = extractFormData(slingHttpServletRequest);
        
        if (formDataMap.isEmpty()) {
            LOG.warn("No form data found in the request.");
            return "Failure: No data submitted.";
        }

        // 3. Delegate the database and email processing to the injected service
        // The CustomFormSubmissionService handles parsing out specific fields like 'emailField' and 'nameField'.
        boolean success = customSubmissionService.processSubmission(formDataMap);

        // 4. Return result
        if (success) {
            LOG.info("AF Submission successfully processed (DB insert and Email sent).");
            return "Success: Form submitted, record inserted, and confirmation email sent.";
        } else {
            LOG.error("AF Submission failed during DB insert or Email send.");
            return "Failure: Form submission failed. Check logs for details.";
        }
    }

    /**
     * Utility method to extract all request parameters into a Map.
     * Note: This method currently only takes the first value for parameters,
     * which is sufficient for most Adaptive Form text fields.
     */
    private Map<String, String> extractFormData(HttpServletRequest request) {
        Map<String, String> data = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            data.put(paramName, paramValue);
            LOG.debug("Extracted form parameter: {} = {}", paramName, paramValue);
            LOG.error("Extracted form parameter: {} = {}", paramName, paramValue);
        }
        return data;
    }
}