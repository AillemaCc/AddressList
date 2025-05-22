package org.AList.common.convention.errorcode;

/**
 * 基础错误码定义
 */
public enum BaseErrorCode implements IErrorCode {
    // ========== 用户端错误 ==========
    User_ERR("A0001", "用户端错误"),
    EMPTY_PARAM("A0002", "请求参数为空"),

    // 用户注册
    REGISTER_FAIL("A0100", "用户注册失败"),
    STU_INFO_MISMATCH("A0102", "学籍信息不匹配"),
    ADDR_BOOK_EXIST("A0103", "通讯录已存在"),

    // 学号相关
    REG_STU_ID_ERR("A0110", "用户注册学号错误"),
    STU_ID_NOT_EXIST("A0111", "学号不存在"),
    STU_ID_EMPTY("A0112", "学号为空"),

    // 密码相关
    REG_PWD_ERR("A0120", "用户注册密码错误"),
    PWD_EMPTY("A0121", "密码为空"),
    PWD_LEN_SHORT("A0122", "密码长度小于八位"),
    PWD_MISS_LOWER("A0123", "密码缺少小写字母"),
    PWD_MISS_UPPER("A0124", "密码缺少大写字母"),
    PWD_MISS_DIGIT("A0125", "密码缺少数字"),

    // 基础信息
    REG_INFO_ERR("A0130", "用户注册基本信息错误"),
    NAME_EMPTY("A0131", "姓名为空"),
    EMAIL_EMPTY("A0132", "邮箱为空"),
    PHONE_EMPTY("A0133", "手机号为空"),

    // 用户登录
    LOGIN_FAIL("A0200", "用户登录失败"),
    USER_NOT_FOUND("A0201", "用户不存在"),
    USER_LOGGED_IN("A0202", "用户已登录"),
    USER_NOT_LOGGED("A0203","用户未登录或用户token不存在"),
    ADMIN_LOGIN_FAIL("A0210", "管理员登录失败"),
    ADMIN_NOT_FOUND("A0211", "管理员账户不存在"),
    ADMIN_LOGGED_IN("A0212", "管理员已登录"),

    // 申请发送
    APPLY_ERR("A0300", "用户发送申请错误"),
    TARGET_NOT_FOUND("A0301", "申请对象不存在"),
    TARGET_IS_SELF("A0302", "申请对象不能为申请者"),
    APPLICANT_ACCEPTED("A0303", "申请者已接受过申请"),
    RECIPIENT_ABNORMAL("A0310", "收信者状态异常"),

    // 申请处理
    PROCESS_APPLY_ERR("A0400", "用户处理申请错误"),
    NO_PENDING_APPLY("A0401", "不存在待处理申请"),
    APPROVE_ERR("A0410", "用户同意申请错误"),
    REJECT_ERR("A0420", "用户拒绝申请错误"),

    // 用户登出
    LOGOUT_ERR("A0500", "用户登出错误"),
    ADMIN_LOGOUT_ERR("A0510", "管理员登出错误"),
    ADMIN_TOKEN_MISSING("A0511", "管理员token不存在"),
    ADMIN_NOT_LOGIN("A0512", "管理员未登录"),

    // 班级管理
    CLASS_ERR("A0600", "用户班级信息异常"),
    CLASS_ID_NAME_EMPTY("A0601", "班级编号或班级名为空"),
    REQ_CLASS_ID_EMPTY("A0602", "请求参数或班级编号为空"),
    REQ_MAJOR_ID_EMPTY("A0603", "请求参数或专业编号为空"),
    REQ_COLLEGE_ID_EMPTY("A0604", "请求参数或学院编号为空"),
    ADD_CLASS_FAIL("A0610", "新增班级失败"),
    CLASS_EXIST("A0611", "新增班级信息已存在"),
    UPDATE_CLASS_FAIL("A0620", "修改班级信息失败"),
    CLASS_NOT_FOUND("A0621", "要修改的班级不存在"),
    ADD_MAJOR_COLLEGE_FAIL("A0622", "新增专业与学院信息失败"),
    UPDATE_MAJOR_COLLEGE_FAIL("A0623", "更新专业与学院信息失败"),
    QUERY_CLASS_FAIL("A0630", "查询班级信息失败"),
    ORIG_MAJOR_MISSING("A0631", "不存在原始专业"),
    MAJOR_NOT_FOUND("A0632", "查询的专业不存在"),
    COLLEGE_NOT_FOUND("A0633", "查询的学院不存在"),

    // 公告管理
    ANNOUNCE_ERR("A0700", "管理员处理公告失败"),
    INVALID_ANNOUNCE_ID("A0701", "无效的公告标识号"),
    ANNOUNCE_ID_EXIST("A0702", "公告标识号已存在"),
    ANNOUNCE_EMPTY_CONTENT("A0703", "公告内容为空"),
    ANNOUNCE_EMPTY_TITLE("A0704", "公告标题为空"),
    ANNOUNCE_EMPTY_STATUS("A0705", "公告状态为空"),

