<!doctype html>
<html>
<body>
<form style="display:none" action=${toPayUrl} method="post">
<input type="hidden" name="cusid" id="cusid" value=${cusid}>
<input type="hidden" name="appid" id="appid" value=${appid}>
<input type="hidden" name="version" id="version" value="12">
<input type="hidden" name="trxamt" id="trxamt" value=${trxamt}>
<input type="hidden" name="reqsn" id="reqsn" value=${reqsn}>
<input type="hidden" name="returl" id="returl" value=${returl}>
<input type="hidden" name="randomstr" id="randomstr" value=${randomstr}>
<input type="hidden" name="notify_url" id="notify_url" value=${notify_url}>
<input type="hidden" name="body" id="body" value="tongdapay">
<input type="hidden" name="sign" id="sign" value=${sign}>
</form>
<input type="submit" value="立即支付" style="display:none" >
<script>document.forms[0].submit();</script>
</body>
</html>
