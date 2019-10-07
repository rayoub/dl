
import tensorflow as tf
from tensorflow.keras import layers

class TorsionLayer(layers.Layer):


    # i believe this needs to take a look up of 3 coords and produce 2 coords
    def __init__(self):
        super(TorsionLayer, self).__init__()

    def call(self, inputs):
        return tf.map_fn(self.map_outer, inputs)

    def map_outer(self, elem):
        return tf.map_fn(self.map_inner, elem)

    def map_inner(self, elem):
        return tf.constant([1.0,2.0], dtype='float32')






