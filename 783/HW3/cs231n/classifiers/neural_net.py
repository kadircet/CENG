import numpy as np
import matplotlib.pyplot as plt
from cs231n.layers import *
from cs231n import optim
from cs231n.rnn_layers import sigmoid

class GANTrainer(object):
    def __init__(self, gen, disc, data, **kwargs):
        self.gen = gen
        self.disc = disc
        self.data = data

        self.update_rule = kwargs.pop('update_rule', 'adam')
        self.optim_config = kwargs.pop('optim_config', {})
        self.lr_decay = kwargs.pop('lr_decay', 1.)
        self.batch_size = kwargs.pop('batch_size', 100)
        self.num_epochs = kwargs.pop('num_epochs', 10)

        self.print_every = kwargs.pop('print_every', 10)
        self.verbose = kwargs.pop('verbose', True)

        self.update_rule = getattr(optim, self.update_rule)
        self._reset()

    def _reset(self):
        self.epoch = 0
        self.best_val_acc = 0
        self.best_params = {}
        self.loss_history = []
        self.train_acc_history = []
        self.val_acc_history = []

        self.optim_configs = [{},{}]
        for i in range(2):
            for p in self.disc.params:
                d = {k: v for k,v in self.optim_config.iteritems()}
                self.optim_configs[i][p] = d

    def _step(self):
        idx = np.random.choice(self.data.shape[0], self.batch_size)
        batch = self.data[idx]

        #train disc on real data
        y = np.ones((self.batch_size,1))
        loss, grads = self.disc.loss(batch, y)

        for p, w in self.disc.params.iteritems():
            dw = grads[p]
            config = self.optim_configs[0][p]
            nextw, nextc = self.update_rule(w, dw, config)
            self.disc.params[p] = nextw
            self.optim_configs[0][p] = nextc

        #train disc on fake data
        batch = self.gen.generate(np.random.uniform(0., 1., (self.batch_size,1)))
        y = np.zeros((self.batch_size,1))
        loss, grads = self.disc.loss(batch, y)

        for p, w in self.disc.params.iteritems():
            dw = grads[p]
            config = self.optim_configs[0][p]
            nextw, nextc = self.update_rule(w, dw, config)
            self.disc.params[p] = nextw
            self.optim_configs[0][p] = nextc

        #train generator
        batch = self.gen.generate(np.random.uniform(0., 1., (self.batch_size,1)))
        y = np.ones((self.batch_size,1))
        loss, grads = self.disc.loss(batch, y)
        success = self.disc.loss(batch)
        success = success[success>=0.5].shape[0]
        self.loss_history.append([batch.mean(), batch.std(), success])

        for p, w in self.gen.params.iteritems():
            dw = grads[p]
            config = self.optim_configs[1][p]
            nextw, nextc = self.update_rule(w, dw, config)
            self.gen.params[p] = nextw
            self.optim_configs[1][p] = nextc

    def train(self):
        num_train = self.data.shape[0]
        its_per_ep = max(1,num_train/self.batch_size)
        num_its = its_per_ep*self.num_epochs

        for t in xrange(num_its):
            self._step()

            if self.verbose and t%self.print_every==0:
                gbatch = self.gen.generate(np.random.uniform(0., 1., (self.batch_size,1)))
                scores = self.disc.disc(gbatch)
                passed = gbatch[scores>=0.5]
                print 'Iteration %d/%d, Discriminator(mean,std): (%f,%f), Generated: (%f,%f)' % (t, num_its, passed.mean(), passed.std(), gbatch.mean(), gbatch.std())

            epend = (t+1)%its_per_ep==0
            if epend:
                self.epoch+=1
                for j in range(2):
                    for k in self.optim_configs[j]:
                        self.optim_configs[j][k]['learning_rate'] *= self.lr_decay


