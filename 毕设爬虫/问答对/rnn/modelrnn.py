# -*- coding: utf-8 -*-
"""
Created on Thu Apr 25 10:10:34 2019

@author: admin
"""
import numpy as np
import pandas as pd

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

#print("# of questions:",len(short_questions))
#print("# of answers:",len(short_answers))
#print("% of data used: {}%".format(round(len(short_questions)/len(questions),4)*100))


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
    
#为模型的输入创建占位符
def model_inputs():
    input_data = tf.placeholder(tf.int32,[None,None],name='input')
    
    targets = tf.placeholder(tf.int32,[None,None],name='targets')
    
    lr = tf.placeholder(tf.float32,name='learning_rate')
    
    keep_prob = tf.placeholder(tf.float32,name='keep_prob')
    
    return input_data,targets,lr,keep_prob

#删除每一个批次之中最后一个单词的ID，并且在每个批次开头加入<GO>标记
def process_encoding_input(target_data,vocab_to_int,batch_size):
    #tf.strided_slice是多维切片函数
    #前四个参数分别为：输入数据，开始切片处，终止切片处，步长。区间为开区间
    
    ending = tf.strided_slice(target_data,[0,0],[batch_size,-1],[1,1])
    
    #用来拼接张量的函数,tf.concat([tensor1, tensor2, tensor3,...], axis)
    #axis=1     代表在第1个维度拼接 
    dec_input = tf.concat([
            tf.fill([batch_size,1],vocab_to_int['<GO>']),ending],
            1
        )
    return dec_input

#编码层
def encoding_layer(rnn_inputs,rnn_size,num_layers,keep_prob,sequence_length):
    #每一个单元的输出为 rnn_size 的向量
    lstm = tf.contrib.rnn.BasicLSTMCell(rnn_size)
    #dropout
    drop = tf.contrib.rnn.DropoutWrapper(
            lstm, 
            input_keep_prob = keep_prob
        )
    #实现多层LSTM循环神经网络
    enc_cell = tf.contrib.rnn.MultiRNNCell([drop]*num_layers)
    #双向RNN
    _,enc_state = tf.nn.bidirectional_dynamic_rnn(
            cell_fw = enc_cell,
            cell_bw = enc_cell,
            sequence_length = sequence_length,
            inputs = rnn_inputs,
            dtype = tf.float32
        )
    
    return enc_state
    
def decoding_layer_train(encoder_state,dec_cell,dec_embed_input,sequence_length,decoding_scope,output_fn,keep_prob,batch_size):
    #三维数组，每个输入的单元代表着相应维的长度
    attention_states = tf.zeros([batch_size,1,dec_cell.output_size])
    #？
    att_keys,att_vals,att_score_fn,att_construct_fn = tf.contrib.seq2seq.prepare_attention(
            attention_states,
            attention_option="bahdanau",
            num_units = dec_cell.output_size
        )
    #？
    train_decoder_fn = tf.contrib.seq2seq.attention_decoder_fn_train(
            encoder_state[0],
            att_keys,
            att_vals,
            att_score_fn,
            att_construct_fn,
            name="attn_dec_train"
        )
    #dynamic的概念，即不需要确定的输入长度，以及batch 大小， 都可以动态。
    
    #但是注意首先每个batch对应所有样本的输入长度还是需要一样的 作为dense数据 否则 不可处理 
    train_pred,_,_ = tf.contrib.seq2seq.dynamic_rnn_decoder(
            dec_cell,
            train_decoder_fn,
            dec_embed_input,
            sequence_length,
            scope=decoding_scope
        )
    
    train_pred_drop = tf.nn.dropout(train_pred,keep_prob)
    
    return output_fn(train_pred_drop)

