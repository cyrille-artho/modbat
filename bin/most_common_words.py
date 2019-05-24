import collections
import re 
import csv

cr = csv.reader(open("outputs.csv","r"))
arguments = []
i=0
for row in cr: 
    if i>0:
        arguments += row[1:]
    i+=1
most_common = collections.Counter(arguments).most_common()
c = csv.writer(open("mostcommon.csv", "w"))
i=0
for couples in most_common:
    if i>0:
        c.writerow(list(couples))
    i+=1