import pandas as pd
import matplotlib.pyplot as plt

mh = pd.read_csv("ml_metrics_microsoft_hoeffding.csv", sep=',', header=None, names=["org", "percentage", "accuracy", "precision", "recall", "f1"])
mk = pd.read_csv("ml_metrics_microsoft_knn.csv", sep=',', header=None, names=["org", "percentage", "accuracy", "precision", "recall", "f1"])
ml = pd.read_csv("ml_metrics_microsoft_logistic.csv", sep=',', header=None, names=["org", "percentage", "accuracy", "precision", "recall", "f1"])
mp = pd.read_csv("ml_metrics_microsoft_pa.csv", sep=',', header=None, names=["org", "percentage", "accuracy", "precision", "recall", "f1"])
nh = pd.read_csv("ml_metrics_nasa_hoeffding.csv", sep=',', header=None, names=["org", "percentage", "accuracy", "precision", "recall", "f1"])
nk = pd.read_csv("ml_metrics_nasa_knn.csv", sep=',', header=None, names=["org", "percentage", "accuracy", "precision", "recall", "f1"])
nl = pd.read_csv("ml_metrics_nasa_logistic.csv", sep=',', header=None, names=["org", "percentage", "accuracy", "precision", "recall", "f1"])
np = pd.read_csv("ml_metrics_nasa_pa.csv", sep=',', header=None, names=["org", "percentage", "accuracy", "precision", "recall", "f1"])

def multiply(number):
    return int(number * 10)

def add_0_row(pd):
    pd.loc[-1] = ["pacany-sialala", 0, 0, 0, 0, 0]
    pd.index = pd.index + 1  # shifting index
    pd.sort_index(inplace=True)
    pd["numbers"] = list(range(0, 11))
    return pd

mh = add_0_row(mh)
mk = add_0_row(mk)
ml = add_0_row(ml)
mp = add_0_row(mp)
nh = add_0_row(nh)
nk = add_0_row(nk)
nl = add_0_row(nl)
np = add_0_row(np)

plt.plot(mh["numbers"], mh["accuracy"], label="Hoeffding Adaptive Tree")
plt.plot(mk["numbers"], mk["accuracy"], label="kNN")
plt.plot(ml["numbers"], ml["accuracy"], label="Logistic Regression")
plt.plot(mp["numbers"], mp["accuracy"], label="PA")
plt.xticks(mh["numbers"])
plt.yticks(mh["percentage"])
plt.title("Microsoft dataset - accuracy")
plt.xlabel("Number of training subsets processed")

plt.legend()
plt.savefig("microsoft_accuracy.png", dpi=300)
plt.clf()

plt.plot(mh["numbers"], mh["precision"], label="Hoeffding Adaptive Tree")
plt.plot(mk["numbers"], mk["precision"], label="kNN")
plt.plot(ml["numbers"], ml["precision"], label="Logistic Regression")
plt.plot(mp["numbers"], mp["precision"], label="PA")
plt.xticks(mh["numbers"])
plt.yticks(mh["percentage"])
plt.title("Microsoft dataset - precision")
plt.xlabel("Number of training subsets processed")

plt.legend()
plt.savefig("microsoft_precision.png", dpi=300)
plt.clf()

plt.plot(mh["numbers"], mh["recall"], label="Hoeffding Adaptive Tree")
plt.plot(mk["numbers"], mk["recall"], label="kNN")
plt.plot(ml["numbers"], ml["recall"], label="Logistic Regression")
plt.plot(mp["numbers"], mp["recall"], label="PA")
plt.xticks(mh["numbers"])
plt.yticks(mh["percentage"])
plt.title("Microsoft dataset - recall")
plt.xlabel("Number of training subsets processed")

plt.legend()
plt.savefig("microsoft_recall.png", dpi=300)
plt.clf()

plt.plot(mh["numbers"], mh["f1"], label="Hoeffding Adaptive Tree")
plt.plot(mk["numbers"], mk["f1"], label="kNN")
plt.plot(ml["numbers"], ml["f1"], label="Logistic Regression")
plt.plot(mp["numbers"], mp["f1"], label="PA")
plt.xticks(mh["numbers"])
plt.yticks(mh["percentage"])
plt.title("Microsoft dataset - F1 score")
plt.xlabel("Number of training subsets processed")

plt.legend()
plt.savefig("microsoft_f1.png", dpi=300)
plt.clf()

plt.plot(nh["numbers"], nh["accuracy"], label="Hoeffding Adaptive Tree")
plt.plot(nk["numbers"], nk["accuracy"], label="kNN")
plt.plot(nl["numbers"], nl["accuracy"], label="Logistic Regression")
plt.plot(np["numbers"], np["accuracy"], label="PA")
plt.xticks(nh["numbers"])
plt.yticks(nh["percentage"])
plt.title("NASA dataset - accuracy")
plt.xlabel("Number of training subsets processed")

plt.legend()
plt.savefig("nasa_accuracy.png", dpi=300)
plt.clf()

plt.plot(nh["numbers"], nh["precision"], label="Hoeffding Adaptive Tree")
plt.plot(nk["numbers"], nk["precision"], label="kNN")
plt.plot(nl["numbers"], nl["precision"], label="Logistic Regression")
plt.plot(np["numbers"], np["precision"], label="PA")
plt.xticks(nh["numbers"])
plt.yticks(nh["percentage"])
plt.title("NASA dataset - precision")
plt.xlabel("Number of training subsets processed")

plt.legend()
plt.savefig("nasa_precision.png", dpi=300)
plt.clf()

plt.plot(nh["numbers"], nh["recall"], label="Hoeffding Adaptive Tree")
plt.plot(nk["numbers"], nk["recall"], label="kNN")
plt.plot(nl["numbers"], nl["recall"], label="Logistic Regression")
plt.plot(np["numbers"], np["recall"], label="PA")
plt.xticks(nh["numbers"])
plt.yticks(nh["percentage"])
plt.title("NASA dataset - recall")
plt.xlabel("Number of training subsets processed")

plt.legend()
plt.savefig("nasa_recall.png", dpi=300)
plt.clf()

plt.plot(nh["numbers"], nh["f1"], label="Hoeffding Adaptive Tree")
plt.plot(nk["numbers"], nk["f1"], label="kNN")
plt.plot(nl["numbers"], nl["f1"], label="Logistic Regression")
plt.plot(np["numbers"], np["f1"], label="PA")
plt.xticks(nh["numbers"])
plt.yticks(nh["percentage"])
plt.title("NASA dataset - F1 score")
plt.xlabel("Number of training subsets processed")

plt.legend()
plt.savefig("nasa_f1.png", dpi=300)
plt.clf()