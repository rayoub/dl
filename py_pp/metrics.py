
import tensorflow as tf
import tensorflow.keras.backend as K
        
class PpMeanAbsoluteError(tf.keras.metrics.Metric):

    def __init__(self, name='pp_mean_absolute_error', **kwargs):
        super(PpMeanAbsoluteError, self).__init__(name=name, **kwargs)
      
        zeros_init = tf.zeros_initializer()

        # mean numerator
        self.total = tf.Variable(name='total', initial_value=zeros_init(shape=(), dtype=tf.float32), trainable=False)
       
        # mean denominator
        self.count = tf.Variable(name='count', initial_value=zeros_init(shape=(), dtype=tf.float32), trainable=False)

    def update_state(self, y_true, y_pred, sample_weight):

        # absolute differences
        vals = tf.math.abs(tf.math.subtract(y_true, y_pred))

        # mean absolute differences
        vals = K.mean(tf.where(tf.math.greater(vals, 180.0), tf.math.subtract(360.0, vals), vals), axis=-1)

        # multiply differences by weights
        vals = tf.math.multiply(vals, sample_weight)

        # sum of differences
        self.total.assign_add(tf.reduce_sum(vals))
       
        # count of differences 
        self.count.assign_add(tf.reduce_sum(sample_weight))

    def result(self):
        return tf.math.divide_no_nan(self.total, self.count)

    def reset_states(self):
        self.count.assign(0.0)
        self.total.assign(0.0)

