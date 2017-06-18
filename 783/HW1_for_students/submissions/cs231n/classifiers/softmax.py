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
  (C, D) = W.shape
  N = X.shape[1]
  for i in xrange(0, N):
    scores = W.dot(X[:, i])
    scores -= np.max(scores)
    loss -= scores[y[i]]
    sum_exp = 0.0
    for s in scores:
      sum_exp += np.exp(s)
    for j in xrange(0, C):
      dW[j, :] += 1.0 / sum_exp * np.exp(scores[j]) * X[:, i]
      if j == y[i]:
        dW[j, :] -= X[:, i]
    loss += np.log(sum_exp)
  
  loss /= N
  dW /= N
  loss += 0.5 * reg * np.sum(W * W)
  dW += reg * W
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
  K = W.shape[0]
  N = X.shape[1]
  D = W.shape[1]
  scores = np.dot(W, X)
  scores -= np.max(scores)
  y_mat = np.zeros(shape = (K, N))
  y_mat[y, range(N)] = 1
  
  correct_wx = np.multiply(y_mat, scores)
  
  sums_wy = np.sum(correct_wx, axis=0)
  exp_scores = np.exp(scores)
  sums_exp = np.sum(exp_scores, axis=0)
  result = np.log(sums_exp)
  result -= sums_wy
  loss = np.sum(result)
  
  loss /= float(N)
  
  loss += 0.5 * reg * np.sum(W * W)
  sum_exp_scores = np.sum(exp_scores, axis=0)
  sum_exp_scores = 1.0 / sum_exp_scores
  dW = exp_scores * sum_exp_scores
  dW = np.dot(dW, X.T)
  dW -= np.dot(y_mat, X.T)
  dW /= float(N)
  
  dW += reg * W
  #############################################################################
  #                          END OF YOUR CODE                                 #
  #############################################################################

  return loss, dW