class GenNet(object):
    def __init__(self, input_size, hidden_size, output_size, std=1e-4):
        self.params = {}
        self.params['W1'] = np.random.randn(input_size, hidden_size) *std
        self.params['b1'] = np.zeros(hidden_size)
        self.params['W2'] = np.random.randn(hidden_size, hidden_size) *std
        self.params['b2'] = np.zeros(hidden_size)
        self.params['W3'] = np.random.randn(hidden_size, output_size) *std
        self.params['b3'] = np.zeros(output_size)

    def loss(self, X, disc=None, **kwargs):
        # Unpack variables from the params dictionary
        W1, b1 = kwargs.pop('W1', self.params['W1']), kwargs.pop('b1', self.params['b1'])
        W2, b2 = kwargs.pop('W2', self.params['W2']), kwargs.pop('b2', self.params['b2'])
        W3, b3 = kwargs.pop('W3', self.params['W3']), kwargs.pop('b3', self.params['b3'])
        N, D = X.shape

        # Compute the forward pass
        Xa, cachea = affine_forward(X, W1, b1)
        Xr, cacher = relu_forward(Xa)
        Xa2, cachea2 = affine_forward(Xr, W2, b2)
        Xs, caches = sig_forward(Xa2)
        Xo, cacheo = affine_forward(Xs, W3, b3)
        
        # If the targets are not given then jump out, we're done
        if disc is None:
          return Xo

        # Compute the loss
        N = X.shape[0]
        y = np.ones((N,1)) #treat the labels as all 1's since we need to predict real data not fake
        loss, dout = disc.loss(Xo, y) #get discriminators loss and grads
        dout = dout['X']

        # Backward pass: compute gradients
        grads = {}
        dout, grads['W3'], grads['b3'] = affine_backward(dout, cacheo)
        dout = sig_backward(dout, caches)
        dout, grads['W2'], grads['b2'] = affine_backward(dout, cachea2)
        dout = relu_backward(dout, cacher)
        grads['X'], grads['W1'], grads['b1'] = affine_backward(dout, cachea)

        return loss, grads

    def generate(self, X):
        return self.loss(X)


class DiscNet(object):
    def __init__(self, input_size, hidden_size, output_size, std=1e-4):
        self.params = {}
        self.params['W1'] = np.random.randn(input_size, hidden_size) *std
        self.params['b1'] = np.zeros(hidden_size)
        self.params['W2'] = np.random.randn(hidden_size, hidden_size) *std
        self.params['b2'] = np.zeros(hidden_size)
        self.params['W3'] = np.random.randn(hidden_size, output_size) *std
        self.params['b3'] = np.zeros(output_size)

    def loss(self, X, y=None, **kwargs):
        # Unpack variables from the params dictionary
        W1, b1 = kwargs.pop('W1', self.params['W1']), kwargs.pop('b1', self.params['b1'])
        W2, b2 = kwargs.pop('W2', self.params['W2']), kwargs.pop('b2', self.params['b2'])
        W3, b3 = kwargs.pop('W3', self.params['W3']), kwargs.pop('b3', self.params['b3'])
        N, D = X.shape

        # Compute the forward pass
        Xa, cachea = affine_forward(X, W1, b1)
        Xr, cacher = relu_forward(Xa)
        Xa2, cachea2 = affine_forward(Xr, W2, b2)
        Xr2, cacher2 = relu_forward(Xa2)
        Xa3, cachea3 = affine_forward(Xr2, W3, b3)
        Xs, caches = sig_forward(Xa3)

        # If the targets are not given then jump out, we're done
        if y is None:
          return Xs

        # Compute the loss
        N = X.shape[0]
        loss, dout = crossentropy_loss(Xs, y) # calculate cross entropy loss, dout is derivative wrt Xs

        #loss = -np.mean(y*np.log(Xs)+(1.-y)*np.log(1.-Xs))

        grads = {}

        # Backward pass: compute gradients
        dout = sig_backward(dout, caches)
        dout, grads['W3'], grads['b3'] = affine_backward(dout, cachea3)
        dout = relu_backward(dout, cacher2)
        dout, grads['W2'], grads['b2'] = affine_backward(dout, cachea2)
        dout = relu_backward(dout, cacher)
        grads['X'], grads['W1'], grads['b1'] = affine_backward(dout, cachea)

        return loss, grads

    def disc(self, X):
        return self.loss(X)


