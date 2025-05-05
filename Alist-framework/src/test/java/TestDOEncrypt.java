import lombok.RequiredArgsConstructor;
import org.AList.domain.dao.entity.TestDO;
import org.AList.domain.dao.mapper.TestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor
public class TestDOEncrypt {
    private final TestMapper testMapper;
    @Test
    public void test() {
        TestDO testDO =TestDO.builder()
                .id(999)
                .name("fuck")
                .build();
        testMapper.insert(testDO);
    }
}
