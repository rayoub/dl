
import tensorflow as tf
import data
import model
import layers

# constants
BATCH_SIZE = 32
BUFFER_SIZE = 1000

# inputs
val_ds = data.get_test_data('../train/val_astral.txt', BUFFER_SIZE, BATCH_SIZE)

# model evaluation
m = model.get_model(BATCH_SIZE)
m.load_weights('./checkpoints/e10')
m.add(layers.TorsionLayer())
m.evaluate(val_ds)



