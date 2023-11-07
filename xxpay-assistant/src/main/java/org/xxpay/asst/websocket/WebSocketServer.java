package org.xxpay.asst.websocket;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.xxpay.asst.common.service.RpcCommonService;
import org.xxpay.asst.utils.RedisUtil;
import org.xxpay.core.entity.AssistantInfo;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    private RpcCommonService rpcCommonService = ApplicationContextProvider.getBean(RpcCommonService.class);

    /**
     * 当前在线连接数
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 用来存放每个客户端对应的 WebSocketServer 对象
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收 userId
     */
    private String userId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
        } else {
            webSocketMap.put(userId, this);
            addOnlineCount();
        }
        log.info("用户连接: " + userId + " ,当前在线人数为: " + getOnlineCount());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg","连接成功！");

            sendMessage(jsonObject.toJSONString());

            // 更新在线码商数据
            UpdateOnlineAssistants();

        } catch (IOException e) {
            log.error("用户:" + userId + ",网络异常!!!!!!");
        }
    }


    /**
     * 更新在线码商
     */
    public void UpdateOnlineAssistants() {

        JSONArray jsonArray = new JSONArray();

        for(Map.Entry<String, WebSocketServer> entry: webSocketMap.entrySet()) {
             System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

            AssistantInfo byAssistantId = rpcCommonService.rpcAssistantInfoService.findByAssistantId(Long.valueOf(entry.getKey()));
            JSONObject param = new JSONObject();
            param.put("assistantId", byAssistantId.getAssistantId());
            param.put("assistantName", byAssistantId.getAssistantName());

            jsonArray.add(param);
        }

        // 设置到Redis存储
        RedisUtil.setString("online_assistants", jsonArray.toJSONString());

    }



    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            subOnlineCount();
        }
        log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());

        // 更新在线码商数据
        UpdateOnlineAssistants();
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + userId + ",报文:" + message);
        if (!StringUtils.isEmpty(message)) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(message);
                jsonObject.put("fromUserId", this.userId);
                String toUserId = jsonObject.getString("toUserId");
                if (!StringUtils.isEmpty(toUserId) && webSocketMap.containsKey(toUserId)) {
                    webSocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
                } else {
                    log.error("请求的 userId:" + toUserId + "不在该服务器上");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        // error.printStackTrace();

        log.error("用户错误: " + this.userId + ",原因:" + error.getMessage());
        // 更新在线码商数据
        UpdateOnlineAssistants();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendInfo(String message) throws IOException {

        List<String> keys = new ArrayList<>(webSocketMap.keySet());
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                webSocketMap.get(key).sendMessage(message);
            }
        }
    }


    // 获取在线用户
    public static synchronized List<String> getOnlineUsers() {
        ArrayList<String> arrayList = new ArrayList<>();

        for(Map.Entry<String, WebSocketServer> entry: webSocketMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            arrayList.add(entry.getKey());
        }
        return arrayList;
    }

    public static synchronized AtomicInteger getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount.getAndIncrement();
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount.getAndDecrement();
    }

}
