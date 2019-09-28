
# TODO
# 1. saving model
# 2. loading model
# 3. optimization 
# 4. regularization

import tensorflow as tf
import data
import metrics

# constants
BATCH_SIZE = 16
BUFFER_SIZE = 1000
MAX_EPOCHS = 3

# inputs
train_ds = data.get_data('train.txt', BUFFER_SIZE, BATCH_SIZE)
val_ds = data.get_data('val_CASP11.txt', BUFFER_SIZE, BATCH_SIZE)
test_ds = data.get_data('test_CASP11.txt', BUFFER_SIZE, BATCH_SIZE)

##########################################################
### model
##########################################################

def get_model():

    # define model
    m = tf.keras.Sequential([
        tf.keras.layers.Masking(0.0, batch_input_shape=(BATCH_SIZE, None, data.MAP_AA_VALS_CNT)),
        tf.keras.layers.Dense(128),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(256, return_sequences=True, stateful=False, recurrent_initializer='glorot_uniform')),
        tf.keras.layers.Dense(data.MAP_SS_VALS_CNT)
    ])

    # compile model
    m.compile(optimizer='adam', 
            loss=tf.keras.losses.CategoricalCrossentropy(from_logits=True),
            metrics=[metrics.CategoricalAccuracyWithMissingData()])

    return m

model = get_model()

##########################################################
### train
##########################################################

# callbacks
callbacks = [
        # early stopping
        tf.keras.callbacks.EarlyStopping(monitor='val_loss', min_delta=0.001, patience=4, verbose=0, restore_best_weights=True)
    ]

# train
model.fit(train_ds, epochs=MAX_EPOCHS, validation_data=val_ds, callbacks=callbacks)






