import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

SMALL_SIZE = 14
MEDIUM_SIZE = 14
BIGGER_SIZE = 16

plt.rc('font', size=SMALL_SIZE)          
plt.rc('axes', titlesize=SMALL_SIZE)     
plt.rc('axes', labelsize=MEDIUM_SIZE)    
plt.rc('xtick', labelsize=SMALL_SIZE)   
plt.rc('ytick', labelsize=SMALL_SIZE)    
plt.rc('legend', fontsize=SMALL_SIZE)   
plt.rc('figure', titlesize=BIGGER_SIZE) 

col_names = ["service", "request_id", "state", "timestamp"]
cpu_col = ["time", "load", "sys_load"]
mem_col = ["time", "committed", "free", "used"]

data_entry = pd.read_csv("data-entry-service.log", sep=',\s+', header=None, names=col_names)
schema = pd.read_csv("schema-service.log", sep=',\s+', header=None, names=col_names)
data = pd.read_csv("data-service.log", sep=',\s+', header=None, names=col_names)
event = pd.read_csv("event-processing-service.log", sep=',\s+', header=None, names=col_names)
prediction = pd.read_csv("prediction-service.log", sep=',\s+', header=None, names=col_names)

all = pd.concat([data_entry, schema, data, event, prediction])
all = all.sort_values(["request_id", "timestamp"])

values = []

def get_basic_values(pd, name):
    print("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
    pd["seconds"] = (pd["timestamp"]/1000).astype('int32')
    total_time = pd["timestamp"].max() - pd["timestamp"].min()
    total_request_count = pd.groupby('request_id').ngroups
    grouped = pd.groupby('request_id')
    min_time = grouped["timestamp"].min()
    max_time = grouped["timestamp"].max()
    only_start = pd[pd["state"] == "start"]
   
    grouped_2 = only_start.groupby("seconds").count()
    print(grouped_2.shape)
    grouped_2["second"] = range(1, len(grouped_2) + 1)
    grouped_2.drop(grouped_2.tail(2).index, inplace= True)
    grouped_2 = grouped_2.drop(columns=["service", "state", "timestamp"])
    fig = plt.figure(figsize=(14,5))
    fig.suptitle(name + " (1000 concurrent user)")
    ax = plt.subplot(1,2,1)
    grouped_2.plot(x="second", y="request_id", legend=None, ax=ax)
    
    z = np.polyfit(grouped_2["second"], grouped_2["request_id"], 1)
    p = np.poly1d(z)
    plt.plot(grouped_2["second"], p(grouped_2["second"]), "r--")
    print("y=%.2fx+%.2f"%(z[0],z[1]))

    plt.xlabel('Second')
    plt.ylabel('Number of requests per second')
    
    time_elapsed = max_time - min_time
    plt.subplot(1,2,2)
    plt.hist(time_elapsed, bins=25)
    plt.xlabel("Processing time (ms)")
    plt.ylabel("Amount of requests")
    plt.savefig(name + ".png", dpi=300, bbox_inches='tight')
    plt.clf()
    print(time_elapsed.describe())
    print(total_time)
    print(total_request_count)
    print(grouped_2.describe())
    print(grouped_2.head())
    values.append([name, int(total_time/1000), total_request_count, time_elapsed.mean(), time_elapsed.min(), time_elapsed.max()])


get_basic_values(data_entry, "Data Entry Service")
get_basic_values(schema, "Schema Service")
get_basic_values(data, "Data Service")
get_basic_values(event, "Event Processing Service")
get_basic_values(prediction, "Prediction Service")
get_basic_values(all, "All services combined")

dataframe = pd.DataFrame(values, columns=["name", "total_time (s)", "total_request_count", "mean processing time (ms)", "min processing time (ms)", "max processing time (ms)"])
dataframe.to_csv('details.csv', index=True)