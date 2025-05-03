import org.AList.common.biz.user.StuIdContext;
import org.AList.common.biz.user.StuIdInfoDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试获取并打印学生上下文ID
 */
public class StuIdContextPrintTest {

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    @BeforeEach
    void setUp() {
        System.out.println("----- 测试开始 -----");
    }

    @AfterEach
    void tearDown() {
        // 清理上下文，避免影响其他测试
        StuIdContext.removeStudentId();
        executor.shutdown();
        System.out.println("----- 测试结束 -----\n");
    }

    @Test
    void testPrintStudentIdInMainThread() {
        // 1. 设置学生ID
        StuIdInfoDTO stuInfo = new StuIdInfoDTO();
        stuInfo.setStudentId("20240001");
        StuIdContext.setStudentId(stuInfo);

        // 2. 获取并打印
        String studentId = StuIdContext.getStudentId();
        System.out.println("[主线程] 当前学生ID: " + studentId);

        // 3. 验证
        assertNotNull(studentId);
        assertEquals("20240001", studentId);
    }

    @Test
    void testPrintStudentIdInAsyncThread() throws Exception {
        // 1. 主线程设置学生ID
        StuIdInfoDTO stuInfo = new StuIdInfoDTO();
        stuInfo.setStudentId("20240002");
        StuIdContext.setStudentId(stuInfo);
        System.out.println("[主线程] 设置学生ID: 20240002");

        // 2. 提交异步任务并打印
        executor.submit(() -> {
            String studentId = StuIdContext.getStudentId();
            System.out.println("[子线程] 获取学生ID: " + studentId);
            assertEquals("20240002", studentId);
        }).get(); // 等待任务完成
    }

    @Test
    void testPrintWhenNoContext() {
        // 不设置上下文，直接获取
        String studentId = StuIdContext.getStudentId();
        System.out.println("[无上下文] 学生ID: " + studentId);

        // 验证应为null
        assertNull(studentId);
    }
}