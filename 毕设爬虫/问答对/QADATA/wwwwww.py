# -*- coding: utf-8 -*-
"""
Created on Fri May  3 18:38:41 2019

@author: admin
"""

from bs4 import BeautifulSoup
import requests 
import re
import operator

answers = []
questions = []
questions_final = []
answers_final = []
drugs = []
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
    
    
def clean_text(result):
    result = myReplace(result,"谢谢","")
    result = myReplace(result,"你好","")
    result = myReplace(result,"您好","")
    result = myReplace(result,"没关系","")
    result = myReplace(result,"不客气","")
    result = myReplace(result,"嗯","")
    result = myReplace(result,"恩","")
    result = myReplace(result,"ok","")
    result = myReplace(result,"OK","")
    result = myReplace(result,"Ok","")
    result = myReplace(result,"嗯嗯","")
    result = myReplace(result,"恩恩","")
    result = myReplace(result,"感谢","")
    
    return result
def myReplace(oldStr,str1,str2):
    
    length = len(str1)
    result = oldStr
    if str1 in oldStr:
        index = oldStr.index(str1)
        leftStr = oldStr[:index]
        midStr = str2
        rightStr = oldStr[index+length:len(oldStr)]
        result = leftStr+ midStr+rightStr
    return result 
def multiOperation4(multio,index,deleted):
    multio = myReplace(multio,"答|||||","")
    e_part = re.findall(r"[\w']+", multio)

    result = "答|||||"
    for ele in e_part:
        if "吗" not in ele :
            if "多久了" not in ele:
                result += ele
                result += "。"
    return result


def multiOperation5(multio,index,deleted):
    multio = myReplace(multio,"答|||||","")
    e_part = re.findall(r"[\w']+", multio)
    result = ""
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
                
        for symptom in symptoms:
            if symptom in ele:
                ele = myReplace(ele,symptom,"symptom")
                entity[ind].append(symptom)
                count += 1

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
    result_part = re.findall(r"[\w']+",result)

    result = "答|||||"
    for ele in result_part:
        result += ele
        result += "。"
    return result
def main1():
    
    fr = open("QADATA.txt","r+",encoding='utf-8')
    line = fr.readline()
    ask = ""
    answer = ""
    while(line):
        
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
    fw = open("QADATA_1.txt","w+",encoding='utf-8')
    for index in range(0,len(questions)):
        fw.write(questions[index])
        fw.write(answers[index])
    
    
def main2():
    
    fr = open("QADATA_1.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        
        questions.append(line[:len(line)-1])
        line = fr.readline()
        answers.append(line[:len(line)-1])
        line = fr.readline()
    
    
    
    
    
    print(len(answers))
    for question in questions:
        stro = question
        if stro[len(stro)-2:len(stro)] == "优质":
            stro = stro[:len(stro)-2]
        questions_final.append(stro)
    
    
    
    
    
    fw = open("QADATA_2.txt","w+",encoding='utf-8')
    for index in range(0,len(questions)):
        fw.write(questions_final[index])
        fw.write("\n")
        fw.write(answers[index])
        fw.write("\n")
    
def main3():
    fr = open("QADATA_2.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        
        questions.append(line[:len(line)-1])
        line = fr.readline()
        answers.append(line[:len(line)-1])    
        line = fr.readline()
        

    for index in range(0,len(answers)):
        answer = answers[index]
        answer = clean_text(answer)
        answers_final.append(answer)
    fw = open("QADATA_3.txt","w+",encoding='utf-8')
    for index in range(0,len(questions)):
        fw.write(questions[index])
        fw.write("\n")
        fw.write(answers_final[index])
        fw.write("\n")
        
def main4():
    fr = open("QADATA_3.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        questions.append(line[:len(line)-1])
        line = fr.readline()
        answers.append(line[:len(line)-1])    
        line = fr.readline()

    deleted = 0
    for index in range(0,len(answers)):
        answer = answers[index]
        multio = multiOperation4(answer,index,deleted)
        answers_final.append(multio)
    fw = open("QADATA_4.txt","w+",encoding='utf-8')
    for index in range(0,len(questions)):
        fw.write(questions[index])
        fw.write("\n")
        fw.write(answers_final[index])
        fw.write("\n")
    fw.close()
    
    
def main5():
    fr = open("QADATA_4.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        questions.append(line[:len(line)-1])
        line = fr.readline()
        answers.append(line[:len(line)-1])  
        line = fr.readline()
    print(len(questions))

    deleted = 0
    for index in range(0,len(answers)):
        answer = answers[index]
        multio = multiOperation5(answer,index,deleted)
        if multio == "答|||||":
            questions.remove(questions[index-deleted])
            deleted+=1
        else:
            answers_final.append(multio)
    print(len(answers_final))
    fw = open("QADATA_5.txt","w+",encoding='utf-8')
    for index in range(0,len(questions)):
        fw.write(questions[index])
        fw.write("\n")
        fw.write(answers_final[index])
        fw.write("\n")
    fw.close()
main5()