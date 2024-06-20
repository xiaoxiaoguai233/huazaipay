<!DOCTYPE html>
<!-- saved from url=(0035)http://www.sseye.cn/demo/mobile.php -->
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="format-detection" content="telephone=no">
<meta name="format-detection" content="email=no">
<!-- 启用360浏览器的极速模式(webkit) -->
<meta name="renderer" content="webkit">
<!-- 避免IE使用兼容模式 -->
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<!-- 针对手持设备优化，主要是针对一些老的不识别viewport的浏览器，比如黑莓 -->
<meta name="HandheldFriendly" content="true">
<!-- 微软的老式浏览器 -->
<meta name="MobileOptimized" content="320">
<!-- uc强制竖屏 -->
<meta name="screen-orientation" content="portrait">
<!-- QQ强制竖屏 -->
<meta name="x5-orientation" content="portrait">
<!-- UC强制全屏 -->
<meta name="full-screen" content="yes">
<!-- QQ强制全屏 -->
<meta name="x5-fullscreen" content="true">
<!-- UC应用模式 -->
<meta name="browsermode" content="application">
<!-- QQ应用模式 -->
<meta name="x5-page-mode" content="app">
<!--这meta的作用就是删除默认的苹果工具栏和菜单栏-->
<meta name="apple-mobile-web-app-capable" content="yes">
<!--网站开启对web app程序的支持-->
<meta name="apple-touch-fullscreen" content="yes">
<!--在web app应用下状态条（屏幕顶部条）的颜色-->
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<!-- windows phone 点击无高光 -->
<meta name="msapplication-tap-highlight" content="no">
<!--移动web页面是否自动探测电话号码-->
<meta http-equiv="x-rim-auto-match" content="none">
<!--移动端版本兼容 start -->
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0" name="viewport">
<!--移动端版本兼容 end -->
    <title>通达支付 移动收银台</title>
     <link href="/css/Reset.css" rel="stylesheet" type="text/css">
    <script src="/js/jquery-1.11.3.min.js"></script>
   <script src="/js/pay.js"></script>
     <link href="/css/main12.css" rel="stylesheet" type="text/css">
    <style> .pc_dis{        display:none;   }
        .pay_li input{
            display: none;
        }
        .border_radis{
            border-radius:0.5em
        }
        .immediate-pay12 {
            padding-top:1em;padding-bottom:1em; padding-right:0em;
        }       
        .PayMethod12 ul li      {           margin-right:60px;      }
        .immediate_pay{
            border:none;
        }
        .PayMethod12
        {
           padding-bottom:15px;
        }
        @media screen and (max-width: 700px) {                      .pc_dis{        display:inline-block;   }                       .mobile_dis{            display:none;           }
            .immediate-pay12 {
            padding-top:0em;padding-bottom:0em; padding-right:0em;
        }
        .immediate-pay12-right
        {
            margin-left:0;
            margin-right:0;
            width:100%;
        }
            .immediate-pay12-right .immediate_pay
            {
            float:none;
 margin:0 auto;         
            }
            .border_radis{
            border-radius:0em
        }
        .immediate_pay
        {
            width:50%;
        }
            .PayMethod12{
                padding-top:0;
            }
            .order-amount12{
                margin-bottom: 0;
            }
            .order-amount12,.PayMethod12{
                padding-left: 15px;padding-right: 15px;
            }
        }
        .order-amount12-right input{
            border:1px solid #efefef;
            width:6em;
            padding:5px 20px;
            font-size: 15px;
            text-indent: 0.5em;
            line-height: 1.8em;
        }       
    </style>
    
   </head>
<body style="background-color:#f9f9f9">
<form action="/api/createOrder" method="post" autocomplete="off">
<!--弹窗开始-->
<div class="pay_sure12">
    <div class="pay_sure12-main">
        <h2>支付确认</h2>
        <h3 class="h3-01">请在新打开的页面进行支付！<br><strong>支付完成前请不要关闭此窗口。</strong></h3>
        <div class="pay_sure12-btngroup">
            <a class="immediate_button immediate_payComplate" onclick="callback_pc();">已完成支付</a>
            <a class="immediate_button immediate_payChange" onclick="hide();">更换支付方式</a>
        </div>
        <p>支付遇到问题？请联系客服获得帮助。</p>
    </div>
</div>
<!--弹窗结束-->
<!--导航-->
<div class="w100 navBD12">
    <div class="w1080 nav12">
        <div class="nav12-left">
            <a href="http://pay.whcxzx.cn/"><img src="/logo2.png" alt="" title=" " style="max-height: 45px;"></a>
        </div>
        <div class="nav12-right">
        </div>
    </div>
</div>
<!--订单金额-->
<div class="w1080 order-amount12 border_radis">
    <ul class="order-amount12-left">
        <li>
            <span>商品名称：</span>
            <span>MIUI/小米 小米手机4 贴膜预付款</span>
        </li>
        <li>
            <span>订单编号：</span>
            <span>${mchOrderNo!}</span>
        </li>
    </ul>
    <div class="order-amount12-right">
        <span>订单金额：</span>
        <strong><input type="text" name="amount" style="width:3em;" value="100"></strong>
        
        <span>元</span>
      
      
                <span style="color:red;" class="notice">金额 &gt;1</span>
    </div>
</div>
<!--支付方式-->
<input type="hidden" name="orderid" value="E2019072722344260048">

<div class="w1080 PayMethod12 border_radis">
    <div class="row">
        <h2>支付方式：</h2>
      <ul>
              <input type="hidden" name="tongdao" value="1">
           <#list passList as pass>
          <label for="${pass.payPassageId}">
                <li class="pay_li" data_power_id="${pass.productId}" data_product_id="${pass.productId}">
                    <input value="${pass.payPassageId}" name="channel" id="WechatWM" type="radio">
                    <input type="hidden" class="money" value="1">
                    <span>${pass.pollParam!}</span>
                </li>
            </label>
      </#list>
      
        </ul>
    </div>
</div>
<!--立即支付-->
  <br>
<div class="w1080">
    <div>
        <center><button type="submit" class="immediate_pay">立即支付</button></center>
    </div>
</div>
<div class="mt_agree">
    <div class="mt_agree_main">
        <h2>提示信息</h2>
        <p id="errorContent" style="text-align:center;line-height:36px;"></p>
        <a class="close_btn" onclick="message_hide()">确定</a>
    </div>
</div>
 </form>
</body></html>