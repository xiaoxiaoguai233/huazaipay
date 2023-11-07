package org.xxpay.pay.ctrl.payment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xxpay.pay.ctrl.common.BaseController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 支付收银台
 * @date 2018-06-07
 * @Copyright: www.xxpay.org
 */
@Controller
@RequestMapping("/api/channel/")
public class ChannelController{


    /**
     * 通联支付
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("tlpay")
    public String tlpay(HttpServletRequest request, ModelMap model) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        model.put("toPayUrl", parameterMap.get("toPayUrl")[0]);
        model.put("cusid", parameterMap.get("cusid")[0]);
        model.put("appid", parameterMap.get("appid")[0]);
        model.put("version", parameterMap.get("version")[0]);
        model.put("reqsn", parameterMap.get("reqsn")[0]);
        model.put("trxamt", parameterMap.get("trxamt")[0]);
        model.put("returl", parameterMap.get("returl")[0]);
        model.put("randomstr", parameterMap.get("randomstr")[0]);
        model.put("notify_url", parameterMap.get("notify_url")[0]);
        model.put("body", parameterMap.get("body")[0]);
        model.put("sign", parameterMap.get("sign")[0]);
        return "channel/tlpay";
    }

}
