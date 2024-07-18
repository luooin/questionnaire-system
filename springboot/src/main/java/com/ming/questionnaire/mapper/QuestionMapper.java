package com.ming.questionnaire.mapper;

import com.ming.questionnaire.pojo.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Ming
 * @since 2022-03-24
 */
@Repository
public interface QuestionMapper extends BaseMapper<Question> {

    // 通过问卷id删除对应的问题
    int physicsDeleteByPId(String paperId);

}
