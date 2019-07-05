package cn.moke.generator.enums;


/**
 * @author moke
 */

public enum  WrapperCode {
    // 基础
    SUCCESS(0, "操作成功"),
    EXCEPTION_VALIDATE(1, "参数校验异常"),
    EXCEPTION_TYPE(2, "参数类型不匹配"),
    EXCEPTION_JSON_PARSER(3,"JSON解析异常,请检查JSON格式"),
    FILETYPE_ERROR(4,"文件类型错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "该资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方式不正确"),
    UNSUPPORTED_MEDIA_TYPE(415, "请求格式不正确"),
    ERROR(500, "系统异常"),

    // 认证模块 100XX - 15000
    AUTH_ALREADY_REGISTERED_FAIL(10000,"该用户名已被注册"),
    AUTH_REGISTER_FAIL(10001, "注册失败"),
    USER_NON_EXIST_USERNAME_FAIL(10002, "该用户名不存在"),
    AUTH_USERNAME_PASSWORD_INCORRECT_AUTH(10003, "该用户名或密码不正确"),
    AUTH_LOGIN_FAIL(10004, "登录失败"),
    AUTH_PASSWORD_ALTER_FAIL(10005, "密码修改失败"),
    AUTH_TOKEN_OBTAIN_FAIL(10006,"令牌获取失败"),
    AUTH_LOGOUT_FAIL(10007,"注销失败"),
    AUTH_MODIFY_USER_INFO_FAIL(10008, "个人信息修改失败"),

    // 用户模块 150XX - 20000
    USER_ADD_FAIL(15000, "用户添加失败"),
    USER_ADD_REPEAT_FAIL(15001, "用户已经存在"),
    USER_DELETE_FAIL(15002,"用户删除失败"),
    USER_ALTER_FAIL(15003, "用户修改失败"),
    USER_NON_EXIST_ROLE_FAIL(15004, "该角色不存在"),
    USER_ENABLE_FAIL(15005, "用户启用失败"),
    USER_DISABLE_FAIL(15006, "用户禁用失败"),

    // 角色模块 200xx-25000
    ROLE_ADD_FAIL(20000, "角色添加失败"),
    ROLE_ADD_REPEAT_FAIL(20001, "角色已存在"),
    ROLE_DELETE_FAIL(20002,"角色删除失败"),
    ROLE_ALTER_FAIL(20003, "角色修改失败"),
    ROLE_RELATIVE_DELETE_FAIL(20004, "角色已关联用户,无法删除"),
    ROLE_DEFAULT_FAIL(20005, "设置/取消默认角色失败"),
    ROLE_MENU_FAIL(20006, "设置菜单失败"),


    // 验证码 550XX
    CAPTCHA_CAPTCHA_EXPIRE(55000, "验证码已失效"),
    CAPTCHA_CAPTCHA_NON_MATCH(55001, "验证码错误"),
    CAPTCHA_SMS_CODE_REPEAT(55002, "短信验证码已发送"),
    CAPTCHA_SMS_CODE_SEND_FAIL(55003, "短信验证码发送失败"),
    CAPTCHA_SMS_CODE_EXPIRE(55004, "短信验证码已失效"),
    CAPTCHA_SMS_CODE_NON_MATCH(55005, "短信验证码错误"),

    // 字典模块 600XX
    DICT_ADD_FAIL(60000, "字典添加失败"),
    DICT_ADD_REPEAT_FAIL(60001, "字典添加失败,检查是否重复"),
    DICT_DELETE_FAIL(60002, "字典删除失败"),
    DICT_ALTER_FAIL(60003, "字典修改失败"),

    // ip模块 650XX
    IP_WEATHER_FAIL(65000, "天气获取失败"),

    // 后端资源模块 700XX
    PATH_ADD_FAIL(60000, "后端资源添加失败"),
    PATH_ADD_REPEAT_FAIL(60001, "后端资源添加失败,检查是否重复"),
    PATH_DELETE_FAIL(60002, "后端资源删除失败"),
    PATH_ALTER_FAIL(60003, "后端资源修改失败"),
    
    // 前端路由模块 750XX
    ROUTER_ADD_FAIL(60000, "前端路由添加失败"),
    ROUTER_ADD_REPEAT_FAIL(60001, "前端路由添加失败,检查是否重复"),
    ROUTER_DELETE_FAIL(60002, "前端路由删除失败"),
    ROUTER_ALTER_FAIL(60003, "前端路由修改失败"),

    // 权限模块 800XX
    PERMISSION_ADD_FAIL(60000, "权限添加失败"),
    PERMISSION_ADD_REPEAT_FAIL(60001, "权限添加失败,检查是否重复"),
    PERMISSION_DELETE_FAIL(60002, "权限删除失败"),
    PERMISSION_ALTER_FAIL(60003, "权限修改失败"),
    PERMISSION_RELATIVE_DELETE_FAIL(60004, "权限已被路由或接口关联,无法删除"),
    // ...
    ;

    private Integer code;
    private String msg;

    private WrapperCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
