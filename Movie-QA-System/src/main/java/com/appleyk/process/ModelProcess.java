package com.appleyk.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;


public class ModelProcess {

	//分类标签号和问句模板对应表
	Map<Double, String> questionsPattern;

	//词语和下标的对应表 词汇表
	Map<String, Integer> vocabulary;

	//关键字与其词性的map键值对集合 句子抽象
	Map<String, String> abstractMap;

	NaiveBayesModel nbModel;

    String rootDirPath = "D:/movie/HanLP/data";

    int modelIndex = 0;

	public ModelProcess() throws Exception{
		questionsPattern = loadQuestionsPattern();
		vocabulary = loadVocabulary();
		nbModel = loadClassifierModel();
	}
	
	public ModelProcess(String rootDirPath) throws Exception{
		this.rootDirPath = rootDirPath+'/';
		questionsPattern = loadQuestionsPattern();
		vocabulary = loadVocabulary();
		nbModel = loadClassifierModel();
	}
	
	public ArrayList<String> analyQuery(String queryString) throws Exception {
		System.out.println("原始句子："+queryString);
		System.out.println("========HanLP开始分词（queryAbstract）========");
		/**
		 * 抽象句子，利用HanPL分词，将关键字进行词性抽象
		 */
		String abstr = queryAbstract(queryString);	
		System.out.println("句子抽象化结果："+abstr);// nm 的 导演 是 谁
		/**
		 * 将抽象的句子与spark训练集中的模板进行匹配，拿到句子对应的模板
		 */
		String strPatt = queryClassify(abstr);
		System.out.println("句子套用模板结果："+strPatt); // nm 制作 导演列表

		String finalPattern = queryExtenstion(strPatt);
		System.out.println("原始句子替换成系统可识别的结果："+finalPattern);// 但丁密码 制作 导演列表

		ArrayList<String> resultList = new ArrayList<String>();
		resultList.add(String.valueOf(modelIndex));
		String[] finalPattArray = finalPattern.split(" ");
		for (String word : finalPattArray)
			resultList.add(word);
		return resultList;
	}

	public  String queryAbstract(String querySentence) {
		// 句子抽象化
		Segment segment = HanLP.newSegment().enableCustomDictionary(true);
		List<Term> terms = segment.seg(querySentence);
		System.out.println("queryAbstract==============================：");
		System.out.println("terms========================="+terms);
		String abstractQuery = "";
		abstractMap = new HashMap<String, String>();
		int nrCount = 0; //nr 人名词性这个 词语出现的频率
		for (Term term : terms) {
			String word = term.word;
			System.out.println(word);
			String termStr = term.toString();
			
			System.out.println(termStr);
			if (termStr.contains("nm")) {        //nm 电影名
				abstractQuery += "nm ";
				abstractMap.put("nm", word);
			} else if (termStr.contains("nr") && nrCount == 0) { //nr 人名
				abstractQuery += "nnt ";
				abstractMap.put("nnt", word);
				nrCount++;
			}else if (termStr.contains("nr") && nrCount == 1) { //nr 人名 再出现一次，改成nnr
				abstractQuery += "nnr ";
				abstractMap.put("nnr", word);
				nrCount++;
			}else if (termStr.contains("x")) {  //x  评分
				abstractQuery += "x ";
				abstractMap.put("x", word);
			} else if (termStr.contains("ng")) { //ng 类型
				abstractQuery += "ng ";
				abstractMap.put("ng", word);
			}
			else {
				abstractQuery += word + " ";
			}
		}
		System.out.println(abstractQuery);
		
		System.out.println("========HanLP分词结束（queryAbstract）========");
		return abstractQuery;
	}

