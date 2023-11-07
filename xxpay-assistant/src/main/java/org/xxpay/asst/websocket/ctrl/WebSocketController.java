package org.xxpay.asst.websocket.ctrl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.asst.common.ctrl.BaseController;
import org.xxpay.asst.websocket.WebSocketServer;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.domain.XxPayPageRes;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(Constant.ASST_CONTROLLER_ROOT_PATH + "/websocket/")
public class WebSocketController extends BaseController {


    @RequestMapping("/list")
    @ResponseBody
    public ResponseEntity<?> getlist(HttpServletRequest request) {

        List<String> onlineUsers = WebSocketServer.getOnlineUsers();

        return ResponseEntity.ok(XxPayPageRes.buildSuccess(onlineUsers));
    }





}
