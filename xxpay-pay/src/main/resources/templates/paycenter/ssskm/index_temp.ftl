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
    <ul class="layui-tab-title">
        <li class="layui-this">充值卡密</li>
        <li>卡密购买</li>
    </ul>
    <div class="layui-tab-content">

        <div class="layui-tab-item layui-show layui-panel" style="padding: 10px;">
            <blockquote class="layui-elem-quote">
                <span style="color: red;font-weight: bolder;">提示:</span>&nbsp;如无卡密，请先在卡密购买处，联系商家。
                <#--                    <button type="reset" class="layui-btn layui-btn-danger" onclick="tis()">提示</button>-->
            </blockquote>

            <form class="layui-form" action="#">
                <div class="layui-form-item layui-form-text">
                    <label class="layui-form-label" style="display: flex;flex-direction: row; max-width: 10%;">卡密&nbsp;<P style="color: red;">*</P></label>
                    <div class="layui-input-block" style="margin-left: 20% !important;">
                        <textarea id="kami_text" placeholder="请输入卡密" class="layui-textarea"></textarea>
                    </div>
                </div>

                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                        <button type="button" class="layui-btn" lay-submit lay-filter="demo1" onclick="submitKami()">提交</button>
                    </div>
                </div>
            </form>


            <blockquote class="layui-elem-quote">
                <span style="color: red;font-weight: bolder;">操作教程:</span>&nbsp;↓ ↓ ↓&nbsp;↓ ↓ ↓&nbsp;↓ ↓ ↓&nbsp;↓ ↓ ↓&nbsp;↓ ↓ ↓
                <#--                    <button type="reset" class="layui-btn layui-btn-danger" onclick="tis()">提示</button>-->
                <image src="https://s1.locimg.com/2023/08/25/68087f6fafc9e.jpg" style="width: 100%;height: auto" onclick="tis()"></image>
            </blockquote>


            <div style="display: flex;flex-direction: column;margin-top: 50px;">
                <div style="display: flex;flex-direction: row; color: #000000">
                    <p style="font-weight: bolder;">格式：</p>
                    <p>卡号,密码(多个请换行)</p>
                </div>

                <div style="display: flex;flex-direction: column; color: #000000">
                    <p style="font-weight: bolder;">示例：</p>
                    <p>9462503327,912234</p>
                    <p>5292857980,651310</p>
                    <p>4230471410,561832</p>
                    <p>2815085138,495504</p>
                    <p>4826979080,959014</p>
                    <p>0394545283,494388</p>
                    <p>9406659016,348847</p>
                    <p>...,...</p>
                </div>
            </div>
        </div>
        <div class="layui-tab-item">
            <div class="layui-panel" style="padding: 10px;">
                <table class="layui-table" id="table_kefu">
                    <colgroup>
                        <col width="150">
                    </colgroup>
                    <thead>
                    <tr>
                        <th>商家列表</th>
                    </tr>
                    </thead>
                    <tbody>


                    <#list AssistantAccounts as AssistantAccount>
                        <tr>
                            <td>
                                点击购买：
                                <button style="color: #0a64ab;border: 0;outline: none;background-color: transparent;" onclick="chooseAssistant('${AssistantAccount ! ''}')">${AssistantAccount ! ''}</button>
                            </td>
                        </tr>
                    </#list>



                    </tbody>
                </table>





                <table class="layui-table" id="table_kefu_shop" style="margin-top: 50px;">
                    <colgroup>
                        <col width="150">
                    </colgroup>
                    <thead>
                    <tr>
                        <th>网点列表 &nbsp;&nbsp;&nbsp;(购买Astropay卡、不要买错了)</th>
                    </tr>
                    </thead>
                    <tbody>


                    <#list ShopLists as ShopList>
                        <tr>
                            <td>
                                点击购买：
                                <a href="${ShopList.link}" style="color: #0a64ab;border: 0;outline: none;background-color: transparent;" target="_blank">${ShopList.name ! ''}</a>
                            </td>
                        </tr>
                    </#list>



                    </tbody>
                </table>




                <div id="payImage" style="display: flex;flex-direction: column;padding: 50px;"></div>

                <div id="succusePay" style="display: flex;flex-direction: column;padding: 50px;">

                </div>

            </div>
        </div>
    </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/layui/2.8.8/layui.js" integrity="sha512-tQC4F8lHFB2IAdzFd0zE4ogMgnco1nJG3hgt26H/ZPysSBR8SGRkNi+T/ah7SerkrstEanqzmJf3mXlVdkqRJw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="https://cdn.jsdelivr.net/npm/clipboard@2.0.6/dist/clipboard.min.js"></script>

