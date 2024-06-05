package com.agentsflex.llm.wenxin.test;

import com.agentsflex.llm.Llm;

import com.agentsflex.llm.wenxin.WenxinLlm;
import com.agentsflex.llm.wenxin.WenxinLlmConfig;
import com.agentsflex.message.AiMessage;

public class WenxinTest {

    public static void main(String[] args) throws InterruptedException {
        WenxinLlmConfig config = new WenxinLlmConfig();
        config.setAppId("753e8253-e7c2-4c88-aaf4-426225d49b6f");
        config.setApiSecret("bce-v3/ALTAK-Q9ddySbqufKiYK0oy0LMj/f0c5d9ebc790d78fd5bd5e44aaf154cd4d6d804d");


        config.setDebug(true);
        Llm llm = new WenxinLlm(config);
        llm.chatStream("赛力斯是一家什么公司", (context, response) -> {
            AiMessage message = response.getMessage();
            System.out.println(">>>> " + message.getContent());
        });

//        String chat = llm.chat("请写一篇文章500-800字，关于牛生肖运势分析并送上祝福？");
//        System.out.println(chat);
        Thread.sleep(60000);
    }
}
