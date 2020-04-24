# -*- coding: utf-8 -*-
"""
Created on Tue Mar  5 23:42:30 2019


@author: admin
"""

from urllib.request import urlopen
import urllib
import requests 
import bs4
from bs4 import BeautifulSoup
import re
import time
import random
headers={
			'Host':'ptlogin2.qq.com',
			'User-Agent': "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0 ",
 }
user_agents = [
                    'Mozilla/5.0 (Windows; U; Windows NT 5.1; it; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11',
                    'Opera/9.25 (Windows NT 5.1; U; en)',
                    'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)',
                    'Mozilla/5.0 (compatible; Konqueror/3.5; Linux) KHTML/3.5.5 (like Gecko) (Kubuntu)',
                    'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.12) Gecko/20070731 Ubuntu/dapper-security Firefox/1.5.0.12',
                    'Lynx/2.8.5rel.1 libwww-FM/2.14 SSL-MM/1.4.1 GNUTLS/1.2.9',
                    "Mozilla/5.0 (X11; Linux i686) AppleWebKit/535.7 (KHTML, like Gecko) Ubuntu/11.04 Chromium/16.0.912.77 Chrome/16.0.912.77 Safari/535.7",
                    "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0 ",
 
                    ]
def dealThePage(url,session):
    url_model = "https://www.haodf.com/jibing"
    r2=session.get(url)
    bsObj = BeautifulSoup(r2.content,"lxml")
    title_inner_inner = bsObj.body.findAll("div",{"class":"m_ctt_green"})
    judge_inner_inner = bsObj.body.findAll("div",{"class":"m_title_green"})
            
    disease_list = []
    introduction_list = []
    disease_url_list = []
    
    count=0;
    for ele_inner_inner in title_inner_inner:
        print(judge_inner_inner[count].text+"---------------------------------")
        if '检查及手术' in judge_inner_inner[count].text:
            count+=1
            continue
        else:
            print(judge_inner_inner[count].text+"---------------------------------")
        
        count+=1
        mark_ul=ele_inner_inner.findAll("ul")
        #ul
        for mark_ul_ele in mark_ul:
            
            mark_li = mark_ul_ele.findAll("li")
            #li
            #    print(mark_li)
            
            for content_li in mark_li:
                if content_li.has_attr("class"):
                    if content_li["class"]==["lineli"]:
                        continue
                    
                    

                title_set.add(content_li.a.text)
                    # f.writelines(content_li.a.text)
                print(content_li.a.text)
                    # time.sleep(3+random.randint(0,3))
                mod = len(content_li.a['href'])
                disease_url_list.append(url_model+content_li.a['href'][7:mod])
                r2=session.get(url_model+content_li.a['href'][7:mod-4]+"/jieshao.htm")
                bsObj = BeautifulSoup(r2.content,"lxml")
                
                introduction_con = bsObj.body.findAll("div",{"class":"dis_description"})[0].p.text
                mod = len(introduction_con)
           #     introduction_con=introduction_con[9:mod-13]
                introduction_con=introduction_con[0:mod]
                fin = open("D://introduction/"+content_li.a.text+".txt","w+",encoding='utf-8')
                fin.write(introduction_con)
                fin.close()
                    # break  #循环疾病单词
            # break  #循环疾病的ul
        # break    #div遍历
    return ""
def getHTMLText(url): 
    try: 
        r = requests.get(url, timeout=30) 
        r.raise_for_status() 
        r.encoding = r.apparent_encoding 
        return r.text 
    except: 
        return " " 
def main(): 
    depth = 10 
    session = requests.Session()
    session.headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
    }
    
    url_model = "https://www.haodf.com/jibing"
    start_url = 'https://www.haodf.com/jibing/xiaoerke/list.htm'
#    r=session.get(start_url)
#    bsObj = BeautifulSoup(r.content,"lxml")
#    title_inner = bsObj.body.findAll("div",{"class":"ksbd"})
#    title_inner = title_inner[0].ul.findAll("li")
    
#    url_list = []
    fr = open("url.txt","r+")
    line = fr.readline()
    url_list=line.split(" ")
    fr.close()
    deal_list = []
    for ele in url_list:
        if ele == '':
            continue
        else:
            deal_list.append(ele)

#    fr.close()
    mod=deal_list.index("https://www.haodf.com/jibing/xiaoeryanke/list.htm")
    print(mod)
    deal_list=deal_list[mod+1:]
 
    for ele in deal_list:
        time.sleep(2)
        dealThePage(ele,session)
 #   dealThePage("https://www.haodf.com/jibing/xiaoeryanke/list.htm",session)
#    f.close()
#    f= open("dis.txt","w+")
#    for ele in title_set:
#        f.write(ele)
#        f.write("\n")
                    
#    f.close()
        

#    print(url_list)

    
    
title_set = set()                   
main()
