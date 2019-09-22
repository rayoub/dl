
import tensorflow as tf
import tensorflow.keras.backend as K
        
# missing data and padding at the end is represented as zero vectors 
# assumption: if y_pred is missing then y_true is also missing
# y_pred will be missing only because of padding at the end
# y_true will be missing because residue coords are not present and for padding at the end

class CategoricalAccuracyWithMissingData(tf.keras.metrics.Metric):

    def __init__(self, name='categorical_accuracy_with_missing_data', **kwargs):
        super(CategoricalAccuracyWithMissingData, self).__init__(name=name, **kwargs)
      
        zeros_init = tf.zeros_initializer()

        # mean numerator
        self.total = tf.Variable(name='total', initial_value=zeros_init(shape=(), dtype=tf.float32), trainable=False)
       
        # mean denominator
        self.count = tf.Variable(name='count', initial_value=zeros_init(shape=(), dtype=tf.float32), trainable=False)

    def update_state(self, y_true, y_pred, sample_weight=None):
        
        # missing data is represented as y_true zero vectors 
        mask = tf.math.logical_not(K.all(tf.math.equal(y_true, tf.zeros_like(y_true)), axis=-1))
        y_true = tf.boolean_mask(y_true, mask)
        y_pred = tf.boolean_mask(y_pred, mask)
        self.total.assign_add(tf.reduce_sum(tf.keras.metrics.categorical_accuracy(y_true, y_pred)))
       
        # only count instances with data present by maxing and summing one-hot vectors
        self.count.assign_add(tf.cast(tf.reduce_sum(tf.reduce_max(y_true, axis=-1)), dtype=tf.float32))

    def result(self):
        return tf.math.divide_no_nan(self.total, self.count)

    def reset_states(self):
        self.count.assign(0.0)
        self.total.assign(0.0)


