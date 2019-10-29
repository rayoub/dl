
import tensorflow as tf
import data
import metrics

def get_model(BATCH_SIZE, OUTPUT_SIZE):

    # build model
    m = tf.keras.Sequential([
        tf.keras.layers.Masking(0.0, batch_input_shape=(BATCH_SIZE, None, data.MAP_AA_VALS_CNT)),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(256, return_sequences=True, stateful=False, recurrent_initializer='glorot_uniform')),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(256, return_sequences=True, stateful=False, recurrent_initializer='glorot_uniform')),
        tf.keras.layers.Dense(128),
        tf.keras.layers.Dense(OUTPUT_SIZE)
    ])

    # compile model
    m.compile(optimizer='adam',
            sample_weight_mode='temporal',
            loss=tf.keras.losses.MeanSquaredError(),
            weighted_metrics=[metrics.RdMeanSquaredError(), metrics.PpMeanAbsoluteError()])

    return m
