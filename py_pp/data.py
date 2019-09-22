
import tensorflow as tf

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

# for mapping after batching TODO: needs to be update for tf 2.0 rc
def map_batch (elem):

    a = tf.strings.split(elem, sep='|')
    b = tf.sparse.to_dense(a, default_value='')
    c = tf.map_fn(map_batch_part_2, b, dtype=(tf.float32, tf.float32))

    return c

def map_batch_part_2 (elem):

    a = tf.expand_dims(elem[0], 0)                      , tf.expand_dims(elem[1], 0)
    b = tf.strings.split(a[0], sep=',')                 , tf.strings.split(a[1], sep=',')
    c = tf.sparse.to_dense(b[0], default_value='')      , tf.sparse.to_dense(b[1], default_value='')
    d = tf.squeeze(c[0])                                , tf.squeeze(c[1])
    e = tf.map_fn(map_aa, d[0], dtype=tf.float32)       , tf.map_fn(map_ss, d[1], dtype=tf.float32)
    
    return e

# for mapping before batching
def map_ds (elem):

    a = tf.strings.split(elem, sep='|')
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(b[1], sep=',')
    c = tf.map_fn(map_aa, b[0], dtype=tf.float32)   , tf.map_fn(map_ss, b[1], dtype=tf.float32)

    return c

def get_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).shuffle(buffer_size).map(map_ds).padded_batch(
            batch_size, padded_shapes=([-1, MAP_AA_VALS_CNT], [-1, MAP_SS_VALS_CNT]), drop_remainder=True)