def decoding_layer_infer(encoder_state,dec_cell,dec_embeddings,start_of_sequence_id,end_of_sequence_id,maximum_length,vocab_size,decoding_scope,output_fn,keep_prob,batch_size):
    attention_states = tf.zeros([batch_size,1,dec_cell.output_size])
    
    att_keys,att_vals,att_score_fn,att_construct_fn=tf.contrib.seq2seq.prepare_attention(
            attention_states,
            attention_option="bahdanau",
            num_units=dec_cell.output_size
    )
    
    infer_decoder_fn = tf.contrib.seq2seq.attention_decoder_fn_inference(
            output_fn,encoder_state[0],
            att_keys,att_vals,
            att_score_fn,
            att_construct_fn,
            dec_embeddings,
            start_of_sequence_id,
            end_of_sequence_id,
            maximum_length,
            vocab_size,
            name="attn_dec_inf"
    )
    
    infer_logits,_,_ = tf.contrib.seq2seq.dynamic_rnn_decoder(
            dec_cell,
            infer_decoder_fn,
            scope=decoding_scope
    )
    
    return infer_logits


#用来预测和训练分数
def decoding_layer(dec_embed_input,dec_embeddings,encoder_state,vocab_size,sequence_length,rnn_size,num_layers,vocab_to_int,keep_prob,batch_size):
    with tf.variable_scope("decoding") as decoding_scope:
        lstm = tf.contrib.rnn.BasicLSTMCell(rnn_size)
        drop = tf.contrib.rnn.DropoutWrapper(lstm,input_keep_prob = keep_prob)
        dec_cell = tf.contrib.rnn.MultiRNNCell([drop]*num_layers)
        
        #生成截断正态分布的初始化程序.
        #
        #这些值与来自 random_normal_initializer 的值类似,不同之处在于值超过两个标准偏差值的值被丢弃并重新绘制.这是推荐的用于神经网络权值和过滤器的初始化器.
        #stddev：一个 python 标量或一个标量张量,要生成的随机值的标准偏差.
        weights = tf.truncated_normal_initializer(stddev = 0.1)
        
        #生成张量初始化为0的初始化器.
        biases = tf.zeros_initializer()
        #lambda定义一个函数，输入变量为x。
        
        #全连接层,每个输入输出存在连接
        output_fn = lambda x : tf.contrib.layers.fully_connected(
                x,
                vocab_size,
                None,
                scope = decoding_scope,
                weights_initializer = weights,
                biases_initializer = biases
            )
        
        train_logits = decoding_layer_train(
                encoder_state,
                dec_cell,
                dec_embed_input,
                sequence_length,
                decoding_scope,
                output_fn,
                keep_prob,
                batch_size
            )
    
        decoding_scope.reuse_variables()
    
        infer_logits = decoding_layer_infer(encoder_state,dec_cell,dec_embeddings,vocab_to_int['<GO>'],vocab_to_int['<EOS>'],sequence_length-1,vocab_size,decoding_scope,output_fn,keep_prob,batch_size)
    
    return train_logits, infer_logits

#seq2seq_model()函数用于将所有先前定义的函数组合起来，还使用随即均匀分布来初始化词嵌入过程。
#该函数将被用于流程图中，以计算训练及推理分对数
    
def seq2seq_model(input_data,target_data,keep_prob,batch_size,sequence_length,answers_vocab_size,questions_vocab_size,enc_embedding_size,dec_embedding_size,rnn_size,num_layers,questions_vocab_to_int):
    #一般用于sequence2sequence网络，可完成对输入序列数据的嵌入工作。
    enc_embed_input = tf.contrib.layers.embed_sequence(
            input_data,
            answers_vocab_size+1,
            enc_embedding_size,
            initializer = tf.random_uniform_initializer(0,1)
        )
    
    enc_state = encoding_layer(
            enc_embed_input,
            rnn_size,
            num_layers,
            keep_prob,
            sequence_length
        )
    
    dec_input = process_encoding_input(
            target_data,
            questions_vocab_to_int,
            batch_size
        )
    #tf.Variable(initializer,name),参数initializer是初始化参数，name是可自定义的变量名称
    dec_embeddings = tf.Variable(
            #返回6*6的矩阵，产生于0和1之间
            tf.random_uniform([questions_vocab_size+1,dec_embedding_size],0,1)
        )
    
    ##查找张量dec_embeddings中的序号为dec_input的
    dec_embed_input = tf.nn.embedding_lookup(dec_embeddings,dec_input)
    
    train_logits, infer_logits = decoding_layer(
            dec_embed_input,
            dec_embeddings,
            enc_state,
            questions_vocab_size,
            sequence_length,
            rnn_size,
            num_layers,
            questions_vocab_to_int,
            keep_prob,
            batch_size
        )
    
    return train_logits,infer_logits
        
