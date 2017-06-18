import cPickle as pickle
import numpy as np
import os


def load_nextchar_dataset(filename):
 """Load a nextchar dataset"""
 with open(filename, 'rb') as f:
    print "Loading X and Y from pickle file " + filename
    X = pickle.load(f)
    Y = pickle.load(f)
    return X, Y

def to_int_list(string):
    return [ord(x) for x in list(string)]

def charstr_to_int(char_string):
    return ord(char_string)

def int_to_charstr(char_int):
    return chr(char_int)

def int_list_to_string(int_list):
    return ''.join([chr(x) for x in int_list])

def plain_text_file_to_dataset(filename, outfilename="dataset/nextchar_training.pkl", input_size=3):
 """Load a plain text file and construct a nextchar dataset from it"""
 data=[]
 labels=[]
 with open(filename, 'rb') as f:
    print "Converting plain text file to trainable dataset (as pickle file)"
    print "Processing file " + filename + " as input"
    print "input_size parameter (i.e. num of neurons) will be " + str(input_size)
    for line in f:  
       l = len(line)
       # Take every consecutive input_size characters
       data.extend([to_int_list(line[x:x+input_size]) for x in range(0, l-input_size-1)])
       # Take the next character to be the label
       labels.extend([to_int_list(line[x+input_size]) for x in range(0, l-input_size-1)])

    import pickle
    with open(outfilename, 'wb') as f:
      print "Writing data and labels to file " + outfilename
      data = np.asarray(data)
      labels = np.asarray(labels)
      pickle.dump(data, f)
      pickle.dump(labels, f)


if __name__ == '__main__':
    filename="dataset/test.txt"
    plain_text_file_to_dataset(filename, outfilename='test.pkl', input_size=4, verbose=1)
