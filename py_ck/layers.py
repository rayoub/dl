
import math
import tensorflow as tf
from tensorflow.keras import layers

# spherical coordinates 
class SpLayer(layers.Layer):

    def __init__(self, lonly):
        super(SpLayer, self).__init__()
        self.lonly = lonly
        self.pi = tf.constant(math.pi, dtype='float32')
        self.factor = tf.constant(180.0 / self.pi, dtype='float32')

    def call(self, inputs):
       return tf.map_fn(self.map_outer, inputs)

    def map_outer(self, elem):
        return tf.map_fn(self.map_inner, elem)

    def map_inner(self, elem):

        # get phi from z
        n = tf.norm(elem)
        z = tf.math.divide(elem[2], n)
        phi = tf.math.acos(z)

        # normalize xy projection
        n = tf.norm(elem[0:2]) 
        x = tf.math.divide(elem[0], n)
        y = tf.math.divide(elem[1], n)

        # get psi
        psi = tf.math.acos(x)
        psi = tf.cond(y < 0, lambda: tf.math.negative(psi), lambda: psi)
     
        # convert and add 15
        phi = tf.math.add(tf.math.multiply(phi, self.factor), 15.0)
        psi = tf.math.multiply(psi, self.factor)

        if self.lonly == True:
            phi = tf.math.negative(phi)
        
        return tf.stack([phi, psi])

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

# left circle coordinates
class CilLayer(layers.Layer):

    def __init__(self):
        super(CilLayer, self).__init__()
        self.pi = tf.constant(math.pi, dtype='float32')
        self.factor = tf.constant(180.0 / self.pi, dtype='float32')

    def call(self, inputs):
       return tf.map_fn(self.map_outer, inputs)

    def map_outer(self, elem):
        return tf.map_fn(self.map_inner, elem)

    def map_inner(self, elem):

        phi_x = tf.math.minimum(tf.math.maximum(elem[0], -1.0), 1.0)
        phi_x = tf.math.add(phi_x, 1.0)
        phi_x = tf.divide(tf.math.multiply(phi_x, self.pi), 2.0)

        n = tf.math.sqrt(tf.math.add(tf.math.square(elem[1]), tf.math.square(elem[2])))
        psi_x = tf.math.divide(elem[1], n)
        psi_y = tf.math.divide(elem[2], n)
        psi = tf.math.acos(psi_x)
        psi = tf.cond(psi_y < 0, lambda: tf.negative(psi), lambda: psi)
        
        return tf.stack([-tf.multiply(phi_x, self.factor), tf.multiply(psi, self.factor)])

# rigth circle coordinates
class CirLayer(layers.Layer):

    def __init__(self):
        super(CirLayer, self).__init__()
        self.pi = tf.constant(math.pi, dtype='float32')
        self.factor = tf.constant(180.0 / self.pi, dtype='float32')

    def call(self, inputs):
       return tf.map_fn(self.map_outer, inputs)

    def map_outer(self, elem):
        return tf.map_fn(self.map_inner, elem)

    def map_inner(self, elem):

        phi_x = tf.math.minimum(tf.math.maximum(elem[0], -1.0), 1.0)
        phi_x = tf.math.add(phi_x, 1.0)
        phi_x = tf.divide(tf.math.multiply(phi_x, self.pi), 2.0)

        n = tf.math.sqrt(tf.math.add(tf.math.square(elem[1]), tf.math.square(elem[2])))
        psi_x = tf.math.divide(elem[1], n)
        psi_y = tf.math.divide(elem[2], n)
        psi = tf.math.acos(psi_x)
        psi = tf.cond(psi_y < 0, lambda: tf.negative(psi), lambda: psi)
        
        return tf.stack([tf.multiply(phi_x, self.factor), tf.multiply(psi, self.factor)])

