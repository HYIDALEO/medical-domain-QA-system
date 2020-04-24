package com.appleyk.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.appleyk.QException;
import com.appleyk.process.InitProcess;
import com.appleyk.question.classify.QuestionAnalysisResult;
import com.appleyk.service.QAService;

@Service
@Primary
public class QAServiceImpl implements QAService{

	@Override
	public void showDictPath() {
		System.out.println("HanLP分词字典及自定义问题模板根目录：");
		System.out.println("用户自定义扩展词库【电影】：");
	}
	
	@Override
	public String answer(String input) throws Exception{
		String result = null;
		InitProcess.getInitProcess();
		String output;
		try {
			//ok
			result =InitProcess.QUESTION_ANALYZER.analysis_DP(input);
			System.out.println(result);
			//
		//	output = InitProcess.ANSWER_EXTRACT.extract(result.chains,result.questionType,result.answerType);
		} catch (QException e) {
            e.printStackTrace();
            output = "我无法理解你的问题，请尝试换种提问方式";
        }

		
		if (result != null && !result.equals("") && !result.equals("\\N")) {
			return result;
		} else {
			return "对不起，没有找到相应的答案";
		}
	}
}
