
import tensorflow as tf
import tensorflow.keras.backend as K


def adj_mean_squared_error (y_true, y_pred):

    # squared differences
    diffs = tf.math.squared_difference(y_true, y_pred)

    # rectified squared differences 
    diffs = K.relu(diffs, threshold = 0.01)

    # mean of rectified squared differences
    return K.mean(diffs, axis=-1)


