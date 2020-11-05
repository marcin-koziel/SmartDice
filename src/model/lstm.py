# from keras import Input
# from keras.models import model_from_json
# from keras.engine import Model
# from keras.layers import Dense, concatenate
# from keras.layers.recurrent import LSTM
# from keras.layers import LSTM, BatchNormalization, Dropout
# from keras.callbacks import Callback
# from keras.models import load_model
from datetime import datetime
from sklearn.preprocessing import MinMaxScaler
from time import sleep, time
from pandas import read_csv
from tensorflow.python.framework.errors_impl import UnknownError
import tensorflow
from tensorflow.keras import Input, Model
from tensorflow.keras.layers import Dense, concatenate, LSTM, BatchNormalization
from tensorflow.keras.callbacks import Callback
from tensorflow.keras.models import model_from_json, load_model

from kerastuner.tuners import RandomSearch
from kerastuner.engine.hyperparameters import HyperParameters

import numpy as np
import pickle
import os

__author__ = "Marcin Koziel"
__credits__ = ["Jason Brownlee", "Louis Yang"]
__email__ = "marcinzkoziel@gmail.com"

# os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

class GetBest(Callback):
    """Get the best model at the end of training.
	# Arguments
        monitor: quantity to monitor.
        verbose: verbosity mode, 0 or 1.
        mode: one of {auto, min, max}.
            The decision
            to overwrite the current stored weights is made
            based on either the maximization or the
            minimization of the monitored quantity. For `val_acc`,
            this should be `max`, for `val_loss` this should
            be `min`, etc. In `auto` mode, the direction is
            automatically inferred from the name of the monitored quantity.
        period: Interval (number of epochs) between checkpoints.
	    # Example
		callbacks = [GetBest(monitor='val_acc', verbose=1, mode='max')]
		mode.fit(X, y, validation_data=(X_eval, Y_eval),
                 callbacks=callbacks)
    """

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
            print('GetBest mode %s is unknown, '
                  'fallback to auto mode.' % (mode),
                  RuntimeWarning)
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


print("///Opening 2. LSTM")

# viewRow = open("Data/02-98/DriverTest.csv", "r")
viewRow = open("Data/02-98/DriverTest.csv", "r")
lst = viewRow.readlines()
lastRow = lst[len(lst) - 1]

with open('Pickle/batchsize.pkl', 'rb')as df:
    batchsize = pickle.load(df)
#
# with open("vars.pkl","rb") as fd:
#     lookbackTest = pickle.load(fd)

# batchsize = 1
lookbackTest = 2
fit_model = True

print("LAST ROW: " + str(lastRow))
print('Batchsize: ' + str(batchsize))


