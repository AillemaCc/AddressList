package org.AList.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;  
import lombok.Data;  
import lombok.NoArgsConstructor;  
  
/**  
 * 学生信息响应实体类  
 */  
@Data  
@AllArgsConstructor  
@NoArgsConstructor  
@Builder  
public class StuInfoDTO {  
    /**  
     * 学院  
     */  
    private String academy;  
      
    /**  
     * 工作城市  
     */  
    private String city;  
      
    /**  
     * 班级  
     */  
    private String className;  
      
    /**  
     * 邮箱  
     */  
    private String email;  
      
    /**  
     * 工作单位  
     */  
    private String employer;  
      
    /**  
     * 入学年份  
     */  
    private String enrollmentYear;  
      
    /**  
     * 毕业年份  
     */  
    private String graduationYear;  
      
    /**  
     * 专业  
     */  
    private String major;  
      
    /**  
     * 姓名  
     */  
    private String name;  
      
    /**  
     * 手机号码  
     */  
    private String phone;  
      
    /**  
     * 学号  
     */  
    private String studentId;  
}