
import tensorflow as tf

# one-hot lookups

MAP_AA_KEYS = ['_','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
MAP_AA_INIT = tf.lookup.KeyValueTensorInitializer(MAP_AA_KEYS, list(range(len(MAP_AA_KEYS))), value_dtype=tf.int64)
MAP_AA = tf.lookup.StaticVocabularyTable(MAP_AA_INIT, 1)

MAP_SS_KEYS = ['_','G','H','I','B','E','C','S','T']
MAP_SS_VALS = [ 0 , 1 , 1 , 1 , 2 , 2 , 3 , 3 , 3 ]
MAP_SS_INIT = tf.lookup.KeyValueTensorInitializer(MAP_SS_KEYS, MAP_SS_VALS, value_dtype=tf.int64)
MAP_SS = tf.lookup.StaticVocabularyTable(MAP_SS_INIT, 1)

# one-hot mapping functions

def map_aa_inner (elem):
    return tf.one_hot(MAP_AA.lookup(elem), len(MAP_AA_KEYS), dtype=tf.int64)

def map_aa_outer (elem):
    return tf.map_fn(map_aa_inner, elem, dtype=tf.int64)

def map_ss_inner (elem):
    return tf.one_hot(MAP_SS.lookup(elem), len(MAP_SS_KEYS), dtype=tf.int64)

def map_ss_outer (elem):
    return tf.map_fn(map_ss_inner, elem, dtype=tf.int64)

# mapping batches

def map_batch_v1 (batch):

    # sequence with no labels 
    a = tf.strings.split(batch, sep=',')
    b = tf.sparse.to_dense(a, default_value='')
    c = tf.map_fn(map_aa_outer, b, dtype=tf.int64)

    return c

def map_batch_v2 (batch):

    # sequence with labels
    a = tf.strings.split(batch, sep='|')
    b = tf.sparse.to_dense(a, default_value='')

    return b

ds = tf.data.TextLineDataset(['aa_ss.txt']).take(10).batch(2).map(map_batch_v2)





