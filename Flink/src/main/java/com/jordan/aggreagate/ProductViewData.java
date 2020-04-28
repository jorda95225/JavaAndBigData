package com.jordan.aggreagate;

/**
 * @autheor masheng
 * @description
 * @date 2020/4/28
 */
public class ProductViewData {
    String productId;
    String userId;
    Long operationType;
    Long timestamp;

    public ProductViewData(String productId, String userId, Long operationType, Long timestamp) {
        this.productId = productId;
        this.userId = userId;
        this.operationType = operationType;
        this.timestamp = timestamp;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getOperationType() {
        return operationType;
    }

    public void setOperationType(Long operationType) {
        this.operationType = operationType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}