import pandas as pd
import numpy
import pickle
import csv
import os
import psutil
from sklearn.preprocessing import MinMaxScaler

__author__ = "Marcin Koziel"
__credits__ = ["Jason Brownlee", "Louis Yang"]
__email__ = "marcinzkoziel@gmail.com"

print("///Opening 1. LOADER")

# ~ functions ~

def create_dataset(dataset, look_back=1):
    dataX, dataY = [], []
    for i in range(len(dataset) - look_back):
        a = dataset[i:(i + look_back), 0]
        dataX.append(a)
        dataY.append(dataset[i + look_back, 0])
    return numpy.array(dataX), numpy.array(dataY)

def create_result(dataset, look_back=1):
    dataX = []
    for i in range(len(dataset) - 1):
        a = dataset[i:(i + look_back), 0]
        dataX.append(a)
    return numpy.array(dataX)

# file = "C:/Users/Chukozy/Desktop/PrimeMain/Data/Output2.csv"
# if os.path.exists(file):
#     os.remove(file)

# ~ ~ ~ ~ ~ ~ ~ ~  ~ ~ ~

timeLog = []
rollLog = []
chanceLog = []
regretLog = []
targetLog = []
resultLog = []

fit_model = True

# ~ settings ~

batchsize = 32
look_back = 2
for row in range(0, 5):


    # path = "Data/02-98/DriverTest.csv"
    path = "Data/02-98/DriverTest.csv"
    df = pd.read_csv(path, names=["Count", "Timestamp", "Roll", "Chance", "Win", "Balance", "Side", "Regret"], engine='python')

    if not fit_model:
        skiprow = int(0.8 * len(df["Roll"]))
        df = pd.read_csv(path, names=["Count", "Timestamp", "Roll", "Chance", "Win", "Balance", "Side", "Regret"],
                         engine='python', skiprows=skiprow)

    #  ~ defining the columns ~
    balance = 0
    betAmount = 0.25
    win = 0
    countReset = False
    countRegret = 0
    for x in range(1, len(df["Roll"])):

        # print("%s/%s" % (x, len(df["Roll"])))

        count = df["Count"][x]
        roll = df["Roll"][x]
        chance = df["Chance"][x]
        payout = ((100 / chance) / (100 / 99))
        moneyEarn = (payout * betAmount) - betAmount

        if float(count) == 0:
            countRegret = 0

        if float(roll) < float(chance):
            win = 1
        else:
            win = 0

        if win == 1:
            countRegret = countRegret + 1
            balance += moneyEarn
        else:
            countRegret = countRegret - 0.25
            balance -= betAmount

        # print("Roll["+str(roll)+"] : Chance["+str(chance)+"] = " + str(newBalance))
        df["Balance"][x] = balance
        df["Side"][x] = win
        # print("Roll: %s | Chance: %s | Side: %s" % (roll, chance, win))
        df["Regret"][x] = countRegret

    #     with open('Data/Output2.csv', 'a', newline='') as csvFile:
    #         importRow = csv.writer(csvFile)
    #         if win ==1:
    #             importRow.writerow([str(x), str(df["Count"][x]), str(df["Timestamp"][x]), str(df["Roll"][x]), str(df["Chance"][x]), str(df["Side"][x]), str(df["Win"][x]), str(df["Balance"][x]), str(moneyEarn)])
    #         else:
    #             importRow.writerow(
    #                 [str(x), str(df["Count"][x]), str(df["Timestamp"][x]), str(df["Roll"][x]), str(df["Chance"][x]),
    #                  str(df["Side"][x]), str(df["Win"][x]), str(df["Balance"][x]), str("-"+str(betAmount))])
    # quit()
    df["Target"] = df["Side"].shift(1)
    df.dropna(inplace=True)

    print(df.head(20))
    # quit()

    print(f"ROW: %s" % row)
    if row == 0:
        dataset = df["Count"]
        print("Started Count")
    if row == 1:
        dataset = df["Roll"]
        print("Started Roll")
    if row == 2:
        dataset = df["Chance"]
        print("Started Chance")
    if row == 3:
        dataset = df["Regret"]
        print("Started Regret")
    if row == 4:
        dataset = df["Target"]
        print("Started Target")

    datasetList = []
    for i in dataset:
        datasetList.append([i])
    dataset = numpy.array(datasetList)
    dataset = numpy.array(dataset).astype('float32')

    if fit_model:
        if row == 4:
            # ~ Setting weight class for 0s 1s
            datasetOne = []
            datasetZero = []

            for x in range(0, len(dataset)):
                if dataset[x] == 0:
                    datasetZero.append(dataset[x])
                else:
                    datasetOne.append(dataset[x])

            lstmTarget = (len(datasetZero) / len(datasetOne))

            print("0s : " + str(len(datasetZero)))
            print("1s : " + str(len(datasetOne)))

            print(lstmTarget)

            with open('Pickle/target.pkl', 'wb') as df:
                pickle.dump(lstmTarget, df)

    # print(dataset)

    # dataset = df.values
    # dataset = dataset.astype('float32')
    #  normalize the dataset

    # ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    scaler = MinMaxScaler(feature_range=(0, 1))
    dataset = scaler.fit_transform(dataset)


    # split into train and test sets

    if fit_model:
        train_size = int(len(dataset) * 0.80)
    else:
        train_size = int(len(dataset) * 0.005)

    test_size = len(dataset) - train_size
    train, test = dataset[0:train_size, :], dataset[train_size:len(dataset), :]

    trainX, trainY = create_dataset(train, look_back)
    testX, testY = create_dataset(test, look_back)

    resultX = create_result(test, look_back)
    # if row == 4:
    #     print("testX:")
    #     print(testX[-6:])
    #     print("testY:")
    #     print(testY[-6:])
    #     print("resultX:")
    #     print(resultX[-6:])
    #     quit()

    before_trainX = len(trainX)
    before_testX = len(testX)
    before_resultX = len(resultX)

    while not (len(trainX) % batchsize) == 0:
        train_size -= 1
        train, test = dataset[0:train_size, :], dataset[train_size:len(dataset), :]
        trainX, trainY = create_dataset(train, look_back)
    print("Samples before: "+str(before_trainX)+"\nSamples after: "+str(len(trainX)))

    while not (len(testX) % batchsize) == 0:
        train_size += 1
        train, test = dataset[0:train_size, :], dataset[train_size:len(dataset), :]
        testX, testY = create_dataset(test, look_back)
    print("Samples before: "+str(before_testX)+"\nSamples after: "+str(len(testX)))

    while not (len(resultX) % batchsize) == 0:
        train_size += 1
        train, test = dataset[0:train_size, :], dataset[train_size:len(dataset), :]
        resultX = create_result(test, look_back)
    print("Samples before: "+str(before_resultX)+"\nSamples after: "+str(len(resultX)))

    # reshape input to be [samples, time steps, features]
    trainX = numpy.reshape(trainX, (trainX.shape[0], 1, trainX.shape[1]))
    testX = numpy.reshape(testX, (testX.shape[0], 1, testX.shape[1]))
    resultX = numpy.reshape(resultX, (resultX.shape[0], 1, resultX.shape[1]))

    # print(testX-20)
    # print(resultX[-20])
    # if row == 2:
    #     quit()
    # Roll
    if row == 0:
        timeLog.append(trainX)
        timeLog.append(trainY)
        timeLog.append(testX)
        timeLog.append(testY)
        timeLog.append(resultX)
        print(len(trainX))
        print(len(trainY))
        print(len(testX))
        print(len(testY))
    # Count
    if row == 1:
        rollLog.append(trainX)
        rollLog.append(trainY)
        rollLog.append(testX)
        rollLog.append(testY)
        rollLog.append(resultX)
        print(len(trainX))
        print(len(trainY))
        print(len(testX))
        print(len(testY))

    if row == 2:
        chanceLog.append(trainX)
        chanceLog.append(trainY)
        chanceLog.append(testX)
        chanceLog.append(testY)
        chanceLog.append(resultX)
        print(len(trainX))
        print(len(trainY))
        print(len(testX))
        print(len(testY))

    # Target
    if row == 3:
        regretLog.append(trainX)
        regretLog.append(trainY)
        regretLog.append(testX)
        regretLog.append(testY)
        regretLog.append(resultX)
        print(len(trainX))
        print(len(trainY))
        print(len(testX))
        print(len(testY))

    if row == 4:
        targetLog.append(trainX)
        targetLog.append(trainY)
        targetLog.append(testX)
        targetLog.append(testY)
        targetLog.append(resultX)
        print(len(trainX))
        print(len(trainY))
        print(len(testX))
        print(len(testY))

trainingData = {"Timestamp": timeLog[0], "Roll": rollLog[0], "Chance": chanceLog[0], "Regret": regretLog[0], "Target": targetLog[0]}
trainingLabels = {"Timestamp": timeLog[1], "Roll":rollLog[1], "Chance": chanceLog[1], "Regret": regretLog[1], "Target": targetLog[1]}

testingData = {"Timestamp": timeLog[2], "Roll": rollLog[2], "Chance": chanceLog[2], "Regret": regretLog[2], "Target": targetLog[2]}
testingLabels = {"Timestamp": timeLog[3], "Roll": rollLog[3], "Chance": chanceLog[3], "Regret": regretLog[3], "Target": targetLog[3]}

resultData = {"Timestamp": timeLog[4], "Roll": rollLog[4], "Chance": chanceLog[4], "Regret": regretLog[4], "Target": targetLog[4]}

print("saved")
with open("Pickle/NewDrivers.pkl", "wb") as df:
    pickle.dump([trainingData, trainingLabels, testingData, testingLabels, resultData], df)
with open("Pickle/batchsize.pkl", "wb") as df:
    pickle.dump(batchsize, df)
