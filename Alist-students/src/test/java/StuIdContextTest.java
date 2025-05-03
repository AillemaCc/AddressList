import org.AList.common.biz.user.StuIdContext;
import org.AList.common.biz.user.StuIdInfoDTO;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StuIdContextTest {

    @Test
    void testSyncContext() {
        // 模拟设置学生ID
        StuIdInfoDTO stuInfo = new StuIdInfoDTO();
        stuInfo.setStudentId("20230001");
        StuIdContext.setStudentId(stuInfo);

        // 验证同步线程中能正确获取
        assertEquals("20230001", StuIdContext.getStudentId());

        // 清理上下文
        StuIdContext.removeStudentId();
        assertNull(StuIdContext.getStudentId());
    }

    @Test
    void testAsyncContext() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 主线程设置学生ID
        StuIdInfoDTO stuInfo = new StuIdInfoDTO();
        stuInfo.setStudentId("20230002");
        StuIdContext.setStudentId(stuInfo);

        // 提交异步任务
        executor.submit(() -> {
            // 子线程应能通过TTL获取父线程的上下文
            assertEquals("20230002", StuIdContext.getStudentId());
        }).get(); // 等待任务完成

        // 清理上下文
        StuIdContext.removeStudentId();
        executor.shutdown();
    }

    @Test
    void testContextIsolation() {
        // 线程1设置ID
        StuIdInfoDTO stu1 = new StuIdInfoDTO();
        stu1.setStudentId("20230003");
        StuIdContext.setStudentId(stu1);

        // 线程2设置不同的ID
        new Thread(() -> {
            StuIdInfoDTO stu2 = new StuIdInfoDTO();
            stu2.setStudentId("20230004");
            StuIdContext.setStudentId(stu2);
            assertEquals("20230004", StuIdContext.getStudentId());
        }).start();

        // 线程1的上下文不受线程2影响
        assertEquals("20230003", StuIdContext.getStudentId());
        StuIdContext.removeStudentId();
    }
}