
import tensorflow as tf
import data
import models

# constants
BATCH_SIZE = 32
BUFFER_SIZE = 1000
MAX_EPOCHS = 4

# inputs
train_ds = data.get_sp_train_data('../train/train_astral.txt', BUFFER_SIZE, BATCH_SIZE)
val_ds = data.get_sp_train_data('../train/val_astral.txt', BUFFER_SIZE, BATCH_SIZE)

# model training
m = models.get_sp_model(BATCH_SIZE)
m.fit(train_ds, validation_data=val_ds, epochs=MAX_EPOCHS)
m.save_weights('./checkpoints/sp1')



