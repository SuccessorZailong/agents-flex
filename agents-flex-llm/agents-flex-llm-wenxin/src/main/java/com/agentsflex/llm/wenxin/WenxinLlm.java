/*
 *  Copyright (c) 2023-2025, Agents-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.agentsflex.llm.wenxin;

import com.agentsflex.document.Document;
import com.agentsflex.llm.BaseLlm;
import com.agentsflex.llm.ChatOptions;
import com.agentsflex.llm.MessageResponse;
import com.agentsflex.llm.StreamResponseListener;
import com.agentsflex.llm.client.BaseLlmClientListener;
import com.agentsflex.llm.client.HttpClient;
import com.agentsflex.llm.client.LlmClient;
import com.agentsflex.llm.client.LlmClientListener;
import com.agentsflex.llm.client.impl.SseClient;
import com.agentsflex.llm.embedding.EmbeddingOptions;
import com.agentsflex.llm.response.AbstractBaseMessageResponse;
import com.agentsflex.llm.response.AiMessageResponse;
import com.agentsflex.llm.response.FunctionMessageResponse;
import com.agentsflex.message.AiMessage;
import com.agentsflex.parser.AiMessageParser;
import com.agentsflex.parser.FunctionMessageParser;
import com.agentsflex.parser.impl.DefaultAiMessageParser;
import com.agentsflex.prompt.FunctionPrompt;
import com.agentsflex.prompt.Prompt;
import com.agentsflex.store.VectorData;
import com.agentsflex.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bud
 */
public class WenxinLlm extends BaseLlm<WenxinLlmConfig> {

    Map<String, String> headers = new HashMap<>(2);
    HttpClient httpClient = new HttpClient();

    public AiMessageParser aiMessageParser = WenxinLlmUtil.getAiMessageParser();
    public FunctionMessageParser functionMessageParser = WenxinLlmUtil.getFunctionMessageParser();

    public WenxinLlm(WenxinLlmConfig config) {
        super(config);
        headers.put("Content-Type", "application/json;charset=utf-8");
        headers.put("Authorization", "Bearer " + getConfig().getApiSecret());
    }

    private void checkConversation() {
        if (StringUtil.noText(config.getConversationId())) {

            String endpoint = config.getEndpoint();
            String url = endpoint + WenxinLlmConfig.NEW_CONVERSATION_API;
            String response = httpClient.post(url,
                headers,
                WenxinLlmUtil.conversationToPayload(config));

            System.out.println("创建会话-> " + url);
            System.out.println(response);
            JSONObject jsonObject = JSON.parseObject(response);
            String conversation_id = jsonObject.getString("conversation_id");
            System.out.println("会话ID-> " + conversation_id);
            config.setConversationId(conversation_id);


        }
    }


    @Override
    public <R extends MessageResponse<M>, M extends AiMessage> R chat(Prompt<M> prompt, ChatOptions options) {
        checkConversation();
        String payload = WenxinLlmUtil.promptChatToPayload(prompt, config);
        String endpoint = config.getEndpoint();
        String url = endpoint + WenxinLlmConfig.NEW_CONVERSATION_RUN_API;
        System.out.println(url);
        String response = httpClient.post(url, headers, payload);
        if (StringUtil.noText(response)) {
            return null;
        }

        if (config.isDebug()) {
            System.out.println(">>>>receive payload:" + response);
        }

        JSONObject jsonObject = JSON.parseObject(response);

        String code = jsonObject.getString("code");
        String message = jsonObject.getString("message");



        AbstractBaseMessageResponse<M> messageResponse;

        if (prompt instanceof FunctionPrompt) {
            //noinspection unchecked
            messageResponse = (AbstractBaseMessageResponse<M>) new FunctionMessageResponse(((FunctionPrompt) prompt)
                .getFunctions()
                , functionMessageParser.parse(jsonObject));
        } else {
            //noinspection unchecked
            messageResponse = (AbstractBaseMessageResponse<M>) new AiMessageResponse(aiMessageParser.parse(jsonObject));
        }

        if (StringUtil.hasText(code)) {
            messageResponse.setError(true);
            messageResponse.setErrorMessage(message);
            messageResponse.setErrorCode(code);
        }

        //noinspection unchecked
        return (R) messageResponse;
    }


    @Override
    public <R extends MessageResponse<M>, M extends AiMessage> void chatStream(Prompt<M> prompt, StreamResponseListener<R, M> listener, ChatOptions options) {
       checkConversation();
        LlmClient llmClient = new SseClient();

        String payload = WenxinLlmUtil.promptChatStreamToPayload(prompt, config);

        LlmClientListener clientListener = new BaseLlmClientListener(this, llmClient, listener, prompt, new DefaultAiMessageParser() {
            int prevMessageLength = 0;

            @Override
            public AiMessage parse(JSONObject content) {
                System.out.println(content.toJSONString());
                AiMessage aiMessage = aiMessageParser.parse(content);
                String messageContent = aiMessage.getContent();
                aiMessage.setContent(messageContent.substring(prevMessageLength));
                prevMessageLength = messageContent.length();
                return aiMessage;
            }
        }, functionMessageParser);

        String endpoint = config.getEndpoint();
        llmClient.start(endpoint + WenxinLlmConfig.NEW_CONVERSATION_RUN_API, headers, payload, clientListener, config);
    }

    @Override
    public VectorData embed(Document document, EmbeddingOptions options) {
        return null;
    }

}
