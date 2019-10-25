
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
m = models.get_sp_model(BATCH_SIZE)
m.load_weights('./checkpoints/sp1')
m.add(layers.SpLayer())
m.evaluate(val_ds)



