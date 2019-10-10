
import math
import tensorflow as tf
from tensorflow.keras import layers

class TorsionLayer(layers.Layer):

    def __init__(self):
        super(TorsionLayer, self).__init__()
        self.pi = tf.constant(math.pi, dtype='float32')
        self.factor = tf.constant(180.0 / self.pi, dtype='float32')

    def call(self, inputs):
       return tf.map_fn(self.map_outer, inputs)

    def map_outer(self, elem):
        return tf.map_fn(self.map_inner, elem)

    def map_inner(self, elem):
        x = elem[0]
        y = elem[1]
        z = elem[2]
        phi = tf.math.acos(z)
        rat = tf.divide(y, tf.sin(phi))
        rat = tf.minimum(1.0, tf.maximum(-1.0, rat))
        psi = tf.asin(rat)
        psi = tf.cond(x < 0, lambda: tf.cond(y < 0, lambda: tf.subtract(tf.negative(self.pi), psi), lambda: tf.subtract(self.pi, psi)), lambda: psi)
        return tf.stack([-tf.multiply(phi, self.factor), tf.multiply(psi, self.factor)])






