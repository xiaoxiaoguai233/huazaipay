<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>通币网</title>
    <meta http-equiv="Content-Type" content="text/html">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="no">
    <script src="/js/jquery-1.11.3.min.js" type="text/javascript"></script>
    <style>
        .layer-test {
            background: none;
            box-shadow: none;
        }

        .layui-layer-title {
            text-align: center;
            padding: 0 !important;
            /*padding-right: 0 ;!important;*/
        }

        body, html {
            padding: 0;
            margin: 0;
            color: #333333;
            font-size: 16px;
        }

        ul, li {
            margin: 0;
            padding: 0;
            list-style: none;
        }

        .header {
            background-color: #ed4215;
            width: 100%;
            height: 68px;
        }

        .header img {
            margin-top: 26px;
            width: 164px;
            margin-left: 20px;
        }

        /*
            .center{
                width:600px;
                margin:0 auto;
            }
        */
        .main {
            background-color: #e8e8e8;
            padding-top: 26px;
        }

        .main .main_box {
            background-color: #fff;
            margin-bottom: 15px;
            margin-left: 10px;
            margin-right: 10px;
        }

        .main_box .main_line {
            padding: 16px 10px;
            overflow: hidden;
            position: relative
        }

        .main_box .main_line.line_bg {
            background-color: #f7f7f7;
        }

        .main_box .main_line .box_lable {
            color: #666666;
            margin-right: 14px;
            font-size: 14px;
            margin-top: 2px;
        }

        .main_box .main_line .box_text {
            width: calc(100% - 72px);
        }

        .main_box .main_line .box_tip {
            text-align: right;
            font-size: 14px;
            position: absolute;
            right: 10px;
            bottom: 15px;
        }

        .box_text ul {
            overflow: hidden;
            margin-bottom: -15px;
        }

        .box_text ul li {
            float: left;
            cursor: pointer;
        }

        .box_text .text_money li {
            margin-right: 5px;
            width: 60px;
            line-height: 24px;
            text-align: center;
            border: 1px solid #9b9b9b;
            margin-bottom: 15px;
            font-size: 14px;
        }

        .box_text .text_tpye li {
            width: 94px;
            margin-bottom: 14px;
        }

        .box_text .text_tpye li img {
            width: 84px;
            display: block;
        }

        .box_text .text_tpye li img:last-child {
            display: none;
        }

        .box_text .text_money li.active {
            border-color: #ff0000;
        }

        .box_text .text_tpye li.active img:first-child {
            display: none;
        }

        .box_text .text_tpye li.active img:last-child {
            display: block;
        }

        .fl {
            float: left;
        }

        .f-bold {
            font-weight: bold;
        }

        .footer {
            width: 100%;
            text-align: center;
            padding-bottom: 45px;
        }

        .btn {
            background-color: #ed2a0e;
            color: #fff;
            width: 156px;
            line-height: 38px;
            border: none;
            border-radius: 5px;
            margin: 0 auto;
        }

        .box_input {
            border: none;
            border-bottom: 1px solid #d2d2d2;
            background-color: #f7f7f7;
            font-size: 16px;
            padding: 5px;
            margin-top: -5px;
            width: 100%;
        }

        .f-14 {
            font-size: 14px;
        }

        .tip_red {
            color: #f40000;
        }

        .remind {
            width: 100%;
            text-align: center;
            padding-bottom: 45px;
            font-size: 14px;
        }
        .ico_log {
            display: inline-block;
            width: 130px;
            height: 38px;
            vertical-align: middle;
            margin-right: 7px
        }
        .ico_WX {
            background: url("/images/logo_weixin.png") no-repeat;
            background-size: cover;
        }
        #logo_wx{

        }

        .layui-layer-title{
            text-align: center;
            padding: 0 ;
        }
        .layui-layer-content{
            text-align:center;
        }
    </style>
</head>

<body>
<!--头部-->
<div class="header">
    <div class="center">
        <img src="/images/cashier/logo1.png">
    </div>
