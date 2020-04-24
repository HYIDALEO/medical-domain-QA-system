# -*- coding: utf-8 -*-
"""
Created on Tue Apr  9 13:17:52 2019

@author: admin
"""

from bert_serving.client import BertClient
bc = BertClient()
print(bc.encode(['中国', '美国']))

