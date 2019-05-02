
# TODO
# 1. early stopping
# 2. missing data
# 3. different sizes (done)
# 4. possibly embedding
# 5. from_logits code
# 6. saving a trained model
# 7. identify amino acids (done)
# 8. bidirectional

import tensorflow as tf

# *******************************************
# *** INPUT PIPELINE ***
# *******************************************

BATCH_SIZE = 32
VAL_SIZE = 512
BUFFER_SIZE = 20000

# one-hot lookups
MAP_AA_KEYS = ['_','A','B','C','D','E','F','G','H','I','K','L','M','N','P','Q','R','S','T','V','W','X','Y','Z']
MAP_AA_VALS = list(range(len(MAP_AA_KEYS)))
MAP_AA_VALS_CNT = len(MAP_AA_VALS)
MAP_AA_INIT = tf.lookup.KeyValueTensorInitializer(MAP_AA_KEYS, MAP_AA_VALS, value_dtype=tf.int64)
MAP_AA = tf.lookup.StaticVocabularyTable(MAP_AA_INIT, 1)

MAP_SS_KEYS = ['_','G','H','I','B','E','C','S','T']
MAP_SS_VALS = [ 0 , 1 , 1 , 1 , 2 , 2 , 3 , 3 , 3 ]
MAP_SS_VALS_CNT = len(set(MAP_SS_VALS))
MAP_SS_INIT = tf.lookup.KeyValueTensorInitializer(MAP_SS_KEYS, MAP_SS_VALS, value_dtype=tf.int64)
MAP_SS = tf.lookup.StaticVocabularyTable(MAP_SS_INIT, 1)

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
ds = tf.data.TextLineDataset(['aa_ss.txt'])
val_ds = ds.take(VAL_SIZE).batch(BATCH_SIZE, drop_remainder=False).map(map_aa_ss_batch)
train_ds = ds.skip(VAL_SIZE).shuffle(BUFFER_SIZE).batch(BATCH_SIZE, drop_remainder=True).map(map_aa_ss_batch)
#ds = tf.data.TextLineDataset(['aa_ss.txt']).shuffle(10000).batch(BATCH_SIZE, drop_remainder=True).map(map_aa_ss_batch)


# *******************************************
# *** MODEL DEFINITION ***
# *******************************************

# define loss function
def loss(labels, logits):
    return tf.losses.categorical_crossentropy(labels, logits, from_logits=True) 

# define the model
model = tf.keras.Sequential([
    tf.keras.layers.Dense(128, batch_input_shape=(BATCH_SIZE, None, MAP_AA_VALS_CNT)),
    tf.keras.layers.LSTM(200, return_sequences=True, stateful=True, recurrent_initializer='glorot_uniform'),
    tf.keras.layers.Dense(MAP_SS_VALS_CNT)
])

# compile the model
model.compile(optimizer='adam', loss=loss)

# *******************************************
# *** TRAINING LOOP ***
# *******************************************

# model.fit(train_ds, epochs=8, validation_data=val_ds)






