# -*- coding: utf-8 -*-
"""
Created on Thu May  2 23:45:27 2019

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
    
    fr = open("9939_QA_dateset_all_page_urls.txt","r+",encoding='utf-8')
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
        ul = bsObj.body.findAll("ul",{"class":"period"})
        if ul:
            ul = ul[0]
            ul_lis = ul.findAll("li")
            for ul_li in ul_lis:
                ul_li_div = ul_li.findAll("div",{"class":"speti fl"})[0]
                ul_li_div_h3 = ul_li_div.findAll("h3")[0]
                ul_li_div_h3_a = ul_li_div_h3.findAll("a")[0]
                got_url = ul_li_div_h3_a['href']
                fw = open("9939_QA_main_dateset_all_page_urls.txt","a+",encoding='utf-8')
                fw.write(got_url)
                fw.write("\n")
                fw.close()
        count+=1
        fw = open("count.txt","w+",encoding='utf-8')
        fw.write(str(count))
        fw.close()
    
main()
