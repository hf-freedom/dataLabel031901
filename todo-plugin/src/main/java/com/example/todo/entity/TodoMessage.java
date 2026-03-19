package com.example.todo.entity;

import java.time.LocalDateTime;

/**
 * 代办消息实体类
 */
public class TodoMessage {
    private Long id;
    private String title;       // 代办标题
    private String content;     // 代办内容
    private Long creatorId;     // 创建人ID
    private String creatorName; // 创建人名称
    private Long executorId;    // 执行人ID
    private String executorName; // 执行人名称
    private Priority priority;  // 优先级
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime dueTime;    // 到期时间
    private TodoStatus status;  // 状态
    private boolean reminded;   // 是否已发送提醒

    public TodoMessage() {
        this.createTime = LocalDateTime.now();
        this.reminded = false;
    }

    public enum Priority {
        HIGH,   // 高优先级
        MEDIUM, // 中优先级
        LOW     // 低优先级
    }

    public enum TodoStatus {
        PENDING,    // 待处理
        IN_PROGRESS, // 处理中
        COMPLETED,  // 已完成
        EXPIRED     // 已过期
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalDateTime dueTime) {
        this.dueTime = dueTime;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    public boolean isReminded() {
        return reminded;
    }

    public void setReminded(boolean reminded) {
        this.reminded = reminded;
    }
}
