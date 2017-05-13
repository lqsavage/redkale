package org.redkale.test.rest;

import java.io.IOException;
import javax.annotation.Resource;

import org.redkale.net.http.*;
import org.redkale.service.RetResult;

@HttpUserType(UserInfo.class)
public class SimpleRestServlet extends HttpServlet {

    protected static final RetResult RET_UNLOGIN = RetCodes.retResult(RetCodes.RET_USER_UNLOGIN);

    protected static final RetResult RET_AUTHILLEGAL = RetCodes.retResult(RetCodes.RET_USER_AUTH_ILLEGAL);

    @Resource
    private UserService userService;

    //普通鉴权
    @Override
    public void authenticate(HttpRequest request, HttpResponse response) throws IOException {
        UserInfo info = request.currentUser();
        if (info == null) {
            String sessionid = request.getSessionid(false);
            if (sessionid != null) info = userService.current(sessionid);
            if (info != null) request.setCurrentUser(info); //必须赋值给request.currentUser
        }
        if (info == null) {
            response.finishJson(RET_UNLOGIN);
            return;
        } else if (!info.checkAuth(request.getModuleid(), request.getActionid())) {
            response.finishJson(RET_AUTHILLEGAL);
            return;
        }
        response.nextEvent();
    }

}
