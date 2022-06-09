from random import random
import pandas as pd
import numpy as np
import sys
from sklearn.model_selection import train_test_split


sensor_names = ['s_{}'.format(i) for i in range(1,18)]
col_names = ["machineID"] + sensor_names + ["rul"]
print(col_names)
test = pd.read_csv("raw_test_2.csv", sep=',', header=0)
print(test["machineID"])

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

test = add_label(test)
test = test[test["model"] == "model4"]
print(test.shape)
test = test.drop(["machineID", "model", "time_in_cycles"], axis=1)
test.columns = sensor_names + ["target"]
print(test.head())

should_store_accuracy = np.full(test.shape[0], False)
should_store_accuracy[len(should_store_accuracy)-1] = True
print(should_store_accuracy)
test["accuracy"] = should_store_accuracy

test.to_csv('test.csv', index=False)