package com.appleyk.classfy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import com.appleyk.InitElements.QAWordVectors;
import com.appleyk.cnn.CNN;
import com.appleyk.cnn.CNN.LayerBuilder;
import com.appleyk.cnn.Layer;
import com.appleyk.cnn.Layer.Size;
import com.appleyk.cnn.dataset.Dataset;

import libsvm.svm_node;

public class Pretreatment {
    private static final Logger LOGGER = Logger.getLogger(Pretreatment.class.getName());
    
    private static Pretreatment instance = new Pretreatment();
    

    private static final int TOP_N = 4;
    //存放chi值前N的一系列字，每个类别的topN，作为关键字
    private final List<String> words = new ArrayList<>();
    private final Map<String, Double> idfs = new HashMap<>();
    
    
    public static Pretreatment getPretreatment () {
    	return instance;
    }

	public List<svm_node> pretreat(String question) {
//		System.out.println("enter pretreat");
	//	System.out.println(question);
        List<String> ws = segment(question);
  //      for(String a : ws) {
  //      	System.out.println(a);
  //      }
  //      System.out.println("ok segment");
        List<svm_node> nodes = new ArrayList<>();
        Map<String, Double> counts = new HashMap<>();
        //问题中每个字和他出现的次数
        ws.forEach(word -> {
            if (!counts.containsKey(word)) {
                counts.put(word, 0.0);
            }
            counts.put(word, counts.get(word) + 1.0);
        });
        int wordSize = ws.size();
        //对于每一个字找到这个字在----中的位置，计算tf_idf，将位置与权值存入svm结点。
        counts.keySet().forEach(word -> {
            if (this.words.contains(word)) {
                int index = this.words.indexOf(word);
                System.out.println(word+":"+index);
                double tf_idf = (counts.get(word) / wordSize) * this.idfs.get(word);
                svm_node node = new svm_node();
                node.index = index;
                node.value = tf_idf;
                nodes.add(node);
            }
        });
        return nodes;
    }

