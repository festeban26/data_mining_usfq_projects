# Python 3
# TextMining
# Author: Esteban Flores at festeban26@gmail.com
# Last modified Oct 14, 2018

import os
from nltk import word_tokenize
from nltk.corpus import stopwords
from nltk.stem import SnowballStemmer
import string
import math
import operator

snowball_stemmer = SnowballStemmer('english')

print("Welcome to TextMining software by festeban26")

cwd = os.getcwd()
articles_directory = os.path.join(cwd, 'articulos')
articles_filenames = os.listdir(articles_directory)

print('Articles: ')

for i in range(0, len(articles_filenames)):
    print('\t(' + str(i) + '): ' + articles_filenames[i])

print('Normalization process...')
articles = []

for i in range(0, len(articles_filenames)):
    print('(' + str(i) + ') ' + os.path.join(articles_directory, articles_filenames[i]) + ': ')

    print('\tOriginal Text: ', end='')
    f = open(os.path.join(articles_directory, articles_filenames[i]))
    raw = f.read()
    print(raw.replace('\n', ' '))

    tokens = word_tokenize(raw)
    print('\tOriginal Tokens (' + str(len(tokens)) + '): ', end='')
    print(tokens)

    filtered_words_no_punctuation = [t for t in tokens if t not in string.punctuation]
    removed_words = [t for t in tokens if t in string.punctuation]
    print('\tWithout Punctuation (' + str(len(filtered_words_no_punctuation)) + '): ', end='')
    print(filtered_words_no_punctuation)
    print('\t\tRemoved Tokens (' + str(len(removed_words)) + '): ', end='')
    print(removed_words)

    nltk_stop_words = set(stopwords.words("english"))
    filtered_words_no_stopwords = [word.lower() for word in filtered_words_no_punctuation if
                                   word.lower() not in nltk_stop_words]
    removed_words = [word.lower() for word in filtered_words_no_punctuation if word.lower() in nltk_stop_words]
    print('\tWithout Stop-Words (' + str(len(filtered_words_no_stopwords)) + '): ', end='')
    print(filtered_words_no_stopwords)
    print('\t\tRemoved Tokens (' + str(len(removed_words)) + '): ', end='')
    print(removed_words)

    word_replaced_by_stemmer = []
    filtered_words_stems = []
    for word in filtered_words_no_stopwords:
        stem = snowball_stemmer.stem(word)
        if word != stem:
            word_replaced_by_stemmer.append(word + ":" + stem)
        filtered_words_stems.append(stem)

    print('\tAfter Snowball Stemmer: ', end='')
    print(filtered_words_stems)
    print('\t\tWords Replaced as Result of Snowball Stemmer: ', end='')
    print(word_replaced_by_stemmer)

    filtered_words_output = filtered_words_stems
    print('\tOutput (' + str(len(filtered_words_output)) + '): ', end='')
    print(filtered_words_output)
    articles.append(filtered_words_output)

raw_query = 'not -q'
while raw_query != '-q':
    raw_query = input("Enter query or '-q' to exit: ")
    if raw_query == '-q':
        exit(0)
    elif len(raw_query) == 0:
        raw_query = 'Hurricane michael floods'

    print('Raw Query Tokens: ', end='')
    print(raw_query.split())
    print('Formatted Query Tokens (lower case and stemmed): ', end='')
    query = [snowball_stemmer.stem(token.lower()) for token in raw_query.split()]
    print(query)

    print('Term Frequency [ tf(t, d) ] process: ')
    tf = []
    max_article_characters = 60
    print('\tOccurrences : Frequency (tf(t, d))')
    print('\tArticle'.ljust(max_article_characters), '|', "Tokens", '|', end='')
    for i in range(0, len(query)):
        print('', str(query[i]).rjust(len(query[i]) + 5), '|', end='')
        tf.append([0 for i in range(len(articles))])
    print()
    for i in range(0, len(articles)):
        print('\t' + articles_filenames[i]
              .ljust(max_article_characters)[0:max_article_characters - 4] + "...", end='')
        tf_idf_sum = []
        for j in range(0, len(query)):
            tf_idf_sum.append(0)
            for token in articles[i]:
                if token == query[j].lower():
                    tf_idf_sum[j] += 1
        num_tokens = len(articles[i])
        print(str(num_tokens).rjust(len('Tokens') + 3), end='')
        for j in range(0, len(query)):
            frequency = tf_idf_sum[j] / num_tokens
            tf[j][i] = frequency
            print(str(str(tf_idf_sum[j]) + ' : ' + format(tf[j][i], '.4f')).rjust(len(query[j]) + 5 + 3), end='')
        print()

    print('Document Frequency [ df(t) ] process: ')
    df = []
    print('\t' + 'Term'.ljust(15) + 'df')
    for i in range(0, len(query)):
        df.append(0)
        print('\t' + str(query[i]).ljust(15) + '', end='')
        for j in range(0, len(articles)):
            if tf[i][j] > 0:
                df[i] += 1
        print(df[i])

    print('Inverse Document Frequency [ idf(t) ] process:')
    idf = []
    N = len(articles)
    print('\tN = ' + str(N))
    print('\t' + 'Term'.ljust(15) + 'idf')
    for i in range(0, len(query)):
        if df[i] != 0:
            idf.append(math.log10(N / df[i]))
        else:
            idf.append(0.0)
        print('\t' + str(query[i]).ljust(15) + format(idf[i], '.4f'), end='')
        print()

    print('tf-idf process:')
    tf_idf = []
    max_article_characters = 60
    print('\tArticle'.ljust(max_article_characters), '|',
          'SUM'.center(len('SUM') + 4), '|', end='')
    for i in range(0, len(query)):
        print('', str(query[i]).rjust(len(query[i]) + 2), '|', end='')
    print()
    for i in range(0, len(articles)):
        print('\t' + articles_filenames[i]
              .ljust(max_article_characters)[0:max_article_characters - 4] + "...", end='')
        tf_idf_sum = []
        for j in range(0, len(query)):
            tf_idf_sum.append(0)

        for j in range(0, len(query)):
            current_tf_idf = tf[j][i] * idf[j]
            tf_idf_sum[j] += current_tf_idf

        tf_idf.append(sum(tf_idf_sum))
        print(str(format(sum(tf_idf_sum), '.5f')).rjust(len('SUM') + 4 + 3), end='')

        for j in range(0, len(query)):
            print(str(format(tf_idf_sum[j], '.5f')).rjust(len(query[j]) + 2 + 3), end='')
        print()

    print('output:')
    sorted_keys = [i[0] for i in sorted(enumerate(tf_idf), key=operator.itemgetter(1), reverse=True)]
    for i in range(0, len(sorted_keys)):
        print('\t(' + format(tf_idf[sorted_keys[i]], '.4f') + ') ' + articles_filenames[sorted_keys[i]])
