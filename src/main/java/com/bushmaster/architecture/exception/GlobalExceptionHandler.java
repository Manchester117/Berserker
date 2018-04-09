package com.bushmaster.architecture.exception;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * @description         判断请求是普通请求还是AJAX请求
     * @param request       请求
     * @return              是否是AJAX请求
     */
    private static boolean isAjax(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader("X-Requested-With")) && Objects.equals(request.getHeader("X-Requested-With"), "XMLHttpRequest");
    }

    /**
     * @description         统一异常处理的方法
     * @param request       请求
     * @param e             处理请求时发生的异常
     * @return              带有异常信息的Object
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public Object exceptHandler(HttpServletRequest request, Exception e) throws Exception {
        if (isAjax(request)) {
            Map<String, String> exceptInfo = new HashMap<>();
            exceptInfo.put("url", request.getRequestURL().toString());
            exceptInfo.put("exceptMessage", e.getMessage());
            return JSONObject.parseObject(JSON.toJSONString(exceptInfo));
        } else {
            ModelAndView view = new ModelAndView();
            view.addObject("url", request.getRequestURL().toString());
            view.addObject("exceptMessage", e.getMessage());
            view.setViewName("scenarioException");
            return view;
        }
    }
}