    public void load(String src) {
        LOGGER.log(Level.INFO, "load training file = {0}", src);
        BufferedReader br = null;
        try {
            //类别，单词，出现次数
        	//trainDatas 存储label
            Map<Double, List<Map<String, Double>>> trainDatas = new HashMap<>();
            br = new BufferedReader(new InputStreamReader(new FileInputStream(src), "UTF-8"));
            String line;
            //逐句分析，获得向量。
            //tf是每一句的向量
            //trainsData是总的向量表
            while ((line = br.readLine()) != null) {
            	
                String[] contents = line.split(" ");
                
                double label = Double.parseDouble(contents[0]);
                //contents[1],所有问题的句子，如“阿莫西林是干什么用的”
                String sentence = contents[1];
                //按字分词
                List<String> ws = segment(sentence);
                
         //       System.out.println(label);
                if (!trainDatas.containsKey(label)) {
                    trainDatas.put(label, new ArrayList<>());
                }
                Map<String, Double> tf = new HashMap<>();
                
                
                //创建对于每一个字的向量，记录出现次数。
                ws.forEach(word -> {
                    if (!word.matches(",|\\.|:|\\?|，|。|？")) {
                        if (!tf.containsKey(word)) {
                            tf.put(word, 0.0);
                        }
                        tf.put(word, tf.get(word) + 1.0);
                    }
                });
                trainDatas.get(label).add(tf);
            }

            //处理数据
            //(1)卡方统计量 (N*(AD-CB)2)/((A+C)(B+D)(A+B)(C+D))
            //N = 训练数据集句子总数 
            //A = 在这个类别中，包含这个词的句子数量
            //B = 在这个类别中，排除这个类别，其他类别中包含这个词的句子数量
            //C = 在这个类别中，不包含这个词的句子数量
            //D = 在这个类别中，排除这个类别，其他类别中不包含这个词的句子数量
            double N = 0.0;
            for (List<Map<String, Double>> list : trainDatas.values()) {
                N += list.size();
            }
            //类别，词语，在该类别中的句子中出现该词语的句子数目
            Map<Double, Map<String, Double>> A = new HashMap<>();
            //类别，词语，在其他类别中的句子中出现该词语句子数目
            Map<Double, Map<String, Double>> B = new HashMap<>();
            //类别，词语，在该类别中的句子中没有出现该词语的句子数目
            Map<Double, Map<String, Double>> C = new HashMap<>();
            //类别，词语，在其他类别中的句子中未出现该词语句子数目
            Map<Double, Map<String, Double>> D = new HashMap<>();

            //A = 在这个类别中，包含这个词的句子数量。stream生成流，entryset生成键值对。
            trainDatas.entrySet().stream().forEach(entry -> {
                double label = entry.getKey();
                List<Map<String, Double>> sentences = entry.getValue();
                Map<String, Double> counts = new HashMap<>();
                sentences.stream().forEach(sentence -> {
                    sentence.keySet().stream().forEach(word -> {
                        if (!counts.containsKey(word)) {
                            counts.put(word, 0.0);
                        }
                        counts.put(word, counts.get(word) + 1.0);
                    });
                });
                A.put(label, counts);
            });

            //B = 在这个类别中，排除这个类别，其他类别中包含这个词的句子数量
            trainDatas.keySet().stream().forEach(label1 -> {
            	//A中label1的所有词
                Set<String> wordsInLabel1 = A.get(label1).keySet();
                Map<String, Double> counts = new HashMap<>();
                
                wordsInLabel1.stream().forEach(wordInlabel1 -> {
                    counts.put(wordInlabel1, 0.0);
                    trainDatas.entrySet().stream().forEach(entry2 -> {
                        double label2 = entry2.getKey();
                        if (label1 != label2) {
                            List<Map<String, Double>> sentences = entry2.getValue();
                            sentences.stream().forEach(sentence -> {
                                if (sentence.keySet().contains(wordInlabel1)) {
                                    counts.put(wordInlabel1, counts.get(wordInlabel1) + 1);
                                }
                            });
                        }
                    });
                });
                B.put(label1, counts);
            });
            //C = 在这个类别中，不包含这个词的句子数量
            trainDatas.entrySet().stream().forEach(entry1 -> {
                double label1 = entry1.getKey();
                List<Map<String, Double>> sentences = entry1.getValue();
                Set<String> wordsInLabel1 = A.get(label1).keySet();
                Map<String, Double> counts = new HashMap<>();
                wordsInLabel1.stream().forEach(wordInlabel1 -> {
                    counts.put(wordInlabel1, (double) entry1.getValue().size());
                    sentences.stream().forEach(sentence -> {
                        if (sentence.containsKey(wordInlabel1)) {
                            counts.put(wordInlabel1, counts.get(wordInlabel1) - 1);
                        }
                    });
                });
                C.put(label1, counts);
            });
            //D = 在这个类别中，排除这个类别，其他类别中不包含这个词的句子数量
            trainDatas.entrySet().stream().forEach(entry1 -> {
                double label1 = entry1.getKey();
                Set<String> wordsInLabel1 = A.get(label1).keySet();
                Map<String, Double> counts = new HashMap<>();
                wordsInLabel1.stream().forEach(wordInlabel1 -> {
                    counts.put(wordInlabel1, 0.0);
                    trainDatas.entrySet().stream().forEach(entry2 -> {
                        double label2 = entry2.getKey();
                        if (label1 != label2) {
                            List<Map<String, Double>> sentences = entry2.getValue();
                            counts.put(wordInlabel1, counts.get(wordInlabel1) + sentences.size());
                            sentences.stream().forEach(sentence -> {
                                if (sentence.keySet().contains(wordInlabel1)) {
                                    counts.put(wordInlabel1, counts.get(wordInlabel1) - 1);
                                }
                            });
                        }
                    });
                });
                D.put(label1, counts);
            });
            //(N*(AD-CB)2)/((A+C)(B+D)(A+B)(C+D))
            for (Map.Entry<Double, Map<String, Double>> entry : A.entrySet()) {
                double kind = entry.getKey();
                Map<String, Double> map = entry.getValue();
                Map<String, Double> chis = new HashMap<>();
                for (String word : map.keySet()) {
                    double a = map.get(word);
                    double b = B.get(kind).get(word);
                    double c = C.get(kind).get(word);
                    double d = D.get(kind).get(word);
                    double chi = (N * Math.pow(a * d - c * b, 2)) / ((a + c) * (b + d) * (a + b) * (c + d));
                    chis.put(word, chi);
                }
                List<Map.Entry<String, Double>> list = new ArrayList<>(chis.entrySet());
                Collections.sort(list, (o1, o2) -> {
                    return Double.compare(o2.getValue(), o1.getValue());
                });
                //Top10
                for (int i = 0; i < TOP_N && i < list.size(); i++) {
                	
                    if (!this.words.contains(list.get(i).getKey())) {
                        this.words.add(list.get(i).getKey());
                    }
                }
            }
            LOGGER.log(Level.INFO, "TOPN = {0}", TOP_N);

            //(2)idf
            StringBuilder sb = new StringBuilder();
            for (String word : this.words) {
                this.idfs.put(word, 0.0);
                trainDatas.entrySet().forEach(entry -> {
                    entry.getValue().forEach(sentence -> {
                        if (sentence.containsKey(word)) {
                            this.idfs.put(word, this.idfs.get(word) + 1.0);
                        }
                    });
                });
                double idf = Math.log10(N / this.idfs.get(word));
                this.idfs.put(word, idf);
                sb.append(word);
                sb.append(" ");
            }
     //       System.out.println("------------------------------------------------------------------");
     //       System.out.println(this.idfs);
     //       System.out.println("------------------------------------------------------------------");
            LOGGER.log(Level.INFO, "words = {0}", sb.toString());
        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(Pretreatment.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(Pretreatment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    private List<String> segment(String sentence) {
        //基于字，分词
        char[] cs = sentence.toCharArray();
        List<String> ws = new ArrayList<>();
        for (char c : cs) {
            ws.add(c + "");
        }
        return ws;
    }
    public static void trainCNN() {
    	LayerBuilder builder = new LayerBuilder();
    	builder.addLayer(Layer.buildInputLayer(new Size(28, 28)));
    	builder.addLayer(Layer.buildConvLayer(6, new Size(5, 5)));
    	builder.addLayer(Layer.buildSampLayer(new Size(2, 2)));
    	builder.addLayer(Layer.buildConvLayer(12, new Size(5, 5)));
    	builder.addLayer(Layer.buildSampLayer(new Size(2, 2)));
    	builder.addLayer(Layer.buildOutputLayer(10));
    	CNN cnn = new CNN(builder, 50);
    	
    	
    	String fileName = "data/train.format";
    	Dataset dataset = Dataset.load(fileName, ",", 784);
    	cnn.train(dataset, 100);
    	Dataset testset = Dataset.load("data/test.format", ",", -1);
    	cnn.predict(testset, "data/test.predict");


    }
    
    
    public static void trainSVM() {
    	Pretreatment p = new Pretreatment();
        p.load("G:\\毕设\\数据\\问题分类训练数据.txt");
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
        	br = new BufferedReader(new FileReader("G:\\毕设\\数据\\问题分类训练数据.txt"));
            bw = new BufferedWriter(new FileWriter("G:\\毕设\\数据\\train_data.txt"));
            String line;
            while ((line = br.readLine()) != null) {
            	String[] contents = line.split(" ");
                double label = Double.parseDouble(contents[0]);
                String sentence = contents[1];
                StringBuilder sb = new StringBuilder();
                sb.append(label);
                sb.append(" ");
                //对每个句子的svm节点，写入sb
                p.pretreat(sentence).forEach(node -> {
                    sb.append(node.index);
                    sb.append(":");
                    sb.append(node.value);
                    sb.append(" ");
                });
                sb.deleteCharAt(sb.length() - 1);
                bw.write(sb.toString());
                bw.newLine();
            }
            br.close();
            bw.close();
            String[] arg = {"-g", "2.0", "-c", "32", "-t", "0", "-m", "500.0", "-h", "0",
                    "G:\\毕设\\数据\\train_data.txt", "G:\\毕设\\数据\\model.txt"};
   //         svm_train.main(arg);
        }catch (IOException | NumberFormatException e) {
        } finally {
        	
        }
    }

}
