package com.example.ManagerApp.domain;
public class Permission {
    private Long id;
    private String permissionName;
    private String actionName;
    private String description;
    private String permissionDescription;
    public Permission() {
    }
    public Permission(Long id, String permissionName, String actionName, String description, String permissionDescription) {
        this.id = id;
        this.permissionName = permissionName;
        this.actionName = actionName;
        this.description = description;
        this.permissionDescription = permissionDescription;
    }
    public Long getId() {
        return id;
    }
    public String getPermissionName() {
        return permissionName;
    }
    public String getActionName() {
        return actionName;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
        public String getPermissionDescription() {
        return permissionDescription;
    }
    public void setPermissionDescription(String permissionDescription) {
        this.permissionDescription = permissionDescription;
    }
}   