    //excel管理
    EXCEL_PROCESS_ERR("A0800","管理员处理excel错误"),
    IMPORT_DATA_EMPTY("A0801","传入空数据列表，不保存"),
    UPLOAD_FILE_EMPTY("A0802","上传的文件为空"),

    // ========== 客户端错误 ==========
    CLIENT_ERR("B0001", "客户端错误"),
    SYS_TIMEOUT_ERR("B0100","系统执行超时"),
    FLOW_LIMIT_ERR("B0101","系统繁忙"),

    // 系统功能
    SYS_FUNC_ERR("B0200", "系统功能错误"),
    PAGE_PARAM_NEGATIVE("B0201","分页参数小于0"),
    JWT_KEY_ERR("B0210", "JWT密钥错误"),
    JWT_ENCRYPT_FAIL("B0211", "JWT密钥加密失败"),
    JWT_DECRYPT_FAIL("B0212", "JWT密钥解密失败"),
    JWT_KEY_EMPTY("B0213", "JWT密钥不能为空"),
    ADMIN_REVIEW_FAIL("B0220", "管理员审核失败"),
    ADMIN_BAN_USER_FAIL("B0221", "管理员禁用用户失败"),
    REVIEW_DATA_ABNORMAL("B0222","管理员审核失败，数据异常"),
    REVIEW_DATA_MISSING("B0223","管理员审核失败，数据缺失"),
    REVIEW_DATA_UPDATE_FAIL("B0224","管理员审核失败，数据未更新"),

    // 系统权限
    PERM_ERR("B0300", "系统权限错误"),
    PERM_VIEW_DENY("B0310", "系统缺乏权限查看"),
    PERM_VIEW_ADDR_DENY("B0311", "系统缺乏权限查看该通讯录"),
    PERM_EDIT_DENY("B0320", "系统缺乏权限修改"),
    PERM_EDIT_USER_DENY("B0321", "系统缺乏权限修改现用户信息"),
    PERM_EDIT_USER_CONFLICT("B0322","系统缺乏权限修改正在被其他管理员修改的用户信息"),
    PERM_DEL_DENY("B0330","系统缺乏权限删除"),
    PERM_DEL_ADDR_DENY("B0331", "系统缺乏权限删除通讯录"),
    PERM_RESTORE_DENY("B0340","系统缺乏权限恢复"),
    PERM_RESTORE_ADDR_DENY("B0341", "系统缺乏权限恢复该通讯录"),

    // ========== 服务端错误 ==========
    SERVER_ERR("C0001", "服务端错误"),

    //服务出错
    REMOTE_ERR("C0100","第三方服务出错"),
    SIGN_IN_EXPIRED_ERROR("C0101","登陆状态过期"),

    //数据库
    DB_ERR("C0300", "数据库操作错误"),

    //数据库删除
    DB_DELETE_ERR("C0310", "数据库删除错误"),
    DEL_ADDR_ERR("C0311", "删除个人通讯信息错误"),

    //数据库修改
    DB_UPDATE_ERR("C0320", "数据库修改错误"),
    DB_RESTORE_ERR("C0330","数据库恢复错误"),

    //数据库插入
    DB_INSERT_ERR("C0340", "数据库插入错误"),
    INSERT_ADDR_FAIL("C0341", "插入通讯录失败"),

    //处理通讯录
    PROCESS_ADDR_ERR("C0350", "处理通讯录失败"),
    ADDR_NOT_FOUND("C0351", "处理的通讯录记录不存在"),
    ADDR_CONTACT_MISSING("C0352", "处理的通讯录联系人不存在"),
    ADDR_STUDENT_MISSING("C0353", "处理的通讯录学生信息不存在"),
    ADDR_MAJOR_MISSING("C0354", "处理的通讯录专业学院不存在"),
    ADDR_CLASS_MISSING("C0355", "处理的通讯录班级信息不存在"),

    //处理注册请求
    PROCESS_REG_ERR("C0360", "处理注册请求失败"),
    REG_REQ_NOT_FOUND("C0361", "处理的注册请求不存在"),
    REG_REQ_APPROVED("C0362", "处理的注册请求已通过"),
    REG_REQ_REJECTED("C0363", "处理的注册请求已拒绝"),
    REG_REQ_DELETED("C0364", "处理的注册请求已删除"),

    //处理公告
    PROCESS_ANNOUNCE_ERR("C0370", "处理公告失败"),
    ANNOUNCE_SAVE_FAIL("C0371", "处理的公告保存失败"),
    ANNOUNCE_UPDATE_FAIL("C0372", "处理的公告更新失败"),
    ANNOUNCE_NOT_FOUND("C0373", "处理的公告不存在或已删除"),
    ANNOUNCE_DEL_FAIL("C0374", "处理的公告删除失败"),
    ANNOUNCE_RESTORE_FAIL("C0375", "处理的公告恢复失败"),
    ANNOUNCE_PUBLISH_FAIL("C0376", "处理的公告发布失败"),
    ANNOUNCE_OFFLINE_FAIL("C0377", "处理的公告下架失败");



    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}