<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="icon" href="/images/paycenter/favicon.ico" type="image/x-icon"/>
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/layui/2.8.8/css/layui.css" integrity="sha512-i0z3bjxCkuX0RBCJ0MD1SnLdE2wOutQc8Jg7f0nwD147c0Z3bJ3LdJ9TRnfh4GKmmyZ3h594rFK7iRRD+/gx8g==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <title>创建订单</title>
</head>
<body>
<div class="layui-tab layui-tab-brief">

<#--    <image src="https://s1.locimg.com/2023/10/16/94f941333d9c8.png" style="width: 100%;height: auto" ></image>-->

<#--    <div style="width:100%; height:100px; text-align:center; line-height:100px;font-size: 6em;    font-weight: bolder;">-->
<#--        AstroPay-->
<#--    </div>-->

    <div class="layui-panel" style="padding: 10px;">
        <blockquote class="" style="color: red;font-size: 23px;font-weight: bolder;">
            &nbsp;温馨提示：先购买卡密，再提交充值，付款过程中不要关闭对话页面，以免找不到相应的售卡客服！
        </blockquote>
    </div>
        <div class="layui-tab-content">

        <div class="layui-panel" style="padding: 10px;">
            <blockquote class="layui-elem-quote" style="color: red; font-size: 16px; font-weight: bolder;">
                <span style="color: red;font-weight: bolder;"></span>&nbsp;购买卡密 （点击下方商家进行购买充值卡）。
            </blockquote>

            <div class="layui-panel" style="padding: 10px;">
<#--                <table class="layui-table" id="table_kefu">-->
<#--                    <colgroup>-->
<#--                        <col width="150">-->
<#--                    </colgroup>-->
<#--                    <thead>-->
<#--                    <tr>-->
<#--                        <th>商家列表</th>-->
<#--                    </tr>-->
<#--                    </thead>-->
<#--                    <tbody>-->
<#--&lt;#&ndash;                    <#list online_assistants as online_assistant>&ndash;&gt;-->
<#--&lt;#&ndash;                        <tr>&ndash;&gt;-->
<#--&lt;#&ndash;                            <td>&ndash;&gt;-->
<#--&lt;#&ndash;                                点击购买：&ndash;&gt;-->
<#--&lt;#&ndash;                                <button style="color: #0a64ab;border: 0;outline: none;background-color: transparent;" onclick="chooseAssistant('${online_assistant.assistantId ! ''}','${online_assistant.assistantName ! ''}')">${online_assistant.assistantName ! ''}</button>&ndash;&gt;-->
<#--&lt;#&ndash;                            </td>&ndash;&gt;-->
<#--&lt;#&ndash;                        </tr>&ndash;&gt;-->
<#--&lt;#&ndash;                    </#list>&ndash;&gt;-->
<#--                    </tbody>-->
<#--                </table>-->





                <table class="layui-table" id="table_kefu_shop" style="margin-top: 10px;">
                    <colgroup>
                        <col width="150">
                    </colgroup>
                    <thead>
                    <tr>
                        <th>网点列表 &nbsp;&nbsp;&nbsp;</th>
                    </tr>
                    </thead>
                    <tbody>


                    <#list ShopLists as ShopList>
                        <tr>
                            <td>
                                点击购买：
                                <a href="${ShopList.link}" style="font-size: 16px;color: #0a64ab;border: 0;outline: none;background-color: transparent;" target="_blank">${ShopList.name ! ''}</a>
                            </td>
                        </tr>
                    </#list>


                    </tbody>
                </table>

                <div id="payImage" style="display: flex;flex-direction: column;padding: 10px;"></div>

                <div id="succusePay" style="display: flex;flex-direction: column;padding: 10px;">

                </div>

            </div>


            <div class="layui-panel" style="padding: 10px;">

                <blockquote class="layui-elem-quote" style="color: red; font-size: 16px; font-weight: bolder;">
                    <span style="color: red;font-weight: bolder;"></span>&nbsp;卡密充值 （将卡密粘贴到下方框内点提交完成充值）。
                </blockquote>


                <div class="layui-panel" style="padding: 10px;">

                    <form class="layui-form" action="#">
                        <div class="layui-form-item layui-form-text">
                            <label class="layui-form-label" style="display: flex;flex-direction: row; max-width: 10%;">卡密&nbsp;<P style="color: red;">*</P></label>
                            <div class="layui-input-block" style="margin-left: 20% !important;">
                                <textarea id="kami_text" placeholder="请输入卡密" class="layui-textarea"></textarea>
                            </div>
                        </div>

                        <div class="layui-form-item">
                            <div class="" style="display: flex;flex-direction: row;justify-content: space-around;">
                                <button type="reset" class="layui-btn layui-btn-fluid layui-btn-primary">重置</button>
                                <button type="button" class="layui-btn layui-btn-fluid" lay-submit lay-filter="demo1" onclick="submitKami()">提交</button>
                            </div>
                        </div>
                    </form>


                    <div style="width:100%;margin: auto;display: flex;flex-direction: column;margin-top: 60px;">
                        <span style="color: red;font-weight: bolder;font-size: 25px;display: flex;justify-content: center;margin-bottom: 20px;"><p>↓ ↓ ↓&nbsp;</p>操作教程<p>&nbsp;↓ ↓ ↓</p></span>
                        <p style="color: red;padding: 10px; font-size: 20px;">1. 点击如上商家购买卡密。</p>
                        <p style="color: red;padding: 10px; font-size: 20px;">2. 购买成功后，将商家发送的卡密粘贴到提交框内进行上分。</p>
                        <p style="color: red;padding: 10px; font-size: 20px;">3. 格式：卡号,密码</p>
                    </div>
                </div>
            </div>
    </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/layui/2.8.8/layui.js" integrity="sha512-tQC4F8lHFB2IAdzFd0zE4ogMgnco1nJG3hgt26H/ZPysSBR8SGRkNi+T/ah7SerkrstEanqzmJf3mXlVdkqRJw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="https://cdn.jsdelivr.net/npm/clipboard@2.0.6/dist/clipboard.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.6.4/jquery.js"></script>

