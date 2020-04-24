package com.appleyk.classfy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import com.appleyk.InitElements.QAWordVectors;

public class PretreatmentVec {
	 private Map<Double, Map<String, List<double[]>>> MatrixClassifyCache = new HashMap<>();
    
    
	 private final QAWordVectors wordVectors;
	 private Map<String, List<String>> segmentCache = new HashMap<>();
	
	public PretreatmentVec(QAWordVectors wordVectors) {
    	this.wordVectors = wordVectors;
    }
	
	
	private List<String> segment1(String token) {
	  List<String> tokens = new ArrayList<>();
	  switch (token.length()) {
	      case 2:
	          tokens.add(token.substring(0, 1));
	          tokens.add(token.substring(1, 2));
	          break;
	      case 3:
	          //12切一刀
	          String token1 = token.substring(0, 1);
	          String token23 = token.substring(1, 3);
	          if (this.wordVectors.hasWord(token23)) {
	              tokens.add(token1);
	              tokens.add(token23);
	          } else {
	              //23切一刀
	              String token12 = token.substring(0, 2);
	              String token3 = token.substring(2, 3);
	              tokens.add(token3);
	              if (this.wordVectors.hasWord(token12)) {
	                  tokens.add(token12);
	              } else {
	                  tokens.addAll(segment1(token12));
	              }
	          }
	          break;
	      case 4:
	          //中间切一刀
	          String token12 = token.substring(0, 2);
	          String token34 = token.substring(2, 4);
	          if (this.wordVectors.hasWord(token12) && this.wordVectors.hasWord(token34)) {
	              tokens.add(token12);
	              tokens.add(token34);
	          } else {
	              token23 = token.substring(1, 3);
	              if (this.wordVectors.hasWord(token23)) {
	                  tokens.add(token.substring(0, 1));
	                  tokens.add(token23);
	                  tokens.add(token.substring(3, 4));
	              } else {
	                  tokens.add(token.substring(0, 1));
	                  tokens.add(token.substring(1, 2));
	                  tokens.add(token.substring(2, 3));
	                  tokens.add(token.substring(3, 4));
	              }
	          }
	          break;
	      default:
	          for (int i = 0; i < token.length(); i++) {
	              tokens.add(token.substring(i, i + 1));
	          }
	  }
	  return tokens;
	}
	public void loadToCreateVecMatrix(String src) {
		BufferedReader br = null;
	
	  try {
	  	Map<Double, List<Map<String, Double>>> trainDatas = new HashMap<>();
	  	br = new BufferedReader(new InputStreamReader(new FileInputStream(src), "UTF-8"));
	  	String line;
			while ((line = br.readLine()) != null) {
				String[] contents = line.split(" ");
			    //存放每一个句子的分词结果
				List<String> senCache = new ArrayList();
				
				double label;
				if(contents[0].length()>1) {
					char tempqq = contents[0].charAt(1);
					label = (double)tempqq;
				}else {
					label = Double.parseDouble(contents[0]);
				}
				
			//	int atemp = Integer.parseInt(contents[0].charAt(1));
			//    double label = Double.parseDouble(temp);
				
				
			    //contents[1],所有问题的句子，如“阿莫西林是干什么用的”
			    String sentence = contents[1];
			  
			    if (!MatrixClassifyCache.containsKey(label)) {
			    	MatrixClassifyCache.put(label, new HashMap<>());
			    }
			    
			    if (this.segmentCache.containsKey(sentence)) {
			    	senCache = this.segmentCache.get(sentence);
		        }
			    File f = new File("infoword.txt");
			    FileWriter fileWritter = new FileWriter(f.getName(),true);
			    List<Word> words = WordSegmenter.seg(sentence);
			    
			    
			    
			    List<String> tokens = new ArrayList<>();
		        //首先判断每个token是否都有词向量值，如果没有则将token分割
		        //当token长度为1时，必定存在词向量
		        //当token长度为2时，分割成两个长度为1的token
		        //当token长度为3时，两次切割判断，一次在12中间，一次在23中间切割
		        //当token长度为4时，中间切割
		        //当token长度大于5时，全部切割成长度为1的token
		        //以上切割结果若失败了，则全部切割为长度为1的token
			    
			    for (Word word : words) {
			    	if (this.wordVectors.hasWord(word.getText())) {
		                tokens.add(word.getText());
		            } else {
		                tokens.addAll(segment1(word.getText()));
		            }
			    }
			    this.segmentCache.put(sentence, tokens);
			    List<double[]> MatrixCache = new ArrayList();
			    fileWritter.write(sentence+"\r\n");
			    for(String token: tokens) {
			    	if(this.wordVectors.getQAWordVector(token)!=null) {
			    		double[] temp = this.wordVectors.getQAWordVector(token).getVector();
			    		fileWritter.write(token+" : ");
			    		for(double a:temp) {
			    			fileWritter.write(a+",");
			    		}
			    		fileWritter.write("\r\n");
			    		MatrixCache.add(temp);
			    	}else {
			    		double[] temp = {2.222,3.333};
			    		MatrixCache.add(temp);
			    		fileWritter.write(token+" : ");
			    		for(double a:temp) {
			    			fileWritter.write(a+",");
			    		}
			    		fileWritter.write("\r\n");
			    	}
			    	
			    }
			    fileWritter.close();
			    this.MatrixClassifyCache.get(label).put(sentence, MatrixCache);
			    
			}
			this.showM();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showM() {
		File f = new File("info.txt");
		try {
			FileWriter fileWritter = new FileWriter(f.getName(),true);
			for (Entry<Double, Map<String, List<double[]>>> entry : this.MatrixClassifyCache.entrySet()) {
				Map<String, List<double[]>> map = entry.getValue();
				
				for (String word : map.keySet()) {
					List<double[]> M = map.get(word);
					fileWritter.write(word+" : "+"\r\n");
					//System.out.print(word+" : ");
					for(double[] wordvec : M) {
						for(double a:wordvec) {
							//System.out.print(a);
							fileWritter.write(String.valueOf(a));
						}
						//System.out.println();
						fileWritter.write("\r\n");
					}
				}
				
			}
			fileWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