epochs = 50
batch_size = 64
rnn_size = 512
num_layers = 2
encoding_embedding_size = 512
decoding_embedding_size = 512
learning_rate = 0.005
learning_rate_decay = 0.9
min_learning_rate = 0.0001
keep_probability = 0.75

tf.reset_default_graph()
# start the session

sess = tf.InteractiveSession()
# loading the model inputs

#print input_data, targets,lr,keep_prob
#Tensor("input:0", shape=(?, ?), dtype=int32)
#Tensor("targets:0", shape=(?, ?), dtype=int32)
#Tensor("learning_rate:0", dtype=float32)
#Tensor("keep_prob:0", dtype=float32)
#
#
#
input_data, targets,lr,keep_prob = model_inputs()






# sequence length is max_line_length for each batch
#max_line_length:100
sequence_length = tf.placeholder_with_default(max_line_length,None,name='sequence_length')

#print(sequence_length)
#Tensor("sequence_length:0", dtype=int32)






#find shape of the input data for sequence_loss

#将矩阵的维度输出为一个维度矩阵
#input_data:Tensor("input:0", shape=(?, ?), dtype=int32)
input_shape = tf.shape(input_data)

#print(input_shape)
#Tensor("Shape:0", shape=(2,), dtype=int32)


#tf.reverse(input_data,[-1])    :Tensor("ReverseV2:0", shape=(?, ?), dtype=int32)
#targets                        :Tensor("targets:0", shape=(?, ?), dtype=int32)
#keep_prob                      :Tensor("keep_prob:0", dtype=float32)
#batch_size                     :64
#sequence_length                :Tensor("sequence_length:0", dtype=int32)
#len(answers_vocab_to_int)      :18987
#len(questions_vocab_to_int)    :18987
#encoding_embedding_size        :512
#decoding_embedding_size        :512
#rnn_size                       :512
#num_layers                     :2
#questions_vocab_to_int         :{'what': 0, 'does': 1, 'medicare': 2, 'ime': 3, 'stand': 4, 'for': 5, 'is': 6, 'long': 7, 'term': 8, 'care': 9,...}


train_logits, inference_logits = seq2seq_model(
        tf.reverse(input_data,[-1]),
        targets,keep_prob,batch_size,
        sequence_length,
        len(answers_vocab_to_int),
        len(questions_vocab_to_int),
        encoding_embedding_size,
        decoding_embedding_size,
        rnn_size,
        num_layers,
        questions_vocab_to_int
    )

#print(train_logits)
#Tensor("decoding/Reshape_1:0", shape=(64, ?, 18987), dtype=float32)

#print(inference_logits)
#Tensor("decoding/dynamic_rnn_decoder_1/transpose:0", shape=(?, ?, 18987), dtype=float32)


#Create inference logits tensor
tf.identity(inference_logits,'logits')

with tf.name_scope("optimization"):
    #calculating loss function
    cost = tf.contrib.seq2seq.sequence_loss(
            train_logits,
            targets,
            tf.ones([input_shape[0],sequence_length])
        )
    
    #using adam optimizer
    optimizer = tf.train.AdamOptimizer(learning_rate)
    
    #performing gradient clipping to handle the vanishing gradient problem
    gradients = optimizer.compute_gradients(cost)
    
    capped_gradients = [(tf.clip_by_value(grad,-5.,5.),var)
    for grad, var in gradients if grad is not None]
    
    train_op = optimizer.apply_gradients(capped_gradients)
    
