
import math
import tensorflow as tf
from tensorflow.keras import layers

# circle coordinates
class CiLayer(layers.Layer):

    def __init__(self):
        super(CiLayer, self).__init__()
        self.pi = tf.constant(math.pi, dtype='float32')
        self.factor = tf.constant(180.0 / self.pi, dtype='float32')

    def call(self, inputs):
       return tf.map_fn(self.map_outer, inputs)

    def map_outer(self, elem):
        return tf.map_fn(self.map_inner, elem)

    def map_inner(self, elem):

        n = tf.math.sqrt(tf.math.add(tf.math.square(elem[0]), tf.math.square(elem[1])))
        phi_x = tf.math.divide(elem[0], n)
        phi_y = tf.math.divide(elem[1], n)
        phi = tf.math.acos(phi_x)
        phi = tf.cond(phi_y < 0, lambda: tf.negative(phi), lambda: phi)

        n = tf.math.sqrt(tf.math.add(tf.math.square(elem[2]), tf.math.square(elem[3])))
        psi_x = tf.math.divide(elem[2], n)
        psi_y = tf.math.divide(elem[3], n)
        psi = tf.math.acos(psi_x)
        psi = tf.cond(psi_y < 0, lambda: tf.negative(psi), lambda: psi)
        
        return tf.stack([tf.multiply(phi, self.factor), tf.multiply(psi, self.factor)])

