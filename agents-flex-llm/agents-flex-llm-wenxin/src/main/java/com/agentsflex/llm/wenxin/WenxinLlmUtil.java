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

import com.agentsflex.message.MessageStatus;
import com.agentsflex.parser.AiMessageParser;
import com.agentsflex.parser.FunctionMessageParser;
import com.agentsflex.parser.impl.DefaultAiMessageParser;
import com.agentsflex.parser.impl.DefaultFunctionMessageParser;
import com.agentsflex.prompt.DefaultPromptFormat;
import com.agentsflex.prompt.Prompt;
import com.agentsflex.prompt.PromptFormat;
import com.agentsflex.util.Maps;
import com.alibaba.fastjson.JSON;

/**
 * @author bud
 */
public class WenxinLlmUtil {


    public static AiMessageParser getAiMessageParser() {
        DefaultAiMessageParser aiMessageParser = new DefaultAiMessageParser();
        aiMessageParser.setContentPath("$.answer");
        aiMessageParser.setStatusPath("$.is_completion");
        aiMessageParser.setStatusParser(content -> parseMessageStatus((Boolean) content));
        return aiMessageParser;
    }


    public static FunctionMessageParser getFunctionMessageParser() {
        DefaultFunctionMessageParser functionMessageParser = new DefaultFunctionMessageParser();
        functionMessageParser.setFunctionNamePath("$.content[0].outputs.text.component_name");
        functionMessageParser.setFunctionArgsPath("$.content[0].outputs.text.arguments");
        functionMessageParser.setFunctionArgsParser(JSON::parseObject);
        return functionMessageParser;
    }


    public static MessageStatus parseMessageStatus(Boolean isCompletion) {

        return isCompletion ? MessageStatus.END : MessageStatus.MIDDLE;
    }


    public static String promptChatToPayload(Prompt<?> prompt, WenxinLlmConfig config) {

        Maps.Builder root = Maps.of("app_id", config.getAppId())
            .put("stream", false)
            .put("conversation_id", config.getConversationId())
            .put("query", prompt.toString());
        return JSON.toJSONString(root.build());
    }

    public static String promptChatStreamToPayload(Prompt<?> prompt, WenxinLlmConfig config) {

        Maps.Builder root = Maps.of("app_id", config.getAppId())
            .put("stream", true)
            .put("conversation_id", config.getConversationId())
            .put("query", prompt.toString());
        return JSON.toJSONString(root.build());
    }

    public static String conversationToPayload(WenxinLlmConfig config) {

        Maps.Builder root = Maps.of("app_id", config.getAppId());
        return JSON.toJSONString(root.build());
    }
}
