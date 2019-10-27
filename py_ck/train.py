
import tensorflow as tf
import data
import models

# constants
BATCH_SIZE = 32
BUFFER_SIZE = 5000
MAX_EPOCHS = 40

# inputs
train_ds = data.get_cil_train_data('../train/train_astral.txt', BUFFER_SIZE, BATCH_SIZE)
val_ds = data.get_cil_train_data('../train/val_astral.txt', BUFFER_SIZE, BATCH_SIZE)

# callbacks
callbacks = [
        # early stopping
        tf.keras.callbacks.EarlyStopping(monitor='val_rd_mean_squared_error', min_delta=0.001, patience=3, verbose=0, restore_best_weights=True)
    ]

# model training
m = models.get_model(BATCH_SIZE, data.CIL_CNT)
m.fit(train_ds, epochs=MAX_EPOCHS, validation_data=val_ds, callbacks=callbacks)
m.save_weights('./checkpoints/cil2')



