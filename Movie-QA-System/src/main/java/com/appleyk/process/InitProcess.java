package com.appleyk.process;

import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.recognition.StopWord;
import org.apdplat.word.util.WordConfTools;

import com.appleyk.InitElements.KnowledgeBase;
import com.appleyk.InitElements.QAWordSegmenter;
import com.appleyk.InitElements.QAWordVectors;
import com.appleyk.answer.AnswerExtract;
import com.appleyk.answer.AnswerTemplates;
import com.appleyk.answer.AnswerType;
import com.appleyk.answer.PossibilityType;
import com.appleyk.classfy.Pretreatment;
import com.appleyk.classfy.PretreatmentVec;
import com.appleyk.question.classify.QuestionClassifier;
import com.appleyk.question.classify.QuestionType;
import com.appleyk.question.classify.ner.impl.AnswerNer;
import com.appleyk.question.classify.ner.impl.EntityNer;
import com.appleyk.question.classify.ner.impl.RelationNer;
import com.appleyk.question.classify.ner.impl.SymptomNer;
import com.appleyk.question.classify.QuestionAnalyzer;


public class InitProcess{
	
	public static QuestionAnalyzer QUESTION_ANALYZER;
    public static AnswerExtract ANSWER_EXTRACT;
	private static InitProcess instance;
	
	
	public static InitProcess getInitProcess() throws Exception {
		if(instance == null) {
			System.out.println("ok");
			instance = new InitProcess();
		}
		return instance;
	}
	public InitProcess() throws Exception{
		//加载疾病药品实体
		WordConfTools.set("dic.path", "classpath:dic.txt，G:\\毕设\\数据\\diseases.txt，G:\\毕设\\数据\\drugs.txt");
		//加载停词
        WordConfTools.set("stopwords.path","G:\\毕设\\数据\\stopwords.txt");
        DictionaryFactory.reload();
        StopWord.reload();
        QAWordVectors.getInstance().loadCache("G:\\毕设\\数据\\new_word2vec_cache.txt");
        QAWordVectors.getInstance().loadCache("G:\\毕设\\数据\\no_word2vec_cache.txt");
        QAWordSegmenter.getInstance().loadCache("G:\\毕设\\数据\\new_word_segment_cache.txt");
        KnowledgeBase.getInstance().init("G:\\毕设\\数据\\kb.ttl");
//		questionsPattern = loadQuestionsPattern();
//		vocabulary = loadVocabulary();
//		nbModel = loadClassifierModel();
        
        
  //      PretreatmentVec pretreatment = new PretreatmentVec(QAWordVectors.getInstance());
  //      pretreatment.loadToCreateVecMatrix("G:\\毕设\\数据\\问题分类训练数据.txt");
   //     pretreatment.loadToCreateVecMatrix("G:\\毕设\\数据\\temp_qqqq.txt");
        
        
    //    Pretreatment pretreatment = new Pretreatment();
    //    pretreatment.load("G:\\毕设\\数据\\问题分类训练数据.txt");
        Pretreatment.getPretreatment().load("G:\\毕设\\数据\\问题分类训练数据.txt");
        QuestionClassifier classify = new QuestionClassifier();
        classify.loadModel("G:\\毕设\\数据\\model.txt");
  //      classify.setPretreatment(pretreatment);
        
        
        QUESTION_ANALYZER = new QuestionAnalyzer(classify);
        
        
        QUESTION_ANALYZER.addEntityNer(new EntityNer());
        
        QUESTION_ANALYZER.addEntityNer(new AnswerNer(QAWordVectors.getInstance(), QAWordSegmenter.getInstance()));
   //     QUESTION_ANALYZER.showNer();
        //ontology:症状,name顽固性便秘
        //ontology:症状,name颌下腺导管口有咸味或脓性分泌物排出
        QUESTION_ANALYZER.addEntityNer(new SymptomNer(QAWordVectors.getInstance(), QAWordSegmenter.getInstance()));
        //只是将词向量与分词加载进去，并未生成关系
        QUESTION_ANALYZER.addRelationNer(new RelationNer(QAWordVectors.getInstance(), QAWordSegmenter.getInstance()));
 //       QUESTION_ANALYZER.showNer();
        
        AnswerTemplates.addTemplate(QuestionType.FACTOR, AnswerType.DISEASE, PossibilityType.SURE, "您患有{0}，建议去医院仔细检查");
        AnswerTemplates.addTemplate(QuestionType.FACTOR, AnswerType.DISEASE, PossibilityType.MAY, "您可能患有{0}，建议去医院仔细检查");
        AnswerTemplates.addTemplate(QuestionType.FACTOR, AnswerType.DISEASE, PossibilityType.CERTAINLY_NOT, "您应该没有患病，建议去医院仔细检查");
        AnswerTemplates.addTemplate(QuestionType.FACTOR, AnswerType.SYMPTOM, PossibilityType.SURE, "{0}");
        AnswerTemplates.addTemplate(QuestionType.FACTOR, AnswerType.SYMPTOM, PossibilityType.MAY, "可能存在{0}");
        AnswerTemplates.addTemplate(QuestionType.FACTOR, AnswerType.SYMPTOM, PossibilityType.CERTAINLY_NOT, "不太理解您的问题");
        AnswerTemplates.addTemplate(QuestionType.FACTOR, AnswerType.DRUG, PossibilityType.SURE, "建议服用{0}");
        AnswerTemplates.addTemplate(QuestionType.FACTOR, AnswerType.DRUG, PossibilityType.MAY, "建议服用{0}");
        AnswerTemplates.addTemplate(QuestionType.FACTOR, AnswerType.DRUG, PossibilityType.CERTAINLY_NOT, "您应该没有患病，建议去医院仔细检查");
        AnswerTemplates.addTemplate(QuestionType.BOOL, AnswerType.DISEASE, PossibilityType.SURE, "能");
        AnswerTemplates.addTemplate(QuestionType.BOOL, AnswerType.DISEASE, PossibilityType.MAY, "可能");
        AnswerTemplates.addTemplate(QuestionType.BOOL, AnswerType.DISEASE, PossibilityType.CERTAINLY_NOT, "不能");
        AnswerTemplates.addTemplate(QuestionType.BOOL, AnswerType.SYMPTOM, PossibilityType.SURE, "能");
        AnswerTemplates.addTemplate(QuestionType.BOOL, AnswerType.SYMPTOM, PossibilityType.MAY, "可能");
        AnswerTemplates.addTemplate(QuestionType.BOOL, AnswerType.SYMPTOM, PossibilityType.CERTAINLY_NOT, "不能");
        AnswerTemplates.addTemplate(QuestionType.BOOL, AnswerType.DRUG, PossibilityType.SURE, "能");
        AnswerTemplates.addTemplate(QuestionType.BOOL, AnswerType.DRUG, PossibilityType.MAY, "可能");
        AnswerTemplates.addTemplate(QuestionType.BOOL, AnswerType.DRUG, PossibilityType.CERTAINLY_NOT, "不能");
        AnswerTemplates.addTemplate(QuestionType.COUNT, AnswerType.DISEASE, PossibilityType.SURE, "共{0}种");
        AnswerTemplates.addTemplate(QuestionType.COUNT, AnswerType.DISEASE, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.COUNT, AnswerType.SYMPTOM, PossibilityType.SURE, "共{0}种");
        AnswerTemplates.addTemplate(QuestionType.COUNT, AnswerType.SYMPTOM, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.COUNT, AnswerType.DRUG, PossibilityType.SURE, "共{0}种");
        AnswerTemplates.addTemplate(QuestionType.COUNT, AnswerType.DRUG, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.LIST, AnswerType.DISEASE, PossibilityType.SURE, "{0},{1},{2}");
        AnswerTemplates.addTemplate(QuestionType.LIST, AnswerType.DISEASE, PossibilityType.MAY, "可能有{0},{1},{2}");
        AnswerTemplates.addTemplate(QuestionType.LIST, AnswerType.DISEASE, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.LIST, AnswerType.SYMPTOM, PossibilityType.SURE, "{0},{1},{2}");
        AnswerTemplates.addTemplate(QuestionType.LIST, AnswerType.SYMPTOM, PossibilityType.MAY, "可能有{0},{1},{2}");
        AnswerTemplates.addTemplate(QuestionType.LIST, AnswerType.SYMPTOM, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.LIST, AnswerType.DRUG, PossibilityType.SURE, "{0},{1},{2}");
        AnswerTemplates.addTemplate(QuestionType.LIST, AnswerType.DRUG, PossibilityType.MAY, "可能有{0},{1},{2}");
        AnswerTemplates.addTemplate(QuestionType.LIST, AnswerType.DRUG, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MAX, AnswerType.DISEASE, PossibilityType.SURE, "{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MAX, AnswerType.DISEASE, PossibilityType.MAY, "");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MAX, AnswerType.DISEASE, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MAX, AnswerType.SYMPTOM, PossibilityType.SURE, "{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MAX, AnswerType.SYMPTOM, PossibilityType.MAY, "可能有{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MAX, AnswerType.SYMPTOM, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MAX, AnswerType.DRUG, PossibilityType.SURE, "{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MAX, AnswerType.DRUG, PossibilityType.MAY, "可能有{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MAX, AnswerType.DRUG, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MIN, AnswerType.DISEASE, PossibilityType.SURE, "{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MIN, AnswerType.DISEASE, PossibilityType.MAY, "");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MIN, AnswerType.DISEASE, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MIN, AnswerType.SYMPTOM, PossibilityType.SURE, "{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MIN, AnswerType.SYMPTOM, PossibilityType.MAY, "可能有{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MIN, AnswerType.SYMPTOM, PossibilityType.CERTAINLY_NOT, "不存在");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MIN, AnswerType.DRUG, PossibilityType.SURE, "{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MIN, AnswerType.DRUG, PossibilityType.MAY, "可能有{0}");
        AnswerTemplates.addTemplate(QuestionType.NUMBER_MIN, AnswerType.DRUG, PossibilityType.CERTAINLY_NOT, "不存在");
        
        ANSWER_EXTRACT = new AnswerExtract();

        
	}
}
