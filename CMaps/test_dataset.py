import pandas as pd
import numpy as np
import sys

index_names = ['unit_nr', 'time_cycles']
setting_names = ['setting_1', 'setting_2', 'setting_3']
sensor_names = ['s_{}'.format(i) for i in range(1,22)] 
col_names = index_names + setting_names + sensor_names

drop_sensors = ['s_1','s_5','s_6','s_10','s_16','s_18','s_19']
drop_labels = index_names+setting_names+drop_sensors

test = pd.read_csv(sys.argv[1], sep='\s+', header=None, names=col_names)
rul = pd.read_csv(sys.argv[2], sep='\s+', header=None, names = ['RUL'])

print(rul.head())

def get_label(number):
    if number == 0:
        return 2
    elif number <= 10:
        return 1
    else:
        return 0

def add_label(df):
    result_frame = test.groupby('unit_nr').last().reset_index()

    print(result_frame.head())

    label = rul.squeeze().apply(get_label)
    result_frame["target"] = label

    return result_frame


test = add_label(test)

should_store_accuracy = np.full(test.shape[0], False)
should_store_accuracy[len(should_store_accuracy)-1] = True
print(should_store_accuracy)
test = test.drop(drop_labels, axis=1)
test["accuracy"] = should_store_accuracy
print(test.head())
print(sys.argv[1])
test.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label.csv'), index=False)