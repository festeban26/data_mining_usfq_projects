"""
Author: Esteban Flores Andrade at festeban26@gmail.com
"""

import numpy
import pandas
from matplotlib import pyplot as plt
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.preprocessing import StandardScaler

input_filename = 'dataset.csv'
print('========================== 1. SUBSETS CREATION ==========================')
print("==> From previous homework, subset columns: ")
subset_columns = [28, 38, 42, 43, 44, 45, 46, 47, 48, 49, 55, 64, 65, 66, 73, 75, 103, 105, 106, 108, 115, 119, 121]
#subset_noel
#subset_columns = [4, 12, 13, 18, 19, 22, 23, 24, 32, 33, 35, 36, 37, 40, 42, 43, 44, 45, 46, 48, 49, 54, 55, 57, 59, 60, 65, 66, 72, 77, 82, 83, 86, 87, 89, 90, 91, 94, 96, 99, 100, 102, 103, 104, 105, 108, 110, 112, 114, 115, 117, 122]
print(subset_columns)
print('==> Reading file (' + input_filename + ')')
data_frame = pandas.read_csv(input_filename, header=0, encoding="ISO-8859-1")
print('==> File successfully loaded:')
print(data_frame.head())
print("==> Dropping columns 'name' & 'Class'")
data_frame = data_frame.drop(['name', 'Class'], axis=1)  # Dropping columns 'name' and 'Class'
print('==> Columns dropped: ')
print(data_frame.head())
X = data_frame.iloc[:, subset_columns]
print("X DATA:")
print(X)
Y = data_frame.iloc[:, -1]
print("Y DATA:")
print(Y)

print('========================== 2. NORMALIZATION ==========================')
# Standardization of datasets is a common requirement for many machine learning estimators implemented in
# scikit-learn; they might behave badly if the individual features do not more or less look like standard normally
# distributed data: Gaussian with zero mean and unit variance.
scaler = StandardScaler().fit(X)
X = scaler.transform(X)
print("STANDARDIZED DATA: \n", numpy.around(X, 3))

print('========================== 3(CONT). SUBSETS CREATION ==========================')
X_cross_validation, X_test, Y_cross_validation, Y_test = train_test_split(X, Y, test_size=0.20)
print("X_cross_validation:")
print(X_cross_validation)
print("Y_cross_validation:")
print(Y_cross_validation)
print("X_test:")
print(X_test)
print("Y_test:")
print(Y_test)

print('========================== 4. KNN ==========================')
euclidean_cv_scores = []
manhattan_cv_scores = []
k_range = list(range(1, 30))
cv = 100
for k in k_range:
    knn_euclidean = KNeighborsClassifier(n_neighbors=k, metric='euclidean')
    knn_manhattan = KNeighborsClassifier(n_neighbors=k, metric='manhattan')
    euclidean_scores = cross_val_score(knn_euclidean, X_cross_validation, Y_cross_validation, cv=cv, scoring='accuracy')
    manhattan_scores = cross_val_score(knn_manhattan, X_cross_validation, Y_cross_validation, cv=cv, scoring='accuracy')
    euclidean_cv_scores.append(euclidean_scores.mean())
    manhattan_cv_scores.append(manhattan_scores.mean())

plt.figure()
plt.title("KNN Scores")
plt.plot(k_range, euclidean_cv_scores, label='Euclidean')
plt.plot(k_range, manhattan_cv_scores, label='Manhattan')
plt.xlabel('Value of K for KNN')
plt.ylabel('Cross-Validated Accuracy')
plt.legend()
plt.show()

euclidean_optimal_knn = euclidean_cv_scores.index(max(euclidean_cv_scores))
manhattan_optimal_knn = manhattan_cv_scores.index(max(manhattan_cv_scores))

print('Euclidean Optimal kNN:', euclidean_optimal_knn, 'with accuracy: ', max(euclidean_cv_scores))
print('Manhattan Optimal kNN:', manhattan_optimal_knn, 'with accuracy: ', max(manhattan_cv_scores))

print('========================== 5. KNN TEST PREDICTION ==========================')
print('With Euclidean Optimal kNN:')
knn_euclidean = KNeighborsClassifier(n_neighbors=euclidean_optimal_knn, metric='euclidean')
knn_manhattan = KNeighborsClassifier(n_neighbors=euclidean_optimal_knn, metric='manhattan')
knn_euclidean.fit(X_cross_validation, Y_cross_validation)
knn_manhattan.fit(X_cross_validation, Y_cross_validation)
euclidean_predict = knn_euclidean.predict(X_test)
manhattan_predict = knn_manhattan.predict(X_test)
print('Euclidean kNN: ', euclidean_predict)
print('Manhattan kNN: ', manhattan_predict)

count = 0
for i in range(0, len(euclidean_predict) - 1):
    if euclidean_predict[i] != manhattan_predict[i]:
        count += 1

print('kNNs different predictions: ', count, ' in ', len(euclidean_predict), '(', count / len(euclidean_predict) * 100,
      '%)')

print('With Manhattan Optimal kNN:')
knn_euclidean = KNeighborsClassifier(n_neighbors=manhattan_optimal_knn, metric='euclidean')
knn_manhattan = KNeighborsClassifier(n_neighbors=manhattan_optimal_knn, metric='manhattan')
knn_euclidean.fit(X_cross_validation, Y_cross_validation)
knn_manhattan.fit(X_cross_validation, Y_cross_validation)
euclidean_predict = knn_euclidean.predict(X_test)
manhattan_predict = knn_manhattan.predict(X_test)
print('Euclidean kNN: ', euclidean_predict)
print('Manhattan kNN: ', manhattan_predict)

count = 0
for i in range(0, len(euclidean_predict) - 1):
    if euclidean_predict[i] != manhattan_predict[i]:
        count += 1

print('kNNs different predictions: ', count, ' in ', len(euclidean_predict), '(', count / len(euclidean_predict) * 100,
      '%)')

