
import tensorflow as tf
import data
import models
import layers

# constants
BATCH_SIZE = 32
BUFFER_SIZE = 1000

# inputs
val_ds = data.get_test_data('../train/val_astral.txt', BUFFER_SIZE, BATCH_SIZE)

# model evaluation
m = models.get_model(BATCH_SIZE, data.CIL_CNT)
m.load_weights('./checkpoints/cil2')
m.add(layers.CilLayer())
m.evaluate(val_ds)



