$.ajaxSetup({
    traditional: true,
    type: 'POST',
    dataType: 'json',
    timeout: (20 * 60 * 1000)
});
var util = {};
function log(msg) {
    try {
        if (console && isTest) {
            console.log(msg);
        }
    } catch(e) {}
}
function logForErr(msg) {
    try {
        if (console) {
            console.log(msg);
        }
    } catch(e) {}
}
function openNav() {
    document.getElementById("mySidenav").style.width = "150px";
}
function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
}
var BrowserDetect = {
    init: function() {
        this.userAgent = navigator.userAgent;
        this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
        this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || "an unknown version";
        this.OS = this.searchString(this.dataOS) || "an unknown OS";
    },
    searchString: function(data) {
        for (var i = 0; i < data.length; i++) {
            var dataString = data[i].string;
            var dataProp = data[i].prop;
            this.versionSearchString = data[i].versionSearch || data[i].identity;
            if (dataString) {
                if (dataString.indexOf(data[i].subString) != -1) return data[i].identity;
            } else if (dataProp) return data[i].identity;
        }
    },
    searchVersion: function(dataString) {
        var index = dataString.indexOf(this.versionSearchString);
        if (index == -1) return;
        return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
    },
    dataBrowser: [{
        string: navigator.userAgent,
        subString: "Edge",
        identity: "Edge",
        icon: "_edge.png"
    },
    {
        string: navigator.userAgent,
        subString: "Chrome",
        identity: "Chrome",
        icon: "_chrome.png"
    },
    {
        string: navigator.userAgent,
        subString: "OmniWeb",
        versionSearch: "OmniWeb/",
        identity: "OmniWeb",
        icon: "_omni.png"
    },
    {
        string: navigator.vendor,
        subString: "Apple",
        identity: "Safari",
        versionSearch: "Version",
        icon: "_safari.png"
    },
    {
        prop: window.opera,
        identity: "Opera",
        versionSearch: "Version",
        icon: "_opera.png"
    },
    {
        string: navigator.vendor,
        subString: "iCab",
        identity: "iCab",
        icon: "_icab.jpg"
    },
    {
        string: navigator.vendor,
        subString: "KDE",
        identity: "Konqueror",
        icon: "_unknown.png"
    },
    {
        string: navigator.userAgent,
        subString: "Firefox",
        identity: "Firefox",
        icon: "_firefox.png"
    },
    {
        string: navigator.vendor,
        subString: "Camino",
        identity: "Camino",
        icon: "_unknown.png"
    },
    {
        string: navigator.userAgent,
        subString: "Netscape",
        identity: "Netscape",
        icon: "_netscape.png"
    },
    {
        string: navigator.userAgent,
        subString: "MSIE",
        identity: "Explorer",
        versionSearch: "MSIE",
        icon: "_ie.png"
    },
    {
        string: navigator.userAgent,
        subString: "Gecko",
        identity: "Mozilla",
        versionSearch: "rv",
        icon: "_unknown.png"
    },
    {
        string: navigator.userAgent,
        subString: "Mozilla",
        identity: "Netscape",
        versionSearch: "Mozilla",
        icon: "_mozilla.png"
    }],
    dataOS: []
};
util.webContext = function() {
    return '/' + window.location.pathname.split("/")[1] + '/';
};
Number.prototype.round = function(places) {
    places = Math.pow(10, places);
    return Math.round(this * places) / places;
};
util.webHref = function(page, params, folderPath) {
    var queryStr = '';
    if (params) {
        if (typeof params == 'object') {
            queryStr = $.param(params);
        } else queryStr = params;
        queryStr = '?' + queryStr;
    }
    if (folderPath) return util.webContext() + folderPath + '/' + page + queryStr;
    else return util.webContext() + page + queryStr;
};
$.isBlank = function(obj) {
    return (!obj || $.trim(obj) === "");
};
var showBlockUI = function(msg, $area) {
    if ($.isBlank(msg)) msg = '请稍后…';
    if ($.blockUI && $.unblockUI) {
        var blockui_option = {
            css: {
                fontSize: '15px',
                borderWidth: '1px',
                textAlign: 'center',
                borderColor: '#0b314a',
                padding: '5px',
                backgroundColor: '#0072be',
                '-webkit-border-radius': '10px',
                '-moz-border-radius': '10px',
                opacity: 1,
                color: '#fff',
                'font-family': 'Lucida Grande,Lucida Sans,Arial,sans-serif'
            },
            message: '<table align="center" border="0"><tr><td><img style="vertical-align:middle;width:16px;" src="../assets/images/spin.gif" /></td><td style="white-space: nowrap">' + msg + '</td></tr></table>',
            overlayCSS: {
                backgroundColor: '#000',
                opacity: 0.5,
                cursor: 'wait'
            }
        };
        if ($.isBlank($area)) {
            $.blockUI(blockui_option);
        } else $area.block(blockui_option);
    }
};
function submitOrder(event) {
    var url = '';
    if (event.data) {
        url = event.data.submitUrl;
    } else url = event;
    var webNum = $('#webNum').val();
    var userName = $("#userName").val();
    var coin = $("#coin").val();
    var payType = $('input[name="payType"]:checked').val();
    var submitDate = $("#submitDate").val();
    var pageId = $("#pageId").val();
    var bankCode = '';
    var authCode = $('#authCode').val();
    var isAlphanumeric = function(str) {
        var regExp = /^[\d|a-zA-Z_-]+$/;
        if (regExp.test(str)) return true;
        else return false;
    };
    if (bankCode == undefined) {
        bankCode = '';
    }
    var ruserName = $("#ruserName").val();
    if (coin == '') {
        alert("[提示]存款额度非有效数字！");
        return false;
    }
    if (isNaN(coin)) {
        alert("[提示]存款额度非有效数字！");
        return false;
    }
    if (coin < 0) {
        alert("[提示]1元以上或者1元才能存款！");
        return false;
    }
    if (payType == null || payType == "") {
        alert("[提示]支付类型不能为空！");
        return false;
    }
    if (userName == null || userName == "") {
        alert("[提示]用户名不能为空！");
        return false;
    }
    if (!isAlphanumeric(userName)) {
        alert('[提示]用户名只能为英数字、底线组成');
        return false;
    }
    var jsonmethodRemarks = {};
    if (methodRemarks) {
        jsonmethodRemarks = $.parseJSON(methodRemarks);
    }
    var payTypeRemark = jsonmethodRemarks[payType];
    if (payTypeRemark.isPaymentRemarkShow === 'Y' && payTypeRemark.isPaymentRemarkRequired === 'Y') {
        var paymentRemark = $("#paymentRemark").val();
        if (paymentRemark == null || paymentRemark == "") {
            alert("[提示]" + payTypeRemark.paymentRemarkShowName + "不能为空！");
            return false;
        }
    }
    if (isEnterTwiceAccount == 'Y') {
        if (ruserName == null || ruserName == "" || ruserName != userName) {
            alert("[提示]2次用户输入不一致！");
            return false;
        }
    }
    if (isRechargeOpenNewWindow == 'N') {
        $('#tabRecharge #submitLink').off('click').addClass('disable');
        showBlockUI('', $('.content'));
    } else {
        setTimeout(function() {
            location.reload()
        },
        5000);
    }
    try {
        var $form = $('#pay');
        url += 'newOrderRedirectController/submitNewOrder.zv';
        if (console) log('submit url:' + url);
        $form.attr('action', url);
        $('#pay').submit();
    } catch(error) {
        log('submit order error:' + error);
    }
}
var Order = {
    isShowNotice: 'N',
    submitUrl: ''
};
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
    results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
} (function(o) {
    var username = getParameterByName('username');
    var paytype = getParameterByName('paytype');
    var amt = getParameterByName('amt');
    var paramCount = 0;
    if (username) {
        $('#userName').val(username);
        $('#ruserName').val(username);
        paramCount += 1;
    }
    if (paytype) {
        $('input[name="payType"]').val();
        $('input[name="payType"][value="' + paytype + '"]').prop('checked', true);
        if ($('input[name="payType"]:checked').size() > 0) paramCount += 1;
    }
    if (amt) {
        $('#coin').val(amt);
        paramCount += 1;
    }
    BrowserDetect.init();
    o.tabEnableTypeMap = {
        'A': 0,
        'B': 1
    };
    o.init = function(isShowNotice, submitUrl) {
        if (isOpenCopyPaste == 'N') {
            $('#userName, #tabGuma #userName').bind('cut copy paste',
            function(e) {
                e.preventDefault();
            });
            $('#ruserName, #tabGuma #ruserName').bind('cut copy paste',
            function(e) {
                e.preventDefault();
            });
        }
        $("#tab").tabs({
            beforeActivate: function(event, ui) {
                var activateContentId = ui.newPanel.attr('id');
                if (activateContentId == 'tabRecharge') {
                    Order.stopGumaInterval();
                } else {
                    if (pageStatus == '1' && !isInBlackList) {
                        Order.startGumaInterval();
                    }
                }
            }
        });
        if (BrowserDetect.browser == 'Safari') {
            $(window).off('pageshow');
            $(window).on("pageshow", {
                submitUrl: submitUrl
            },
            o.pageShow);
        }
        $('.content').unblock();
        $('.content_2').unblock();
        o.isShowNotice = isShowNotice;
        o.submitUrl = submitUrl;
        $('#tabRecharge #submitLink').on('click', {
            submitUrl: o.submitUrl
        },
        submitOrder).removeClass('disable');
        $('#tabGuma #submitLink').on('click', {
            submitUrl: o.submitUrl
        },
        o.submitGumaOrder).removeClass('disable');
        o.createDialog();
        if (o.isShowNotice == 'Y') {
            $('#noticeDialog').dialog('open');
        }
        if (paramCount == 3) {
            $('#tabRecharge #submitLink').trigger('click');
        }
        var loadTab = function(tabIndex) {
            if (tabIndex == 0) {
                $("#tab").tabs('option', 'active', 0);
                var firstPayType = $('input[name="payType"]:first').val();
                if (firstPayType) {
                    $('input[name="payType"]:first').click();
                }
            } else if (tabIndex == 1) {
                $("#tab").tabs('option', 'active', 1);
                o.startGumaInterval();
            }
        };
        log('rechargeCenterEnabledType=' + rechargeCenterEnabledType + '; rechargeCenterDefaultEnabledType=' + rechargeCenterDefaultEnabledType);
        if (rechargeCenterEnabledType.split(',').length == 1) {
            $('.tab-title').hide();
        }
        if (rechargeCenterEnabledType.indexOf(rechargeCenterDefaultEnabledType) > -1) {
            loadTab(o.tabEnableTypeMap[rechargeCenterDefaultEnabledType]);
        } else {
            var enabledType = rechargeCenterEnabledType.split(',')[0];
            loadTab(o.tabEnableTypeMap[enabledType]);
        }
        $("#tabGuma .pay-btn").click(function() {
            $("#tabGuma .content_2").show();
            $("#tabGuma .content").hide();
        });
        $("#tabGuma .go-back-btn").click(function() {
            $("#tabGuma .content_2").hide();
            $("#tabGuma .content").show();
        });
    };
    o.initWap = function(isShowNotice, submitUrl) {
        var loadPageByEnabledType = function(enabledType) {
            var index = o.tabEnableTypeMap[enabledType];
            switch (enabledType) {
            case 'A':
                break;
            case 'B':
                window.location = fntWebContext + '/wap/guma.jsp';
                break;
            }
        };
        if (rechargeCenterEnabledType.split(',').length == 1) {
            $('#mySidenav').hide();
            $('.menu').hide();
        }
        var userClick = getParameterByName('click');
        log('userClick=' + userClick);
        if (!userClick) {
            if (rechargeCenterEnabledType.indexOf(rechargeCenterDefaultEnabledType) > -1) {
                loadPageByEnabledType(rechargeCenterDefaultEnabledType);
            } else {
                var enabledType = rechargeCenterEnabledType.split(',')[0];
                loadPageByEnabledType(enabledType);
            }
        }
        if (isOpenCopyPaste == 'N') {
            $('#userName, #tabGuma #userName').bind('cut copy paste',
            function(e) {
                e.preventDefault();
            });
            $('#ruserName, #tabGuma #ruserName').bind('cut copy paste',
            function(e) {
                e.preventDefault();
            });
        }
        $('.bxslider').bxSlider({
            auto: true,
            controls: false
        });
        $('.marquee').marquee({
            duration: 10000,
            startVisible: true,
            duplicated: true
        });
        Order.loadWapQuickAmount();
        if ($(".pay-label").length > 0) {
            $(".pay-label:first-child input[type='radio']").click();
        }
        if (BrowserDetect.browser == 'Safari') {
            $(window).off('pageshow');
            $(window).on("pageshow", {
                submitUrl: submitUrl,
                pcWap: 'wap'
            },
            o.pageShow);
        }
        $('.content').unblock();
        o.isShowNotice = isShowNotice;
        o.submitUrl = submitUrl;
        $('#submitLink').on('click', {
            submitUrl: o.submitUrl
        },
        submitOrder).removeClass('disable');
        o.createDialog();
        if (o.isShowNotice == 'Y') {
            $('#noticeDialog').dialog('open');
        }
        if (paramCount == 3) {
            $('#submitLink').trigger('click');
        }
    };
    o.cleanQueryString = function() {
        var queryStr = window.location.search;
        queryStr = queryStr.split('&')[0];
        if (history.pushState) {
            var newurl = window.location.protocol + "//" + window.location.host + window.location.pathname + queryStr;
            window.history.pushState({
                path: newurl
            },
            '', newurl);
        }
    };
    o.createDialog = function() {
        var width;
        if (!/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) width = 450;
        $("#noticeDialog").dialog({
            title: "讯息公告",
            modal: true,
            show: "fade",
            hide: "fade",
            autoOpen: false,
            width: width || 300
        });
    };
    o.loadMethodRemark = function(payType, pcWap) {
        var jsonmethodRemarks = {};
        if (methodRemarks) {
            jsonmethodRemarks = $.parseJSON(methodRemarks);
        } else {
            return;
        }
        var payTypeRemark = jsonmethodRemarks[payType];
        var $remarkRow = $("#paymentRemarkRow");
        var html = '';
        if (payTypeRemark.isPaymentRemarkShow === 'Y') {
            html = '<div class="col col-name" ><div style="display: inline-block;vertical-align: middle;line-height: normal;color:' + payTypeRemark.paymentRemarkFontColor + ';">' + payTypeRemark.paymentRemarkShowName + '：</div></div>';
            html += ' <div class="col col-data"><input type="text" name="paymentRemark" id="paymentRemark" class="reg-input" placeholder="请填写' + payTypeRemark.paymentRemarkShowName + '"></div>';
            html += '<div class="col col-else">';
            if (payTypeRemark.isPaymentRemarkRequired === 'Y' && pcWap === 'pc') html += '*必填';
            html += '</div>';
            $remarkRow.next().removeClass('col-bdrTop').next().removeClass('col-bdrTop').next().removeClass('col-bdrTop');
        }
        $remarkRow.empty().html(html);
    };
    o.quickAmountObj = {};
    o.loadQuickAmount = function(payType) {
        o.loadMethodRemark(payType, 'pc');
        var jsonQuickAmtsRemarks = {};
        var jsonQuickAmts = null;
        var jsonPaymentRemarks = null;
        if (quickAmtsRemarks) {
            jsonQuickAmtsRemarks = $.parseJSON(quickAmtsRemarks);
            jsonQuickAmts = jsonQuickAmtsRemarks.quickAmounts;
            jsonPaymentRemarks = jsonQuickAmtsRemarks.paymentRemarks;
        }
        var getQuickAmtAry = function(key) {
            var amtAry = jsonQuickAmts[key].split('|');
            log('amtAry[' + key + ']=' + amtAry);
            return amtAry;
        };
        var renderBindQuickAmt = function(quickAmtAry) {
            if (quickAmtAry && quickAmtAry.length > 0) {
                $('#quickAmts').html('');
                quickAmtAry.forEach(function(elm) {
                    if ($.trim(elm)) $('#quickAmts').append('<span class="money-btn" >' + elm + '</span>');
                });
                if ($('#quickAmts span').length == 0) {
                    $('#quickAmts').html('&nbsp;');
                }
            } else {
                $('#quickAmts').html('&nbsp;');
            }
            $(".money-btn").click(function() {
                $(this).addClass("active");
                $(".money-btn").not(this).removeClass("active");
                $('#coin').val($(this).text());
            });
        };
        log('是否顯示支付方式的快選金額:' + isPaymentMethodQuickAmtShow);
        if (isPaymentMethodQuickAmtShow == 'Y') {
            if (!jsonQuickAmts) {
                alert('快选金额开启显示，但没有设定可用的快选金额');
                return;
            }
            log('使用共用的快選金額:' + isQuickAmtSame);
            if (isQuickAmtSame == 'Y') {
                if ($('#quickAmts span').length == 0) {
                    if (jsonQuickAmts) {
                        var quickAmtAry = getQuickAmtAry('0');
                        renderBindQuickAmt(quickAmtAry);
                    }
                }
            } else {
                var reg = /\d{2}/;
                if (!reg.test(payType)) {
                    payType = '0' + payType;
                }
                $('#quickAmts span').remove();
                if (jsonQuickAmts) {
                    var quickAmtAry = getQuickAmtAry(payType);
                    renderBindQuickAmt(quickAmtAry);
                }
            }
        } else {
            log('不載入快選金額');
        }
        log('支付方式RemarkMsg是否相同:' + isPaymentMethodRemarkSame);
        if (isPaymentMethodRemarkSame == 'Y') {
            if (jsonPaymentRemarks) {
                $('#coin').attr('placeholder', jsonPaymentRemarks.remarkMsg);
            }
        } else {
            var reg = /\d{2}/;
            if (!reg.test(payType)) {
                payType = '0' + payType;
            }
            if (jsonPaymentRemarks) {
                $('#coin').attr('placeholder', jsonPaymentRemarks['remarkMsg' + payType]);
            }
        }
    };
    o.changeAmt = function(o) {
        var amt = $(o).val();
        if (amt != $('.money-btn active').text()) {
            $('.money-btn').removeClass('active');
            $('#quickAmts span').each(function(index) {
                log('index=' + index);
                if (amt == $(this).text()) {
                    $(this).addClass('active');
                    return;
                }
            })
        }
    };
    o.wapChangeAmt = function(o) {
        var amt = $(o).val();
        if (amt != $('.money-btn active span').text()) {
            $('.money-btn').removeClass('active');
            $('#quickAmts li').each(function(index) {
                log('index=' + index);
                if (amt == $(this).find('span').text()) {
                    $(this).addClass('active');
                    return;
                }
            })
        }
    };
    o.loadWapQuickAmount = function(payType) {
        var jsonQuickAmtsRemarks = {};
        var jsonQuickAmts = null;
        var jsonPaymentRemarks = null;
        if (quickAmtsRemarks) {
            jsonQuickAmtsRemarks = $.parseJSON(quickAmtsRemarks);
            jsonQuickAmts = jsonQuickAmtsRemarks.quickAmounts;
            jsonPaymentRemarks = jsonQuickAmtsRemarks.paymentRemarks;
        }
        var getQuickAmtAry = function() {
            var amtAry;
            try {
                amtAry = jsonQuickAmts['0'].split('|');
            } catch(err) {
                logForErr('方法[getQuickAmtAry]取得快選金額發生錯誤， jsonQuickAmts:' + jsonQuickAmts + ', errorMsg:' + err);
            }
            log('amtAry["0"]=' + amtAry);
            return amtAry;
        };
        var renderBindQuickAmt = function(quickAmtAry) {
            if (quickAmtAry && quickAmtAry.length > 0) {
                $('#quickAmts').html('');
                quickAmtAry.forEach(function(elm) {
                    if ($.trim(elm)) $('#quickAmts').append('<li class="money-btn" style="width: 20%"><p><span class="value">' + elm + '</p></li>');
                });
            } else {}
            $(".money-btn").click(function() {
                $(this).addClass("active");
                $(".money-btn").not(this).removeClass("active");
                $('#coin').val($(this).text());
            });
        };
        log('是否顯示支付方式的快選金額:' + isPaymentMethodQuickAmtShow);
        if (isPaymentMethodQuickAmtShow == 'Y') {
            if (!jsonQuickAmts) {
                alert('快选金额开启显示，但没有设定可用的快选金额');
                return;
            }
            var quickAmtAry = getQuickAmtAry();
            renderBindQuickAmt(quickAmtAry);
        } else {
            log('不載入快選金額');
        }
        if (jsonPaymentRemarks) {
            $('#coin').attr('placeholder', jsonPaymentRemarks.remarkMsg);
        }
    };
    o.getAPIUrl = function(action) {
        var url = fntWebContext + '/newOrderController/' + action;
        return url;
    };
    o.intervalId = null;
    o.initWapGuma = function(isShowNotice, submitUrl) {
        if (rechargeCenterEnabledType.split(',').length == 1) {
            $('#mySidenav').hide();
            $('.menu').hide();
        }
        if (isOpenCopyPaste == 'N') {
            $('#userName').bind('cut copy paste',
            function(e) {
                e.preventDefault();
            });
            $('#ruserName').bind('cut copy paste',
            function(e) {
                e.preventDefault();
            });
        }
        $('.bxslider').bxSlider({
            auto: true,
            controls: false
        });
        $('.marquee').marquee({
            duration: 10000,
            startVisible: true,
            duplicated: true
        });
        $('.content_2').unblock();
        o.isShowNotice = isShowNotice;
        o.submitUrl = submitUrl;
        $('#submitLink').on('click', {
            submitUrl: o.submitUrl
        },
        o.submitGumaOrder).removeClass('disable');
        o.createDialog();
        if (o.isShowNotice == 'Y') {
            $('#noticeDialog').dialog('open');
        }
        if (rechargeCenterEnabledType.indexOf('B') > -1) o.startGumaInterval();
    };
    o.pageShow = function(event) {
        var submitUrl = event.data.submitUrl;
        var pcWap = event.data.pcWap;
        $('.content').unblock();
        if (pcWap == 'wap') {
            $('#submitLink').on('click', {
                submitUrl: submitUrl
            },
            submitOrder).removeClass('disable');
        } else {
            $('#tabRecharge #submitLink').on('click', {
                submitUrl: submitUrl
            },
            submitOrder).removeClass('disable');
        }
    };
    o.stopGumaInterval = function() {
        clearInterval(o.intervalId);
    };
    o.startGumaInterval = function() {
        var minute = gumaPageRefreshTime;
        log('重載固碼間隔:' + minute + '分鐘');
        var intervalTime = minute * 60 * 1000;
        o.loadGumaQrcode(pageId);
        o.intervalId = setInterval(function() {
            o.loadGumaQrcode(pageId);
        },
        intervalTime);
    };
    o.loadGumaQrcode = function(pageId) {
        log('重載固碼');
        var url = o.getAPIUrl('gumaQrCode.zv');
        $.ajax(url, {
            data: {
                pageId: pageId
            },
            success: function(json) {
                if (json.code == 0) {
                    if (json.result) {
                        $('#tabGuma #qrcode').attr('src', 'data:image/png;base64,' + json.result);
                    } else {
                        $('#tabGuma #qrcode').attr('src', fntWebContext + '/assets/images/alert.jpg');
                    }
                } else {
                    $('#tabGuma #qrcode').attr('src', fntWebContext + '/assets/images/alert.jpg');
                }
            }
        });
    };
    o.addNotArrived = function(coin, username, transOrderNum) {
        var url = o.getAPIUrl('addNotArrived.zv');
        $.ajax(url, {
            data: {
                'amt': coin,
                'username': username,
                'transOrderNum': transOrderNum
            },
            success: function(json) {
                $('.content_2').unblock();
                $('#tabGuma #submitLink, #submitLink').off('click');
                $('#tabGuma #submitLink, #submitLink').on('click', {
                    submitUrl: o.submitUrl
                },
                o.submitGumaOrder).removeClass('disable');
                if (json.code == 0) {
                    alert('新增未到帐资料完成');
                    $('#tabGuma #userName, #userName').val('');
                    $('#tabGuma #ruserName, #ruserName').val('');
                    $('#tabGuma #coin, #coin').val('');
                    $('#tabGuma #transOrderNum, #transOrderNum').val('');
                } else {
                    alert(json.message);
                }
            }
        });
    };
    o.submitGumaOrder = function(event) {
        var url = '';
        if (event.data) {
            url = event.data.submitUrl;
        } else url = event;
        var $userName = $("#tabGuma #userName").length > 0 ? $("#tabGuma #userName") : $("#userName");
        var $coin = $("#tabGuma #coin").length > 0 ? $("#tabGuma #coin") : $("#coin");
        var $transOrderNum = $("#tabGuma #transOrderNum").length > 0 ? $("#tabGuma #transOrderNum") : $("#transOrderNum");
        var userName = $userName.val();
        var coin = $coin.val();
        var transOrderNum = $transOrderNum.val();
        var isAlphanumeric = function(str) {
            var regExp = /^[\d|a-zA-Z_]+$/;
            if (regExp.test(str)) return true;
            else return false;
        };
        var $ruserName = $("#tabGuma #ruserName").length > 0 ? $("#tabGuma #ruserName") : $("#ruserName");
        var ruserName = $ruserName.val();
        if (coin == '') {
            alert("[提示]存款额度非有效数字！");
            return false;
        }
        if (isNaN(coin)) {
            alert("[提示]存款额度非有效数字！");
            return false;
        }
        if (coin < 0) {
            alert("[提示]1元以上或者1元才能存款！");
            return false;
        }
        if (userName == null || userName == "") {
            alert("[提示]用户名不能为空！");
            return false;
        }
        if (!isAlphanumeric(userName)) {
            alert('[提示]用户名只能为英数字底线');
            return false;
        }
        if (!transOrderNum) {
            alert("[提示]交易订单号码不能为空！");
            return false;
        }
        if (!isAlphanumeric(transOrderNum)) {
            alert('[提示]交易订单号码只能為英數字底线');
            return false;
        }
        if (transOrderNum.length < 9) {
            alert('[提示]交易订单号码必须至少9位数');
            return false;
        }
        if (isEnterTwiceAccount == 'Y') {
            if (ruserName == null || ruserName == "" || ruserName != userName) {
                alert("[提示]2次用户输入不一致！");
                return false;
            }
        }
        $('#tabGuma #submitLink').off('click').addClass('disable');
        $('#submitLink').off('click').addClass('disable');
        showBlockUI('', $('.content_2'));
        try {
            Order.addNotArrived(coin, userName, transOrderNum);
        } catch(error) {
            log('submit guma order error:' + error);
        }
    }
})(Order);