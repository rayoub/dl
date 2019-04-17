
import tensorflow as tf

# one-hot lookups

MAP_AA_KEYS = ['_','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
MAP_AA_VALS_CNT = len(MAP_AA_KEYS)
MAP_AA_INIT = tf.lookup.KeyValueTensorInitializer(MAP_AA_KEYS, list(range(len(MAP_AA_KEYS))), value_dtype=tf.int64)
MAP_AA = tf.lookup.StaticVocabularyTable(MAP_AA_INIT, 1)

MAP_SS_KEYS = ['_','G','H','I','B','E','C','S','T']
MAP_SS_VALS = [ 0 , 1 , 1 , 1 , 2 , 2 , 3 , 3 , 3 ]
MAP_SS_VALS_CNT = len(set(MAP_SS_VALS))
MAP_SS_INIT = tf.lookup.KeyValueTensorInitializer(MAP_SS_KEYS, MAP_SS_VALS, value_dtype=tf.int64)
MAP_SS = tf.lookup.StaticVocabularyTable(MAP_SS_INIT, 1)

# one-hot mapping functions

def map_aa_inner (elem):
    return tf.one_hot(MAP_AA.lookup(elem), MAP_AA_VALS_CNT, dtype=tf.int64)

def map_aa_outer (elem):
    return tf.map_fn(map_aa_inner, elem, dtype=tf.int64)

def map_ss_inner (elem):
    return tf.one_hot(MAP_SS.lookup(elem), MAP_SS_VALS_CNT, dtype=tf.int64)

def map_ss_outer (elem):
    return tf.map_fn(map_ss_inner, elem, dtype=tf.int64)

# if mapping before batching

def map_aa_ds (elem):

    a = tf.expand_dims(elem, 0)
    b = tf.strings.split(a, sep=',')
    c = tf.sparse.to_dense(b, default_value='_')
    d = tf.unstack(c, num=1)
    e = tf.map_fn(map_aa_inner, c, dtype=tf.int64)

    return e

def map_ss_ds (elem):

    a = tf.expand_dims(elem, 0)
    b = tf.strings.split(a, sep=',')
    c = tf.sparse.to_dense(b, default_value='_')
    d = tf.unstack(c, num=1)
    e = tf.map_fn(map_ss_inner, c, dtype=tf.int64)

    return e

# if mapping after batching

def map_aa_batch (elem):

    a = tf.strings.split(elem, sep=',')
    b = tf.sparse.to_dense(a, default_value='_')
    c = tf.map_fn(map_aa_outer, b, dtype=tf.int64)

    return c

def map_ss_batch (elem):

    a = tf.strings.split(elem, sep=',')
    b = tf.sparse.to_dense(a, default_value='_')
    c = tf.map_fn(map_ss_outer, b, dtype=tf.int64)

    return c

# running 


aa = tf.data.TextLineDataset(['aa.txt']).map(map_aa_ds)
ss = tf.data.TextLineDataset(['ss.txt']).map(map_ss_ds)
combo = aa.zip(ss).take(10).batch(2)





