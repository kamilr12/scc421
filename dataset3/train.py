from random import random
import pandas as pd
import numpy as np
import sys
from sklearn.model_selection import train_test_split


sensor_names = ['s_{}'.format(i) for i in range(1,18)]
col_names = ["machineID"] + sensor_names + ["rul"]
print(col_names)
train = pd.read_csv("raw_train_2.csv", sep=',', header=0)
#print(train["rul"])

def get_label(number):
    if number == 0:
        return 1
    elif number <= 10:
        return 1
    else:
        return 0


def add_label(df):
    remaining_useful_life = df["RUL_I"].apply(get_label)
    df["target"] = remaining_useful_life
    
    df = df.drop("RUL_I", axis=1)

    return df

train = add_label(train)
train = train[train["model"] == "model4"]
print(train.shape)
train = train.drop(["machineID", "model", "time_in_cycles"], axis=1)
train.columns = sensor_names + ["target"]
print(train.head())
train_1, train_2, train_3, train_4, train_5, train_6, train_7, train_8, train_9, train_10 = np.array_split(train, 10)
train_1.to_csv('train_1.csv', index=False)
train_2.to_csv('train_2.csv', index=False)
train_3.to_csv('train_3.csv', index=False)
train_4.to_csv('train_4.csv', index=False)
train_5.to_csv('train_5.csv', index=False)
train_6.to_csv('train_6.csv', index=False)
train_7.to_csv('train_7.csv', index=False)
train_8.to_csv('train_8.csv', index=False)
train_9.to_csv('train_9.csv', index=False)
train_10.to_csv('train_10.csv', index=False)