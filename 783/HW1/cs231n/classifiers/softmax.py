import numpy as np
from random import shuffle

def softmax_loss_naive(W, X, y, reg):
  """
  Softmax loss function, naive implementation (with loops)
  Inputs:
  - W: C x D array of weights
  - X: D x N array of data. Data are D-dimensional columns
  - y: 1-dimensional array of length N with labels 0...K-1, for K classes
  - reg: (float) regularization strength
  Returns:
  a tuple of:
  - loss as single float
  - gradient with respect to weights W, an array of same size as W
  """
  # Initialize the loss and gradient to zero.
  loss = 0.0
  dW = np.zeros_like(W)

  #############################################################################
  # TODO: Compute the softmax loss and its gradient using explicit loops.     #
  # Store the loss in loss and the gradient in dW. If you are not careful     #
  # here, it is easy to run into numeric instability. Don't forget the        #
  # regularization!                                                           #
  #############################################################################
  C = W.shape[0]
  D = W.shape[1]
  N = X.shape[1]
  for i in xrange(N):
    scores = W.dot(X[:,i])
    scores -= np.max(scores)
    loss += -scores[y[i]] + np.log(np.sum(np.exp(scores)))
    scores = np.exp(scores)
    scores /= np.sum(scores)
    scores[y[i]] -= 1
    for j in xrange(C):
        dW[j,:] += scores[j] * X[:,i]
  loss/=N
  loss+=.5*reg*np.sum(W**2)
  dW/=N
  dW+=reg*W


  #############################################################################
  #                          END OF YOUR CODE                                 #
  #############################################################################

  return loss, dW


def softmax_loss_vectorized(W, X, y, reg):
  """
  Softmax loss function, vectorized version.

  Inputs and outputs are the same as softmax_loss_naive.
  """
  # Initialize the loss and gradient to zero.
  loss = 0.0
  dW = np.zeros_like(W)

  #############################################################################
  # TODO: Compute the softmax loss and its gradient using no explicit loops.  #
  # Store the loss in loss and the gradient in dW. If you are not careful     #
  # here, it is easy to run into numeric instability. Don't forget the        #
  # regularization!                                                           #
  #############################################################################
  C = W.shape[0]
  D = W.shape[1]
  N = X.shape[1]
  scores = W.dot(X)
  scores -= np.max(scores)
  scores = np.exp(scores)
  scores /= np.sum(scores, axis=0)

  loss = -np.sum(np.log(scores[y, np.arange(N)]))/N + .5*reg*np.sum(W**2)
  scores[y, np.arange(N)] -= 1
  dW = np.dot(scores,X.T)/N + reg*W
  #############################################################################
  #                          END OF YOUR CODE                                 #
  #############################################################################

  return loss, dW
