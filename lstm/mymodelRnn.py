# -*- coding: utf-8 -*-
"""
Created on Mon May  6 14:19:07 2019

@author: admin
"""
import numpy as np
import tensorflow as tf
from gensim.models.word2vec import Word2Vec
import jieba
import time

def checkList(theList,name):
    print("--------------------------the information of "+name+"--------------------")
    print("len:"+str(len(theList)))
    print("the top 10 elements:")
    for index in range(0,10):
        print(theList[index])
    
    print("-------------------------- end --------------------------")

def stopWordsList():
     stopwords = [line.strip() for line in open('stopwords.txt',encoding='UTF-8').readlines()]
     return stopwords
def myReadQAFile():
    questions = []
    answers = []
    fr = open(r"C:\Users\admin\Desktop\eclipse\QADATA_5_5_5_5.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        questions.append(line[6:len(line)-1])
        line = fr.readline()
        answers.append(line[6:len(line)-1])  
        line = fr.readline()
    return questions,answers

def myReplace(oldStr,str1,str2):
    
    length = len(str1)
    result = oldStr
    while(str1 in result):
        index = result.index(str1)
        leftStr = result[:index]
        midStr = str2
        rightStr = result[index+length:len(result)]
        result = leftStr+ midStr+rightStr
    return result 
def stopWordsOperation(questions,stopwords):
    for index in range(0,len(questions)):
        for stopword in stopwords:
            if stopword in questions[index]:
                questions[index] = myReplace(questions[index],stopword,"")
    return questions

def trainWord2Vec(answers_jieba):
#    model.build_vocab(answers_jieba)
#    model.train(answers_jieba,total_examples = model.corpus_count,epochs = model.iter)
    model=Word2Vec(answers_jieba, size=100, window=5, min_count=1, workers=4)
    model.save(u'QAvecs_3.model')
    

def operation(total_jieba):
    total_train =[]
    for index in range(0,len(total_jieba)):
        tmp = total_jieba[index].split(' ')
        total_train.append(tmp)
    return total_train

def readDrDiSy():
    drugs =[]
    diseases = []
    symptoms = []
    fr = open("drugs.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        drugs.append(line[:len(line)-1])
        line = fr.readline()
    fr.close()
    diseases = []
    fr = open("diseases.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        diseases.append(line[:len(line)-1])
        line = fr.readline()
    symptoms = []
    fr = open("entities.txt","r+",encoding='utf-8')
    line = fr.readline()
    while(line):
        symptoms.append(line[:len(line)-1])
        line = fr.readline()
    return drugs,diseases,symptoms
def getLength(questions_jieba):
    min_q_length=200
    max_q_length=0
    for question_jieba in questions_jieba:
        if len(question_jieba) > max_q_length:
            max_q_length = len(question_jieba)
        elif len(question_jieba) < min_q_length:
            min_q_length = len(question_jieba)
    return min_q_length,max_q_length
def QAAddUNK(questions_jieba,answers_jieba,QA_word2vecs):
    questions_UNK = []
    answers_UNK = []
    

    for question_jieba in questions_jieba:
        words = []
        for word in question_jieba:
            if word not in QA_word2vecs.wv:
                words.append('<UNK>')
            else:
                words.append(word)
        questions_UNK.append(words)
        
    for answer_jieba in answers_jieba:
        words = []
        for word in answer_jieba:
       #     print(word)
            if word not in QA_word2vecs.wv:
            #    print(word)
                words.append('<UNK>')
            else:
                words.append(word)
        answers_UNK.append(words)
    return questions_UNK,answers_UNK

def calculate_UNK_ratio(questions_UNK,answers_UNK):
    word_count = 0
    unk_count = 0
    
    for question_UNK in questions_UNK:
        for word in question_UNK:
            if word == '<UNK>':
                unk_count += 1
            word_count += 1
            
    for answer_UNK in answers_UNK:
        for word in answer_UNK:
            if word == '<UNK>':
                unk_count += 1
            word_count += 1
            
    unk_ratio = round(unk_count/word_count,4)*100
    print("Number of times <UNK>: {}%".format(round(unk_ratio,3)))

def sortQA(questions_UNK,answers_UNK,min_q_length,max_q_length):
    sorted_questions=[]
    sorted_answers = []
    
    for length in range(min_q_length,max_q_length+1):
        for index in range(0,len(questions_UNK)):
            if len(questions_UNK[index]) == length:
                sorted_questions.append(questions_UNK[index])
                sorted_answers.append(answers_UNK[index])
    
    return sorted_questions,sorted_answers
def vecArray2float32(vecArray):
    floatList = []
    for num in vecArray:
        floatList.append(num)
    return floatList
def getIdsWtvWtiItv(word2vecs):
    keys_list = []
    keys_list = word2vecs.wv.vocab.keys()
    ids = []
    
    for key in keys_list:
        ids.append(key)
        
    word_to_vecs = {}
    word_to_ids = {}
    ids_to_vecs = []
    for index in range(0,len(ids)):
        word_to_vecs[ids[index]] = vecArray2float32(word2vecs[ids[index]])
        word_to_ids[ids[index]] = index
        ids_to_vecs.append(vecArray2float32(word2vecs[ids[index]]))
        
        
    
    return ids,word_to_vecs,word_to_ids,ids_to_vecs

def getQAint(sorted_questions,sorted_answers,QA_word_to_ids):
    questions_int = []

    for question in sorted_questions:
        ints = []
        for word in question:
            if word not in QA_word_to_ids:
                ints.append(QA_word_to_ids['<UNK>'])
            else:
                ints.append(QA_word_to_ids[word])
        questions_int.append(ints)
        
    answers_int = []
    
    for answer in sorted_answers:
        ints = []
        for word in answer:
            if word not in QA_word_to_ids:
                ints.append(QA_word_to_ids['<UNK>'])
            else:
                ints.append(QA_word_to_ids[word])
        answers_int.append(ints)
    return questions_int,answers_int


def model_inputs():
    #question batch
    input_data = tf.placeholder(tf.int32,[None,None],name='input')
    #answer batch 
    targets = tf.placeholder(tf.int32,[None,None],name='targets')
    
    lr = tf.placeholder(tf.float32,name='learning_rate')
    
    keep_prob = tf.placeholder(tf.float32,name='keep_prob')
    
    vocab_table = tf.placeholder(tf.float32,[None,None],name='vocab_table')
    
    return input_data,targets,lr,keep_prob,vocab_table

#编码层
def encoding_layer(
        rnn_inputs,
        rnn_size,
        num_layers,
        keep_prob,
        sequence_length
    ):
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

def _embed(self):
    """
    词向量矩阵 * ids向量；不是矩阵相乘，具体操作查看embedding_lookup（好像是把ids相关的向量在矩阵中提前排列）
    The embedding layer, question and passage share embeddings
    """
    with tf.device('/cpu:0'), tf.variable_scope('word_embedding'):
        self.word_embeddings = tf.get_variable(
            'word_embeddings',
            shape=(self.vocab.size(), self.vocab.embed_dim),
            initializer=tf.constant_initializer(self.vocab.embeddings),
            #initializer=tf.zeros_initializer(),
            trainable=True #加入训练！！
        )
        #输出还是矩阵，只是行序发生改变
        self.p_emb = tf.nn.embedding_lookup(self.word_embeddings, self.p)

#删除每一个批次之中最后一个单词的ID，并且在每个批次开头加入<GO>标记
def pad_sentence_batch(sentence_batch,vocab_to_int):
#    print(sentence_batch)
#    max_sentence = len(sentence_batch)
#    print([sentence + [vocab_to_int['<PAD>']]*(max_sentence - len(sentence)) for sentence in sentence_batch])
    max_sentence = max([len(sentence) for sentence in sentence_batch])
    
    return [sentence + [vocab_to_int['<PAD>']]*(max_sentence - len(sentence)) for sentence in sentence_batch]

def process_encoding_input(
        target_data,
        vocab_to_int,
        batch_size
    ):
    #tf.strided_slice是多维切片函数
    #前四个参数分别为：输入数据，开始切片处，终止切片处，步长。区间为开区间
    
    #删除每一个批次之中最后一个单词的ID
    ending = tf.strided_slice(target_data,[0,0],[batch_size,-1],[1,1])
    
    #用来拼接张量的函数,tf.concat([tensor1, tensor2, tensor3,...], axis)
    #axis=1     代表在第1个维度拼接 
    #tf.fill([batch_size,1],vocab_to_int['<GO>'])
    #生成[batch_size,1]维度的vocab_to_int['<GO>']
    #tf.concat将ending加到每个batch的<GO>的后面
    dec_input = tf.concat([
            tf.fill([batch_size,1],vocab_to_int['<GO>']),ending],
            1
        )
    return dec_input
def decoding_layer_train(
        encoder_state,
        dec_cell,
        dec_embed_input,
        sequence_length,
        decoding_scope,
        output_fn,
        keep_prob,
        batch_size
    ):
    #三维数组，每个输入的单元代表着相应维的长度
#    train_decoder_fn = tf.contrib.seq2seq.simple_decoder_fn_train(encoder_state[0])
    
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
    #tf.nn.dropout是TensorFlow里面为了防止或减轻过拟合而使用的函数，它一般用在全连接层。
    train_pred_drop = tf.nn.dropout(train_pred,keep_prob)
    
    return output_fn(train_pred_drop)

def decoding_layer_infer(
        encoder_state,
        dec_cell,
        dec_embeddings,
        start_of_sequence_id,
        end_of_sequence_id,
        maximum_length,
        vocab_size,
        decoding_scope,
        output_fn,
        keep_prob,
        batch_size
    ):
    '''
    infer_decoder_fn = tf.contrib.seq2seq.simple_decoder_fn_inference(
            output_fn, 
            encoder_state[0], 
            dec_embeddings, 
            start_of_sequence_id,
            end_of_sequence_id,
            maximum_length,
            vocab_size,
            dtype=tf.int32, 
            name=None)

    '''
    
    attention_states = tf.zeros([batch_size,1,dec_cell.output_size])
    
    att_keys,att_vals,att_score_fn,att_construct_fn=tf.contrib.seq2seq.prepare_attention(
            attention_states,
            attention_option="bahdanau",
            num_units=dec_cell.output_size
    )
    
    infer_decoder_fn = tf.contrib.seq2seq.attention_decoder_fn_inference(
            output_fn,
            encoder_state[0],
            att_keys,
            att_vals,
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

def decoding_layer(
        dec_embed_input,
        dec_embeddings,
        encoder_state,
        vocab_size,
        sequence_length,
        rnn_size,
        num_layers,
        vocab_to_int,
        keep_prob,
        batch_size
    ):
    with tf.variable_scope("decoding") as decoding_scope:
        lstm = tf.contrib.rnn.BasicLSTMCell(rnn_size)
        drop = tf.contrib.rnn.DropoutWrapper(lstm,input_keep_prob = keep_prob)
        dec_cell = tf.contrib.rnn.MultiRNNCell([drop]*num_layers)
        
        
   #     decoder_embeddings = tf.Variable(tf.random_uniform([target_vocab_size, decoding_embedding_size]))
   #     decoder_embed_input = tf.nn.embedding_lookup(decoder_embeddings, decoder_input)

        '''
        
        output_layer = Dense(vocab_size,
                         kernel_initializer = tf.truncated_normal_initializer(mean = 0.0, stddev=0.1))

        
        training_helper = tf.contrib.seq2seq.TrainingHelper(inputs=dec_embed_input,
                                                            sequence_length=sequence_length,
                                                            time_major=False)
        # 构造decoder
        training_decoder = tf.contrib.seq2seq.BasicDecoder(dec_cell,
                                                           training_helper,
                                                           encoder_state,
                                                           output_layer) 
        training_decoder_output, _ , __ = tf.contrib.seq2seq.dynamic_decode(training_decoder,
                                                                       impute_finished=True,
                                                                       maximum_iterations=sequence_length)
        
        
        
        '''
        
        
        
        
        
        
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
    
        '''
    
                # 创建一个常量tensor并复制为batch_size的大小
        start_tokens = tf.tile(tf.constant([vocab_to_int['<GO>']], dtype=tf.int32), [batch_size], 
                               name='start_tokens')
        predicting_helper = tf.contrib.seq2seq.GreedyEmbeddingHelper(dec_embeddings,
                                                                start_tokens,
                                                                vocab_to_int['<EOS>'])
        predicting_decoder = tf.contrib.seq2seq.BasicDecoder(dec_cell,
                                                        predicting_helper,
                                                        encoder_state,
                                                        output_layer)
        predicting_decoder_output, _ , __= tf.contrib.seq2seq.dynamic_decode(predicting_decoder,
                                                            impute_finished=True,
                                                            maximum_iterations=sequence_length)
        
        '''
        infer_logits = decoding_layer_infer(
                encoder_state,
                dec_cell,
                dec_embeddings,
                vocab_to_int['<GO>'],
                vocab_to_int['<EOS>'],
                sequence_length-1,
                vocab_size,
                decoding_scope,
                output_fn,
                keep_prob,
                batch_size
            )
        
    return train_logits, infer_logits

def seq2seq_model(
        input_data,
        target_data,
        keep_prob,
        batch_size,
        sequence_length,
        answers_vocab_size,
        questions_vocab_size,
        enc_embedding_size,
        dec_embedding_size,
        rnn_size,
        num_layers,
        QA_word_to_ids,
        QA_ids_to_vecs,
    ):
    #一般用于sequence2sequence网络，可完成对输入序列数据的嵌入工作。
    
    '''
    enc_embed_input = tf.contrib.layers.embed_sequence(
            input_data,
            answers_vocab_size+1,
            enc_embedding_size,
  #          initializer = tf.random_uniform_initializer(0,1)
        )
    '''
    
    enc_embed = tf.get_variable(
            name = None,#'enc_embed_the',
            shape=(questions_vocab_size+1, enc_embedding_size),
            initializer=tf.constant_initializer(QA_ids_to_vecs),
            #initializer=tf.zeros_initializer(),
            trainable=True #加入训练！！
        )
    
    enc_embed_input = tf.nn.embedding_lookup(enc_embed, input_data)
    '''
    ##给词向量矩阵 赋值
    data = tf.Variable(
            tf.zeros([answers_vocab_size, enc_embedding_size]),
            dtype=tf.float32
        )
    
    data = tf.nn.embedding_lookup(ids_to_vecs,input_data)

    '''
    
    enc_state = encoding_layer(
            enc_embed_input,
            rnn_size,
            num_layers,
            keep_prob,
            sequence_length
        )
    
    dec_input = process_encoding_input(
            target_data,
            QA_word_to_ids,
            batch_size
        )
    #tf.Variable(initializer,name),参数initializer是初始化参数，name是可自定义的变量名称
    '''
    dec_embeddings = tf.Variable(
            #返回6*6的矩阵，产生于0和1之间
            tf.random_uniform([questions_vocab_size+1,dec_embedding_size],0,1)
        )
    
    ##查找张量dec_embeddings中的序号为dec_input的
    dec_embed_input = tf.nn.embedding_lookup(dec_embeddings,dec_input)
    '''
    '''
    dec_embeddings = tf.Variable(
            tf.zeros([questions_vocab_size+1, dec_embedding_size]),
            dtype=tf.float32
        )
    
    
    
    dec_embed_input = tf.nn.embedding_lookup(dec_embeddings,dec_input)
    '''
    dec_embeddings = tf.get_variable(
            'dec_embed',
            shape=(answers_vocab_size+1, enc_embedding_size),
            initializer=tf.constant_initializer(QA_ids_to_vecs),
            #initializer=tf.zeros_initializer(),
            trainable=True #加入训练！！
        )
    
    dec_embed_input = tf.nn.embedding_lookup(dec_embeddings,dec_input)
    
    
    train_logits, infer_logits = decoding_layer(
            dec_embed_input,
            dec_embeddings,
            enc_state,
            answers_vocab_size,
            sequence_length,
            rnn_size,
            num_layers,
            QA_word_to_ids,
            keep_prob,
            batch_size
        )
    
    return train_logits,infer_logits


        
        
def question_to_seq(question,vocab_to_int):
    
    
    return [vocab_to_int.get(word,vocab_to_int['<UNK>']) for word in question.split()]

def main():
    questions,answers = myReadQAFile()
#    stopwords = stopWordsList()
    
  #  questions = stopWordsOperation(questions,stopwords)
    answers_jieba = []
    questions_jieba = []
#    codes = ['<PAD>','<EOS>','<UNK>','<GO>']
    for index in range(0,len(answers)):

        answers_jieba.append(" ".join(jieba.cut(answers[index])))
        questions_jieba.append(" ".join(jieba.cut(questions[index])))
    '''
    total_jieba = answers_jieba + questions_jieba
    total_train = operation(total_jieba)
    total_train.append(codes)
    total_train.append(codes)
    total_train.append(codes)
    total_train.append(codes)
    total_train.append(codes)
    total_train.append(codes)
    total_train.append(codes)
    total_train.append(codes)
    total_train.append(codes)
    total_train.append(codes)
    '''
    answers_ope = operation(answers_jieba)
    questions_ope = operation(questions_jieba)
    checkList(questions_ope,"questions_ope")
  #  print(questions_jieba)
#    trainWord2Vec(total_train)
    QAword2vecs = Word2Vec.load(r'C:\Users\admin\Desktop\eclipse\allQAvecs_5_5_5_5.model')
  #  wordVectors = np.load('QAvecs_3.model')
 #   print(wordVectors)
#    drugs,diseases,symptoms = readDrDiSy()
    
    
    #GO与<start>开始标记相同，是第一个提供给解码器的标记，与向量一起，为答案产生标记
    #EOS 意思是句尾，一旦解码器产生EOS标记，表示答案已经产生
    #UNK 未知标记
    #PAD 填充标记
    
    #1, 86
    
    min_q_length,max_q_length = getLength(questions_ope)
    print(min_q_length)
    print(max_q_length)
    questions_UNK,answers_UNK = QAAddUNK(questions_ope,answers_ope,QAword2vecs)
    
    calculate_UNK_ratio(questions_UNK,answers_UNK)
    
    sorted_questions,sorted_answers = sortQA(questions_UNK,answers_UNK,min_q_length,max_q_length)
    
    QA_ids,QA_word_to_vecs,QA_word_to_ids,QA_ids_to_vecs = getIdsWtvWtiItv(QAword2vecs)

    sorted_questions_int,sorted_answers_int = getQAint(sorted_questions,sorted_answers,QA_word_to_ids)

#    checkList(sorted_questions_int,"sorted_questions_int")

    
    del QA_word_to_vecs
    
    
    epochs = 50
    batch_size = 64
 #   length_of_vector = 100
    rnn_size = 256
    num_layers = 2
    encoding_embedding_size = 256
    decoding_embedding_size = 256
    learning_rate = 0.001
    learning_rate_decay = 0.9
    min_learning_rate = 0.0001
    keep_probability = 0.75
    
    tf.reset_default_graph()
  
    # start the session

    sess = tf.InteractiveSession()
  
    input_data, targets,lr,keep_prob,vocab_table = model_inputs()
    
    sequence_length = tf.placeholder_with_default(
        max_q_length,
        None,
        name='sequence_length'
    )
    
    input_shape = tf.shape(input_data)
    

 #   print(sess.run(enc_embed_input))
    
    
    train_logits, inference_logits = seq2seq_model(
        tf.reverse(input_data,[-1]),
        targets,
        keep_prob,
        batch_size,
        sequence_length,
        len(QA_word_to_ids),
        len(QA_word_to_ids),
        encoding_embedding_size,
        decoding_embedding_size,
        rnn_size,
        num_layers,
        QA_word_to_ids,
        QA_ids_to_vecs,
    )

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
    
    def batch_data(
        questions,
        answers,batch_size
    ):
        for batch_i in range(0,len(questions)//batch_size):
            start_i = batch_i*batch_size
            
            questions_batch = questions[start_i:start_i + batch_size]
            
            answers_batch = answers[start_i:start_i + batch_size]
            
            pad_questions_batch = np.array(
                    pad_sentence_batch(
                            questions_batch,
                            QA_word_to_ids
                            )
                    )
            
            
            pad_answers_batch = np.array(
                    pad_sentence_batch(
                            answers_batch,
                            QA_word_to_ids
                            )
                    )
            
            yield pad_questions_batch,pad_answers_batch
    del questions_UNK,answers_UNK,sorted_questions,sorted_answers,questions_ope,answers_ope,answers_jieba,questions_jieba,questions,answers
    train_valid_split = int(len(sorted_questions_int)*0.15)
    train_change = int(len(sorted_questions_int)*0.15)

    train_questions = sorted_questions_int[train_valid_split:]
    print(len(train_questions))
    
    train_answers = sorted_answers_int[train_valid_split:]
    
    valid_questions = sorted_questions_int[:train_change]
    print(len(valid_questions))
    valid_answers = sorted_answers_int[:train_change]
    
    del sorted_questions_int,sorted_answers_int
    
    display_step = 20

    stop_early = 0
    
    stop = 5
    
    validation_check = ((len(train_questions))//batch_size//2)-1

    total_train_loss = 0
    
    summary_valid_loss = []
    
    checkpoint = r"C:\Users\admin\Desktop\eclipse\my_QA_model.ckpt"
    
    sess.run(tf.global_variables_initializer())
    
    for epoch_i in range(1,epochs +1):
        for batch_i,(questions_batch,answers_batch) in enumerate(
                batch_data(train_questions,train_answers,batch_size)):
            
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
                
                for batch_ii,(questions_batch,answers_batch) in enumerate(
                        batch_data(valid_questions,valid_answers,batch_size)):
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
                    
                #    builder = tf.saved_model.builder.SaveModelBuilder('./Models/')
                    
                #    builder.add_meta_graph_and_variables(sess,[tf.saved_model.tag_constants.TRAINING])
                #    builder.save()
                    
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
    
    

  
  
     
main()
