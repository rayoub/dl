
import tensorflow as tf

# constants
PP_CNT = 2

# one-hot lookup
MAP_AA_KEYS = ['A','B','C','D','E','F','G','H','J','I','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
MAP_AA_VALS = list(range(len(MAP_AA_KEYS)))
MAP_AA_VALS_CNT = len(MAP_AA_VALS)
MAP_AA_INIT = tf.lookup.KeyValueTensorInitializer(MAP_AA_KEYS, MAP_AA_VALS, value_dtype=tf.int64)
MAP_AA = tf.lookup.StaticHashTable(MAP_AA_INIT, -1)

# mapping elements to one-hot lookup
def map_aa (elem):
    return tf.one_hot(MAP_AA.lookup(elem), MAP_AA_VALS_CNT, dtype=tf.float32)

def map_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # pp data is at indices 1 and 2
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[1], sep=',')           , tf.strings.split(a[2], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, PP_CNT])       , c[2]

    return d

def get_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).shuffle(buffer_size).map(map_ds).padded_batch(
            batch_size, padded_shapes=([-1, MAP_AA_VALS_CNT], [-1, PP_CNT], [-1]), drop_remainder=True)

