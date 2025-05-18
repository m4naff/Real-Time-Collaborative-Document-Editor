package com.devlab.docseditor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String DOCUMENT_CACHE_PREFIX = "document:";
    private static final String ACTIVE_USERS_PREFIX = "active_users:";
    private static final long DEFAULT_CACHE_TTL = 3600; // 1 hour in seconds
    
    /**
     * Caches document content
     * @param documentId the document ID
     * @param content the document content
     */
    public void cacheDocumentContent(String documentId, String content) {
        String key = DOCUMENT_CACHE_PREFIX + documentId;
        redisTemplate.opsForValue().set(key, content, DEFAULT_CACHE_TTL, TimeUnit.SECONDS);
        log.debug("Cached content for document: {}", documentId);
    }
    
    /**
     * Gets cached document content
     * @param documentId the document ID
     * @return the cached content, or null if not found
     */
    public String getCachedDocumentContent(String documentId) {
        String key = DOCUMENT_CACHE_PREFIX + documentId;
        Object cachedContent = redisTemplate.opsForValue().get(key);
        return cachedContent != null ? cachedContent.toString() : null;
    }
    
    /**
     * Invalidates cached document content
     * @param documentId the document ID
     */
    public void invalidateDocumentCache(String documentId) {
        String key = DOCUMENT_CACHE_PREFIX + documentId;
        redisTemplate.delete(key);
        log.debug("Invalidated cache for document: {}", documentId);
    }
    
    /**
     * Adds a user to the list of active users for a document
     * @param documentId the document ID
     * @param userId the user ID
     */
    public void addActiveUser(String documentId, String userId) {
        String key = ACTIVE_USERS_PREFIX + documentId;
        redisTemplate.opsForSet().add(key, userId);
        redisTemplate.expire(key, DEFAULT_CACHE_TTL, TimeUnit.SECONDS);
        log.debug("Added user {} to active users for document: {}", userId, documentId);
    }
    
    /**
     * Removes a user from the list of active users for a document
     * @param documentId the document ID
     * @param userId the user ID
     */
    public void removeActiveUser(String documentId, String userId) {
        String key = ACTIVE_USERS_PREFIX + documentId;
        redisTemplate.opsForSet().remove(key, userId);
        log.debug("Removed user {} from active users for document: {}", userId, documentId);
    }
    
    /**
     * Gets the list of active users for a document
     * @param documentId the document ID
     * @return the set of active user IDs
     */
    public java.util.Set<Object> getActiveUsers(String documentId) {
        String key = ACTIVE_USERS_PREFIX + documentId;
        return redisTemplate.opsForSet().members(key);
    }
}