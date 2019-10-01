
# TODO
# 1. saving model
# 2. loading model
# 3. optimization 
# 4. regularization

import tensorflow as tf
import data

# constants
BATCH_SIZE = 32
BUFFER_SIZE = 1000
MAX_EPOCHS = 3

# inputs
train_ds = data.get_data('../train/train_astral.txt', BUFFER_SIZE, BATCH_SIZE)
val_ds = data.get_data('../train/val_astral.txt', BUFFER_SIZE, BATCH_SIZE)

##########################################################
### model
##########################################################

def get_model():

    # define model
    m = tf.keras.Sequential([
        tf.keras.layers.Masking(0.0, batch_input_shape=(BATCH_SIZE, None, data.MAP_AA_VALS_CNT)),
        tf.keras.layers.Dense(128),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(256, return_sequences=True, stateful=False, recurrent_initializer='glorot_uniform')),
        tf.keras.layers.Dense(data.COORD_CNT)  
    ])

    # compile model
    m.compile(optimizer='adam',
            sample_weight_mode='temporal',
            loss='mse')

    return m

model = get_model()

##########################################################
### train
##########################################################

# train
model.fit(train_ds, validation_data=val_ds, epochs=MAX_EPOCHS)






