##### ContextSensitiveSpellingCorrection

### Index
* [Introduction](README.md#introduction)
* [Method Used](README.md#method-used)
* [Feature Used](README.md#feature-used)
 
### Introduction 
* In this  assignment,  I am using  Java to write  a program  to perform  context-sensitive  spelling 
* correction.  This  task is to detect spelling  errors that result  in  valid  words. 

### Method Used 
* Logistic  Regression  including  its gradient  ascent algorithm

### Feature Used 
* Surrounding  Words Feature:  Each word that appears in the sentence  containing  the 
confusable  word w is a feature.  All  surrounding  words are converted  to lowercase,  and stop 
words and punctuation  symbols  are removed.   
* Collocation  Feature:  A collocation  Ci,j  is an ordered sequence of words in the local, 
narrow context  of the confusable  word w. Offsets  i and j denote the starting  and ending 
positions  (relative  to w) of the sequence,  where a negative  (positive)  offset  refers  to a word to 
its left  (right).  The collocation  features  we used are  C1,1  , C−1,−1 ,  C −2,−1  and   C 1,2 . This  is 
because the chances  of the word before and after the confusing  word co-occurrence with  the 
confusing  word are fairly  high.  (Hassel  1990) Collocation  features  with C 1,1   , C −1,−1 ,  C −2,−1  
and  C 1,1   , C −1,−1  are also tested because they  are also believed  to be reliable  features.  (Hassel 1990) 
* Stop words filtering  for collocation  feature  does not produce a good result  as suggested 
by Hassel  (1990) and thus  is not used. 

### Structure:  In this  assignment,  sctrain.java,  sctest.java  and Evaluation.java  are written.   
* sctrain.java:  
** It is used to train  the model  of the confusing  words. To run it, you can use 
** the following  command  in the ssh secure shell  (unix  system): 
** >> java sctrain  word1 word2 train_file  model_file 
** Where word1 and word2 are the confusable  words, for example  adapt and adopt 
** the file  train_file  is a file  containing  the  training  sentences,  the example  of training  sentences 
** are stated below: 
** 0144  Hungary   joins   the  European   Union   in   May  2004  and  could   >> adopt << the euro 
** by 2008 . 
** The file  model_file  contains  the features  and weights  computed  from  the training  process, 
** each line  i in the model  file  contains  of a line  in the format: feature:==:weight
** For example:  big >>:==:-0.013325415928373883 
 
* sctest.java:  
** It is used to predicts  the confusing  words needed in the test files.  To run  it, 
** you can use the following  command  in the ssh secure shell  (unix  system):   
** >> java sctest word1 word2 test_file  model_file  actual_file 
** Where word1, word2 are the confusing  words,  
** test_file  has a similar  format  with  train_file  except that the confusing  word is not stated, for 
** example: 
** 0501  The decree allows  the government  to >> << unusual  political  , military  and tax 
** measures  with  an aim  to restoring  order . 
** model_file  is the model_file  trained  by sctrain 
** For each test sentence  in test_file,  actual_file  contains  one line  indicating  the test sentence   id  
** and  the  disambiguated   confusable   word  as  determined   by  the  logistic  regression 
** classifier:   
** 0501 adopt 

* Evaluation.java:  
** is use to check the accuracy  of the  actual_file  generated  by the sctest, command: 
** >> java Evaluation  answer_file  actual_file 
** Where answer_file  is the correct answer expected by the program  and actual  file  is the actual 
** answer generated  by sctest. It will  return  an accuracy  value. 
