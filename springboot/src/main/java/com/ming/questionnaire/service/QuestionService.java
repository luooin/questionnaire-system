package com.ming.questionnaire.service;

import com.ming.questionnaire.pojo.Question;

import java.util.List;

public interface QuestionService {

    // 通过问卷id查找对应的问题
    List<Question> selectQuestionByPId(String paperId);

}