def batch_data(questions,answers,batch_size):
    for batch_i in range(0,len(questions)//batch_size):
        start_i = batch_i*batch_size
        
        questions_batch = questions[start_i:start_i + batch_size]
        
        answers_batch = answers[start_i:start_i + batch_size]
        
        pad_questions_batch = np.array(
                pad_sentence_batch(
                        questions_batch,
                        questions_vocab_to_int
                        )
                )
        
        
        pad_answers_batch = np.array(
                pad_sentence_batch(
                        answers_batch,
                        answers_vocab_to_int
                        )
                )
        
        yield pad_questions_batch,pad_answers_batch

train_valid_split = int(len(sorted_questions)*0.15)

train_questions = sorted_questions[train_valid_split:]

train_answers = sorted_answers[train_valid_split:]

valid_questions = sorted_questions[:train_valid_split]

valid_answers = sorted_answers[:train_valid_split]

#print(len(train_questions))
#16242
#print(len(valid_questions))
#2866


#check training loss after every 20 batches
display_step = 20

stop_early = 0

#if validation loss decreases after 5 consecutive checks, stop training
stop = 5


#Counter for cheching validation loss
validation_check = ((len(train_questions))//batch_size//2)-1

#record the validation loss for saving improvments in the model
total_train_loss = 0

summary_valid_loss = []



#creating the chechpoint file in the current directory
checkpoint = "./best_model.ckpt"
#在TensorFlow的世界里，变量的定义和初始化是分开的，所有关于图变量的赋值和计算都要通过tf.Session的run来进行。想要将所有图变量进行集体初始化时应该使用tf.global_variables_initializer。
sess.run(tf.global_variables_initializer())



for epoch_i in range(1,epochs +1):
    for batch_i,(questions_batch,answers_batch) in enumerate(batch_data(train_questions,train_answers,batch_size)):
        
        start_time = time.time()
        
        _,loss = sess.run(
                [train_op,cost],
                {input_data:questions_batch,
                 targets:answers_batch,
                 lr:learning_rate,
                 sequence_length:answers_batch.shape[1],
                 keep_prob:keep_probability
                 }
            )
        total_train_loss += loss
        end_time = time.time()
        batch_time = end_time - start_time
        if batch_i % display_step ==0:
            print('Epoch {:>3}/{} Batch {:>4}/{} - Loss:{:>6.3f},Second:{:4.2f}'.format(epoch_i,epochs,batch_i,len(train_questions)//batch_size,total_train_loss/display_step,batch_time*display_step))
            total_train_loss = 0
        #?
        if batch_i % validation_check == 0 and batch_i >0:
            total_valid_loss = 0
            start_time = time.time()
            
            for batch_ii,(questions_batch,answers_batch) in enumerate(batch_data(valid_questions,valid_answers,batch_size)):
                valid_loss = sess.run(
                        cost,
                        {input_data:questions_batch,
                         targets:answers_batch,
                         lr:learning_rate,
                         sequence_length:answers_batch.shape[1],
                         keep_prob:1
                         }
                    )
                
                total_valid_loss += valid_loss
            end_time = time.time()
            batch_time = end_time - start_time
            
            avg_valid_loss = total_valid_loss/(len(valid_questions)/batch_size)
            
            print('Valid Loss:{:>6.3f},Seconds:{:5.2f}'.format(avg_valid_loss,batch_time))
            
            # reduce learning rate
            
            learning_rate *= learning_rate_decay
            
            if learning_rate < min_learning_rate:
                learning_rate = min_learning_rate
                
            summary_valid_loss.append(avg_valid_loss)
            
            if avg_valid_loss <= min(summary_valid_loss):
                print("New Record!")
                stop_early = 0
                saver = tf.train.Saver()
                saver.save(sess,checkpoint)
            else:
                print("No Imprvement")
                stop_early += 1
                if stop_early == stop:
                    break
    if stop_early == stop:
        print("Stopping Training.")
        break

def question_to_seq(question,vocab_to_int):
    question = clean_text(question)
    
    
    return [vocab_to_int.get(word,vocab_to_int['<UNK>']) for word in question.split()]

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


            
sess.close()
