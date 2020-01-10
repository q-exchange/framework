package cn.ztuo.bitrade.core;

import cn.ztuo.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author GuoShuai
 * @date 2017年12月22日
 */
@Slf4j
@ControllerAdvice
public class AdminMyControllerAdvice {
    /**
     * 拦截捕捉无权限异常
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = AuthorizationException.class)
    public MessageResult handleAuthorizationError(AuthorizationException ex) {
        log.info("handleAuthorizationError={}",ex);
        MessageResult result = MessageResult.error(5000, "unauthorized");
        return result;
    }

    @ResponseBody
    @ExceptionHandler({AuthenticationException.class,UnauthenticatedException.class})
    public MessageResult handleAuthenticationError(AuthorizationException ex) {
        log.info("handleAuthenticationError={}",ex);
        MessageResult result = MessageResult.error(4000, "please login");
        return result;
    }
}