def buildModel():
    dataLength = 1
    labelLength = 1

    # firstLayer = hp.Int("input_num1", 1024, 1024)
    # secondLayer = hp.Int("input_num2", 1024, 1024, 256)
    # thirdLayer = hp.Int("input_num3", 512, 1024, 128)
    # fourthLayer = hp.Int("input_num4", 128, 256, 64)
    # fifthLayer = hp.Int("input_num5", 32, 64, 16)
    # sixthLayer = hp.Int("input_num6", 8, 16, 8)
    # seventhLayer = hp.Int("input_num7", 4, 8, 4)
    # eightLayer = hp.Int("input_num8", 2, 4, 1)

    firstLayer = 2196
    secondLayer = 1314
    thirdLayer = 160
    fourthLayer = 26
    fifthLayer = 8
    sixthLayer = 1

    timestamp = Input(batch_shape=(batchsize, dataLength, lookbackTest), name='Timestamp')
    roll = Input(batch_shape=(batchsize, dataLength, lookbackTest), name='Roll')
    chance = Input(batch_shape=(batchsize, dataLength, lookbackTest), name='Chance')
    regret = Input(batch_shape=(batchsize, dataLength, lookbackTest), name='Regret')
    target = Input(batch_shape=(batchsize, dataLength, lookbackTest), name='Target')

    timestampLayers = LSTM(firstLayer, return_sequences=True, stateful=True)(timestamp)
    timestampLayers = LSTM(secondLayer, return_sequences=True, stateful=True)(timestampLayers)
    timestampLayers = LSTM(thirdLayer, return_sequences=True, stateful=True)(timestampLayers)
    timestampLayers = LSTM(fourthLayer, return_sequences=True, stateful=True)(timestampLayers)
    timestampLayers = LSTM(fifthLayer, return_sequences=True, stateful=True)(timestampLayers)
    timestampLayers = LSTM(sixthLayer, return_sequences=False, stateful=True)(timestampLayers)
    timestampLayers = BatchNormalization()(timestampLayers)

    rollLayers = LSTM(firstLayer, return_sequences=True, stateful=True)(roll)
    rollLayers = LSTM(secondLayer, return_sequences=True, stateful=True)(rollLayers)
    rollLayers = LSTM(thirdLayer, return_sequences=True, stateful=True)(rollLayers)
    rollLayers = LSTM(fourthLayer, return_sequences=True, stateful=True)(rollLayers)
    rollLayers = LSTM(fifthLayer, return_sequences=True, stateful=True)(rollLayers)
    rollLayers = LSTM(sixthLayer, return_sequences=False, stateful=True)(rollLayers)
    rollLayers = BatchNormalization()(rollLayers)

    chanceLayers = LSTM(firstLayer, return_sequences=True, stateful=True)(chance)
    chanceLayers = LSTM(secondLayer, return_sequences=True, stateful=True)(chanceLayers)
    chanceLayers = LSTM(thirdLayer, return_sequences=True, stateful=True)(chanceLayers)
    chanceLayers = LSTM(fourthLayer, return_sequences=True, stateful=True)(chanceLayers)
    chanceLayers = LSTM(fifthLayer, return_sequences=True, stateful=True)(chanceLayers)
    chanceLayers = LSTM(sixthLayer, return_sequences=False, stateful=True)(chanceLayers)
    chanceLayers = BatchNormalization()(chanceLayers)

    regretLayers = LSTM(firstLayer, return_sequences=True, stateful=True)(regret)
    regretLayers = LSTM(secondLayer, return_sequences=True, stateful=True)(regretLayers)
    regretLayers = LSTM(thirdLayer, return_sequences=True, stateful=True)(regretLayers)
    regretLayers = LSTM(fourthLayer, return_sequences=True, stateful=True)(regretLayers)
    regretLayers = LSTM(fifthLayer, return_sequences=True, stateful=True)(regretLayers)
    regretLayers = LSTM(sixthLayer, return_sequences=False, stateful=True)(regretLayers)
    regretLayers = BatchNormalization()(regretLayers)

    targetLayers = LSTM(firstLayer, return_sequences=True, stateful=True)(target)
    targetLayers = LSTM(secondLayer, return_sequences=True, stateful=True)(targetLayers)
    targetLayers = LSTM(thirdLayer, return_sequences=True, stateful=True)(targetLayers)
    targetLayers = LSTM(fourthLayer, return_sequences=True, stateful=True)(targetLayers)
    targetLayers = LSTM(fifthLayer, return_sequences=True, stateful=True)(targetLayers)
    targetLayers = LSTM(sixthLayer, return_sequences=False, stateful=True)(targetLayers)
    targetLayers = BatchNormalization()(targetLayers)

    # winLayers = LSTM(1024, return_sequences=True, stateful=True)(win)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(1024, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(512, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(512, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(256, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(256, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(128, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(128, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(64, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(64, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(32, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(32, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(8, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(8, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(2, return_sequences=True, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    # winLayers = LSTM(2, return_sequences=False, stateful=True)(winLayers)
    # winLayers = BatchNormalization()(winLayers)
    #
    # rollLayers = LSTM(1024, return_sequences=True, stateful=True)(roll)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(1024, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(512, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(512, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(256, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(256, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(128, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(128, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(64, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(64, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(32, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(32, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(8, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(8, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(2, return_sequences=True, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    # rollLayers = LSTM(2, return_sequences=False, stateful=True)(rollLayers)
    # rollLayers = BatchNormalization()(rollLayers)
    #
    # countLayers = LSTM(1024, return_sequences=True, stateful=True)(count)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(1024, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(512, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(512, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(256, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(256, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(128, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(128, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(64, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(64, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(32, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(32, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(8, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(8, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(2, return_sequences=True, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    # countLayers = LSTM(2, return_sequences=False, stateful=True)(countLayers)
    # countLayers = BatchNormalization()(countLayers)
    #
    # rewardLayers = LSTM(1024, return_sequences=True, stateful=True)(reward)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(1024, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(512, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(512, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(256, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(256, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(128, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(128, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(64, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(64, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(32, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(32, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(8, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(8, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(2, return_sequences=True, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)
    # rewardLayers = LSTM(2, return_sequences=False, stateful=True)(rewardLayers)
    # rewardLayers = BatchNormalization()(rewardLayers)

    output = concatenate(
        [
            timestampLayers,
            rollLayers,
            chanceLayers,
            regretLayers,
            targetLayers
        ]
    )

    output = Dense(10, activation='sigmoid')(output)
    output = Dense(10, activation='sigmoid')(output)
    output = Dense(1, activation='sigmoid', name='weightedAverage_output')(output)

    model = Model(
        inputs=
        [
            timestamp,
            roll,
            chance,
            regret,
            target
        ],
        outputs=
        [
            output
        ]
    )

    model.compile(optimizer='rmsprop', loss='mse', metrics=['acc'])

    return model


