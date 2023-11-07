<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="icon" href="/images/paycenter/favicon.ico" type="image/x-icon"/>
    <title>创建订单</title>

    <style>
        *{
            margin:0;padding:0
        }
        ol,ul{
            list-style: none;
        }
        .col-xs-1, .col-xs-2, .col-xs-3, .col-xs-4, .col-xs-5, .col-xs-6, .col-xs-7, .col-xs-8, .col-xs-9, .col-xs-10, .col-xs-11, .col-xs-12, .col-xs-13, .col-xs-14{
            float:none;
        }
        .mask{
            background:#f0eff5;
            position:fixed;
            top:0;
            bottom:0;
            left:0;
            right:0;
            z-index:-1;
        }
        .h1{padding-top:50px}
        .container{
            width:90%;
            margin:0 auto;
            background:#fff;
            margin-top:5%;
        }
        .container_logo{
            background:#fbfbfb
        }
        .play{
            margin:0 auto;
            padding:10px 0px;
        }
        .play img{
            width:100%;
            display:block;
            margin:0 auto;
        }
        .number_amount{
            position:relative;
            height:42px;
        }
        .number_amount label{
            position:absolute;
            top:50%;
            margin-top:-18px;
            font-size:24px;
            left:0;
        }
        .play input[type="number"]{
            width:100%;
            height:100%;
            font-size:24px;
            border:none;
            border-bottom:1px solid #eee;
            outline: none;
            padding:0 30px;
        }
        .quick_amount p{
            display:inline-block;
            text-align:center;
            font-size:16px;
            line-height:40px;
            border:dashed 1px #eee;
            color:#888;
            margin:10px 3.3%;
        }
        .quick_amount p.active{
            border:#337ab7 1px solid;
            color:#337ab7;
            border-radius:2px;
        }
    </style>

    <script>
        //使用匿名函数方法
        function countDown(){
            var time = document.getElementById("expireTime");
            //alert(time.innerHTML);
            //获取到id为time标签中的内容，现进行判断

            var expireTime = time.innerHTML.substring(0, time.innerHTML.lastIndexOf('秒'))

            if(expireTime <= 0){
                //等于0时清除计时
                window.location.reload();
            }else{
                time.innerHTML = (expireTime - 1) + '秒';
            }
        }
        //1000毫秒调用一次
        window.setInterval("countDown()",1000);
    </script>
</head>
<body>

<div class="container" style="margin-top: 30%;border-radius: 15px;">
    <div class="row">
        <div class="container_logo" style="border-radius: 15px;background: #fff">
            <div class="play col-xs-10 col-sm-10 col-md-10 col-lg-10">
                <img style="border-radius: 8px;" src="/images/paycenter/loading.gif" />
            </div>

            <div class="play col-xs-10 col-sm-10 col-md-10 col-lg-10" style="text-align: center;font-weight: bolder;">
                 创建订单中，正常等待15秒...
            </div>
            <div class="form-group" style="padding: 25px;text-align: center;font-weight: bolder;">
                <label id="expireTime">${expireTime}秒</label>
            </div>
        </div>
    </div>
</div>
</div>
</div>
<div class="mask"></div>

<script src="/layui/layui.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.6.3/jquery.min.js"></script>

<script>
    function GetStatus() {
        //这里可以写ajax方法提交表单
        $.ajax({
            type: "GET",
            url: '/api/slrj/status/' + '${orderId}',
            success: function (res) {
                console.log(res)
                if (res.code == 0) {
                    if(res.data.status == '1' && res.data.payLink != null){
                        window.location.href = res.data.payLink;
                    }else if (res.data.status == '-1' ){
                        window.location.reload();
                    }
                }
            },
            error: function (res) {

            }
        })
    }

    //3000毫秒调用一次
    window.setInterval("GetStatus()",3000);
</script>

</body>
</html>