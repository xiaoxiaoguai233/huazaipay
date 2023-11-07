package org.xxpay.mch.order.ctrl;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.mch.common.ctrl.BaseController;
import org.xxpay.mch.common.service.RpcCommonService;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@PreAuthorize("hasRole('" + MchConstant.MCH_ROLE_NORMAL + "')")
@RequestMapping(Constant.MCH_CONTROLLER_ROOT_PATH + "/download")
public class OrderDownloadController extends BaseController {

    @Autowired
    private RpcCommonService rpcCommonService;

    /**
     * 导出支付订单
     */
    @GetMapping("/order")
    public ResponseEntity<byte[]> csv(HttpServletRequest request) throws Exception {

        JSONObject param = getJsonParam(request);
        PayOrder payOrder = getObject(param, PayOrder.class);
        // 订单起止时间
        String createTimeStartStr = getString(param, "createTimeStart");
        String createTimeEndStr = getString(param, "createTimeEnd");
        SimpleDateFormat sfm = new SimpleDateFormat("yyyy-MM-dd");
        Date start = sfm.parse(createTimeStartStr);
        Date end = sfm.parse(createTimeEndStr);
        int count = rpcCommonService.rpcPayOrderService.count(null, payOrder, null, null);
        List<PayOrder> payOrderList = null;
//        if (count == 0)
//            return null;
        payOrderList = rpcCommonService.rpcPayOrderService.select(null,
                0, count, payOrder, null, null);

        String name = campaignsListCsvDown(payOrderList);
        FileInputStream fis = new FileInputStream(name);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        HttpHeaders httpHeaders = new HttpHeaders();
        String fileName = new String("order.xls".getBytes("UTF-8"), "iso-8859-1");
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ResponseEntity<byte[]> filebyte = new ResponseEntity<byte[]>(bos.toByteArray(), httpHeaders, HttpStatus.CREATED);
        return filebyte;
    }


    public static String campaignsListCsvDown(List<PayOrder> orders) {
        try {
            //CSV文件生成
            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFSheet sheet = workbook.createSheet("campaignsListCsvDown");

            HSSFCellStyle style = workbook.createCellStyle();           // 样式对象
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  // 垂直
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);             // 水平

            HSSFRow row = sheet.createRow((short) 0);
            HSSFRow row1 = sheet.createRow((short) 1);

            String head = "付订单号,商户ID,商户类型,商户费率,商户入账（单位分）,应用ID,商户订单号,代理商ID,一级代理商ID,代理商费率,一级代理商费率,代理商利润（单位分）,一级代理商利润（单位分）,支付产品ID,通道ID,通道账户ID,渠道类型,渠道ID,支付金额（单位分）,三位货币代码,支付状态,客户端IP,设备,商品标题,商品描述信息,特定渠道发起额外参数,渠道用户标识,渠道商户ID,渠道订单号,渠道数据包,平台利润（单位分）,渠道费率,渠道成本（单位分）,是否退款,退款次数,成功退款金额,单位分,渠道支付错误码,渠道支付错误描述,扩展参数1,扩展参数2,通知地址,跳转地址,订单失效时间,订单支付成功时间,创建时间,更新时间,产品类型,商户子账户";
            String[] split = head.split(",");
            for (int i = 0; i < split.length; i++) {
                row.createCell(i).setCellValue(split[i]);
            }


            //从第3行开始循环写数据
            int rowNum = 1;
            for (PayOrder order : orders) {
                HSSFRow row2 = sheet.createRow(rowNum);
                Field[] fields = order.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Object o = fields[i].get(order);
                    String value = "";
                    if (o != null) {
                        value = o.toString();
                    }

                    row2.createCell(i).setCellValue(value);
                }
                rowNum++;
            }


            File file = new File("D://test" + ".xls");
            String fileName = file.getPath();

            workbook.write(new FileOutputStream(file));
            return fileName;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}




