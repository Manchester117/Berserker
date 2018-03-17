package com.bushmaster.architecture.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PreventRepeatSubmitInterceptor extends HandlerInterceptorAdapter {
    /**
     * @description         计算文件MD5,用于重复提交的验证
     * @param uploadFile    上传的文件
     * @return              返回文件的MD5
     */
    private String calculateFileMD5(MultipartFile uploadFile) {
        byte [] uploadBytes = null;
        try {
            uploadBytes = uploadFile.getBytes();
            byte [] digest = DigestUtils.md5Digest(uploadBytes);
            String hashString = new BigInteger(1, digest).toString(16);
            return hashString.toUpperCase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @description             拦截表单重复提交
     * @param request           HttpServletRequest request
     * @param response          HttpServletResponse response
     * @param handler           Object handler
     * @return                  返回false代表重复提交,true为非重复提交
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            PreventRepeatSubmit annotation = method.getAnnotation(PreventRepeatSubmit.class);
            if (annotation != null) {
                if(this.preventRepeatSubmitValidator(request))    // 如果重复相同数据
                    return false;                          // 返回false代表重复提交
                else
                    return true;                           // 返回true代表非重复提交
            }
            return true;
        } else {
            return super.preHandle(request, response, handler);
        }
    }

    /**
     * @description     判断是否重复提交的验证方法,通过前后两次请求的URI和参数对比,来判断是否是重复提交
     * @param request   当前请求
     * @return          如果返回True代表是重复提交,false为非重复提交
     */
    private boolean preventRepeatSubmitValidator(HttpServletRequest request) {
        Map<String, String[]> requestParams = request.getParameterMap();
        // 获取请求URL
        String url = request.getRequestURI();
        // 获取文件的HashCode
        MultipartFile uploadFile = ((MultipartHttpServletRequest) request).getMultiFileMap().get("scriptFile").get(0);
        String uploadFileMD5 = this.calculateFileMD5(uploadFile);
        // 获取请求参数,转成JSON字符串
        JSONObject paramsJsonObject = JSONObject.parseObject(JSON.toJSONString(requestParams));
        // 将上传文件的HashCode放置到待验证的参数Json中
        paramsJsonObject.put("uploadFileMD5", uploadFileMD5);

        // 将URL和参数组成map,转成字符串
        Map<String, String> currentRequestObject = new HashMap<>();
        currentRequestObject.put(url, paramsJsonObject.toJSONString());
        String currentRequestString = currentRequestObject.toString();

        Object preRequestObject = request.getSession().getAttribute("repeatSubmit");
        if (Objects.isNull(preRequestObject)) {
            // 如果是第一次提交,则不是重复提交
            request.getSession().setAttribute("repeatSubmit", currentRequestString);
            return false;
        } else {
            if (Objects.equals(preRequestObject.toString(), currentRequestString)) {
                // 如果本次提交的数据和上次相同,则被认为是重复提交
                return true;
            } else {
                // 如果本次提交的数据和上次不同,则不是重复提交
                request.getSession().setAttribute("repeatSubmit", currentRequestString);
                return false;
            }
        }
    }
}