<script type="text/javascript">


    var orderId = '${orderId!}';
    var status = '${status!}';
    var expireTime = '${expireTime!}';

    var amount = '${amount!}';

    var WebSocketUrl = '${WebSocketUrl!}';

    var loading;

    var assistantName;

    // 创建Socket对象
    let socket = new WebSocket(WebSocketUrl + orderId);

    // 2. 监听 WebSocket 连接事件
    socket.addEventListener("open", function (event) {
        console.log("WebSocket connected:", event);
    });

    var $ = layui.jquery

    function submitKami(){
        // loading = layer.msg('系统错误，请联系管理员', {icon: 16, shade: 0.3, time:0});

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


                // lay.close(loading);
            }
        });
    }



    function confirmPay(data){

        var msg = ''

        if (data == '4'){
            msg = '等待客服发送卡密';
        }else{
            msg = '等待客服重新发码';
        }

        loading = layer.msg(msg, {icon: 16, shade: 0.3, time:0});
        var data = {
            "fromUserId": orderId,
            "toUserId": assistantName,
            "msg": data
        };
        sendMessage(JSON.stringify(data));
    }

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



        // copyFn = function() {
        //     var clipboard = new Clipboard('#bar', {
        //         text: function (trigger) {
        //             return $('#bar').text();
        //         }
        //     });
        //     clipboard.on('success', function () {
        //         alert('复制成功！');
        //     });
        // }
        // layer.msg(('已复制'), {
        //     offset: '6px'
        // });
    }

    function msgDoFunction(data) {

        assistantName = data.fromUserId;

        if ( data.msg ){
            const msg = data.msg.split(" ");
            console.log("msg " + msg)
            if (msg[0] == '1') {
                console.log("msg[0] " + msg[0] )
                var payImage = document.getElementById("payImage");

                // 还有两个按钮
                payImage.innerHTML =
                    '<div style="font-size: 28px;text-align: center;margin-bottom: 20px;">扫码购买卡密</div>' +
                    '<img id="payImage" src="' + msg[1] + '" style="width: 100%;height: auto"/>' +
                    '<div class="layui-btn-container" style="margin-top: 20px;display: flex;flex-direction: row;justify-content: center;">' +
                    '<button type="button" class="layui-btn" onclick="confirmPay(' + "4" + ')">确认支付</button>' +
                    '<button type="button" class="layui-btn layui-btn-warm" onclick="confirmPay(' + "2" + ')">码失效，重发</button>' +
                    '</div>';
                // 关闭蒙版
            }
            else if(msg[0] == '5'){

                // var payImage = document.getElementById("payImage");
                // payImage.innerHTML = '';
                //
                // var table_kefu = document.getElementById("table_kefu");
                // table_kefu.innerHTML = '';

                var succusePay = document.getElementById("succusePay");
                // 还有两个按钮
                succusePay.innerHTML =
                    '<div style="font-size: 28px;text-align: center;margin-bottom: 20px;">购买成功-卡密</div>' +
                    '<textarea style="cursor: pointer" onclick="" id="bar">' + msg[1] + '</textarea>' +
                    '<button type="button" style="margin-top: 30px;" class="layui-btn" data-clipboard-action="cut" data-clipboard-target="#bar" onclick="copykami()">一键复制</button>';
            }
            layer.close(loading);
        }else{
            layer.close(loading);
            loading = layer.msg('系统错误，请联系管理员', {icon: 16, shade: 0.3, time:0});
        }

    }

    // 3. 监听 WebSocket 收到消息事件
    socket.addEventListener("message", function (event) {
        console.log("WebSocket message received:", event.data);


        msgDoFunction(JSON.parse(event.data));

    });
    // 4. 监听 WebSocket 关闭事件
    socket.addEventListener("close", function (event) {
        console.log("WebSocket closed:", event);
    });
    // 5. 监听 WebSocket 出错事件
    socket.addEventListener("error", function (event) {
        console.error("WebSocket error:", event);
    });


    function sendMessage(text) {
        let message = text;
        if (message) {
            socket.send(message);
        }
    }


    // 等待客服接单
    function chooseAssistant(name){
        console.log(name);

        layer.confirm('确认选择 [' + name + "] 卡商购买", function(index){

            layer.confirm('选择支付方式', {
                btn: ['微信', '支付宝'] //按钮
            }, function () {
                layer.msg('微信', {icon: 1});

                var data = {
                    "fromUserId": orderId,
                    "toUserId": "M_" + name,
                    "msg": "00"
                };
                // sendMessage(JSON.stringify(data));
                // //加载中样式...
                // loading = layer.msg('等待客服接单', {icon: 16, shade: 0.3, time:0});

                layer.prompt({title: '请输入您的支付名称'}, function(value, index, elem){
                    if(value === '') return elem.focus();
                    data.nick = value;
                    // 关闭 prompt
                    layer.close(index);

                    sendMessage(JSON.stringify(data));
                    //加载中样式...
                    loading = layer.msg('等待客服接单', {icon: 16, shade: 0.3, time:0});
                });

            }, function () {
                layer.msg('支付宝', {icon: 1});
                var data = {
                    "fromUserId": orderId,
                    "toUserId": "M_" + name,
                    "msg": "01"
                };
                // sendMessage(JSON.stringify(data));
                // //加载中样式...
                // loading = layer.msg('等待客服接单', {icon: 16, shade: 0.3, time:0});

                layer.prompt({title: '请输入您的支付名称'}, function(value, index, elem){
                    if(value === '') return elem.focus();
                    console.log(value);
                    data.nick = value;
                    // 关闭 prompt
                    layer.close(index);

                    sendMessage(JSON.stringify(data));
                    //加载中样式...
                    loading = layer.msg('等待客服接单', {icon: 16, shade: 0.3, time:0});
                });

            });
        });



    }


    function tis(){
        layer.photos({
            photos: {
                "title": "", //相册标题
                "id": 123, //相册id
                "start": 0, //初始显示的图片序号，默认0
                "data": [   //相册包含的图片，数组格式
                    {
                        "alt": "1",
                        "pid": 1, //图片id
                        "src": "https://s1.locimg.com/2023/08/25/68087f6fafc9e.jpg", //原图地址
                        "thumb": "https://s1.locimg.com/2023/08/25/68087f6fafc9e.jpg" //缩略图地址
                    }
                ]
            }
            ,anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机（请注意，3.0之前的版本用shift参数）
        });
    }

</script>
</body>
</html>