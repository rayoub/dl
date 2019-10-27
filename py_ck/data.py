
import tensorflow as tf

# constants
SPL_CNT = 3
SPR_CNT = 3
CI_CNT = 4
CIL_CNT = 3
CIR_CNT = 3
PP_CNT = 2

# one-hot lookup
MAP_AA_KEYS = ['A','B','C','D','E','F','G','H','J','I','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
MAP_AA_VALS = list(range(len(MAP_AA_KEYS)))
MAP_AA_VALS_CNT = len(MAP_AA_VALS)
MAP_AA_INIT = tf.lookup.KeyValueTensorInitializer(MAP_AA_KEYS, MAP_AA_VALS, value_dtype=tf.int64)
MAP_AA = tf.lookup.StaticHashTable(MAP_AA_INIT, -1)

def map_aa (elem):
    return tf.one_hot(MAP_AA.lookup(elem), MAP_AA_VALS_CNT, dtype=tf.float32)

def map_spl_train_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # spl data is at indices 3 and 4
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[3], sep=',')           , tf.strings.split(a[4], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, SPL_CNT])           , c[2]

    return d

def map_spr_train_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # spr data is at indices 5 and 6
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[5], sep=',')           , tf.strings.split(a[6], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, SPR_CNT])           , c[2]

    return d

def map_ci_train_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # ci data is at indices 7 and 8
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[7], sep=',')           , tf.strings.split(a[8], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, CI_CNT])            , c[2]

    return d

def map_cil_train_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # cil data is at indices 9 and 10
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[9], sep=',')           , tf.strings.split(a[10], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, CIL_CNT])           , c[2]

    return d

def map_cir_train_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # cir data is at indices 11 and 12
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[11], sep=',')           , tf.strings.split(a[12], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, CIR_CNT])           , c[2]

    return d

def map_test_ds (elem):

    a = tf.strings.split(elem, sep='|')
    # pp data is at indices 1 and 2
    b = tf.strings.split(a[0], sep=',')             , tf.strings.split(a[1], sep=',')           , tf.strings.split(a[2], sep=',')
    c = b[0]                                        , tf.strings.to_number(b[1], tf.float32)    , tf.strings.to_number(b[2], tf.float32)
    d = tf.map_fn(map_aa, c[0], dtype=tf.float32)   , tf.reshape(c[1], [-1, PP_CNT])            , c[2]
    return d

def get_spl_train_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).shuffle(buffer_size).map(map_spl_train_ds).padded_batch(
            batch_size, padded_shapes=([-1, MAP_AA_VALS_CNT], [-1, SPL_CNT], [-1]), drop_remainder=True)

def get_spr_train_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).shuffle(buffer_size).map(map_spr_train_ds).padded_batch(
            batch_size, padded_shapes=([-1, MAP_AA_VALS_CNT], [-1, SPR_CNT], [-1]), drop_remainder=True)

def get_ci_train_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).shuffle(buffer_size).map(map_ci_train_ds).padded_batch(
            batch_size, padded_shapes=([-1, MAP_AA_VALS_CNT], [-1, CI_CNT], [-1]), drop_remainder=True)

def get_cil_train_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).shuffle(buffer_size).map(map_cil_train_ds).padded_batch(
            batch_size, padded_shapes=([-1, MAP_AA_VALS_CNT], [-1, CIL_CNT], [-1]), drop_remainder=True)

def get_cir_train_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).shuffle(buffer_size).map(map_cir_train_ds).padded_batch(
            batch_size, padded_shapes=([-1, MAP_AA_VALS_CNT], [-1, CIR_CNT], [-1]), drop_remainder=True)
    
def get_test_data(file_name, buffer_size, batch_size):

    return tf.data.TextLineDataset([file_name]).map(map_test_ds).padded_batch(
            batch_size, padded_shapes=([-1, MAP_AA_VALS_CNT], [-1, PP_CNT], [-1]), drop_remainder=True)
    

