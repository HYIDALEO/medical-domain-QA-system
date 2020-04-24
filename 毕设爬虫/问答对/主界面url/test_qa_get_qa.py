# -*- coding: utf-8 -*-
"""
Created on Tue Apr 16 16:45:49 2019

@author: admin
"""

import requests 
from bs4 import BeautifulSoup 
import time
import random

def getTheFormal(qa_div_text):
    qa = ""
    for ele in qa_div_text:
        if '\u4e00' <= ele <= '\u9fff' or ele.isdigit() or ele.isalpha():
            qa = qa + ele
        elif ele in "？》《。，：；“”‘’！%"+"?><.,;:!":
            qa = qa + ele
        result = ""
        for index in range(0,len(qa)):
            if index == 1:
                result = result + "|||||"
                result = result + qa[index]
            else:
                result = result + qa[index]
    return result

def main():
    session = requests.Session()
    session.headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
    }
    
    fr = open("count.txt","r+",encoding='utf-8')
    line = fr.readline()
    fr.close()
    print(line)
    count = int(line)
    
    
    
    fr = open("QA_dateset_urls.txt","r+",encoding='UTF-8')
    line = fr.readline()
    url_list = []
    list_count=0
    while(line):
        mod = len(line)
        if(list_count>=count):
            url_list.append(line[:mod-1])
        line = fr.readline()
        list_count+=1
    fr.close()
    print(list_count)
    
    
    for url in url_list:
        print(url)
        r=session.get(url)
        bsObj = BeautifulSoup(r.content,"lxml")
        qa_main_div = bsObj.body.findAll("div",{"class":"hot-qa main-block"})
        while(len(qa_main_div)<=0):
            r=session.get(url)
            bsObj = BeautifulSoup(r.content,"lxml")
            qa_main_div = bsObj.body.findAll("div",{"class":"hot-qa main-block"})
        qa_div_list = qa_main_div[0].findAll("div",{"class":"hot-qa-item"})
        for div in qa_div_list:
            qa_div_ask = div.findAll("div",{"class":"qa-item-ask"})[0]
      #      qa_div_answer = div.findAll("div",{"class":"qa-item-answer"})[0]
            qa_div_ask_a = qa_div_ask.findAll("a")[0]['href']
            print(qa_div_ask_a)
            res = "https://www.chunyuyisheng.com"+qa_div_ask_a
   #         qa_div_ask_text = qa_div_ask.text
    #        qa_div_answer_text = qa_div_answer.text
     #       result_ask = getTheFormal(qa_div_ask_text)
      #      result_answer = getTheFormal(qa_div_answer_text)
            fw = open("QA_dateset_main_page_urls.txt","a+",encoding='utf-8')
            fw.write(res)
            fw.write("\n")
            fw.close()
        count+=1
        fw = open("count.txt","w+",encoding='utf-8')
        fw.write(str(count))
        fw.close()
    
main()
#fw = open("count.txt","w+",encoding='utf-8')
#fw.write(str(0))
#fw.close()

