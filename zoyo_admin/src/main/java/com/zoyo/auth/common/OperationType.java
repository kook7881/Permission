package com.zoyo.auth.common;

/**
 * 操作类型枚举
 */
public enum OperationType {
    
    /**
     * 其它操作
     */
    OTHER(0, "其它"),
    
    /**
     * 新增操作
     */
    INSERT(1, "新增"),
    
    /**
     * 修改操作
     */
    UPDATE(2, "修改"),
    
    /**
     * 删除操作
     */
    DELETE(3, "删除"),
    
    /**
     * 查询操作
     */
    SELECT(4, "查询"),
    
    /**
     * 导出操作
     */
    EXPORT(5, "导出"),
    
    /**
     * 导入操作
     */
    IMPORT(6, "导入"),
    
    /**
     * 授权操作
     */
    GRANT(7, "授权");
    
    private final int code;
    private final String description;
    
    OperationType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static OperationType getByCode(int code) {
        for (OperationType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return OTHER;
    }
}
