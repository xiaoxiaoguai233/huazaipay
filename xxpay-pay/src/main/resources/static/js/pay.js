   var lastClickTime;
        var orderNo = "${mchOrderNo!}";
        $(function () {
            $('.PayMethod12 ul li').each(function (index, element) {
               // $('.PayMethod12 ul li').eq(4 * index + 4).css('margin-right', '0')
            });
            //支付方式选择
            $('.PayMethod12 ul li').click(function (e) {
                $(this).addClass('active').siblings().removeClass('active');
            });
            $(".pay_li").click(function () {
                $(".pay_li").removeClass("active");
                $(".notice").text("金额 >"+$(this).find(".money").val());
                $(this).addClass("active");
            });
            //点击立即支付按钮
            $(".immediate_pay").click(function () {
                //判断用户是否选择了支付渠道
                if (!$(".pay_li").hasClass("active")) {
                    message_show("请选择支付功能");
                    return false;
                }
                //获取选择的支付渠道的li
                var payli = $(".pay_li[class='pay_li active']");
                if (payli[0]) {
                    prepay(payli.attr("data_power_id"), payli.attr("data_product_id"));
                } else {
                    message_show("请重新选择支付功能");
                }
            });
            $('.mt_agree').click(function (e) {
                $('.mt_agree').fadeOut(300);
            });
            $('.mt_agree_main').click(function (e) {
                return false;
            });
            //弹窗
        //      $('.pay_sure12').click(function(e) {
        //          $(this).fadeOut();
        //      });
            $('.pay_sure12-main').click(function (e) {
                //e. stopPropagation();
                return false;
            });
        });
        
        
        if(/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {    } else {  }
        
        
        
        function message_show(message) {
            $("#errorContent").html(message);
            $('.mt_agree').fadeIn(300);
        }
        function message_hide() {
            $('.mt_agree').fadeOut(300);
        }