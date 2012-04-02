PredictionError
===============

This processor checks the processed data items for the existence of
a `@label` and one or more `@prediction` keys. It will compute the
loss of each of the `@prediction` values against the `@label` and add
an `@error` value for each of these.

As an example, assume that the data item contains a `@label` key and
the predictions `@prediction:NaiveBayes` and `@prediction:Perceptron`.
Then the *PredictionError* processor will add `@error:NaiveBayes` and
`@error:Perceptron`, which will contain the difference of the predicted
values against the `@label` value.

The default loss function is a "zero-one" loss.

