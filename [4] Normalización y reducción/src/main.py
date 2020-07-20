import pandas
from matplotlib import pyplot as plt
from sklearn.feature_selection import RFECV
from sklearn.preprocessing import StandardScaler
from sklearn.svm import LinearSVC
from sklearn.svm import SVC

cv = 4

input_filename = 'dataset.csv'
print('==> Reading file (' + input_filename + ')')
data_frame = pandas.read_csv(input_filename, header=0, encoding="ISO-8859-1")  # header: Row number(s) to use as the
print('==> File successfully loaded:')
print(data_frame.head())
print("==> Dropping columns 'name' & 'Class'")
data_frame = data_frame.drop(['name', 'Class'], axis=1)  # Dropping columns 'name' and 'Class'
print('==> Columns dropped: ')
print(data_frame.head())

'''
###########################################################################
print('###########################################################################')
if_more_than_percent = 0.25
columns_to_drop = []
for column in data_frame:
    zero_counter = 0
    for row in data_frame[column]:
        if row == 0:
            zero_counter += 1
    if zero_counter / 1072 > if_more_than_percent:
        columns_to_drop.append(column)
print(len(columns_to_drop), columns_to_drop)
data_frame = data_frame.drop(columns_to_drop, axis=1)  # Dropping columns 'name' and 'Class'
###########################################################################
###########################################################################
print('###########################################################################')
if_more_than_percent = 0.25
rows_to_drop = []
for row in data_frame.iloc[:, 0:-1].itertuples():
    zero_counter = 0
    for element in row:
        if element == 0:
            zero_counter += 1
    if zero_counter / 126 > if_more_than_percent:
        rows_to_drop.append(row[0])
print(len(rows_to_drop), rows_to_drop)
#print(rows_to_drop[0])
#print(data_frame.iloc[rows_to_drop[0], :])
data_frame = data_frame.drop(rows_to_drop, axis=0)  # Dropping columns 'name' and 'Class'
###########################################################################
'''

array = data_frame.values
X = data_frame.iloc[:, 0:-1]
Y = data_frame.iloc[:, -1]
print('==> X:')
print(X)
print('==> Y:')
print(Y)

'''
###########################################################################
print('###########################################################################')
columns_means_excluding_zero_values = []
for column in X:
    print(column)
    num_valid_elements = 0
    sum = 0
    for row in data_frame[column]:
        if row != 0:
            num_valid_elements += 1
            sum += row
    mean = sum / num_valid_elements
    print(column, mean)
    X[column] = X[column].replace(0, mean)
    #for row in data_frame[column]:
    #    if row == 0:
    #        row = mean
'''

print('================================= Scaling data(NORMALIZATION) =================================')
# SVMs assume that the data it works with is in a standard range
scaler = StandardScaler().fit(X)  # Standardize features by removing the mean and scaling to unit variance
X = scaler.transform(X)
print('Scaled data')
print('==> X:')
print(X)


print('================================= FEATURE SELECTION  =================================')
# Esteban:
# as for penalty=: l2 works better
# as for loss. No real change
# as for dual. This must set to false because n_samples > n_features
# as tolerance. The lower this number, the higher the accuracy
# as for max_iter: graph is sharper with more iter.
# A supervised learning estimator with a fit method that updates a coef_ attribute that holds the
# fitted parameters. Important features must correspond to high absolute values in the coef_ array.
estimator = LinearSVC(dual=False, tol=1e-5, random_state=0)
wrapper = RFECV(estimator, cv=cv, verbose=True, n_jobs=-1)
wrapper.fit(X, Y)

plt.figure()
plt.xlabel("Number of features selected")
plt.ylabel("Cross validation score \n of number of selected features")
plt.title("RFECV(LinearSVC(penalty='l2', tol=1e-5)) cv=" + str(cv))
plt.plot(range(1, len(wrapper.grid_scores_) + 1), wrapper.grid_scores_)
plt.show()

print(wrapper.support_)
counter = 0
selected_features = []
for element in wrapper.support_:
    if element:
        selected_features.append(counter)
    counter += 1
print('select (', wrapper.n_features_, ') features at score:', max(wrapper.grid_scores_))
print(selected_features)
print()

