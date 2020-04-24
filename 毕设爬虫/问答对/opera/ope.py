# -*- coding: utf-8 -*-
"""
Created on Sat May 11 18:35:40 2019

@author: admin
"""

from gensim.models.word2vec import Word2Vec
import jieba
import re
def myReadQAFile():
    questions = []
    answers = []
    fr = open("QADATA_4.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        questions.append(line[0:len(line)-1])
        line = fr.readline()
        answers.append(line[0:len(line)-1])  
        line = fr.readline()
    fr.close()
    return questions,answers

def myReadallQAFile():
    questions = []
    answers = []
    fr = open("QADATA_5_5_5_5.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        questions.append(line[6:len(line)-1])
        line = fr.readline()
        answers.append(line[6:len(line)-1])  
        line = fr.readline()
    fr.close()
    return questions,answers

def myReplace(oldStr,str1,str2):
    
    length = len(str1)
    result = oldStr
    while(str1 in result):
        index = result.index(str1)
        leftStr = result[:index]
        midStr = str2
        rightStr = result[index+length:len(result)]
        result = leftStr+ midStr+rightStr
    return result 

def myReplaceList(input_list,str1,str2):
    result = []
    for ele in input_list:
        result.append(myReplace(ele,str1,str2))
    return result

def myReadQAFileQAD():
    questions = []
    answers = []
    fr = open("QA_dateset_1.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        questions.append(line[0:len(line)-1])
        line = fr.readline()
        answers.append(line[0:len(line)-1])  
        line = fr.readline()
    fr.close()
    return questions,answers

def operateQ(questions):
    questions = myReplaceList(questions,"医生。","")
    questions = myReplaceList(questions,"医生，","")
    questions = myReplaceList(questions,"医生","")
    
    questions = myReplaceList(questions,"您好，","")
    questions = myReplaceList(questions,"您好。","")
    questions = myReplaceList(questions,"您好！","")
    questions = myReplaceList(questions,"您好","")
    
    questions = myReplaceList(questions,"你好。","")
    questions = myReplaceList(questions,"你好，","")
    questions = myReplaceList(questions,"你好！","")
    questions = myReplaceList(questions,"你好","")
    
    questions = myReplaceList(questions,"不谢，","")
    questions = myReplaceList(questions,"不谢。","")
    questions = myReplaceList(questions,"不谢！","")
    questions = myReplaceList(questions,"不谢","")
    
    questions = myReplaceList(questions,"不客气！","")
    questions = myReplaceList(questions,"不客气，","")
    questions = myReplaceList(questions,"不客气。","")
    questions = myReplaceList(questions,"不客气","")
    
    questions = myReplaceList(questions,"没关系！","")
    questions = myReplaceList(questions,"没关系。","")
    questions = myReplaceList(questions,"没关系，","")
    questions = myReplaceList(questions,"没关系","")
    
    questions = myReplaceList(questions,"应该的。","")
    questions = myReplaceList(questions,"应该的，","")
    questions = myReplaceList(questions,"应该的！","")
    questions = myReplaceList(questions,"应该的","")
    
    questions = myReplaceList(questions,"，，","")
    questions = myReplaceList(questions,"。。","")
    questions = myReplaceList(questions,"..","")
    questions = myReplaceList(questions,"？？","")
    questions = myReplaceList(questions,"！！","")
    
    questions = myReplaceList(questions,"大夫。","")
    questions = myReplaceList(questions,"大夫，","")
    questions = myReplaceList(questions,"大夫","")
    
    questions = myReplaceList(questions,"优质","")
    
    questions = myReplaceList(questions,"在吗？","")
    questions = myReplaceList(questions,"在吗。","")
    questions = myReplaceList(questions,"在吗，","")
    questions = myReplaceList(questions,"在吗","")
    
    questions = myReplaceList(questions,"您在吗？","")
    questions = myReplaceList(questions,"您在吗，","")
    questions = myReplaceList(questions,"您在吗。","")
    questions = myReplaceList(questions,"您在吗","")
    
    questions = myReplaceList(questions,"谢谢！","")
    questions = myReplaceList(questions,"谢谢。","")
    questions = myReplaceList(questions,"谢谢，","")
    questions = myReplaceList(questions,"谢谢","")
    return questions

def deleteSentbyq(answers):
    '''
    length = len(str1)
    result = oldStr
    while(str1 in result):
        index = result.index(str1)
        leftStr = result[:index]
        midStr = str2
        rightStr = result[index+length:len(result)]
        result = leftStr+ midStr+rightStr
    return result 
    '''
    front = 0
    back = 0
    index_o = 0
    while(index_o<len(answers)):
      #  mark = 0
        front = 0
        back = 0
        print(index_o)
        for index in range(0,len(answers[index_o])):
            
            if answers[index_o][index] == "。" or answers[index_o][index] == "？" or answers[index_o][index] == "！":
                front = back
                back = index
            if answers[index_o][index] == "？":
               # mark=1
                print(index_o)
                answers[index_o] = answers[index_o][:front] + answers[index_o][back+1:len(answers[index_o])]
                index_o-=1
                break
        
        index_o+=1
        
        
    front = 0
    back = 0    
    index_o = 0
    while(index_o<len(answers)):
      #  mark = 0
        front = 0
        back = 0
        print(index_o)
        for index in range(0,len(answers[index_o])):
            
            if answers[index_o][index] == "。" or answers[index_o][index] == "？" or answers[index_o][index] == "！":
                front = back
                back = index
            if answers[index_o][index] == "吗":
               # mark=1
                print(index_o)
                answers[index_o] = answers[index_o][:front] + answers[index_o][back+1:len(answers[index_o])]
                index_o-=1
                break
        
        index_o+=1
    
    return answers

def operateA(answers):
    answers = myReplaceList(answers,"您好，","")
    answers = myReplaceList(answers,"您好。","")
    answers = myReplaceList(answers,"您好！","")
    answers = myReplaceList(answers,"您好","")
    
    answers = myReplaceList(answers,"你好。","")
    answers = myReplaceList(answers,"你好，","")
    answers = myReplaceList(answers,"你好！","")
    answers = myReplaceList(answers,"你好","")
    
    answers = myReplaceList(answers,"不谢，","")
    answers = myReplaceList(answers,"不谢。","")
    answers = myReplaceList(answers,"不谢！","")
    answers = myReplaceList(answers,"不谢","")
    
    answers = myReplaceList(answers,"不用谢，","")
    answers = myReplaceList(answers,"不用谢。","")
    answers = myReplaceList(answers,"不用谢！","")
    answers = myReplaceList(answers,"不用谢","")
    
    answers = myReplaceList(answers,"别着急，","")
    answers = myReplaceList(answers,"别着急。","")
    answers = myReplaceList(answers,"别着急！","")
    answers = myReplaceList(answers,"别着急","")
    
    answers = myReplaceList(answers,"不客气！","")
    answers = myReplaceList(answers,"不客气，","")
    answers = myReplaceList(answers,"不客气。","")
    answers = myReplaceList(answers,"不客气","")
    
    answers = myReplaceList(answers,"没关系！","")
    answers = myReplaceList(answers,"没关系。","")
    answers = myReplaceList(answers,"没关系，","")
    answers = myReplaceList(answers,"没关系","")
    
    answers = myReplaceList(answers,"应该的。","")
    answers = myReplaceList(answers,"应该的，","")
    answers = myReplaceList(answers,"应该的！","")
    answers = myReplaceList(answers,"应该的","")
    
    answers = myReplaceList(answers,"祝健康","")
    answers = myReplaceList(answers,"祝您安康愉快，","")
    answers = myReplaceList(answers,"应该的！","")
    answers = myReplaceList(answers,"应该的","")
    
    answers = deleteSentbyq(answers)
    return answers

def readDrDiSy():
    drugs =[]
    diseases = []
    symptoms = []
    fr = open("drugs.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        drugs.append(line[:len(line)-1])
        line = fr.readline()
    fr.close()
    diseases = []
    fr = open("diseases.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        diseases.append(line[:len(line)-1])
        line = fr.readline()
    symptoms = []
    fr = open("entities.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        symptoms.append(line[:len(line)-1])
        line = fr.readline()
    return drugs,diseases,symptoms

def main1():
    answers = []
    questions = []
    fr = open("QA_dateset.txt","r+",encoding='utf-8')
    line = fr.readline()
    line = fr.readline()
    line = fr.readline()
    ask = ""
    answer = ""
    while(line):
        print(line)
        if line[0:6] == "问|||||":
            print(line)
            if answer != "":
                answers.append(answer)
            answer = ""
            ask = line
            line = fr.readline()
            while(line):
                if line[0:6] == "答|||||":
                    break
                else:
                    ask = ask[:len(ask)-1]+line
                    line = fr.readline()
        if line[0:6] == "答|||||":
            questions.append(ask)
            ask = ""
            answer = line
            line = fr.readline()
            while(line):
                if line[0:6] == "问|||||":
                    break
                else:
                    answer = answer[:len(answer)-1]+line
                    line = fr.readline()
    
    answers.append(answer)
    answer = ""
    print(len(answers))
    fw = open("QA_dateset_1.txt","w+",encoding='utf-8')
    for index in range(0,len(questions)):
        fw.write(questions[index])
        fw.write(answers[index])
def saveQA(num,questions,answers):
    fw = open("QA_dateset_"+str(num)+".txt","w+",encoding='utf-8')
    for index in range(0,len(questions)):
        fw.write(questions[index])
        fw.write("\n")
        fw.write(answers[index])
        fw.write("\n")
    return questions,answers 

def trainQAWord2Vec(answers_jieba):
#    model.build_vocab(answers_jieba)
#    model.train(answers_jieba,total_examples = model.corpus_count,epochs = model.iter)
    model=Word2Vec(answers_jieba, size=256, window=5, min_count=1, workers=4)
    model.save(u'allQAvecs_5_5_5_5.model')
    
def trainAWord2Vec(answers_jieba):
#    model.build_vocab(answers_jieba)
#    model.train(answers_jieba,total_examples = model.corpus_count,epochs = model.iter)
    model=Word2Vec(answers_jieba, size=512, window=5, min_count=1, workers=4)
    model.save(u'allAvecs_3.model')

def multiOperation5(multio,index,drugs,diseases,symptoms,questions,answers):
    multio = myReplace(multio,"答|||||","")
    e_part = re.findall(r"[\w']+", multio)
    result = "答|||||"
    count = 0
    entity = []
    ind = 0
    for ele in e_part:
        temp = []
        entity.append(temp)
        for drug in drugs:
            if drug in ele:
                ele = myReplace(ele,drug,"drug")
                entity[ind].append(drug)
                count += 1
                
        for disease in diseases:
            if disease in ele:
                ele = myReplace(ele,disease,"disease")
                entity[ind].append(disease)
                count += 1
        result += ele
        result += "。"
        
        '''
        if count != 0:
            if count == 1:
                if entity[ind][0] in questions[index-deleted]:
                    entity.pop(ind)
                    count =0
                    continue
            ind += 1
            result += ele
            result += "。"
            count = 0
        else:
            entity.remove([])
        '''
 #   result_part = re.findall(r"[\w']+",result)

    

    return result
def main():
  #  questions,answers = myReadQAFile()
  #  questionsQA,answersQA = myReadQAFileQAD()
  #  questions = questions + questionsQA
  #  answers = answers + answersQA
    drugs,diseases,symptoms = readDrDiSy()
    answers = []
    answers.append("多长时间了？有没有弹响？有没有肿胀？拍过片子吗？干什么工作？肿胀有吗？怀疑滑膜炎半月板退变现在的年龄算不上肩周炎关节炎最好拍个片子看看是的，就是这样造成的休息一下热敷一下，保暖加三七，伸筋草，首乌，血竭熬水洗对，这就对了没事应该查一下看看到什么程度了还有什么别的疑问？对，就是这样注意保暖滑膜炎在作怪注意保暖休息一下热敷一下，保暖加三七，伸筋草，首乌，血竭熬水洗口服滑膜炎颗粒都有的谢谢，祝您早日康复")
    answers.append("扶他林药膏用来缓解关节肌肉疼痛的，药店都有20块一支。神灯治疗仪淘宝上有，100块左右的，销量最多那种台灯式的就行。有助于加快血液循环，促进关节形成，减轻疼痛。口服维d2磷普钙片，氨基葡萄糖钙片。预防关节炎")
    answers.append("可以吃软的，然后消炎治疗可以口服塞来昔布在加消炎的药物针对本次问诊，医生更新了总结建议：祝您安康愉快")
    answers.append("下颌关节炎可以局部热敷，按摩，避免长大口，吃硬物否则有脱位的可能恩，可以局部热敷，外用扶他林软膏涂抹局部，多吃软食物是，下颌关节炎的表现，不代表那种全身的关节炎不需要，多小心别张口过大扶他林局部外用，热敷会缓解我也不知道祝健康")
  #  questions = operateQ(questions)
    answers = operateA(answers)
    print(answers)
    
def main5():
    drugs,diseases,symptoms = readDrDiSy()
    questions,answers = myReadQAFile()
    questionsQA,answersQA = myReadQAFileQAD()
    questions = questions + questionsQA
    answers = answers + answersQA
    

    
    answers = operateA(answers)
    answers_final= []
    deleted = 0
    for index in range(0,len(answers)):
        answer = answers[index]
        print(index)
        multio = multiOperation5(answer,index,deleted,drugs,diseases,symptoms,questions,answers)
        if multio == "答|||||":
            questions.remove(questions[index-deleted])
            deleted+=1
        else:
            answers_final.append(multio)
    print(len(answers_final))
    fw = open("allQADATA_5.txt","w+",encoding='utf-8')
    for index in range(0,len(questions)):
        fw.write(questions[index])
        fw.write("\n")
        fw.write(answers_final[index])
        fw.write("\n")
    fw.close()
def operation(total_jieba):
    total_train =[]
    for index in range(0,len(total_jieba)):
        tmp = total_jieba[index].split(' ')
        total_train.append(tmp)
    return total_train

def main5_5():
    questions,answers = myReadallQAFile()
    drugs,diseases,symptoms = readDrDiSy()
    answers_final= []
    for index in range(0,len(answers)):
        answer = answers[index]
        multio = multiOperation5(answer,index,drugs,diseases,symptoms,questions,answers)

        answers_final.append(multio)
    print(len(answers_final))
    fw = open("allQADATA_5_5.txt","w+",encoding='utf-8')
    for index in range(0,len(questions)):
        fw.write(questions[index])
        fw.write("\n")
        fw.write(answers_final[index])
        fw.write("\n")
    fw.close()
def jiebamain():
  #  drugs,diseases,symptoms = readDrDiSy()
    questions,answers = myReadallQAFile()
    answers_jieba = []
    questions_jieba = []
    codes = ['<PAD>','<EOS>','<UNK>','<GO>']
    for index in range(0,len(answers)):
        answers_jieba.append(" ".join(jieba.cut(answers[index])))
        questions_jieba.append(" ".join(jieba.cut(questions[index])))
    questions_train = operation(questions_jieba)
    answers_train = operation(answers_jieba)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train.append(codes)
    questions_train = questions_train + answers_train
    trainQAWord2Vec(questions_train)
 #   trainAWord2Vec(answers_train)
def vecArray2float32(vecArray):
    floatList = []
    for num in vecArray:
        floatList.append(num)
    return floatList

def getIdsWtvWtiItv(word2vecs):
    keys_list = []
    keys_list = word2vecs.wv.vocab.keys()
    ids = []
    
    for key in keys_list:
        ids.append(key)
        
    word_to_vecs = {}
    word_to_ids = {}
    ids_to_vecs = []
    for index in range(0,len(ids)):
        word_to_vecs[ids[index]] = vecArray2float32(word2vecs[ids[index]])
        word_to_ids[ids[index]] = index
        ids_to_vecs.append(vecArray2float32(word2vecs[ids[index]]))
        
        
    
    return ids,word_to_vecs,word_to_ids,ids_to_vecs

def main34():
    Qword2vecs = Word2Vec.load(u'allQvecs_3.model')
    Aword2vecs = Word2Vec.load(u'allAvecs_3.model')
    Q_ids,Q_word_to_vecs,Q_word_to_ids,Q_ids_to_vecs = getIdsWtvWtiItv(Qword2vecs)
    A_ids,A_word_to_vecs,A_word_to_ids,A_ids_to_vecs = getIdsWtvWtiItv(Aword2vecs)
    
    
    print(len(Q_word_to_ids))
    print(len(A_word_to_ids))
    print(len(Q_ids))
 #   for ele in Q_word_to_ids:
 #       if ele > 30000:
 #           print(ele)
 #   print(Q_ids[78905])
    print(Qword2vecs["<UNK>"])
#jiebamain()
#main5_5()
    
jiebamain()