def loadModel():
    rnn = load_model('Model/model.h5')

    pred = rnn.predict(resultData, batch_size=batchsize)

    # Predict
    print(pred[0])

    with open('Pickle/predict.pkl', 'wb') as df:
        pickle.dump(pred, df)


if __name__ == '__main__':

    with open("Pickle/NewDrivers.pkl", "rb") as df:
        trainingData, trainingLabels, testingData, testingLabels, resultData = pickle.load(df)

    # print("###############")
    # print("testingData")
    # print(testingData["Target"][-6:])
    # print("testingLabels")
    # print(testingLabels["Target"][-6:])
    # print("resultData")
    # print(resultData["Target"][-6:])
    # print("###############")
    # quit()

    with open('Pickle/target.pkl', 'rb') as df:
        lstmTarget = pickle.load(df)

    MY_DIR = f"Test\{int(time())}"

    print("classweight: " + str(lstmTarget))

    if fit_model:
        rnn = buildModel()

        callbacks = [GetBest(monitor='val_acc', verbose=1, mode='max')]

        startTime = str(datetime.now().time()).split('.')[0]
        try:
            history = rnn.fit(
                [
                    trainingData["Timestamp"],
                    trainingData["Roll"],
                    trainingData["Chance"],
                    trainingData["Regret"],
                    trainingData["Target"]
                ],

                trainingLabels["Target"]
                ,
                validation_data=(
                    [
                        testingData["Timestamp"],
                        testingData["Roll"],
                        testingData["Chance"],
                        testingData["Regret"],
                        testingData["Target"]
                    ],
                    testingLabels["Target"]
                ),
                epochs=50,
                batch_size=batchsize,
                shuffle=False,
                callbacks=[GetBest(monitor='val_acc', verbose=1, mode='max')],
                verbose=1,
                class_weight={0: 1, 1: lstmTarget}
            )
        except UnknownError:
            input('Error... (Waiting for user input)')

        val_acc = str(max(history.history['val_acc']))

        with open('Pickle/val-loss.pkl', 'wb') as df:
            pickle.dump(val_acc, df)

        rnn.save("Model/%s-%s.h5" % (MY_DIR, val_acc))

        pred = rnn.predict(resultData, batch_size=batchsize)

        # Predict
        print(pred[0])

        with open('Pickle/predict.pkl', 'wb') as df:
            pickle.dump(pred, df)

        # Fit Duration
        try:
            endTime = str(datetime.now().time()).split('.')[0]
            tdelta = str(datetime.strptime(endTime, '%H:%M:%S') - datetime.strptime(startTime, '%H:%M:%S'))
            fitDur = int(sum(x * int(t) for x, t in zip([3600, 60, 1], tdelta.split(":"))))

            with open("Data/fitDur.pkl", "wb") as df:
                pickle.dump(fitDur, df)
        except ValueError:
            print("ValueError for FitDur")

        # tuner = RandomSearch(
        #     buildModel,
        #     objective="val_acc",
        #     max_trials=10,
        #     directory=MY_DIR,
        #     executions_per_trial=1,
        # )
        #
        # tuner.search(x=[
        #     trainingData["Timestamp"],
        #     trainingData["Roll"],
        #     trainingData["Chance"],
        #     trainingData["Regret"],
        #     trainingData["Target"]
        # ],
        #     y=trainingLabels["Target"],
        #     epochs=1,
        #     shuffle=False,
        #     class_weight={0: 1, 1: lstmTarget},
        #     batch_size=batchsize,
        #     validation_data=([
        #         testingData["Timestamp"],
        #         testingData["Roll"],
        #         testingData["Chance"],
        #         testingData["Regret"],
        #         testingData["Target"]
        #         ],
        #         testingLabels["Target"]
        #         )
        # )
        #
        # dir_time = time()
        #
        # import pickle
        # with open("test/"+str(dir_time)+".pkl", "wb") as f:
        #     pickle.dump(tuner, f)
        #
        # tuner.results_summary()
        #
        # with open("test/"+str(dir_time)+".pkl", "rb") as f:
        #     tuner = pickle.load(f)

    else:
        loadModel()
