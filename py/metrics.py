
import tensorflow as tf

class CategoricalAccuracyWithMissingData(tf.keras.metrics.Metric):

    def __init__(self, name='categorical_accuracy_with_missing_data', **kwargs):
        super(CategoricalAccuracyWithMissingData, self).__init__(name=name, **kwargs)
        
        zeros_init = tf.zeros_initializer()

        # mean numerator
        self.total = tf.Variable(name='total', initial_value=zeros_init(shape=(), dtype=tf.float32), trainable=False)
       
        # mean denominator
        self.count = tf.Variable(name='count', initial_value=zeros_init( shape=(), dtype=tf.float32), trainable=False)

    def update_state(self, y_true, y_pred, sample_weight=None):
        # TODO figure out the missing data part here
        # it will present either as an all zero vector
        #
        # for total we need to figure out how to get rid of argmax problem not returning -1
        self.total.assign_add(tf.reduce_sum(tf.keras.metrics.categorical_accuracy(y_true, y_pred)))

        # for count we should figure out how to filter out elements that are not all zero to count proper size
        self.count.assign_add(tf.divide(tf.cast(tf.size(y_pred), dtype=tf.float32), tf.cast(tf.shape(y_pred)[-1], dtype=tf.float32)))

    def result(self):
        return tf.math.divide_no_nan(self.total, self.count)

    def reset_states(self):
        self.count.assign(0.0)
        self.total.assign(0.0)