</div>
<!--主体内容-->
<div class="main">
    <div class="center">
        <div class="main_box">
            <div class="main_line line_bg">
                <div class="box_lable fl">会员账号</div>
                <div id="subMchId" class="box_text fl f-bold">${subMchId}</div>
                <div class="box_tip fl">请核对账号</div>
            </div>
            <div class="main_line">
                <div class="box_lable fl">支付类型</div>
                <div class="box_text fl">
                    <ul class="text_tpye">
                        <#list payType as bank>
                            <li value="${bank}"><img src="/images/cashier/${bank}.png"/><img
                                        src="/images/cashier/${bank}_x.png"></li></label>
                        </#list>
                    </ul>
                </div>
                <div class="box_tip tip_red fl">*必选</div>
            </div>
            <div class="main_line line_bg">
                <div class="box_lable fl">支付说明</div>
                <div id="msg" class="box_text fl" style="font-size: 12px ;color:orange">请先选择支付类型</div>
            </div>
        </div>
        <div class="main_box">
            <div id="kx" class="main_line" hidden="true">
                <div class="box_lable fl">快选金额</div>
                <div class="box_text fl">
                    <ul class="text_money">
                        <li class="active" hidden="true">50</li>

                    </ul>
                </div>
            </div>
            <div class="main_line line_bg">
                <div class="box_lable fl">确认金额</div>
                <div class="box_text fl">
                    <input type="number" class="box_input" placeholder="请输入金额">
                </div>
                <div class="box_tip tip_red fl">*必填</div>
            </div>
            <div class="main_line">
                <div class="box_lable fl">存款时间</div>
                <div id="time" class="box_text f-14 fl">${ctime}</div>
                <div class="box_tip fl">无需填写</div>
            </div>
        </div>
        <!--按钮-->
        <div class="footer">
            <button class="btn">确认支付</button>
        </div>
    </div>



</div>
<div class="remind">友情提醒：如果多次尝试付款失败，请返回游戏选择其它支付入口。</div>
<!--js-->
<script src="/layui/layui.all.js"></script>  <!-- 先引入 -->
<script src="/layui/layui.js"></script>  <!-- 后引入 -->

