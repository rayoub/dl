
import tensorflow as tf
import data
import layers

# constants
BATCH_SIZE = 2
BUFFER_SIZE = 1000

# inputs
val_ds = data.get_data('../train/val_astral.txt', BUFFER_SIZE, BATCH_SIZE).take(1)

##########################################################
### model
##########################################################

model = tf.keras.models.load_model('sm')
model.add(layers.TorsionLayer())

##########################################################
### evaluate
##########################################################

# evaluate
result = model.predict(val_ds)


