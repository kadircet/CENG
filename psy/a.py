import numpy as np
import matplotlib.pyplot as plt

data = np.array((4.4,2.2,2.2))
data = np.array((3.2,.6,1.))
data = np.array((3.2,1.4,2.2))
x = np.arange(3)
fig, ax = plt.subplots()
ax.bar(x, data*100./7)
ax.set_ylabel('Words Recalled(%)')
ax.set_xlabel('Position in sequence')
ax.set_xticks(x)
ax.set_xticklabels(('Primacy', 'Intermediate', 'Recency'))

plt.show()


