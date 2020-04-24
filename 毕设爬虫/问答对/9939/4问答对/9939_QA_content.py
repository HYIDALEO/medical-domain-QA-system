# -*- coding: utf-8 -*-
"""
Created on Fri May  3 10:06:22 2019

@author: admin
"""

from bs4 import BeautifulSoup
import requests 

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
    

    fr = open("9939_QA_main_dateset_all_page_urls.txt","r+",encoding='utf-8')
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
    for url in url_list:
        r=session.get(url)
        bsObj = BeautifulSoup(r.content,"lxml")
        div = bsObj.body.findAll("div",{"class":"descip paint1"})
        
        ask_div = bsObj.body.findAll("div",{"class":"user_ask"})
        if ask_div:
            ask_div = ask_div[0]
            if div:
                ask_div_p = ask_div.findAll("p")[2]
                ask_div_p_text = "问|||||" + ask_div_p.text
                div = div[0]
                div_p = div.findAll("p")[1]
                div_p_text = "答|||||"+div_p.text
                fw = open("9939_QA_main_dateset.txt","a+",encoding='utf-8')
                fw.write(ask_div_p_text)
                fw.write("\n")
                fw.write(div_p_text)
                fw.write("\n")
                fw.close()        
        count+=1
        fw = open("count.txt","w+",encoding='utf-8')
        fw.write(str(count))
        fw.close()        
    
    
main()