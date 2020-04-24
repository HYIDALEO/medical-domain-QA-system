# -*- coding: utf-8 -*-
"""
Created on Tue Apr 30 23:10:43 2019

@author: admin
"""
import numpy as np
import tensorflow as tf
import re
import time


def clean_text(text):
    text = text.lower()
    text = re.sub(r"i'm","i am",text)
    text = re.sub(r"he's","he is",text)
    text = re.sub(r"she's","she is",text)
    text = re.sub(r"it's","it is",text)
    text = re.sub(r"that's","that is",text)
    text = re.sub(r"what's","what is",text)
    text = re.sub(r"where's","where is",text)
    text = re.sub(r"how's","how is",text)
    text = re.sub(r"\'ll"," will",text)
    text = re.sub(r"\'ve"," have",text)
    text = re.sub(r"\'re"," are",text)
    text = re.sub(r"\'d"," would",text)
    text = re.sub(r"won't","will not",text)
    text = re.sub(r"can't","cannot",text)
    text = re.sub(r"n't"," not",text)
    text = re.sub(r"n'","ng",text)
    text = re.sub(r"'bout","about",text)
    text = re.sub(r"'til","until",text)
    text = re.sub(r"[-()\"#/@;:<>{}`+=~|.!?,']","",text)
    return text



#填充
def pad_sentence_batch(sentence_batch,vocab_to_int):
#    print(sentence_batch)
#    max_sentence = len(sentence_batch)
#    print([sentence + [vocab_to_int['<PAD>']]*(max_sentence - len(sentence)) for sentence in sentence_batch])
    max_sentence = max([len(sentence) for sentence in sentence_batch])
    
    return [sentence + [vocab_to_int['<PAD>']]*(max_sentence - len(sentence)) for sentence in sentence_batch]





#
lines = open('vocabulary.txt',encoding='utf-8').read().split('\n')

#question

conv_lines = open('InsuranceQAquestionanslabelraw.encoded',encoding='utf-8').read().split('\n')

#answers

conv_lines1 = open('InsuranceQAlabel2answerraw.encoded',encoding='utf-8').read().split('\n')

#print(" -- Vocabulary -- ")

#print(lines[:10])

#print(" -- Questions -- ")

#print(conv_lines[:2])

#print(" -- Answers -- ")

#print(conv_lines1[:2])

id2line = {}

vocab_lines = lines

question_lines = conv_lines

answer_lines = conv_lines1


#id2line['idx_26638'] = 'ordered'
for line in vocab_lines:
    _line = line.split('\t')
    if len(_line) == 2:
        id2line[_line[0]] = _line[1]

convs, ansid = [], []
#convs[i] = 'idx_1285 idx_1010 idx_467 idx_47610 idx_18488 idx_65760'
#ansid[i] = '16696'

#a = ['3','2','w','d','c','cw']
#print(a[:-1])
#>['3', '2', 'w', 'd', 'c']

for line in question_lines[:-1]:
    _line = line.split('\t')
    ansid.append(_line[2].split(' '))
    convs.append(_line[1])
    
#convs1[i] = 'idx_1 idx_2 idx_3 idx_4 idx_5 idx_6 idx_7 idx_8 idx_9 idx_10 idx_11 idx_12 ...'
#
#
convs1 = [ ]
for line in answer_lines[:-1]:
    _line = line.split('\t')
    convs1.append(_line[1])

#a = ['3','2','w','d','c','cw']
#print(a[:2])
#['3', '2']

#print(convs[:2]) # word tokens present in the question

#print(ansid[:2]) # answers IDs mapped to the questions

#print(convs1[:2]) # word tokens present in the answers

#creating matching pair between questions and answers on the basis of the ID allocated to each

questions, answers = [], []

#print(len(ansid[1]))
for a in range(len(ansid)):
    for b in range(len(ansid[a])):
        questions.append(convs[a])
        
for a in range(len(ansid)):
    for b in range(len(ansid[a])):
        answers.append(convs1[int(ansid[a][b])-1])

#将问题与答案都换成英文
ques, ans =[],[]

m=0
while m<len(questions):
    i=0
    a=[]
    while i<(len(questions[m].split(' '))):
        a.append(id2line[questions[m].split(' ')[i]])
        i=i+1
    ques.append(' '.join(a))
    m=m+1
        
n=0
while n<len(answers):
    j=0
    b=[]
    while j<(len(answers[n].split(' '))):
        b.append(id2line[answers[n].split(' ')[j]])
        j=j+1
    ans.append(' '.join(b))
    n=n+1
    
#limit = 0
#for i in range(limit,limit+5):
#    print(ques[i])
#    print(ans[i])
#    print('----')
    
    
#print(len(questions))
#print(len(answers))
    
clean_questions = []
for question in ques:
    clean_questions.append(clean_text(question))
    
clean_answers = []
for answer in ans:
    clean_answers.append(clean_text(answer))
    
#limit = 0
#for i in range(limit,limit+5):
#    print(clean_questions[i])
#    print(clean_answers[i])
#    print('----')
    
#lengths.describe(percentiles=[0,0.25,0.5,0.75,0.85,0.9,0.95,0.99])

#抽取出字数在2-100的文本

min_line_length,max_line_length  = 2,100

short_questions_temp, short_answers_temp =[],[]

i=0

for question in clean_questions:
    if len(question.split()) >= min_line_length and len(question.split()) <= max_line_length:
        short_questions_temp.append(question)
        short_answers_temp.append(clean_answers[i])
    i += 1

short_questions, short_answers = [], []