<script type='text/javascript'>
    (function(a, b, c, d, e, j, s) {
        a[d] = a[d] || function() {
            (a[d].a = a[d].a || []).push(arguments)
        };
        j = b.createElement(c),
            s = b.getElementsByTagName(c)[0];
        j.async = true;
        j.charset = 'UTF-8';
        j.src = 'https://static.meiqia.com/widget/loader.js';
        s.parentNode.insertBefore(j, s);
    })(window, document, 'script', '_MEIQIA');
    _MEIQIA('entId', '7b94d75df1f38f167800bdba0c407e01');
</script>

<script type="text/javascript">

    var orderId = '${orderId!}';

    var status = '-1';

    var expireTime = '${expireTime!}';
    var interval = '${interval!}';

    var amount = '${amount!}';

    var loading;

    var pay_for_name = '';
    var pay_method = '';


    var global_interval; //启动,func不能使用括号

    // 每 interval 秒刷新一次图表
    // setInterval(function () {
    //需要执行的代码写在这里
    // });

    order_info();

    function order_info(){
        const data = get_order_status();

        if (data.data.status != status){
            // 停止定时器
            clearInterval(global_interval);
            // 关闭加载
            layer.close(loading);

            status = data.data.status;
            if(status == "2"){  // 已发收款码
                var payImage = document.getElementById("payImage");
                // 还有两个按钮
                payImage.innerHTML =
                    '<div style="font-size: 28px;text-align: center;margin-bottom: 20px;">扫码购买卡密</div>' +
                    '<img id="payImage" src="' + data.data.imageurl + '" style="width: 100%;height: auto"/>' +
                    '<div class="layui-btn-container" style="margin-top: 20px;display: flex;flex-direction: row;justify-content: center;">' +
                    '<button type="button" class="layui-btn" onclick="confirmPay(' + "3" + ')">确认已支付</button>' +
                    '<button type="button" class="layui-btn layui-btn-warm" onclick="confirmPay(' + "4" + ')">码失效，重发</button>' +
                    '</div>';
            }else if(status == "5"){  // 已发送卡密
                var payImage = document.getElementById("payImage");
                // 还有两个按钮
                payImage.innerHTML =
                    '<div style="font-size: 28px;text-align: center;margin-bottom: 20px;">扫码购买卡密</div>' +
                    '<img id="payImage" src="' + data.data.imageurl + '" style="width: 100%;height: auto"/>' +
                    '<div class="layui-btn-container" style="margin-top: 20px;display: flex;flex-direction: row;justify-content: center;">' +
                    '<button type="button" class="layui-btn layui-btn-disabled">确认已支付</button>' +
                    '<button type="button" class="layui-btn layui-btn-warm layui-btn-disabled">码失效，重发</button>' +
                    '</div>';

                var succusePay = document.getElementById("succusePay");
                // 还有两个按钮
                succusePay.innerHTML =
                    '<div style="font-size: 28px;text-align: center;margin-bottom: 20px;">购买成功-卡密</div>' +
                    '<textarea style="cursor: pointer" onclick="" id="bar">' + data.data.card_pwd + '</textarea>' +
                    '<button type="button" style="margin-top: 30px;" class="layui-btn" data-clipboard-action="cut" data-clipboard-target="#bar" onclick="copykami()">一键复制</button>';
            }
        }

    }

    function confirmPay(data){
        var msg = '';
        if (data == '3'){
            msg = '等待客服发送卡密';
        }else{
            msg = '等待客服重新发码';
        }
        loading = layer.msg(msg, {icon: 16, shade: 0.3, time:0});

        var param = {}
        param.status = data;
        status = param.status;
        update_order_status(param);
    }


    /**
     * 提交卡密
     */
    function submitKami(){
        var kami_text = $("#kami_text").val();
        $.ajax({
            type: "get",
            url: '/api/ssskm/submit', //列表接口
            data: {
                card: kami_text,
                orderId: orderId
            },
            dataType:"json",
            success: function (data) {
                console.log(data)
                if (data.code == 0){
                    layer.confirm('卡密已充值' + data.data + '元, 请稍等', function(index){
                        layer.close(index)
                    });
                }else {
                    layer.confirm(data.data, function(index){
                        layer.close(index)
                    });
                }

                setTimeout(function (){
                    window.location.reload();
                }, 3000)


            }
        });
    }

    // 1. 选择码商
    function chooseAssistant(assistantId, assistantName){
        // 1.确认是否使用该码商
        layer.confirm('确认选择 [' + assistantName + "] 卡商购买", function(index){
            // 2. 选择支付宝或者微信
            layer.confirm('选择支付方式', {
                btn: ['微信', '支付宝'] //按钮
            }, function () {
                layer.msg('已选择微信', {icon: 1});
                pay_method = '微信';
                layer.close(index);

                // 3. 填入付款名称
                layer.prompt({title: '请输入您的付款人名称'}, function (value, index_, elem) {
                    if (value === '') return elem.focus();
                    pay_for_name = value;
                    // 关闭 prompt
                    layer.close(index_);
                    loading = layer.msg('等待客服接单, 预计时间20秒', {icon: 16, shade: 0.3, time: 0});

                    console.log(pay_for_name, pay_method);

                    var param = {}
                    param.assistantId = assistantId;
                    param.pay_for_name = pay_for_name;
                    param.pay_method = pay_method;
                    param.status = '1';

                    status = param.status;

                    update_order_status(param);
                });
            },function () {
                layer.msg('已选择支付宝', {icon: 1});
                pay_method = '支付宝';
                layer.close(index);

                // 3. 填入付款名称
                layer.prompt({title: '请输入您的付款人名称'}, function (value, index_, elem) {
                    if (value === '') return elem.focus();
                    pay_for_name = value;
                    // 关闭 prompt
                    layer.close(index_);
                    loading = layer.msg('等待客服接单, 预计时间20秒', {icon: 16, shade: 0.3, time: 0});

                    console.log(pay_for_name, pay_method);

                    var param = {}
                    param.assistantId = assistantId;
                    param.pay_for_name = pay_for_name;
                    param.pay_method = pay_method;
                    param.status = '1';

                    status = param.status;

                    update_order_status(param);
                });
            });
        })
    }

    // 更新订单信息
    function update_order_status(param){
        // 获取当前订单状态
        $.ajax({
            type: "get",
            url: '/api/update/' + orderId, //模拟基本信息接口,
            data: param,
            dataType:"json",
            success: function (data) {
                console.log(data);
            },fail: function (date){
                setInterval(function () {
                    //需要执行的代码写在这里
                    update_order_status(param);
                }, 5000);
            }
        });

        global_interval = setInterval(order_info, interval * 1000);
    }


    // 购买卡密成功后复制卡密的功能
    function copykami(){
        var bar = document.getElementById("bar");
        const input = document.createElement('input');
        document.body.appendChild(input);
        input.setAttribute('value', bar.value);
        input.select();

        if (document.execCommand('copy')) {
            document.execCommand('copy');
            layer.msg('复制成功: ' + bar.value, {icon: 1, time: 3000});
        }
        input.remove();
    }


    // 获取订单状态
    function get_order_status(){
        console.log("订单更新")

        var result = {};
        // 获取当前订单状态
        $.ajax({
            type: "get",
            url: '/api/status/' + orderId, //模拟基本信息接口,
            dataType:"json",
            async : false,
            success: function (data) {
                console.log(data)
                result = data;
            }
        });
        return result;
    }

</script>
</body>
</html>