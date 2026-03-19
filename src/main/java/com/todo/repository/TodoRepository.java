package com.todo.repository;

import com.todo.entity.Todo;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class TodoRepository {

    private Cache<Long, Todo> todoCache;
    private AtomicLong idGenerator;

    @PostConstruct
    public void init() {
        todoCache = Caffeine.newBuilder()
                .expireAfterWrite(365, TimeUnit.DAYS)
                .maximumSize(10000)
                .build();

        idGenerator = new AtomicLong(1);
    }

    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(idGenerator.getAndIncrement());
        }
        todoCache.put(todo.getId(), todo);
        return todo;
    }

    public Todo findById(Long id) {
        return todoCache.getIfPresent(id);
    }

    public List<Todo> findAll() {
        return new ArrayList<>(todoCache.asMap().values());
    }

    public List<Todo> findByCreatorId(Long creatorId) {
        return todoCache.asMap().values().stream()
                .filter(todo -> todo.getCreatorId().equals(creatorId))
                .collect(Collectors.toList());
    }

    public List<Todo> findByAssigneeId(Long assigneeId) {
        return todoCache.asMap().values().stream()
                .filter(todo -> todo.getAssigneeId().equals(assigneeId))
                .collect(Collectors.toList());
    }

    public List<Todo> findByAssigneeIdAndStatus(Long assigneeId, Integer status) {
        return todoCache.asMap().values().stream()
                .filter(todo -> todo.getAssigneeId().equals(assigneeId))
                .filter(todo -> status == null || todo.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public List<Todo> findByStatus(Integer status) {
        return todoCache.asMap().values().stream()
                .filter(todo -> todo.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public List<Todo> findPendingTodos() {
        return todoCache.asMap().values().stream()
                .filter(todo -> todo.getStatus() == Todo.STATUS_PENDING || todo.getStatus() == Todo.STATUS_IN_PROGRESS)
                .collect(Collectors.toList());
    }

    public List<Todo> findByDueTimeBeforeAndNotNotified(LocalDateTime time) {
        return todoCache.asMap().values().stream()
                .filter(todo -> todo.getDueTime() != null)
                .filter(todo -> todo.getDueTime().isBefore(time) || todo.getDueTime().isEqual(time))
                .filter(todo -> !todo.getNotified())
                .filter(todo -> todo.getStatus() != Todo.STATUS_COMPLETED && todo.getStatus() != Todo.STATUS_CANCELLED)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        todoCache.invalidate(id);
    }

    public boolean existsById(Long id) {
        return todoCache.getIfPresent(id) != null;
    }
}
