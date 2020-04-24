# -*- coding: utf-8 -*-
"""
Created on Tue Apr 16 23:00:18 2019

@author: admin
"""
import requests 
from bs4 import BeautifulSoup 
import time
import random
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
    
    fr = open("QA_operation_urls.txt","r+")
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
        mod = len(url)
        print(url)
        left_url=url[:mod-16]
        right_url="&high_quality=0"
        for index in range(1,31):
            fw = open("QA_dateset_urls.txt","a+",encoding='utf-8')
            fw.write(left_url+str(index)+right_url)
            fw.write("\n")
            fw.close()
            
            r=session.get(left_url+str(index)+right_url)
     
            bsObj = BeautifulSoup(r.content,"lxml")
#            pagebar = bsObj.body.findAll("div",{"class":"ui-grid ui-main clearfix"})[0]
#            pagebar = pagebar.findAll("div",{"class":"main-wrap"})[0]
#            pagebar = pagebar.findAll("div",{"class":"pagebar"})[0]
            pagebar_next = bsObj.body.findAll("a",{"class":"next"})
            while(len(pagebar_next)<=0):
                r=session.get(left_url+str(index)+right_url)
                bsObj = BeautifulSoup(r.content,"lxml")
                pagebar_next = bsObj.body.findAll("a",{"class":"next"})
            if pagebar_next[0]['href'] == "javascript:void(0)":
                break
            print(left_url+str(index)+right_url)
        count+=1
        fw = open("count.txt","w+",encoding='utf-8')
        fw.write(str(count))
        fw.close()
        

main()
#fw = open("count.txt","w+",encoding='utf-8')
#fw.write(str(450))
#fw.close()

#fr = open("QA_dateset_urls.txt","r+",encoding='UTF-8')
#line = fr.readline()
#url_list = set()
#while(line):
#    mod = len(line)
#    url_list.add(line[:mod])
#    line = fr.readline()
#fr.close()
#fw = open("QA_dateset_urls.txt","w+",encoding='UTF-8')
#for url in url_list:
#    fw.write(url)
#fw.close()