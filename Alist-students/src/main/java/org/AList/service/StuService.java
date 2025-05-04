package org.AList.service;

import org.AList.domain.dto.req.StuLoginReqDTO;
import org.AList.domain.dto.req.StuRegisterRemarkReqDTO;
import org.AList.domain.dto.req.StuRegisterReqDTO;
import org.AList.domain.dto.resp.StuLoginRespDTO;
import org.AList.domain.dto.resp.StuRegisterRemarkRespDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * 学生客户端服务接口层
 */
public interface StuService {
    /**
     * 用户登录接口
     *
     * @param requestParam 用户登录请求实体
     * @param request
     * @return 用户登录响应实体--token
     */
    StuLoginRespDTO login(StuLoginReqDTO requestParam, HttpServletRequest request);

    /**
     * 检查用户是否登录
     * @param studentId 用户名
     * @param token 用户登录产生的token
     * @return 是否登录的结果
     */
    Boolean checkLogin(String studentId, String token);

    /**
     * 用户登出
     * @param studentId 学号
     * @param token 用户登录产生的token
     */
    void logout(String studentId, String token);

    /**
     * 用户注册接口
     * @param requestParam 用户注册请求实体
     */
    String register(StuRegisterReqDTO requestParam);

    /**
     * 用户查询注册结果
     * @param requestParam 用户查询注册结果请求体
     * @return 用户查询注册结果响应体
     */
    StuRegisterRemarkRespDTO getReamrk(StuRegisterRemarkReqDTO requestParam);
}
