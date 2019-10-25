
import tensorflow as tf
import data
import metrics

def get_sp_model(BATCH_SIZE):

    # define model
    m = tf.keras.Sequential([
        tf.keras.layers.Masking(0.0, batch_input_shape=(BATCH_SIZE, None, data.MAP_AA_VALS_CNT)),
        tf.keras.layers.Dense(128),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(256, return_sequences=True, stateful=False, recurrent_initializer='glorot_uniform')),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(128, return_sequences=True, stateful=False, recurrent_initializer='glorot_uniform')),
        tf.keras.layers.Dense(data.SP_CNT)
    ])

    # compile model
    m.compile(optimizer='adam',
            sample_weight_mode='temporal',
            loss=tf.keras.losses.MeanSquaredError(),
            weighted_metrics=[tf.keras.metrics.MeanSquaredError(), metrics.PpMeanSquaredError()])

    return m

def get_ci_model(BATCH_SIZE):

    # define model
    m = tf.keras.Sequential([
        tf.keras.layers.Masking(0.0, batch_input_shape=(BATCH_SIZE, None, data.MAP_AA_VALS_CNT)),
        tf.keras.layers.Dense(128),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(256, return_sequences=True, stateful=False, recurrent_initializer='glorot_uniform')),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(128, return_sequences=True, stateful=False, recurrent_initializer='glorot_uniform')),
        tf.keras.layers.Dense(data.CI_CNT)
    ])

    # compile model
    m.compile(optimizer='adam',
            sample_weight_mode='temporal',
            loss=tf.keras.losses.MeanSquaredError(),
            weighted_metrics=[tf.keras.metrics.MeanSquaredError(), metrics.PpMeanSquaredError()])

    return m


