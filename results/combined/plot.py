import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

col_names = ["index", "service", "total_time", "total_requests", "mean", "min", "max"]

d10 = pd.read_csv("details_10.csv", sep=',', header=None, names=col_names)
d100 = pd.read_csv("details_100.csv", sep=',', header=None, names=col_names)
d1000 = pd.read_csv("details_1000.csv", sep=',', header=None, names=col_names)
all = pd.read_csv("all.csv", sep=',', header=None, names=col_names)
all["index"] = all["index"].map(str)
plt.errorbar(all["index"], all["mean"], [all["mean"] - all["min"], all["max"] - all["mean"]], fmt= 'o', elinewidth=2, capsize=10)
plt.xlabel("Number of concurrent users")
plt.ylabel("Processing time (ms)")

plt.savefig("figure.png", dpi=300)