	public  String queryExtenstion(String queryPattern) {
		// 句子还原
		Set<String> set = abstractMap.keySet();
		System.out.println("========HanLP分词（queryExtenstion）========");
		for (String key : set) {
			/**
			 * 如果句子模板中含有抽象的词性
			 */
			if (queryPattern.contains(key)) {
				
				/**
				 * 则替换抽象词性为具体的值 
				 */
				String value = abstractMap.get(key);
				queryPattern = queryPattern.replace(key, value);
				System.out.println(queryPattern);
			}
		}
		String extendedQuery = queryPattern;
		/**
		 * 当前句子处理完，抽象map清空释放空间并置空，等待下一个句子的处理
		 */
		abstractMap.clear();
		abstractMap = null;
		System.out.println("========HanLP分词结束（queryExtenstion）========");
		return extendedQuery;
	}

	public  Map<String, Integer> loadVocabulary() {
		Map<String, Integer> vocabulary = new HashMap<String, Integer>();
		File file = new File(rootDirPath + "question/vocabulary.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(":"); //["1","合作"]
				int index = Integer.parseInt(tokens[0]);
				String word = tokens[1];
				vocabulary.put(word, index);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vocabulary;
	}

	public  String loadFile(String filename) throws IOException {
		File file = new File(rootDirPath + filename);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String content = "";
		String line;
		while ((line = br.readLine()) != null) {
			content += line + "`";
		}
		br.close();
		return content;
	}

	public  double[] sentenceToArrays(String sentence) throws Exception {
		//句子分词后与词汇表进行key匹配转换为double向量数组
		double[] vector = new double[vocabulary.size()];

		//初始化
		for (int i = 0; i < vocabulary.size(); i++) {
			vector[i] = 0;
		}

		Segment segment = HanLP.newSegment();
		List<Term> terms = segment.seg(sentence);
		for (Term term : terms) {
			String word = term.word;

			if (vocabulary.containsKey(word)) {
				int index = vocabulary.get(word);
				vector[index] = 1;
			}
		}
		return vector;
	}

	public  NaiveBayesModel loadClassifierModel() throws Exception {
			//生成训练集，并将其加载至nbmodel
			SparkConf conf = new SparkConf().setAppName("NaiveBayesTest").setMaster("local[*]");
			JavaSparkContext sc = new JavaSparkContext(conf);

			//labeled point 是一个局部向量，要么是密集型的要么是稀疏型的
			List<LabeledPoint> train_list = new LinkedList<LabeledPoint>();
			String[] sentences = null;

			/**
			 * 英雄的评分是多少
			 */
			String scoreQuestions = loadFile("question/【0】评分.txt");
			sentences = scoreQuestions.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(0.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			/**
			 * 英雄什么时候上映的
			 */
			String timeQuestions = loadFile("question/【1】上映.txt");
			sentences = timeQuestions.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(1.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			/**
			 * 英雄是什么类型的
			 */
			String styleQuestions = loadFile("question/【2】风格.txt");
			sentences = styleQuestions.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(2.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			/**
			 * 英雄的简介、英雄的剧情是什么
			 */
			String storyQuestions = loadFile("question/【3】剧情.txt");
			sentences = storyQuestions.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(3.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			/**
			 * 英雄有哪些演员出演、出演英雄的演员都有哪些
			 */
			String actorsQuestion = loadFile("question/【4】某电影有哪些演员出演.txt");
			sentences = actorsQuestion.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(4.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			/**
			 * 章子怡
			 */
			String actorInfos = loadFile("question/【5】演员简介.txt");
			sentences = actorInfos.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(5.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			/**
			 * 周星驰出演的科幻类型的电影有哪些
			 */
			String genreMovies = loadFile("question/【6】某演员出演过的类型电影有哪些.txt");
			sentences = genreMovies.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(6.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			/**
			 * 周星驰演了哪些电影
			 */
			String actorsMovie = loadFile("question/【7】某演员演了什么电影.txt");
			sentences = actorsMovie.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(7.0, Vectors.dense(array));
				train_list.add(train_one);
			}
			
			/**
			 * 巩俐参演的电影评分在8.5分以上的有哪些、巩俐参演的电影评分大于8.5分的有哪些
			 */
			String highScoreMovies = loadFile("question/【8】演员参演的电影评分【大于】.txt");
			sentences = highScoreMovies.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(8.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			/**
			 * 巩俐参演的电影评分在8.5分以下的有哪些、巩俐参演的电影评分小于8.5分的有哪些
			 */
			String lowScoreMovies = loadFile("question/【9】演员参演的电影评分【小于】.txt");
			sentences = lowScoreMovies.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(9.0, Vectors.dense(array));
				train_list.add(train_one);
			}
			
			/**
			 * 章子怡演过哪些类型的电影
			 */
			String actorMovieGenres = loadFile("question/【10】某演员出演过哪些类型的电影.txt");
			sentences = actorMovieGenres.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(10.0, Vectors.dense(array));
				train_list.add(train_one);
			}
			
			/**
			 * 章子怡与李连杰合作过哪些电影、章子怡和巩俐一起演过什么电影
			 */
			String withMovies = loadFile("question/【11】演员A和演员B合作了哪些电影.txt");
			sentences = withMovies.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(11.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			/**
			 * 章子怡与李连杰合作过哪些电影、章子怡和巩俐一起演过什么电影
			 */
			String countMovies = loadFile("question/【12】某演员一共演过多少电影.txt");
			sentences = countMovies.split("`");
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);
				LabeledPoint train_one = new LabeledPoint(12.0, Vectors.dense(array));
				train_list.add(train_one);
			}
			
			/**
			 * 章子怡的生日是多少、章子怡的出生日期是什么时候
			 */
			String actorBirthdayQuestion = loadFile("question/【13】演员出生日期.txt");
			sentences = actorBirthdayQuestion.split("`"); //之前用`替换了换行
			for (String sentence : sentences) {
				double[] array = sentenceToArrays(sentence);				
				LabeledPoint train_one = new LabeledPoint(13.0, Vectors.dense(array));
				train_list.add(train_one);
			}

			//System.out.println(actorBirthdayQuestion);
			//System.out.println(train_list);
			
			/**
			 * SPARK的核心是RDD(弹性分布式数据集)
			 * Spark是Scala写的,JavaRDD就是Spark为Java写的一套API
			 * JavaSparkContext sc = new JavaSparkContext(sparkConf);    //对应JavaRDD
			 * SparkContext	    sc = new SparkContext(sparkConf)    ;    //对应RDD
			 */
			JavaRDD<LabeledPoint> trainingRDD = sc.parallelize(train_list);
			NaiveBayesModel nb_model = NaiveBayes.train(trainingRDD.rdd());
			sc.close();
			return nb_model;
	}

	public  Map<Double, String> loadQuestionsPattern() {
		Map<Double, String> questionsPattern = new HashMap<Double, String>();
		File file = new File(rootDirPath + "question/question_classification.txt");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(":");
				double index = Double.valueOf(tokens[0]);
				String pattern = tokens[1];
				questionsPattern.put(index, pattern); //0:nm 评分
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return questionsPattern;
	}

	public  String queryClassify(String sentence) throws Exception {
		System.out.println("========queryClassify========");
		double[] testArray = sentenceToArrays(sentence);
		System.out.println(testArray);
		Vector v = Vectors.dense(testArray);
		System.out.println(v);
		//看一下所有的概率
		
		Vector allposibility = nbModel.predictProbabilities(v);
		System.out.println("十三个模板的概率" + allposibility);
		//对数据进行预测predict
		double index = nbModel.predict(v);
		modelIndex = (int)index;
		System.out.println("将问题分类至模板 " + index);
		//System.out.println("问题模板分类【0】概率："+vRes.toArray()[0]);
		//System.out.println("问题模板分类【13】概率："+vRes.toArray()[13]);
		System.out.println("========queryClassify========");
		return questionsPattern.get(index);
		
	}

	public static void main(String[] agrs) throws Exception {
		System.out.println("Hello World !");
	}
}
