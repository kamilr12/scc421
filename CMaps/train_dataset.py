from random import random
import pandas as pd
import numpy as np
import sys
from sklearn.model_selection import train_test_split

index_names = ['unit_nr', 'time_cycles']
setting_names = ['setting_1', 'setting_2', 'setting_3']
sensor_names = ['s_{}'.format(i) for i in range(1,22)] 
col_names = index_names + setting_names + sensor_names

drop_sensors = ['s_1','s_5','s_6','s_10','s_16','s_18','s_19']
drop_labels = index_names+setting_names+drop_sensors

train = pd.read_csv(sys.argv[1], sep='\s+', header=None, names=col_names)
#print(sys.argv[1])

def get_label(number):
    if number == 0:
        return 2
    elif number <= 10:
        return 1
    else:
        return 0


def add_label(df):
    # Get the total number of cycles for each unit
    grouped_by_unit = df.groupby(by="unit_nr")
    max_cycle = grouped_by_unit["time_cycles"].max()
    
    # Merge the max cycle back into the original frame
    result_frame = df.merge(max_cycle.to_frame(name='max_cycle'), left_on='unit_nr', right_index=True)
    
    # Calculate remaining useful life for each row
    remaining_useful_life = result_frame["max_cycle"] - result_frame["time_cycles"]
    
    #print('xd')
    
    remaining_useful_life = remaining_useful_life.apply(get_label)
    #print(remaining_useful_life.head())
    result_frame["target"] = remaining_useful_life
    
    # drop max_cycle as it's no longer needed
    result_frame = result_frame.drop("max_cycle", axis=1)
    #print('xd')

    return result_frame
  
train = add_label(train)
train = train.drop(drop_labels, axis=1)
train_1, train_2, train_3, train_4, train_5, train_6, train_7, train_8, train_9, train_10 = np.array_split(train.sample(frac=1, random_state=2115), 10)
print(train_1.head())
#print(train.head())
train_1.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_1.csv'), index=False)
train_2.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_2.csv'), index=False)
train_3.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_3.csv'), index=False)
train_4.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_4.csv'), index=False)
train_5.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_5.csv'), index=False)
train_6.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_6.csv'), index=False)
train_7.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_7.csv'), index=False)
train_8.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_8.csv'), index=False)
train_9.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_9.csv'), index=False)
train_10.to_csv((sys.argv[1][:len(sys.argv[1]) - 4] + '_with_label_.csv'), index=False)



#print(train.head())