import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.AList.domain.dao.entity.TestDO;
import org.AList.domain.dao.mapper.TestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = org.AList.AListStudentApplication.class)
public class TestDOEncrypt {
    @Autowired
    private TestMapper testMapper;
    @Test
    public void test() {
        LambdaQueryWrapper<TestDO> eq = Wrappers.lambdaQuery(TestDO.class)
                .eq(TestDO::getId, 99999);
        TestDO testDO=TestDO.builder()
                .id(99999)
                        .name("aaaaaaaaaaaaaaaaaa")
                                .build();
        testMapper.insert(testDO);
        testMapper.selectList(eq).forEach(System.out::println);
    }
}
