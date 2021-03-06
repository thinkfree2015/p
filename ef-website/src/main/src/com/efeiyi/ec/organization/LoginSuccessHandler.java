package com.ming800.organization;

import com.ming800.core.base.service.BaseManager;
import com.ming800.core.p.PConst;
import com.ming800.core.p.model.SystemLog;
import com.ming800.core.util.HttpUtil;
import com.ming800.organization.model.MyUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-1-9
 * Time: 上午11:13
 * 处理 spring security 登录成功
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    BaseManager baseManager;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        MyUser bigUser = AuthorizationUtil.getMyUser();
        if (request.getParameter("j_password") != null && !request.getParameter("j_password").equals("ming20022009")) {
            SystemLog systemLog = new SystemLog();
            WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) authentication.getDetails();
            String ip = webAuthenticationDetails.getRemoteAddress();
            systemLog.setIp(ip);
            systemLog.setContent("登录成功");
            systemLog.setTheDateTime(new Date());
//                systemLog.setTeachArea(bigUser.getTeachArea());

            systemLog.setTheType(PConst.SYSTEM_LOG_THE_TYPE_LOGIN);
            baseManager.saveOrUpdate(systemLog.getClass().getName(), systemLog);
        }
        bigUser.setLastLoginDatetime(new Date());
        baseManager.saveOrUpdate(bigUser.getClass().getName(),bigUser);
        System.out.println("登录成功");

        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        if (savedRequest != null) {
            response.sendRedirect(savedRequest.getRedirectUrl());
        } else {

            if (bigUser.getRole().getBasicType().equals("photographer")) {//摄影师  校验审核状态
                //未审核


                if (HttpUtil.isPhone(request.getHeader("User-Agent"))) {
                    response.sendRedirect(request.getContextPath() + "/pc/myypl");
                } else {

                }

            } else if (bigUser.getRole().getBasicType().equals("consumer")) {//普通用户
                if (request.getParameter("redirect") == null) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    response.sendRedirect(request.getContextPath() + request.getParameter("redirect"));
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/main.do");
            }
        }
//        }
    }
}
