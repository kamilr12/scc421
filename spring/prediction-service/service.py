from pyexpat import model
from statistics import mode
from json import dumps, loads
import pickle
import json
from river import linear_model
from river import metrics
from river import preprocessing
from river import optim
from river import imblearn
from river import ensemble
from river import facto
from river import tree
from river import neighbors
import logging
import datetime
import time
from kafka import KafkaConsumer
from kafka import KafkaProducer

from threading import Thread
import collections
import signal
import sys

metric = metrics.ROCAUC()
accuracy = metrics.Accuracy()
precision = metrics.Precision()
recall = metrics.Recall()
f1 = metrics.F1()
confusion_matrix = metrics.ConfusionMatrix()

prediction_map = dict()
ml_metrics = open("logs/ml_metrics.csv", "w")
dataset_percentage = dict()
models = dict()

logger = logging.getLogger("my_logger")
logger.setLevel(logging.DEBUG)

file_handler = logging.FileHandler("logs/prediction-service_" + datetime.datetime.today().strftime('%Y-%m-%d--%H-%M') + ".log")
file_handler.setLevel(logging.DEBUG)

logger.addHandler(file_handler)


scaler = preprocessing.StandardScaler()
#model = linear_model.LogisticRegression(optimizer=optim.SGD(0.01))
#model = tree.HoeffdingAdaptiveTreeClassifier(grace_period=100, split_confidence=1e-5, nominal_attributes=["normal", "alarm"])
model = linear_model.PAClassifier()
#model = neighbors.KNNClassifier()


def get_database(name):
    from pymongo import MongoClient
    import pymongo

    CONNECTION_STRING = "mongodb://root:password@localhost:27017"

    from pymongo import MongoClient
    client = MongoClient(CONNECTION_STRING)

    return client[name]

def get_model(db, organisation_id):
    model = db["models"].find_one({"organisationId": organisation_id})
  
    if model == None:
        return None, None
    return (pickle.loads(model["model"]), pickle.loads(model["scaler"]))

def save_model(db, organisation_id, data):
    scaler, model = data
    db["models"].insert_one({"organisationId": organisation_id, "scaler": scaler, "model": model})

def update_model(db, organisation_id, data):
    scaler, model = data
    filter = {"organisationId": organisation_id}
    new_values = {"$set": {"model": model, "scaler": scaler}}
    db["models"].update_one(filter, new_values)

def store_prediction(db, organisation_id, data):
    data["timestamp"] = get_timemillis()
    db[organisation_id].insert_one(data)

def get_timemillis():
    return int(round(time.time() * 1000))

def get_str_prediction(prediction):
    if prediction == 0:
        return "Normal"
    else:
        return "Alarm"

def check_and_store(key):
    predictions = prediction_map[key]
    if (len(predictions) > 1000):
        predictionsdb[key].insert_many(predictions)
        prediction_map[key].clear()

def update_metrics(target, prediction, should_store, organisation_id):
    global accuracy
    global precision
    global recall
    global f1
    global confusion_matrix

    accuracy.update(target, prediction)
    precision.update(target, prediction)
    recall.update(target, prediction)
    f1.update(target, prediction)
    confusion_matrix.update(target, prediction)
    print(should_store)

    if should_store == "True":
        if not organisation_id in dataset_percentage:
            dataset_percentage[organisation_id] = 0.1
        
        percentage = dataset_percentage[organisation_id]
        dataset_percentage[organisation_id] = dataset_percentage[organisation_id] + 0.1

        ml_metrics.write("{},{},{},{},{},{},{}\n".format(organisation_id, percentage, accuracy.get(), precision.get(), recall.get(), f1.get(), confusion_matrix))
        metric = metrics.ROCAUC()
        accuracy = metrics.Accuracy()
        precision = metrics.Precision()
        recall = metrics.Recall()
        f1 = metrics.F1()
        confusion_matrix = metrics.ConfusionMatrix()


counter = 0
modelsdb = get_database("prediction-service")
predictionsdb = get_database("predictions")
print('everything set up')

def process_test(message):
    global models
    global model
    global scaler
    key = message.key.decode('utf-8')
    value = json.loads(message.value.decode('utf-8'))
    logger.debug("prediction-service, " + value["requestId"] + ", start, " + str(get_timemillis()))

    model, scaler= get_model(modelsdb, key)
    
    if not key in models:
        print('laduje')
        db_model, db_scaler= get_model(modelsdb, key)
        models[key] = {"model": db_model, "scaler": db_scaler}

    model = models[key]["model"]
    scaler = models[key]["scaler"]
    
    data_to_predict = value["content"]
    device_id = data_to_predict.pop("deviceId", None)
    should_store = data_to_predict.pop("accuracy")
    target = data_to_predict.pop("target")
    
    scaled = scaler.transform_one(data_to_predict)
    prediction = model.predict_one(scaled)

    if not key in prediction_map:
        prediction_map[key] = []

    prediction_map[key].append({"deviceId": device_id, "timestamp": get_timemillis(), "prediction": get_str_prediction(prediction)})

    if get_str_prediction(prediction) == "Alarm":
        producer.send("predictions", key=key.encode(), value={"organisationId": key, "requestId": value["requestId"], "deviceId": device_id, "prediction": get_str_prediction(prediction)})
        producer.flush()

    check_and_store(key)

    logger.debug("prediction-service, " + value["requestId"] + ", end, " + str(get_timemillis()))
    update_metrics(target, prediction, should_store, key)


def process_train(message):
    global model
    global scaler
    key = message.key.decode('utf-8')
    value = json.loads(message.value.decode('utf-8'))
    model, scaler = get_model(modelsdb, key)
    
    if model == None:
        scaler = preprocessing.StandardScaler()
        #model = linear_model.LogisticRegression(optimizer=optim.SGD(0.01))
        #model = tree.HoeffdingAdaptiveTreeClassifier(grace_period=100, split_confidence=1e-5, nominal_attributes=["normal", "alarm"])
        model = linear_model.PAClassifier()
        #model = neighbors.KNNClassifier()
        data_model = pickle.dumps(model)
        data_scaler = pickle.dumps(scaler)
        save_model(modelsdb, key, (data_scaler, data_model))

    data_to_learn = value["content"]
    data_to_learn.pop("deviceId", None)
    target = data_to_learn.pop("target")
    scaled = scaler.learn_one(data_to_learn).transform_one(data_to_learn)
    y_pred = model.predict_one(scaled)

    model.learn_one(scaled, target)
    data_model = pickle.dumps(model)
    data_scaler = pickle.dumps(scaler)
    update_model(modelsdb, key, (data_scaler, data_model))
    print(metric)

def signal_handler(sig, frame):
    print('You pressed Ctrl+C!')
    ml_metrics.close()
    sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)


consumer = KafkaConsumer(
     bootstrap_servers=['localhost:9092'],
     auto_offset_reset='latest',
     enable_auto_commit=True,
     group_id="group3")

consumer.subscribe(['readings', 'train'])

producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda m: json.dumps(m).encode('utf-8')
)

for message in consumer:
    counter += 1
    print(counter)
    if message.topic == 'train':
        process_train(message)
    else:
        process_test(message)