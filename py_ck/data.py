
import tensorflow as tf

# constants
FT_CNT = 19
PP_CNT = 2
CI_CNT = 4

# one-hot lookup
MAP_AA_KEYS = ['A','C','D','E','F','G','H','I','K','L','M','N','P','Q','R','S','T','V','W','Y']
MAP_AA_VALS = [
    '0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.8',
    '0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,2.5',
    '1.0,0.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,-3.5',
    '1.0,0.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,-3.5',
    '0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,2.8',
    '0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,-0.4',
    '1.0,0.0,0.0,1.0,1.0,0.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,-3.2',
    '0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,4.5',
    '1.0,0.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,-3.9',
    '0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,3.8',
    '0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,1.9',
    '1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,-3.5',
    '0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,-1.6',
    '1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,-3.5',
    '1.0,0.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,-4.5',
    '1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,-0.8',
    '1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,-0.7',
    '0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,4.2',
    '0.0,1.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,-0.9',
    '1.0,0.0,0.0,1.0,0.0,1.0,1.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,-1.3'
];
MAP_AA_INIT = tf.lookup.KeyValueTensorInitializer(MAP_AA_KEYS, MAP_AA_VALS)
MAP_AA_DEFAULT = ','.join(str(x) for x in [0.0] * FT_CNT)
MAP_AA = tf.lookup.StaticHashTable(MAP_AA_INIT, MAP_AA_DEFAULT)

def map_aa (elem):
    return tf.strings.to_number(tf.strings.split(MAP_AA.lookup(elem), sep=','), out_type=tf.float32)

def map_pp_train_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # pp data is at indices 1 and 2
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[1], sep=',')           , tf.strings.split(a[2], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, PP_CNT])            , c[2]

    return d

def map_ci_train_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # ci data is at indices 3 and 4
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[3], sep=',')           , tf.strings.split(a[4], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, CI_CNT])            , c[2]

    return d

def map_test_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # pp data is at indices 1 and 2
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[1], sep=',')           , tf.strings.split(a[2], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, PP_CNT])            , c[2]

    return d

def get_pp_train_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).shuffle(buffer_size).map(map_pp_train_ds).padded_batch(
            batch_size, padded_shapes=([-1, FT_CNT], [-1, PP_CNT], [-1]), drop_remainder=True)

def get_ci_train_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).shuffle(buffer_size).map(map_ci_train_ds).padded_batch(
            batch_size, padded_shapes=([-1, FT_CNT], [-1, CI_CNT], [-1]), drop_remainder=True)

def get_test_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).map(map_test_ds).padded_batch(
            batch_size, padded_shapes=([-1, FT_CNT], [-1, PP_CNT], [-1]), drop_remainder=True)
    

