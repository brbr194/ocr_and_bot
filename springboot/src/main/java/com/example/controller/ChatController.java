package com.example.controller;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.example.entity.MyMessage;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Arrays;
import java.util.concurrent.Semaphore;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {

    @Value("${ai.api_key}")
    private String apiKey;

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @PostMapping("/askOne")
    public String askOne(@RequestBody MyMessage msg) throws Exception {

        Generation generation = new Generation();

        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content(msg.getAsk())
                .build();

        GenerationParam param = GenerationParam.builder()
                .model("qwen-turbo")
                .messages(Arrays.asList(userMessage))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .apiKey(apiKey)
                .enableSearch(true)
                .build();
        GenerationResult generationResult =generation.call(param);;
        return generationResult.getOutput().getChoices().get(0).getMessage().getContent();

    }

    @PostMapping("/askMore")
    public String askMore(@RequestBody MyMessage msg) throws Exception {
        Generation gen = new Generation();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(msg.getAsk()).build();
        StringBuilder fullContent = new StringBuilder();

        try {
            //streamCallWithMessage(gen, userMsg, fullContent);
            streamCallWithCallback(gen, userMsg, fullContent);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            logger.error("An exception occurred: {}", e.getMessage());
            throw new Exception("Error occurred while processing the request.");
        }

        return fullContent.toString();
    }

    private  void handleGenerationResult(GenerationResult message, StringBuilder fullContent) {
        fullContent.append(message.getOutput().getChoices().get(0).getMessage().getContent());
        logger.info("Received message: {}", JsonUtils.toJson(message));
    }

    private  void streamCallWithMessage(Generation gen, Message userMsg, StringBuilder fullContent)
            throws NoApiKeyException, ApiException, InputRequiredException {
        GenerationParam param = buildGenerationParam(userMsg);
        Flowable<GenerationResult> result = gen.streamCall(param);

        result.blockingForEach(message -> handleGenerationResult(message, fullContent));

        logger.info("Full content: \n{}", fullContent.toString());
    }

    private  void streamCallWithCallback(Generation gen, Message userMsg, StringBuilder fullContent)
            throws NoApiKeyException, ApiException, InputRequiredException, InterruptedException {
        GenerationParam param = buildGenerationParam(userMsg);
        Semaphore semaphore = new Semaphore(0);

        gen.streamCall(param, new ResultCallback<GenerationResult>() {
            @Override
            public void onEvent(GenerationResult message) {
                handleGenerationResult(message, fullContent);
            }

            @Override
            public void onError(Exception err) {
                logger.error("Exception occurred: {}", err.getMessage());
                semaphore.release();
            }

            @Override
            public void onComplete() {
                logger.info("Completed");
                semaphore.release();
            }
        });

        semaphore.acquire();
        logger.info("Full content: \n{}", fullContent.toString());
    }

    private  GenerationParam buildGenerationParam(Message userMsg) {
        return GenerationParam.builder()
                .model("qwen-turbo")
                .messages(Arrays.asList(userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .apiKey(apiKey)
                .incrementalOutput(true)
                .build();
    }

    @PostMapping("/askF")
    public String askF(@RequestBody MyMessage msg) {
        try {
            Generation gen = new Generation();
            Message userMsg = Message.builder().role(Role.USER.getValue()).content(msg.getAsk()).build();

            StringBuilder fullContent = new StringBuilder();

            // 使用阻塞方式获取生成结果
            streamCallWithMessage(gen, userMsg, fullContent);

            // 或者使用异步回调方式获取生成结果
             //streamCallWithCallback(gen, userMsg, fullContent);

            logger.info("Full content: \n{}", fullContent.toString());
            return fullContent.toString();
        } catch (Exception e) {
            logger.error("An exception occurred: {}", e.getMessage());
            return "Error occurred: " + e.getMessage();
        }
    }






}

