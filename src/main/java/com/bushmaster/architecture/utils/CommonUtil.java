package com.bushmaster.architecture.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommonUtil {
    public static Map<String, List<String>> paramsValidator(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errorResult = new HashMap<>();
            List<String> errorMsgList = new ArrayList<>();
            for (ObjectError objectError: bindingResult.getAllErrors())
                errorMsgList.add(objectError.getDefaultMessage());
            errorResult.put("paramErrors", errorMsgList);
            return errorResult;
        } else {
            return null;
        }
    }

    public static Date getDateTimeValue(String dateTime) {
        Date dateTimeValue = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dateTimeValue = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeValue;
    }

    public static Boolean getBooleanValue(String status) {
        Boolean statusValue = null;
        if (Objects.nonNull(status) && StringUtils.isNumeric(status)) {
            if (Objects.equals(status, "1"))
                statusValue = Boolean.TRUE;
            else
                statusValue = Boolean.FALSE;
        } else {
            statusValue = null;
        }
        return statusValue;
    }
}
