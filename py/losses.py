import tensorflow as tf
import tensorflow.keras.backend as K

# missing data and padding at the end is represented as zero vectors 
# assumption: if y_pred is missing then y_true is also missing
# y_pred will be missing only because of padding at the end
# y_true will be missing because residue coords are not present and for padding at the end

def categorical_crossentropy_with_missing_data(y_true, y_pred):

    mask = tf.math.logical_not(K.all(tf.math.equal(y_true, tf.zeros_like(y_true)), axis=-1))
    y_true = tf.boolean_mask(y_true, mask)
    y_pred = tf.boolean_mask(y_pred, mask)
    
    return tf.keras.losses.categorical_crossentropy(y_true, y_pred, from_logits=True)
