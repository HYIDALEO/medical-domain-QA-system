# -*- coding: utf-8 -*-
"""
Created on Sat May 11 15:03:10 2019

@author: admin
"""

import numpy as np
import tensorflow as tf
import re
import time
from gensim.models.word2vec import Word2Vec
import jieba
from sys import argv


def checkList(theList,name):
    print("--------------------------the information of "+name+"--------------------")
    print("len:"+str(len(theList)))
    print("the top 10 elements:")
    for index in range(0,10):
        print(theList[index])
    
    print("-------------------------- end --------------------------")


def myReadQAFile():
    questions = []
    answers = []
    fr = open(r"C:\Users\admin\Desktop\eclipse\QADATA_5_5_5_5.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        questions.append(line[6:len(line)-1])
        line = fr.readline()
        answers.append(line[6:len(line)-1])  
        line = fr.readline()
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




def operation(total_jieba):
    total_train =[]
    for index in range(0,len(total_jieba)):
        tmp = total_jieba[index].split(' ')
        total_train.append(tmp)
    return total_train

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
def getLength(questions_jieba):
    min_q_length=200
    max_q_length=0
    for question_jieba in questions_jieba:
        if len(question_jieba) > max_q_length:
            max_q_length = len(question_jieba)
        elif len(question_jieba) < min_q_length:
            min_q_length = len(question_jieba)
    return min_q_length,max_q_length
def QAAddUNK(questions_jieba,answers_jieba,word2vecs):
    questions_UNK = []
    answers_UNK = []
    

    for question_jieba in questions_jieba:
        words = []
        for word in question_jieba:
            if word not in word2vecs.wv:
                words.append('<UNK>')
            else:
                words.append(word)
        questions_UNK.append(words)
        
    for answer_jieba in answers_jieba:
        words = []
        for word in answer_jieba:
       #     print(word)
            if word not in word2vecs.wv:
            #    print(word)
                words.append('<UNK>')
            else:
                words.append(word)
        answers_UNK.append(words)
    return questions_UNK,answers_UNK

def calculate_UNK_ratio(questions_UNK,answers_UNK):
    word_count = 0
    unk_count = 0
    
    for question_UNK in questions_UNK:
        for word in question_UNK:
            if word == '<UNK>':
                unk_count += 1
            word_count += 1
            
    for answer_UNK in answers_UNK:
        for word in answer_UNK:
            if word == '<UNK>':
                unk_count += 1
            word_count += 1
            
    unk_ratio = round(unk_count/word_count,4)*100
    print("Number of times <UNK>: {}%".format(round(unk_ratio,3)))

def sortQA(questions_UNK,answers_UNK,min_q_length,max_q_length):
    sorted_questions=[]
    sorted_answers = []
    
    for length in range(min_q_length,max_q_length+1):
        for index in range(0,len(questions_UNK)):
            if len(questions_UNK[index]) == length:
                sorted_questions.append(questions_UNK[index])
                sorted_answers.append(answers_UNK[index])
    
    return sorted_questions,sorted_answers
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
        word_to_vecs[ids[index]] = vecArray2float32(word2vecs.wv[ids[index]])
        word_to_ids[ids[index]] = index
        ids_to_vecs.append(vecArray2float32(word2vecs.wv[ids[index]]))
        
        
    
    return ids,word_to_vecs,word_to_ids,ids_to_vecs

def getQAint(sorted_questions,sorted_answers,word_to_ids):
    questions_int = []

    for question in sorted_questions:
        ints = []
        for word in question:
            if word not in word_to_ids:
                ints.append(word_to_ids['<UNK>'])
            else:
                ints.append(word_to_ids[word])
        questions_int.append(ints)
        
    answers_int = []
    
    for answer in sorted_answers:
        ints = []
        for word in answer:
            if word not in word_to_ids:
                ints.append(word_to_ids['<UNK>'])
            else:
                ints.append(word_to_ids[word])
        answers_int.append(ints)
    return questions_int,answers_int

def question_to_seq(question,word_to_ids):
    result = []
    for ele in question:
        result.append(word_to_ids[ele])
    
    
    return result

#填充
def pad_sentence_batch(sentence_batch,vocab_to_int):
#    print(sentence_batch)
#    max_sentence = len(sentence_batch)
#    print([sentence + [vocab_to_int['<PAD>']]*(max_sentence - len(sentence)) for sentence in sentence_batch])
    max_sentence = max([len(sentence) for sentence in sentence_batch])
    
    return [sentence + [vocab_to_int['<PAD>']]*(max_sentence - len(sentence)) for sentence in sentence_batch]

def stopWordOperation(input_question,stopwords):
    result = ""
    for stopword in stopwords:
        if stopword in input_question:
            result = myReplace(input_question,stopword,"")
    return result
    



def main():

    questions,answers = myReadQAFile()

    batch_size = 64
#    keep_probability = 0.75

    answers_jieba = []
    questions_jieba = []
    codes = ['<PAD>','<EOS>','<UNK>','<GO>']
    for index in range(0,len(answers)):
        answers_jieba.append(" ".join(jieba.cut(answers[index])))
        questions_jieba.append(" ".join(jieba.cut(questions[index])))

    

 #   answers_ope = operation(answers_jieba)
    questions_ope = operation(questions_jieba)


    word2vecs = Word2Vec.load(r'C:\Users\admin\Desktop\eclipse\allQAvecs_5_5_5_5.model')

  #  drugs,diseases,symptoms = readDrDiSy()
    
    
    #GO与<start>开始标记相同，是第一个提供给解码器的标记，与向量一起，为答案产生标记
    #EOS 意思是句尾，一旦解码器产生EOS标记，表示答案已经产生
    #UNK 未知标记
    #PAD 填充标记
    
    #2, 26
    
    min_q_length,max_q_length = getLength(questions_ope)

    ids,word_to_vecs,word_to_ids,ids_to_vecs = getIdsWtvWtiItv(word2vecs)
 #   tf.reset_default_graph()
    with tf.Session() as sess:

        saver = tf.train.Saver()
        #saver = tf.train.import_meta_graph(r"C:\Users\admin\Desktop\eclipse\my_QA_model.ckpt.meta")  
        
        

        saver.restore(sess,r"C:\Users\admin\Desktop\eclipse\my_QA_model.ckpt")
        graph = tf.get_default_graph()
        inference_logits = graph.get_tensor_by_name("logits:0")
        input_data = graph.get_tensor_by_name("input:0")
        keep_prob = graph.get_tensor_by_name("keep_prob:0")
    
    
    
    
    
      #  input_question=argv[1]
        input_question="drug治什么病"
        input_question = " ".join(jieba.cut(input_question))
        input_question = input_question.split(' ')
    
        input_question = question_to_seq(input_question,word_to_ids)
        
        
        input_question = input_question + [word_to_ids['<PAD>']]*(max_q_length - len(input_question))
        
        
    
        batch_shell = np.zeros((batch_size,max_q_length))
    
        batch_shell [0] = input_question
        
    
        answer_logits = sess.run(inference_logits,{input_data:batch_shell,keep_prob:1.0})[0]
    
        
    
    
        pad_q = word_to_ids["<PAD>"]

        

        result = ' '.join(([ids[i] for i in np.argmax(answer_logits,1) if i != pad_q]))

        print(result)








main()
def other():
    word2vecs = Word2Vec.load(r'C:\Users\admin\Desktop\eclipse\allQAvecs_5_5_5_5.model')
    keys_list = []
    keys_list = word2vecs.wv.vocab.keys()
    print(len(keys_list))

#other()