i=0
for answer in short_answers_temp:
    if len(answer.split()) >= min_line_length and len(answer.split()) <= max_line_length:
        short_answers.append(answer)
        short_questions.append(short_questions_temp[i])
    i+=1



#计算词频
vocab = {}
for question in short_questions:
    for word in question.split():
        if word not in vocab:
            vocab[word] = 1
        else:
            vocab[word] += 1

for answer in short_answers:
    for word in answer.split():
        if word not in vocab:
            vocab[word] = 1
        else:
            vocab[word] += 1

threshold = 1
count = 0
for k,v in vocab.items():
    if v>= threshold:
        count += 1

#print("Size of total vocab:", len(vocab))

#print("Size of vocab we will use:",count)

questions_vocab_to_int = {}

word_num = 0

for word,count in vocab.items():
    if count >= threshold:
        questions_vocab_to_int[word] = word_num
        word_num += 1

answers_vocab_to_int = {}

word_num = 0

for  word,count in vocab.items():
    if count >= threshold:
        answers_vocab_to_int[word] = word_num
        word_num += 1

codes = ['<PAD>','<EOS>','<UNK>','<GO>']

for code in codes:
    questions_vocab_to_int[code] = len(questions_vocab_to_int)+1
    
for code in codes:
    answers_vocab_to_int[code] = len(answers_vocab_to_int)+1
    
questions_int_to_vocab = {v_i: v for v, v_i in questions_vocab_to_int.items()}

answers_int_to_vocab = {v_i: v for v, v_i in answers_vocab_to_int.items()}

#print(len(questions_vocab_to_int))
#print(len(questions_int_to_vocab))
#print(len(answers_vocab_to_int))
#print(len(answers_int_to_vacab))

#
questions_int = []

for question in short_questions:
    ints = []
    for word in question.split():
        if word not in questions_vocab_to_int:
            ints.append(questions_vocab_to_int['<UNK>'])
        else:
            ints.append(questions_vocab_to_int[word])
    questions_int.append(ints)
    
answers_int = []

for answer in short_answers:
    ints = []
    for word in answer.split():
        if word not in answers_vocab_to_int:
            ints.append(answers_vocab_to_int['<UNK>'])
        else:
            ints.append(answers_vocab_to_int[word])
    answers_int.append(ints)
    
#
    
word_count = 0
unk_count = 0

for question in questions_int:
    for word in question:
        if word == questions_vocab_to_int['<UNK>']:
            unk_count += 1
        word_count += 1
        
for answer in answers_int:
    for word in answer:
        if word == answers_vocab_to_int['<UNK>']:
            unk_count += 1
        word_count += 1
        
unk_ratio = round(unk_count/word_count,4)*100

#print("Total number of words:",word_count)

#print("Number of times <UNK>: {}%".format(round(unk_ratio,3)))

sorted_questions = []
short_questions1 = []
sorted_answers = []
short_answers1 = []




#按照question文本长度排列
for length in range(1,max_line_length+1):
    #enumerate() 函数用于将一个可遍历的数据对象(如列表、元组或字符串)组合为一个索引序列，同时列出数据和数据下标
    for i in enumerate(questions_int):
        if(len(i[1]) == length):
            sorted_questions.append(questions_int[i[0]])
            short_questions1.append(short_questions[i[0]])
            sorted_answers.append(answers_int[i[0]])
            short_answers1.append(short_answers[i[0]])
            
#print(len(sorted_questions))
#print(len(short_questions1))
#print(len(sorted_answers))
#print(len(short_answers1))

#for i in range(3):
#    print(sorted_questions[i])
#    print(short_questions1[i])
#    print(sorted_answers[i])
#    print(short_answers1[i])
#    print()


batch_size = 64
keep_probability = 0.75








def question_to_seq(question,vocab_to_int):
    question = clean_text(question)
    
    
    return [vocab_to_int.get(word,vocab_to_int['<UNK>']) for word in question.split()]


with tf.Session() as sess:
    saver=tf.train.Saver()
    saver.restore(sess,tf.train.latest_checkpoint('./'))
    graph = tf.get_default_graph()
    inference_logits = graph.get_tensor_by_name("logits:0")
    input_data = graph.get_tensor_by_name("input:0")
    keep_prob = graph.get_tensor_by_name("keep_prob:0")





    random = np.random.choice(len(short_questions))
    input_question = short_questions[random]
    print(input_question)

    input_question = question_to_seq(input_question,questions_vocab_to_int)

    input_question = input_question + [questions_vocab_to_int['<PAD>']]*(max_line_length - len(input_question))



    batch_shell = np.zeros((batch_size,max_line_length))

    batch_shell [0] = input_question
    

    answer_logits = sess.run(inference_logits,{input_data:batch_shell,keep_prob:1.0})[0]




    pad_q = questions_vocab_to_int["<PAD>"]
    pad_a = answers_vocab_to_int["<PAD>"]

    print('Question')
    print('Word Ids: {}'.format([questions_int_to_vocab[i] for i in input_question if i != pad_q]))

    print('\n')

    print('\nAnswer')
    print('Word Ids: {}'.format([i for i in np.argmax(answer_logits,1) if i != pad_a]))

    print('Response Words: {}'.format([answers_int_to_vocab[i] for i in np.argmax(answer_logits,1) if i != pad_a]))

    print('\n')

    print(' '.join(([questions_int_to_vocab[i] for i in input_question if i != pad_q])))

    print(' '.join(([answers_int_to_vocab[i] for i in np.argmax(answer_logits,1) if i != pad_a])))
