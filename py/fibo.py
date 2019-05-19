
# TODO
# 1. saving model
# 2. loading model
# 3. padding
# 4. optimization 
# 5. regularization
# 6. validation data

import tensorflow as tf
import tensorflow.keras.backend as K
import metrics

# *******************************************
# *** INPUT PIPELINE ***
# *******************************************

BATCH_SIZE = 32
TIME_STEPS = 200
VAL_SIZE = 512
BUFFER_SIZE = 20000
MAX_EPOCHS = 40

# one-hot lookups
MAP_AA_KEYS = ['A','B','C','D','E','F','G','H','J','I','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
MAP_AA_VALS = list(range(len(MAP_AA_KEYS)))
MAP_AA_VALS_CNT = len(MAP_AA_VALS)
MAP_AA_INIT = tf.lookup.KeyValueTensorInitializer(MAP_AA_KEYS, MAP_AA_VALS, value_dtype=tf.int64)
MAP_AA = tf.lookup.StaticHashTable(MAP_AA_INIT, -1)

MAP_SS_KEYS = ['G','H','I','B','E','C','S','T']
MAP_SS_VALS = [ 0 , 0 , 0 , 1 , 1 , 2 , 2 , 2 ]
MAP_SS_VALS_CNT = len(set(MAP_SS_VALS))
MAP_SS_INIT = tf.lookup.KeyValueTensorInitializer(MAP_SS_KEYS, MAP_SS_VALS, value_dtype=tf.int64)
MAP_SS = tf.lookup.StaticHashTable(MAP_SS_INIT, -1)

# mapping functions
def map_aa (elem):
    return tf.one_hot(MAP_AA.lookup(elem), MAP_AA_VALS_CNT, dtype=tf.float32)

def map_ss (elem):
    return tf.one_hot(MAP_SS.lookup(elem), MAP_SS_VALS_CNT, dtype=tf.float32)

def map_aa_ss (elem):

    a = tf.expand_dims(elem[0], 0)                      , tf.expand_dims(elem[1], 0)
    b = tf.strings.split(a[0], sep=',')                 , tf.strings.split(a[1], sep=',')
    c = tf.sparse.to_dense(b[0], default_value='')      , tf.sparse.to_dense(b[1], default_value='')
    d = tf.squeeze(c[0])                                , tf.squeeze(c[1])
    e = tf.map_fn(map_aa, d[0], dtype=tf.float32)       , tf.map_fn(map_ss, d[1], dtype=tf.float32)
    
    return e

def map_aa_ss_batch (elem):

    a = tf.strings.split(elem, sep='|')
    b = tf.sparse.to_dense(a, default_value='')
    c = tf.map_fn(map_aa_ss, b, dtype=(tf.float32, tf.float32))

    return c

# run input pipeline
ds = tf.data.TextLineDataset(['train.txt'])
val_ds = ds.take(VAL_SIZE).batch(BATCH_SIZE, drop_remainder=False).map(map_aa_ss_batch)
test_ds = ds.skip(VAL_SIZE).take(VAL_SIZE).batch(BATCH_SIZE, drop_remainder=False).map(map_aa_ss_batch)
train_ds = ds.skip(2 * VAL_SIZE).shuffle(BUFFER_SIZE).batch(BATCH_SIZE, drop_remainder=True).map(map_aa_ss_batch)

# *******************************************
# *** MODEL DEFINITION ***
# *******************************************

# define the model
model = tf.keras.Sequential([
    tf.keras.layers.Dense(128, batch_input_shape=(BATCH_SIZE, None, MAP_AA_VALS_CNT)),
    tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(256, return_sequences=True, stateful=False, recurrent_initializer='glorot_uniform')),
    tf.keras.layers.Dense(MAP_SS_VALS_CNT)
])

# define loss function
def loss(labels, logits):

    mask = tf.math.logical_not(K.all(tf.math.equal(labels, tf.zeros_like(labels)), axis=-1))
    labels = tf.boolean_mask(labels, mask)
    logits = tf.boolean_mask(logits, mask)
    
    return tf.keras.losses.categorical_crossentropy(labels, logits, from_logits=True)

# compile the model
model.compile(optimizer='adam', 
        loss=loss, 
        metrics=[metrics.CategoricalAccuracyWithMissingData(BATCH_SIZE,TIME_STEPS)])

# *******************************************
# *** TRAINING LOOP ***
# *******************************************

callbacks = [
        # early stopping
        tf.keras.callbacks.EarlyStopping(monitor='val_loss', min_delta=0.001, patience=4, verbose=0, restore_best_weights=True)
    ]

model.fit(train_ds, epochs=MAX_EPOCHS, validation_data=val_ds, callbacks=callbacks)


# to look at prediction 
# p = tf.math.argmax(model.predict(test_ds), axis=-1)




