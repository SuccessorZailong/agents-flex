/*
 *  Copyright (c) 2022-2023, Agents-Flex (fuhai999@gmail.com).
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
package com.agentsflex.client.impl;

import com.agentsflex.client.LlmClient;
import com.agentsflex.client.LlmClientListener;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SseClient extends EventSourceListener implements LlmClient {

    private OkHttpClient client;
    private EventSource eventSource;

    private LlmClientListener listener;
    private boolean isStop = false;

    @Override
    public void start(String url, Map<String, String> headers, String payload, LlmClientListener listener) {
        this.listener = listener;
        this.isStop = false;

        Request.Builder rBuilder = new Request.Builder()
            .url(url);

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(rBuilder::addHeader);
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(payload, mediaType);
        rBuilder.post(body);


        this.client = new OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .build();


        EventSource.Factory factory = EventSources.createFactory(this.client);
        this.eventSource = factory.newEventSource(rBuilder.build(), this);

        this.listener.onStart(this);
    }

    @Override
    public void stop() {
        if (!isStop) {
            this.isStop = true;
            eventSource.cancel();
            client.dispatcher().executorService().shutdown();
            this.listener.onStop(this);
        }
    }


    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        if (!isStop) {
            this.isStop = true;
            this.listener.onStop(this);
        }
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        this.listener.onMessage(this, data);
    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        this.listener.onFailure(this, t);
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        //super.onOpen(eventSource, response);
    }
}
