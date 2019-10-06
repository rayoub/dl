
import tensorflow as tf
import data

# constants
BATCH_SIZE = 32
BUFFER_SIZE = 1000

# inputs
val_ds = data.get_data('../train/val_astral.txt', BUFFER_SIZE, BATCH_SIZE)

##########################################################
### model
##########################################################

model = tf.keras.models.load_model('sm')

##########################################################
### evaluate
##########################################################

# evaluate
model.evaluate(val_ds)


