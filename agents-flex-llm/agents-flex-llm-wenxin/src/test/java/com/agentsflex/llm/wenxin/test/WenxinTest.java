package com.agentsflex.llm.wenxin.test;

import com.agentsflex.llm.Llm;

import com.agentsflex.llm.wenxin.WenxinLlm;
import com.agentsflex.llm.wenxin.WenxinLlmConfig;

public class WenxinTest {

    public static void main(String[] args) throws InterruptedException {
        WenxinLlmConfig config = new WenxinLlmConfig();
        config.setAppId("a8f7a50f-x-40ea-aa8f-7f7ecbe7393x");
        config.setApiSecret("bce-v3/xxx");

        config.setDebug(true);
        Llm llm = new WenxinLlm(config);
//        llm.chatStream("请写一篇文章500-800字，关于牛生肖运势分析并送上祝福", (context, response) -> {
//            AiMessage message = response.getMessage();
//            System.out.println(">>>> " + message.getContent());
//        });

        String chat = llm.chat("请写一篇文章500-800字，关于牛生肖运势分析并送上祝福？");
        System.out.println(chat);
        Thread.sleep(60000);
    }
}
