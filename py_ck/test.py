
import tensorflow as tf
import data
import layers

# constants
BATCH_SIZE = 32
BUFFER_SIZE = 1000

# inputs
val_ds = data.get_test_data('../train/val_astral.txt', BUFFER_SIZE, BATCH_SIZE)

##########################################################
### model
##########################################################

model = tf.keras.models.load_model('sm')
model.add(layers.TorsionLayer())

##########################################################
### evaluate
##########################################################

# evaluate
result = model.evaluate(val_ds)


