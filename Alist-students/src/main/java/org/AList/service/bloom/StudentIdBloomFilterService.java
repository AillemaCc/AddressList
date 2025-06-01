package org.AList.service.bloom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.AList.domain.dao.mapper.StudentDefaultInfoMapper;
import org.redisson.api.RBloomFilter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 布隆过滤器缓存全量校方数据库学生学号方法实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentIdBloomFilterService {
    private final StudentDefaultInfoMapper studentDefaultInfoMapper;
    private final RBloomFilter<String> studentIdBloomFilter;

    @PostConstruct
    public void init() {
        loadAllStudentIds();
    }

    /**
     * 加载全量学号到布隆过滤器当中
     */
    private void loadAllStudentIds() {
        List<String> ids=studentDefaultInfoMapper.selectAllStudentIds();
        ids.forEach(studentIdBloomFilter::add);
        log.info("已加载{}个学号到布隆过滤器", ids.size());
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void refreshBloomFilter() {
        loadAllStudentIds();
    }

    /**
     * 验证学号是否存在布隆过滤器当中
     * @param studentId 学号
     * @return 验证结果 true为存在
     */
    public boolean contain(String studentId) {
        return !studentIdBloomFilter.contains(studentId);
    }

    /**
     * 添加学号到布隆过滤器方法
     * 注册成功可以使用
     * 新生入学可以使用
     * @param studentId 学号
     */
    public void add(String studentId) {
        studentIdBloomFilter.add(studentId);
    }

}
