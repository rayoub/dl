
import math
import tensorflow as tf
from tensorflow.keras import layers

# learning circles
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

# spherical coordinates with phi always negated 
#class TorsionLayer(layers.Layer):
#
#    def __init__(self):
#        super(TorsionLayer, self).__init__()
#        self.pi = tf.constant(math.pi, dtype='float32')
#        self.factor = tf.constant(180.0 / self.pi, dtype='float32')
#
#    def call(self, inputs):
#       return tf.map_fn(self.map_outer, inputs)
#
#    def map_outer(self, elem):
#        return tf.map_fn(self.map_inner, elem)
#
#    def map_inner(self, elem):
#
#        # get phi from z
#        n = tf.norm(elem)
#        z = tf.math.divide(elem[2], n)
#        phi = tf.math.acos(z)
#
#        # normalize xy projection
#        n = tf.norm(elem[0:2]) 
#        x = tf.math.divide(elem[0], n)
#        y = tf.math.divide(elem[1], n)
#
#        # get psi
#        psi = tf.math.acos(x)
#        psi = tf.cond(y < 0, lambda: tf.math.negative(psi), lambda: psi)
#        
#        return tf.stack([-tf.multiply(phi, self.factor), tf.multiply(psi, self.factor)])

# torus coordinates
#class TorsionLayer(layers.Layer):
#
#    def __init__(self):
#        super(TorsionLayer, self).__init__()
#        self.pi = tf.constant(math.pi, dtype='float32')
#        self.factor = tf.constant(180.0 / self.pi, dtype='float32')
#
#    def call(self, inputs):
#       return tf.map_fn(self.map_outer, inputs)
#
#    def map_outer(self, elem):
#        return tf.map_fn(self.map_inner, elem)
#
#    def map_inner(self, elem):
#       
#        x = elem[0]
#        y = elem[1]
#        z = elem[2]
#        
#        R = tf.constant(2.0, dtype='float32')
#
#        # get psi
#        n = tf.math.sqrt(tf.math.add(tf.math.square(x), tf.math.square(y)));
#        psi = tf.math.acos(tf.math.divide(x, n));
#        psi = tf.cond(y < 0.0, lambda: tf.negative(psi), lambda: psi)
#
#        # rotate point to half xz-plane with x > 0
#        rot = tf.negative(psi)
#        rot_x = tf.math.subtract(tf.math.multiply(tf.math.cos(rot), x), tf.math.multiply(tf.math.sin(rot), y))
#
#        # translate point by R
#        rot_x = tf.math.subtract(rot_x, R)
#
#        # get phi
#        n = tf.math.sqrt(tf.math.add(tf.math.square(rot_x), tf.math.square(z)));
#        phi = tf.math.acos(tf.math.divide(rot_x, n));
#        phi = tf.cond(z < 0.0, lambda: tf.negative(phi), lambda: phi)
#
#        # convert to radians and return
#        return tf.stack([tf.multiply(phi, self.factor), tf.multiply(psi, self.factor)])





