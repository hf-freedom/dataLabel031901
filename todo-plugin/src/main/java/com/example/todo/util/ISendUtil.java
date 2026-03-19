package com.example.todo.util;

import com.example.todo.entity.TodoMessage;

/**
 * 发送消息工具接口
 */
public interface ISendUtil {

    /**
     * 发送消息
     * @param todoMessage 代办消息
     */
    void send(TodoMessage todoMessage);
}
