import argparse
from tensorflow.keras import Input, Model
from tensorflow.keras.layers import Dense, concatenate, LSTM, BatchNormalization
from tensorflow.keras.callbacks import Callback

import numpy as np
import pickle


class GetBest(Callback):

    def __init__(self, monitor='val_acc', sec_monitor='acc', verbose=0,
                 mode='auto', period=1):
        super(GetBest, self).__init__()
        self.monitor = monitor
        self.sec_monitor = sec_monitor
        self.verbose = verbose
        self.period = period
        self.best_epochs = 0
        self.epochs_since_last_save = 0

        if mode not in ['auto', 'min', 'max']:
            mode = 'auto'

        if mode == 'min':
            self.monitor_op = np.less
            self.best = np.Inf
        elif mode == 'max':
            self.monitor_op = np.greater
            self.best = -np.Inf
        else:
            if 'acc' in self.monitor or self.monitor.startswith('fmeasure'):
                self.monitor_op = np.greater
                self.best = -np.Inf
            else:
                self.monitor_op = np.less
                self.best = np.Inf

    def on_train_begin(self, logs=None):
        self.best_weights = self.model.get_weights()

    def on_epoch_end(self, epoch, logs=None):
        logs = logs or {}
        self.epochs_since_last_save += 1
        if self.epochs_since_last_save >= self.period:
            self.epochs_since_last_save = 0
            # filepath = self.filepath.format(epoch=epoch + 1, **logs)
            current = logs.get(self.monitor)
            if current is None:
                print('Can pick best model only with %s available, '
                      'skipping.' % (self.monitor), RuntimeWarning)
            else:
                if self.monitor_op(current, self.best):
                    if self.verbose > 0:
                        print('\nEpoch %05d: %s improved from %0.5f to %0.5f,'
                              ' storing weights.'
                              % (epoch + 1, self.monitor, self.best,
                                 current))
                    self.best = current
                    self.best_epochs = epoch + 1
                    self.best_weights = self.model.get_weights()
                else:
                    if self.verbose > 0:
                        print('\nEpoch %05d: %s did not improve' %
                              (epoch + 1, self.monitor))

    def on_train_end(self, logs=None):
        if self.verbose > 0:
            print('Using epoch %05d with %s: %0.5f' % (self.best_epochs, self.monitor,
                                                       self.best))
        self.model.set_weights(self.best_weights)

def buildModel():
    dataLength = 1
    labelLength = 1

    layersUnits = [2688, 736, 176, 32, 12, 4]

    timeDiff = Input(batch_shape=(1, dataLength, 2), name='timeDiff')
    rollNo = Input(batch_shape=(1, dataLength, 2), name='rollNo')
    target = Input(batch_shape=(1, dataLength, 2), name='target')

    timeDiffLayers = LSTM(layersUnits[0], return_sequences=True, stateful=True)(timeDiff)
    timeDiffLayers = LSTM(layersUnits[1], return_sequences=True, stateful=True)(timeDiffLayers)
    timeDiffLayers = LSTM(layersUnits[2], return_sequences=True, stateful=True)(timeDiffLayers)
    timeDiffLayers = LSTM(layersUnits[3], return_sequences=True, stateful=True)(timeDiffLayers)
    timeDiffLayers = LSTM(layersUnits[4], return_sequences=True, stateful=True)(timeDiffLayers)
    timeDiffLayers = LSTM(layersUnits[5], return_sequences=False, stateful=True)(timeDiffLayers)
    timeDiffLayers = BatchNormalization()(winLayers)

    rollNoLayers = LSTM(layersUnits[0], return_sequences=True, stateful=True)(rollNo)
    rollNoLayers = LSTM(layersUnits[1], return_sequences=True, stateful=True)(rollNoLayers)
    rollNoLayers = LSTM(layersUnits[2], return_sequences=True, stateful=True)(rollNoLayers)
    rollNoLayers = LSTM(layersUnits[3], return_sequences=True, stateful=True)(rollNoLayers)
    rollNoLayers = LSTM(layersUnits[4], return_sequences=True, stateful=True)(rollNoLayers)
    rollNoLayers = LSTM(layersUnits[5], return_sequences=False, stateful=True)(rollNoLayers)
    rollNoLayers = BatchNormalization()(rollLayers)

    targetLayers = LSTM(layersUnits[0], return_sequences=True, stateful=True)(target)
    targetLayers = LSTM(layersUnits[1], return_sequences=True, stateful=True)(targetLayers)
    targetLayers = LSTM(layersUnits[2], return_sequences=True, stateful=True)(targetLayers)
    targetLayers = LSTM(layersUnits[3], return_sequences=True, stateful=True)(targetLayers)
    targetLayers = LSTM(layersUnits[4], return_sequences=True, stateful=True)(targetLayers)
    targetLayers = LSTM(layersUnits[5], return_sequences=False, stateful=True)(targetLayers)
    targetLayers = BatchNormalization()(rewardLayers)

    output = concatenate(
        [
            timeDiffLayers,
            rollNoLayers,
            targetLayers
        ]
    )

    output = Dense(10, activation='sigmoid')(output)
    output = Dense(10, activation='sigmoid')(output)
    output = Dense(1, activation='sigmoid', name='weightedAverage_output')(output)

    model = Model(
        inputs=
        [
            timeDiffLayers,
            rollNoLayers,
            targetLayers
        ],
        outputs=
        [
            output
        ]
    )

    model.compile(optimizer='rmsprop', loss='mse', metrics=['acc'])

    return model


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("dataPklFile", help="pass the matrices using pickle format", default="data.pkl")
    args = parser.parse_args()

    with open(args.dataPklFile, "rb") as df:
        trainingData, trainingLabels, testingData, testingLabels, lstmTarget, resultData = pickle.load(df)

    rnn = buildModel()

    callbacks = [GetBest(monitor='val_acc', verbose=1, mode='max')]

    history = rnn.fit(
        [
            trainingData["timeDiff"],
            trainingData["rollNo"],
            trainingData["target"]
        ],

        trainingLabels["target"]
        ,
        validation_data=(
            [
                testingData["timeDiff"],
                testingData["rollNo"],
                testingData["target"]
            ],

            testingLabels["target"]
        ),
        epochs=3,
        batch_size=1,
        shuffle=False,
        callbacks=[GetBest(monitor='val_acc', verbose=1, mode='max')],
        verbose=1,
        class_weight={0: 1, 1: lstmTarget}
    )

    print(history.history['val_acc'])

    pred = rnn.predict(resultData, batch_size=1)

    print(pred[0])
