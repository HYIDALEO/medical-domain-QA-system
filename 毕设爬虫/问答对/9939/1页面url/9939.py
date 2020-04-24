# -*- coding: utf-8 -*-
"""
Created on Thu May  2 18:55:25 2019

@author: admin
"""

from bs4 import BeautifulSoup
import requests 


def main():
    session = requests.Session()
    session.headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
    }
    for ind in range(1,52):
        url = "http://ask.9939.com/wenti/" + str(ind) + ".html"
        r=session.get(url)
        bsObj = BeautifulSoup(r.content,"lxml")
        qa_main_div = bsObj.body.findAll("div",{"class":"mapsdata"})[0]
        qa_main_div_lis = qa_main_div.findAll("li")
        for qa_main_div_li in qa_main_div_lis:
            
            qa_main_div_li_a = qa_main_div_li.findAll("a")[0]
            fw = open("9939_QA_dateset_main_page.txt","a+",encoding='utf-8')
            fw.write(qa_main_div_li_a['href'])
            fw.write("\n")
            fw.close()

main()