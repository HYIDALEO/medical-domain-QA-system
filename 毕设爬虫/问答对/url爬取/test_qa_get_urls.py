# -*- coding: utf-8 -*-
"""
Created on Tue Apr 16 15:35:31 2019

@author: admin
"""
import requests 
from bs4 import BeautifulSoup 
import time

def main():
    session = requests.Session()
    session.headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
    }
    start_url="https://www.chunyuyisheng.com/pc/qalist/?high_quality=0"
    r=session.get(start_url)
    bsObj = BeautifulSoup(r.content,"lxml")
    
    #爬取一级目录
    first_clinic_div = bsObj.body.findAll("div",{"class":"first-clinic"})
    first_clinic_ul = first_clinic_div[0].findAll("ul")
    first_clinic_li = first_clinic_ul[0].findAll("li")
    first_url_list = []
    for ele in first_clinic_li:
        first_clinic_a = ele.findAll("a")[0]
        first_clinic_url = "https://www.chunyuyisheng.com"+first_clinic_a['href']
        first_url_list.append(first_clinic_url)
        
    #爬取二级目录
    second_url_list = []
    for url in first_url_list:
        time.sleep(1)
        r=session.get(url)
        bsObj = BeautifulSoup(r.content,"lxml")
        second_clinic_ul = bsObj.body.findAll("ul")[3]
        second_clinic_li = second_clinic_ul.findAll("li")
        for ele in second_clinic_li:
            second_clinic_a = ele.findAll("a")[0]
            second_clinic_url = "https://www.chunyuyisheng.com"+second_clinic_a["href"]
            second_url_list.append(second_clinic_url)
            
    #爬取疾病问题链接
    third_url_set = set()
    for url in second_url_list:
        time.sleep(1)
        r=session.get(url)
        bsObj = BeautifulSoup(r.content,"lxml")
        tmp_third_clinic_ul = bsObj.body.findAll("ul")
        if len(tmp_third_clinic_ul) >4:
            third_clinic_ul = bsObj.body.findAll("ul")[4]
            third_clinic_li = third_clinic_ul.findAll("li")
            for ele in third_clinic_li:
                third_clinic_a = ele.findAll("a")[0]
                third_clinic_url = "https://www.chunyuyisheng.com"+third_clinic_a["href"]
                third_url_set.add(third_clinic_url)
 #       else:
 #           third_url_list.append(url)
    
    fr = open("QA_operation_urls.txt","w+")
    for url in third_url_set:
        fr.write(url)
        fr.write('\n')
    fr.close()
main()