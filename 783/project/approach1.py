import numpy as np

from layers import *

class SeqNN(object):
  def __init__(self, word_to_idx, wordvec_dim=128, hidden_dim=128, 
          cell_type='rnn', sentlen=7, storylen=70, qlen=5,
               dtype=np.float32):
    if cell_type not in {'rnn', 'lstm', 'gru'}:
      raise ValueError('Invalid cell_type "%s"' % cell_type)
    
    self.cell_type = cell_type
    self.T3=qlen
    self.T2=sentlen
    self.T=storylen
    self.dtype = dtype
    self.word_to_idx = word_to_idx
    self.idx_to_char = {i: w for w, i in word_to_idx.iteritems()}
    self.params = {}
    
    vocab_size = len(word_to_idx)

    self._null = word_to_idx['<NULL>']
    self._start = word_to_idx['<Start>']
    self._query = word_to_idx['<Query>']
    self._end = word_to_idx['<End>']

    # Initialize word vectors
    self.params['W_embed'] = np.random.randn(vocab_size, wordvec_dim)/100

    # Initialize parameters for the RNN
    dim_mul = {'lstm': 4, 'rnn': 1, 'gru': 3}[cell_type]
    self.params['Wx1'] = np.random.randn(wordvec_dim, dim_mul * hidden_dim)
    self.params['Wx1'] /= np.sqrt(wordvec_dim)
    self.params['Wh1'] = np.random.randn(hidden_dim, dim_mul * hidden_dim)
    self.params['Wh1'] /= np.sqrt(hidden_dim)
    self.params['b1'] = np.zeros(dim_mul * hidden_dim)
    self.params['Wx2'] = np.random.randn(wordvec_dim, dim_mul * hidden_dim)
    self.params['Wx2'] /= np.sqrt(wordvec_dim)
    self.params['Wh2'] = np.random.randn(hidden_dim, dim_mul * hidden_dim)
    self.params['Wh2'] /= np.sqrt(hidden_dim)
    self.params['b2'] = np.zeros(dim_mul * hidden_dim)
    
    # Initialize output to vocab weights
    self.params['W_vocab'] = np.random.randn(hidden_dim, vocab_size)
    self.params['W_vocab'] /= np.sqrt(hidden_dim)
    self.params['b_vocab'] = np.zeros(vocab_size)
      
    # Cast parameters to correct dtype
    for k, v in self.params.iteritems():
      self.params[k] = v.astype(self.dtype)


  def loss(self, stories, sample=False):
    """
    suports: (N,T)
        Each story has N support sentences(not fixed)
        Each supporting sentence has T words
    queries: (K,T)
        Each story has K questions(not fiexed)
        Each query has T words
    answers: (K,1)
        Each query has an answer as a word
    """

    T, T2, T3 = self.T, self.T2, self.T3

    loss, grads = 0.0, {}
    scores = np.zeros(stories.shape[0], dtype=np.int32)
    for p in self.params:
        grads[p] = 0.0
    for idx in xrange(stories.shape[0]):
      X = stories[idx,:T]# (1, T) stories
      X = np.asarray(np.split(X, 10))# (10, T) sentences, at most 10 sent per story

      # Word embedding
      W_embed = self.params['W_embed']

      # Input-to-hidden, hidden-to-hidden, and biases for the RNN
      Wx1, Wh1, b1 = self.params['Wx1'], self.params['Wh1'], self.params['b1']
      Wx2, Wh2, b2 = self.params['Wx2'], self.params['Wh2'], self.params['b2']
  
      # Weight and bias for the hidden-to-vocab transformation.
      W_vocab, b_vocab = self.params['W_vocab'], self.params['b_vocab']
    
      h0 = np.zeros((X.shape[0], Wh1.shape[0]))
  
      X_in, cache_we = word_embedding_forward(X, W_embed)
      func = rnn_forward
      if self.cell_type=='lstm':
          func = lstm_forward
      h, cache_fwd = func(X_in, h0, Wx1, Wh1, b1)
      h = h[:, -1, :] # get only the last output
  
      X2 = stories[idx, T:T+T3].reshape(1,T3) #get the query
      h0 = np.sum(h, axis=0).reshape(1,h.shape[1]) # sum all encodings of the support sentences

      X_in2, cache_we2 = word_embedding_forward(X2, W_embed)
      h, cache_fwd2 = func(X_in2, h0, Wx2, Wh2, b2)
  
      h = h[:, -1, :] # get only the last output
  
      _scores, cache_aff = affine_forward(h, W_vocab, b_vocab)
      if sample==True:
          scores[idx]=_scores.argmax()
          continue
      _loss, dscores = softmax_loss(_scores, stories[idx,-1])
      loss += _loss
  
      dscores, grads['W_vocab'], grads['b_vocab'] = affine_backward(dscores, cache_aff)
      func = rnn_backward
      if self.cell_type=='lstm':
          func = lstm_backward
      dX_in2, dh0, _Wx, _Wh, _b = func(dscores, cache_fwd2)
      grads['Wx2'] += _Wx
      grads['Wh2'] += _Wh
      grads['b2'] += _b
      grads['W_embed'] += word_embedding_backward(dX_in2, cache_we2)
  
      #since h_0 = h_1+h_2+...+h_n, where h_i is the representation for ith support sentence
      #dh_0/dh_i = 1
      dh0 = np.tile(dh0.mean(axis=0), (X.shape[0], 1))
  
      dX_in, dh0, _Wx, _Wh, _b = func(dh0, cache_fwd, T=X_in.shape[1])
      grads['Wx1'] += _Wx
      grads['Wh1'] += _Wh
      grads['b1'] += _b
      grads['W_embed'] += word_embedding_backward(dX_in, cache_we)
      W_embed = self.params['W_embed']
  
    if sample==True:
        return scores
    return loss, grads