<script>
    /*快选金额*/
    function flush() {
        $(".text_money li").click(function () {
            $(this).addClass("active").siblings().removeClass("active")
            $('.box_input').val($(this).text());
        })
        $("#kx").attr("hidden", false);
    };
    /*支付类型*/
    $(".text_tpye li").click(function () {
        $(this).addClass("active").siblings().removeClass("active")
        obj = eval(${amountMsg})
        var anArray = obj[$(this).val()].fixAmount;

        if (obj[$(this).val()].amountType != undefined) {
            var msg = "请输入";
            if (obj[$(this).val()].amountType[1] != undefined) {
                for (i in obj[$(this).val()].amountType[1]) {
                    msg += "<span style='color:red'>[" + (obj[$(this).val()].amountType[1][i].min) + "--" + (obj[$(this).val()].amountType[1][i].max) + "]</span>";
                }
                msg += "范围内的任意金额";
            }
            if (obj[$(this).val()].amountType[2] != undefined) {
                if (msg != "请输入") {
                    msg += " 或者";
                }
                for (i in obj[$(this).val()].amountType[2]) {
                    msg += "<span style='color:red'>[" + (obj[$(this).val()].amountType[2][i].min) + "--" + (obj[$(this).val()].amountType[2][i].max) + "]</span>";
                }
                msg += "范围内的<span style='font-weight:bold;color:red' >非整10金额</span>";
            }
            if (obj[$(this).val()].amountType[4] != undefined) {
                if (msg != "请输入") {
                    msg += " 或者";
                }
                for (i in obj[$(this).val()].amountType[4]) {
                    msg += "<span style='color:red'>[" + (obj[$(this).val()].amountType[4][i].min) + "--" + (obj[$(this).val()].amountType[4][i].max) + "]</span>";
                }
                msg += "范围内的<span style='font-weight:bold;color:red' >整10的金额</span>";
            }
            if (obj[$(this).val()].amountType[5] != undefined) {
                if (msg != "请输入") {
                    msg += " 或者";
                }
                for (i in obj[$(this).val()].amountType[5]) {
                    msg += "<span style='color:red'>[" + (obj[$(this).val()].amountType[5][i].min) + "--" + (obj[$(this).val()].amountType[5][i].max) + "]</span>";
                }
                msg += "范围内的<span style='font-weight:bold;color:red' >整100的金额</span>";
            }
            if (anArray.length > 0 && msg != "请输入") {
                msg += " 或者选择以下快选金额";
            } else if (anArray.length > 0) {
                msg = "请选择以下快选金额";
            }
            if("1"== $(".text_tpye li.active ").val())
            {
                msg+=" ；通道支持支付宝秒到、微信支付、云闪付、银行卡等支付方式，<span style='font-weight:bold;color:red'>请优先选择[支付宝秒到]，支付时必须输入付款人姓名！</span>"
            }
            // alert(msg)
            $("#msg").html(msg);
        }


        // alert(anArray.length);
        $(".text_money").children().remove();
        if (anArray.length > 0) {
            $.each(anArray, function (n, d) {
                    $(".text_money").append("<li>" + d + "</li>")
                }
            )
            flush();
        } else {
            $("#kx").attr("hidden", true);
        }

    });


    $(".btn").click(function () {
        var subMchId = $("#subMchId").text();
        var payType = $(".text_tpye li.active ").val();
        console.log(subMchId)
        console.log(payType)
        if (payType == null || payType == "" || payType == "NaN" || payType == "undefined") {
            layer.msg('请选择支付方式');
            return;
        }
        var amount = $(".box_input").val();
        console.log(amount)
        if (amount == null || amount == "" || amount == "NaN" || amount == "undefined") {
            layer.msg('请选择或者输入金额');
            return;
        }
        var ctime = $("#time").text();
        console.log(ctime);
        var loadIndex = layer.load(1, {
            shade: [0.1, '#fff'] //0.1透明度的白色背景
        });
        $(".btn").attr("disabled", true);
        $.ajax("/api/cashier/common/pay",
            {
                type: 'POST',
                data: {
                    subMchId: subMchId,
                    payType: payType,
                    amount: amount,
                    ctime: "${ctime}",
                    mchId: "${mchId}",
                    appId: "${appId}"
                },
                success: function (json) {
                    layer.close(loadIndex);
                    $(".btn").attr("disabled", false);
                    if (json.code == 0) {
                        if (json.data.errCode == "0121") {
                            layer.open({
                                type: 1,
                                title: "温馨提示",
                                area: ['250px', '150px'],
                                btn: ['确认'],
                                shadeClose: true,
                                // skin: 'layui-layer-rim',
                                closeBtn: 0,
                                btnAlign: 'c',
                                content: json.data.errDes,//这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响\
                                yes: function (index, layero) {
                                    //doSomething
                                    layer.close(index); //如果设定了yes回调，需进行手工关闭

                                },
                                btn2: function (index, node) {
                                    //只有当点击confirm框的确定时，该层才会关闭
                                    layer.close(index)

                                    return false;
                                }
                            });
                            return;
                        }
                        console.log(json);
                        console.log(json.data.payParams.payMethod);
                        if (json.data.payParams.payMethod == "formJump") {
                            document.write(json.data.payParams.payUrl);


                        } else if (json.data.payParams.payMethod == 'codeImg') {
                            var codeImgUrl = json.data.payParams.codeImgUrl;
                            var index = layer.open({
                                title: "扫描二维码支付",
                                type: 1,
                                anim: 1,
                                content: "<span class='ico_log ico_WX' > </span><div><img width='200' height='200' src='" + codeImgUrl + "' /></div>"
                                    +  "<div style='margin: 10px; color:red;font-size: 16px'>截图保存后用微信扫码打开<br>在相册中选择二维码</div>"
                                ,cancel:function (index,layro) {
                                    layer.close(index);
                                    layer.open({
                                        type: 1,
                                        title: "温馨提示",
                                        area: ['250px', '150px'],
                                        btn: ['确认'],
                                        shadeClose: true,
                                        // skin: 'layui-layer-rim',
                                        closeBtn: 0,
                                        btnAlign: 'c',
                                        skin: 'layer-test',
                                        content: "完成支付后，请关闭支付页面返回游戏App继续游戏",//这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响\
                                        yes: function (index, layero) {
                                            //doSomething
                                            layer.close(index); //如果设定了yes回调，需进行手工关闭

                                        },
                                        btn2: function (index, node) {
                                            //只有当点击confirm框的确定时，该层才会关闭
                                            layer.close(index)

                                            return false;
                                        }
                                    });
                                }

                            });
                            setTimeout(function () {
                                layer.close(index);
                            }, 300000);
                        }

                        else {
                            window.location.href = json.data.payParams.payUrl;
                        }
                    } else {

                        layer.msg(json.msg);
                    }
                }
            });
    });
</script>
</body>
</html>
