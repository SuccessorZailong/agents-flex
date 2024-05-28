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

import com.agentsflex.llm.LlmConfig;

/**
 * @author bud
 */
public class WenxinLlmConfig extends LlmConfig {

    private String appId;
    private String conversationId;
    private static final String DEFAULT_MODEL = "ERNIE-4.0-8K / ERNIE Speed-AppBuilder";
    private static final String DEFAULT_ENDPOINT = "https://qianfan.baidubce.com";
    private static final String DEFAULT_APP_ID = "";
    private static final String DEFAULT_API_SECRET = "";
    public static final String NEW_CONVERSATION_API = "/v2/app/conversation";
    public static final String NEW_CONVERSATION_RUN_API = "/v2/app/conversation/runs";
    public static final String FILE_UPLOAD_API = "/v2/app/conversation/file/upload";


    public WenxinLlmConfig() {
        setEndpoint(DEFAULT_ENDPOINT);
        setModel(DEFAULT_MODEL);
        setAppId(DEFAULT_APP_ID);
        setApiSecret(DEFAULT_API_SECRET);
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
