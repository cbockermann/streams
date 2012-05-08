PredictionService
=================

This service is provided by classifiers or regression models. It provides
a single method `predict` which requires a data item as argument and will
return a prediction:


       public Serializable predict( Data item );
