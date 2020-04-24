# -*- coding: utf-8 -*-
"""
Created on Thu May  2 19:08:58 2019

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
    
    fr = open("9939_QA_dateset_main_page.txt","r+",encoding='utf-8')
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
        print(url)
        bsObj = BeautifulSoup(r.content,"lxml")
        a = bsObj.body.findAll("a",{"class":"spot"})
        if a:
            a = a[1]
            a_text = a.text
            left_text = a_text.split("，")[0]
            page_num = ""
            for ele in left_text:
                if "0" in ele:
                    page_num+=ele
                elif "1" in ele:
                    page_num+=ele
                elif "2" in ele:
                    page_num+=ele
                elif "3" in ele:
                    page_num+=ele
                elif "4" in ele:
                    page_num+=ele
                elif "5" in ele:
                    page_num+=ele
                elif "6" in ele:
                    page_num+=ele
                elif "7" in ele:
                    page_num+=ele
                elif "8" in ele:
                    page_num+=ele
                elif "9" in ele:
                    page_num+=ele
            for page_index in range(1,int(page_num)+1):
                left_url = url[:len(url)-6]
                right_url = ".html"
                got_url = left_url + str(page_index) + right_url
                fw = open("9939_QA_dateset_all_page_urls.txt","a+",encoding='utf-8')
                fw.write(got_url)
                fw.write("\n")
                fw.close()
        count+=1
        fw = open("count.txt","w+",encoding='utf-8')
        fw.write(str(count))
        fw.close()
    
main()