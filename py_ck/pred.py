
import tensorflow as tf
import numpy as np
import data
import models
import layers

# constants
BATCH_SIZE = 10
BUFFER_SIZE = 1000

# inputs
val_ds = data.get_test_data('../train/val_astral.txt', BUFFER_SIZE, BATCH_SIZE).take(1)

# model evaluation
m = models.get_model(BATCH_SIZE, data.CI_CNT)
m.load_weights('./checkpoints/ci1')
m.add(layers.CiLayer())

# make predictions on the batch
preds = m.predict_on_batch(val_ds).reshape((-1,2))

# gather symbols and weights
for (symbols, values, weights) in val_ds:
    symbols = tf.reshape(symbols, (-1, data.MAP_AA_VALS_CNT))
    symbols = tf.argmax(symbols, axis=-1)
    symbols = tf.map_fn(data.map_aa_reverse, symbols, dtype='string')
    values = tf.reshape(values, (-1,2))
    weights = tf.reshape(weights, (-1,))

# get numpy arrays
symbols = symbols.numpy()
values = values.numpy()
weights = weights.numpy()

# filter arrays
symbols = symbols[weights == 1.0]
values = values[weights == 1.0]
preds = preds[weights == 1.0]

# save to files
np.savetxt('symbols.txt', symbols, fmt='%s', delimiter=',')
np.savetxt('values.txt', values, fmt='%5.2f', delimiter=',')
np.savetxt('preds.txt', preds, fmt='%5.2f', delimiter=',')



