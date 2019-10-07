
from tensorflow.keras import layers

class IdentityLayer(layers.Layer):

    def __init__(self):
        super(IdentityLayer, self).__init__()

    def call(self, inputs):
        return inputs
